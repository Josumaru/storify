package id.overlogic.storify.ui.common.factory

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import id.overlogic.storify.MainViewModel
import id.overlogic.storify.data.repository.AuthRepository
import id.overlogic.storify.data.repository.StoryRepository
import id.overlogic.storify.di.Injection
import id.overlogic.storify.ui.auth.login.LoginViewModel
import id.overlogic.storify.ui.auth.register.RegisterViewModel
import id.overlogic.storify.ui.create.CreateViewModel
import id.overlogic.storify.ui.detail.DetailViewModel
import id.overlogic.storify.ui.home.HomeViewModel
import id.overlogic.storify.ui.map.MapViewModel


class ViewModelFactory(private val application: Application, private val authRepository: AuthRepository, private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(application, authRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(application, authRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(application, storyRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(CreateViewModel::class.java) -> {
                CreateViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel(application, storyRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.injectApplication(), Injection.injectAuth(context), Injection.injectStory(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}