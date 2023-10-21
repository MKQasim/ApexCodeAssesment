package com.apex.codeassesment.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apex.codeassesment.R
//import com.apex.codeassesment.data.UserRepository
//import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.ui.details.DetailsActivity
import com.apex.codeassesment.databinding.ActivityMainBinding
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.recyclerview.widget.LinearLayoutManager




// Import necessary libraries
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apex.codeassesment.data.UserRepository
import com.apex.codeassesment.data.local.LocalDataSource
import com.apex.codeassesment.data.local.PreferencesManager
import com.apex.codeassesment.data.model.Picture
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.data.remote.RemoteDataSource
import com.apex.codeassesment.di.MainComponent
import com.bumptech.glide.Glide
import javax.inject.Inject

// TODO (5 points): Move calls to repository to Presenter or ViewModel.
// TODO (5 points): Use combination of sealed/Dataclasses for exposing the data required by the view from viewModel .
// TODO (3 points): Add tests for viewmodel or presenter.
// TODO (1 point): Add content description to images
// TODO (3 points): Add tests
// TODO (Optional Bonus 10 points): Make a copy of this activity with different name and convert the current layout it is using in
// TODO (Jetpack Compose).

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private lateinit var mainViewModel: MainViewModel
  private lateinit var localDataSource: LocalDataSource
  private lateinit var userRepository: UserRepository
  private var userAdapter: UserAdapter? = null
  private var userListView: RecyclerView? = null

  companion object {
    // Declare a sharedContext variable to hold the context
    lateinit var sharedContext: Context
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    sharedContext = this
    // Initialize PreferencesManager with the application context
    val preferencesManager = PreferencesManager(sharedContext)
    // Initialize LocalDataSource with PreferencesManager
    localDataSource = LocalDataSource(preferencesManager)
    userRepository = UserRepository(localDataSource, RemoteDataSource())

    // Initialize the ViewModel directly
    mainViewModel = MainViewModel(userRepository)

    // Observe LiveData for user data
    mainViewModel.userList.observe(this, { userList -> userAdapter?.updateUsers(userList) })
    mainViewModel.userData.observe(this, { user ->
      if (user != null) {
        // Update other UI components using data binding
        binding.mainEmail.text = user.email
        binding.mainName.text = user.name?.first

        // Update the user picture using the Picture object
        user.picture = Picture(
          large = "https://images.unsplash.com/photo-1575936123452-b67c3203c357?auto=format&fit=crop&q=80&w=2940&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          medium = "https://images.unsplash.com/photo-1575936123452-b67c3203c357?auto=format&fit=crop&q=80&w=2940&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
          thumbnail = "https://images.unsplash.com/photo-1575936123452-b67c3203c357?auto=format&fit=crop&q=80&w=2940&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
        )
        // TODO (1 point): Use Glide to load images after getting the data from endpoints mentioned in RemoteDataSource
        // Load the user image with Glide using data binding
        if (user.picture != null) {
          Glide.with(this).load(user.picture!!.large).into(binding.mainImage)
        }
      }
    })

    binding.mainSeeDetailsButton.setOnClickListener {
      mainViewModel.userData.value?.let { it1 -> this.navigateDetails(it1) }
    }
    binding.mainUserList.layoutManager = LinearLayoutManager(this)
    binding.mainUserList.adapter = userAdapter

    binding.mainRefreshButton.setOnClickListener {
      mainViewModel.refreshUser()
    }
    binding.mainUserListButton.setOnClickListener {
      mainViewModel.showUserList()
    }
    // Load initial user data and show the user list initially
    mainViewModel.loadInitialUser()
    mainViewModel.showUserList()
  }
  // TODO (2 points): Convert to extenstion function.
  private fun navigateDetails(user: User) {
    // Create an Intent to start the DetailsActivity
    val intent = Intent(this, DetailsActivity::class.java)
    intent.putExtra("saved-user-key", user)
    // Start the DetailsActivity using the Intent
    startActivity(intent)
  }
}

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
  // LiveData to hold the user data
  val userData = MutableLiveData<User>()
  val userList = MutableLiveData<List<User>>()

  fun loadInitialUser() {
    val user = userRepository.getSavedUser()
    userData.value = user
  }

  fun refreshUser() {
    val user = userRepository.getUser(true)
    userData.value = user
  }

  fun showUserList() {
    val users = userRepository.getUsers()
    userList.value = users
  }

  fun navigateDetails(user: User) {
    // Navigate to user details using user data
    // You can use an Intent here or navigate to another fragment/activity.

  }
}

class UserAdapter(
  private var userList: List<User>,
  private val onItemClickListener: (User) -> Unit
) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

  inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val userNameTextView: TextView = itemView.findViewById(R.id.userNameTextView)
    // Add other views from your item layout
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
    val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item_layout, parent, false)
    return UserViewHolder(itemView)
  }

  override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
    val currentUser = userList[position]
    holder.userNameTextView.text = currentUser.name?.first
    // Set other data for your item views
    holder.itemView.setOnClickListener { onItemClickListener(currentUser) }
  }

  override fun getItemCount(): Int {
    return userList.size
  }
  // Create a function to update the user list in the adapter
  fun updateUsers(users: List<User>) {
    userList = users
    notifyDataSetChanged()
  }
}


