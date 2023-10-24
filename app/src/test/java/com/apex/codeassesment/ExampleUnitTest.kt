package com.apex.codeassesment
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.apex.codeassesment.data.UserRepository
import com.apex.codeassesment.data.model.User
import com.apex.codeassesment.ui.main.MainViewModel
import com.apex.codeassesment.ui.main.MainViewState
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

class MainViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModel = MainViewModel(userRepository)
    }

    @Test
    fun `loadInitialUser sets UserDataLoaded ViewState`() {
        // Given
        val user = User() // Create a sample user
        Mockito.`when`(userRepository.getSavedUser()).thenReturn(user)

        val observer: Observer<MainViewState> = Mockito.mock(Observer::class.java) as Observer<MainViewState>
        viewModel.viewState.observeForever(observer)

        // When
        viewModel.loadInitialUser()

        // Then
        Mockito.verify(observer).onChanged(MainViewState.UserDataLoaded(user))
    }

    // Add more tests for other viewModel functions like refreshUser and showUserList
}
