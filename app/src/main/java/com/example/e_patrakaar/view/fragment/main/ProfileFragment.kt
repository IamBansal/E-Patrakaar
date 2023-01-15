package com.example.e_patrakaar.view.fragment.main

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.e_patrakaar.R
import com.example.e_patrakaar.databinding.CustomBottomSheetBinding
import com.example.e_patrakaar.databinding.FragmentProfileBinding
import com.example.e_patrakaar.model.Collection
import com.example.e_patrakaar.view.OnItemClickListener
import com.example.e_patrakaar.view.WrapContentStaggeredGridLayoutManager
import com.example.e_patrakaar.view.activity.AuthActivity
import com.example.e_patrakaar.view.activity.MainActivity
import com.example.e_patrakaar.view.adapter.CollectionAdapter
import com.example.e_patrakaar.view.adapter.LatestTechAdapter
import com.example.e_patrakaar.viewmodel.RandomNewsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database

class ProfileFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: FragmentProfileBinding
    private var savedNews = ArrayList<Collection>()
    private lateinit var randomNewsViewModel: RandomNewsViewModel
    private lateinit var adapterSavedNews: LatestTechAdapter
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var progressBar: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private var currentUser: String? = null
    private var collectionHashMap: Map<* , *> = hashMapOf<String , Map<String , String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        database = Firebase.database.reference
        currentUser = FirebaseAuth.getInstance().currentUser?.uid
        savedNews = ArrayList()
        randomNewsViewModel = ViewModelProvider(this)[RandomNewsViewModel::class.java]
        randomNewsViewModel.getNewsFromAPI()

        Glide.with(requireActivity()).load(R.drawable.pic).circleCrop().into(binding.ivProfile)

        val collectionListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.value !=null){
                    collectionHashMap = snapshot.value as Map<* , *>
                }
                val list = mutableListOf<Collection>()
                collectionHashMap.forEach { (key, value) ->
                    val collection = Collection()
                    val map = value as Map<*, *>
                    map.forEach{ (key1 , value1) ->
                        when(key1 as String){
                            "title" -> collection.title = value1 as String
                            "description" -> collection.description = value1 as String
                            "image" -> collection.image = value1
                        }
                    }
                    list.add(collection)
                }
                collectionAdapter.setData(list)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        if(currentUser!=null){
            database.child("users").child(currentUser!!).child("collections").addValueEventListener(collectionListener)
        }
        binding.rvSaved.layoutManager =
            WrapContentStaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        collectionAdapter = CollectionAdapter(
            this@ProfileFragment,
            mutableListOf(), this
        )
        binding.rvSaved.adapter = collectionAdapter

        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.hamburger -> {
                        showBottomSheetDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        (activity as MainActivity).supportActionBar!!.title = binding.tvUsername.text

        binding.btnEditInfo.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_profile_to_navigation_edit_profile)
        }

        binding.btnNewCollection.setOnClickListener {
            showPopUpWindow()
        }
    }

    //Changes for database
    private fun randomNewsViewModelObserver() {
        randomNewsViewModel.randomNewsResponse.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                val random = (0..100).random()
                if (it.articles.size >= random + 10) {
                    for (i in random..random + 10) {
                        val e = it.articles[i]
                        savedNews.add(Collection(e.title, e.description, e.urlToImage))
                        adapterSavedNews.setData(savedNews)
                    }
                } else {
                    for (i in random..random + 1) {
                        val e = it.articles[i]
                        savedNews.add(Collection(e.title, e.description, e.urlToImage))
                        adapterSavedNews.setData(savedNews)
                    }
                }
                progressBar.dismiss()
            }
        }

        randomNewsViewModel.randomNewsLoadingError.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                progressBar.dismiss()
            }
        }

        randomNewsViewModel.loadRandomNews.observe(
            viewLifecycleOwner
        ) {
            it?.let {
                if (it) {
                    progressBar.show()
                } else {
                    progressBar.dismiss()
                }
            }
        }
    }

    private fun showBottomSheetDialog() {
        val dialog = BottomSheetDialog(requireActivity())
        val view = CustomBottomSheetBinding.inflate(layoutInflater)

        view.tvYourActivity.setOnClickListener {
            Toast.makeText(requireActivity(), "Your activity is accessed.", Toast.LENGTH_SHORT)
                .show()
            dialog.dismiss()
        }
        view.tvArchive.setOnClickListener {
            Toast.makeText(requireActivity(), "Archive is accessed.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        view.tvPolicy.setOnClickListener {
            Toast.makeText(requireActivity(), "Privacy Policy is accessed.", Toast.LENGTH_SHORT)
                .show()
            dialog.dismiss()
        }
        view.tvHelp.setOnClickListener {
            Toast.makeText(requireActivity(), "Help is accessed.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        view.tvTheme.setOnClickListener {
            Toast.makeText(requireActivity(), "Theme is accessed.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        view.tvSettings.setOnClickListener {
            Toast.makeText(requireActivity(), "Settings is accessed.", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        view.tvLogout.setOnClickListener {
            logout()

            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.setContentView(view.root)
        dialog.show()
    }

    private fun logout() {
        val alert = AlertDialog.Builder(requireActivity())
        alert.setTitle("Logout Requested!")
            .setMessage("You sure you want to logout?")
            .setPositiveButton("Yes"){_,_->
                Firebase.auth.signOut()
//                Toast.makeText(requireActivity(), firebaseAuth.currentUser!!.uid, Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
//                Toast.makeText(requireActivity(), firebaseAuth.currentUser!!.uid, Toast.LENGTH_SHORT).show()
//                (activity as MainActivity).finish()
            }
            .setNegativeButton("No"){_,_->}
            .create()
            .show()
    }

    private fun newsDetails(news: Collection) {
        findNavController().navigate(
            ProfileFragmentDirections.actionNavigationProfileToNavigationExpandedNews(
                news
            )
        )
    }

    override fun onItemClick(news: Collection) {
        newsDetails(news)
    }

    override fun onItemClickReturnViewHolder(viewHolder: RecyclerView.ViewHolder) {
        if ((viewHolder as CollectionAdapter.ViewHolder).rvSavedNews.visibility == View.VISIBLE) {
            viewHolder.rvSavedNews.visibility = View.GONE
            return
        }
        viewHolder.rvSavedNews.visibility = View.INVISIBLE
        adapterSavedNews = LatestTechAdapter(this, savedNews, this)
        progressBar = ProgressDialog(requireActivity())
        progressBar.setMessage("Loading news..")
        progressBar.show()
        randomNewsViewModelObserver()
        viewHolder.rvSavedNews.layoutManager =
            WrapContentStaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        viewHolder.rvSavedNews.adapter = adapterSavedNews
        viewHolder.rvSavedNews.visibility = View.VISIBLE
        progressBar.dismiss()
    }

    private fun showPopUpWindow() {

        binding.fragProf.foreground =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_blur)

        val popView = layoutInflater.inflate(R.layout.new_collection_popup_window, null)
        val popupWindow = PopupWindow(
            popView,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.isOutsideTouchable = true
        popupWindow.isFocusable = true
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        val edtTxtCollectionName = popView.findViewById<EditText>(R.id.edtTxtNewCollPopWindow)
        val btnCreate = popView.findViewById<MaterialButton>(R.id.btnCreateColl)

        btnCreate.setOnClickListener {
            popupWindow.dismiss()
            val collectionName = edtTxtCollectionName.text.toString()
            var collectionExists = false
            collectionHashMap.forEach { key, value ->
                if(key as String == collectionName) {
                    collectionExists = true
                    Toast.makeText(context , "Collection already exists" , Toast.LENGTH_SHORT).show()
                    return@forEach
                }
            }
            if(collectionName != "" && currentUser!=null && !collectionExists){
                val collection = Collection(
                    edtTxtCollectionName.text.toString(),
                    "50 saved posts",
                    R.drawable.proone
                )
                database.child("users").child(currentUser!!).child("collections").child(collectionName).setValue(collection)
            }
        }
        popupWindow.setOnDismissListener {
            binding.fragProf.foreground = null
        }
    }
}