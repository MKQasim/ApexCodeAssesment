package com.apex.codeassesment.data.remote

import com.apex.codeassesment.data.model.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import timber.log.Timber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
class RemoteDataSource {
  interface ApiService {
    @GET("api")
    suspend fun getUsers(@Query("results") limit: Int): Response<List<User>>
  }

  // Create an OkHttp client with a logging interceptor
  private val okHttpClient = OkHttpClient.Builder().apply {
    addInterceptor(HttpLoggingInterceptor { message ->
      // Log the URL, request, and response
      Timber.d(message)
    }.setLevel(HttpLoggingInterceptor.Level.BODY)) // Change Level to BODY for more details
  }.build()


  private val retrofit = Retrofit.Builder()
    .baseUrl("https://randomuser.me/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(okHttpClient) // Set the custom OkHttpClient
    .build()

  private val service = retrofit.create(ApiService::class.java)

  suspend fun getUsersFromApi(limit: Int): List<User>? {
    return withContext(Dispatchers.IO) {
      try {
        val response = service.getUsers(limit)
        if (response.isSuccessful) {
          response.body()
        } else {
          Timber.e("Error response: ${response.code()} - ${response.message()}")
          null
        }
      } catch (e: Exception) {
        e.printStackTrace()
        null
      }
    }
  }

  suspend fun loadRemoteUsers(limit: Int): ApiResult<List<User>> {
    return try {
      val users = getUsersFromApi(limit)
      if (users != null) {
        ApiResult.Success(users)
      } else {
        ApiResult.Failure(ApiError("Failed to fetch users", 500))
      }
    } catch (e: Exception) {
      ApiResult.Failure(ApiError("Network error", 500))
    }
  }
}

// Define an error class to hold the error information
data class ApiError(val message: String, val code: Int)

// Define a sealed class for the result
sealed class ApiResult<out T> {
  data class Success<out T>(val data: T) : ApiResult<T>()
  data class Failure(val error: ApiError) : ApiResult<Nothing>()
}
