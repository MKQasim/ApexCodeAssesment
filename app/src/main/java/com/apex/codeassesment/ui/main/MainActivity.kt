package com.apex.codeassesment.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apex.codeassesment.R
import com.apex.codeassesment.ui.details.DetailsActivity
import com.apex.codeassesment.databinding.ActivityMainBinding
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager


// Import necessary libraries
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.apex.codeassesment.data.ApiResult
import com.apex.codeassesment.data.UserRepository
import com.apex.codeassesment.data.local.LocalDataSource
import com.apex.codeassesment.data.local.PreferencesManager
import com.apex.codeassesment.data.model.Picture
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.data.remote.RemoteDataSource
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

// TODO (5 points): Move calls to repository to Presenter or ViewModel.
// TODO (5 points): Use combination of sealed/Dataclasses for exposing the data required by the view from viewModel .
// TODO (3 points): Add tests for viewmodel or presenter.
// TODO (1 point) : Add content description to images.
// TODO (3 points): Add tests.
// TODO (Optional Bonus 10 points): Make a copy of this activity with different name and convert the current layout it is using in
// TODO (Jetpack Compose).

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private lateinit var mainViewModel: MainViewModel
  private lateinit var localDataSource: LocalDataSource
  private lateinit var userRepository: UserRepository
  private var userAdapter: UserAdapter? = null

  companion object {
    lateinit var sharedContext: Context
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    sharedContext = this

    val preferencesManager = PreferencesManager(sharedContext)
    localDataSource = LocalDataSource(preferencesManager)
    userRepository = UserRepository(localDataSource, RemoteDataSource())

    mainViewModel = MainViewModel(userRepository)

    // Initialize the userAdapter and set it on the RecyclerView
    userAdapter = UserAdapter(emptyList()) { user ->
      // Handle item click here if needed
      print("${user.email}")
    }
    binding.mainUserList.layoutManager = LinearLayoutManager(this)
    binding.mainUserList.adapter = userAdapter

    mainViewModel.viewState.observe(this, { viewState ->
      when (viewState) {
        is MainViewState.UserDataLoaded -> {
          val user = viewState.user
          binding.mainEmail.text = user.email
          binding.mainName.text = user.name?.first
          // Update the user picture using the Picture object
          user.picture = Picture(
            large = "https://images.unsplash.com/photo-1575936123452-b67c3203c357?auto=format&fit=crop&q=80&w=2940&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            medium = "https://images.unsplash.com/photo-1575936123452-b67c3203c357?auto.format&fit=crop&q=80&w=2940&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            thumbnail = "https://images.unsplash.com/photo-1575936123452-b67c3203c357?auto.format&fit=crop&q=80&w=2940&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
          )
          // Load the user's picture
          if (user.picture != null) {
            val imageView = binding.mainImage
            Glide.with(this)
              .load(user?.picture?.large)
              .into(imageView)
            // Set the content description for the image
            // Access the content description
            val contentDescription = imageView.contentDescription

            // Now you can use the content description as needed
            if (contentDescription != null) {
              // Do something with the content description
              // For example, set it as a label for the image view
              imageView.contentDescription = "Profile picture of ${user.name?.first}"
              print(imageView.contentDescription);
            }
          }
        }
        is MainViewState.UserListLoaded -> {
          val users = viewState.users
          userAdapter?.updateUsers(users)
        }
        is MainViewState.Error -> {
          // Handle error state, e.g., show an error message
          // viewState.message contains the error message
        }
        MainViewState.Loading -> {
          // Handle loading state, e.g., show a loading indicator
        }
      }
    })

    binding.mainSeeDetailsButton.setOnClickListener {
      val userViewState = mainViewModel.viewState.value as? MainViewState.UserDataLoaded
      val user = userViewState?.getUserFromViewState()
      user?.navigateDetails(this)
    }

    binding.mainRefreshButton.setOnClickListener {
      // Refresh all UI components
      lifecycleScope.launch {
        mainViewModel.refreshUser()
      }
    }

    binding.mainUserListButton.setOnClickListener {
      // Show the user list obtained from a remote source

      lifecycleScope.launch {
        mainViewModel.showUserList()
      }
    }
    lifecycleScope.launch {
      mainViewModel.loadInitialUser()
    }
  }

  // Extension function to navigate to user details
  private fun User.navigateDetails(context: Context) {
    val intent = Intent(context, DetailsActivity::class.java)
    intent.putExtra("saved-user-key", this)
    context.startActivity(intent)
  }

  private fun MainViewState.UserDataLoaded.getUserFromViewState(): User? {
    return this.user
  }

  private fun navigateDetails(user: User) {
    val intent = Intent(this, DetailsActivity::class.java)
    intent.putExtra("saved-user-key", user)
    startActivity(intent)
  }

  private fun getUserFromViewState(viewState: MainViewState): User? {
    return if (viewState is MainViewState.UserDataLoaded) {
      viewState.user
    } else {
      null
    }
  }
}

sealed class MainViewState {
  data class UserDataLoaded(val user: User) : MainViewState()
  data class UserListLoaded(val users: List<User>) : MainViewState()
  data class Error(val message: String) : MainViewState()
  object Loading : MainViewState()
}

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
  val viewState = MutableLiveData<MainViewState>()

  suspend fun loadInitialUser() {
    try {
      val user = userRepository.getSavedUser()
      user?.let {
        viewState.value = MainViewState.UserDataLoaded(user)
      } ?: run {
        viewState.value = MainViewState.Error("Failed to load user data")
      }
    } catch (e: Exception) {
      viewState.value = MainViewState.Error("Failed to load user data: ${e.message}")
    }
  }

  suspend fun refreshUser() {
    try {
      viewState.value = MainViewState.Loading
      val user = userRepository.getSavedUser()
      user?.let {
        viewState.value = MainViewState.UserDataLoaded(user)
      } ?: run {
        viewState.value = MainViewState.Error("Failed to load user data")
      }
    } catch (e: Exception) {
      viewState.value = MainViewState.Error("Failed to load user data: ${e.message}")
    }
  }

  suspend fun showUserList() {
    try {
      viewState.value = MainViewState.Loading
      val usersResult = userRepository.getUsers(true)
      if (usersResult is ApiResult.Success<List<User>>) {
        val users = usersResult.data
        viewState.value = MainViewState.UserListLoaded(users)
      } else if (usersResult is ApiResult.Failure) {
        viewState.value = MainViewState.Error("Failed to load user list: ${usersResult.error.message}")
      }
    } catch (e: Exception) {
      viewState.value = MainViewState.Error("Failed to load user list: ${e.message}")
    }
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

