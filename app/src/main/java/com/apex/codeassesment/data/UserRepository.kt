package com.apex.codeassesment.data
import com.apex.codeassesment.data.ApiResult.Success
import com.apex.codeassesment.data.local.LocalDataSource
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.data.remote.RemoteDataSource
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

// TODO (2 points) : Add tests
// TODO (3 points) : Hide this class through an interface, inject the interface in the clients instead and remove warnings

// Define an interface for UserRepository
class UserRepository(
  private val localDataSource: LocalDataSource,
  private val remoteDataSource: RemoteDataSource
) {
  private val savedUser = AtomicReference<User>()

  suspend fun getSavedUser(): User {
    return localDataSource.loadUser()
  }

  suspend fun getUsers(forceUpdate: Boolean): ApiResult<List<User>> {
    if (forceUpdate) {
      val users = remoteDataSource.loadRemoteUsers(10)
      if (users is ApiResult.Success<*>) {
        val data = users.data as List<User>
        if (data.isNotEmpty()) {
          localDataSource.saveUser(data[0]) // Save the first user
          return ApiResult.Success(data)
        }
      }
      // If it's not a success or if the data is empty, handle the failure case and return ApiResult.Failure
      return ApiResult.Failure(ApiError("Failed to fetch users", 500))
    }

    // Handle the case when forceUpdate is false
    val locallyStoredUser = localDataSource.loadUser()
    if (locallyStoredUser != null) {
      return ApiResult.Success(listOf(locallyStoredUser))
    }

    // Handle the case when there's no locally stored user
    return ApiResult.Failure(ApiError("No locally stored user", 404))
  }



}


data class ApiError(val message: String, val code: Int)

// Sealed class for API results
sealed class ApiResult<out T> {
  data class Success<out T>(val data: T) : ApiResult<T>()
  data class Failure(val error: ApiError) : ApiResult<Nothing>()
}