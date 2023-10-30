import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.apex.codeassesment.data.UserRepository
import com.apex.codeassesment.data.local.LocalDataSource
import com.apex.codeassesment.data.model.*
import com.apex.codeassesment.data.remote.RemoteDataSource
import com.apex.codeassesment.ui.main.MainViewModel
import com.apex.codeassesment.ui.main.MainViewState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations


class MainViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var remoteDataSource: RemoteDataSource

    @Mock
    private lateinit var localDataSource: LocalDataSource

    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var observer: Observer<MainViewState>

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        userRepository = UserRepository(localDataSource, remoteDataSource)
        mainViewModel = MainViewModel(userRepository)
        mainViewModel.viewState.observeForever(observer)
    }

    @Test
    fun testLoadInitialUser() {
        // Mock the user data
        val user = User() // Set up the user as needed
        `when`(userRepository?.getSavedUser()).thenReturn(user)
        // Call the method to be tested
        mainViewModel.loadInitialUser()
        mainViewModel.refreshUser()

        // Verify the LiveData value
        Mockito.verify(observer).onChanged(MainViewState.UserDataLoaded(user))
    }

    @Test
    fun testRefreshUser() {
        // Mock the user data
        val user = User() // Set up the user as needed
        `when`(userRepository?.getSavedUser()).thenReturn(user)
        // Call the method to be tested
        mainViewModel.loadInitialUser()
        mainViewModel.refreshUser()

        // Verify the LiveData value
        Mockito.verify(observer).onChanged(MainViewState.UserDataLoaded(user))
    }

    @Test
    fun testShowUserList() {
        // Mock the user list
        val users = listOf(User.createRandom()) // Set up the user list as needed
        `when`(remoteDataSource.loadUsers()).thenReturn(users)

        // Call the method to be tested
        mainViewModel.showUserList()

        // Verify the LiveData value
        Mockito.verify(observer).onChanged(MainViewState.UserListLoaded(users))
    }
}
