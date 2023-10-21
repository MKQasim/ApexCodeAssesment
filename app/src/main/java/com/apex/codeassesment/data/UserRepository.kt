package com.apex.codeassesment.data
import com.apex.codeassesment.data.local.LocalDataSource
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.data.remote.RemoteDataSource
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

// TODO (2 points) : Add tests
// TODO (3 points) : Hide this class through an interface, inject the interface in the clients instead and remove warnings

// Define an interface for UserRepository
class UserRepository @Inject constructor(
  private val localDataSource: LocalDataSource,
  private val remoteDataSource: RemoteDataSource
) {

  private val savedUser = AtomicReference(User())

  fun getSavedUser() = localDataSource.loadUser()!!

  fun getUser(forceUpdate: Boolean): User {
    if (forceUpdate) {
      val user = remoteDataSource.LoadUser()
      localDataSource.saveUser(user)
      savedUser.set(user)
    }
    return savedUser.get()
  }

  fun getUsers() = remoteDataSource.loadUsers()
}
