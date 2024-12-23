package id.overlogic.storify.di
import android.app.Application
import android.content.Context
import id.overlogic.storify.data.repository.AuthRepository
import id.overlogic.storify.data.repository.StoryRepository
import id.overlogic.storify.data.source.local.database.StoryDatabase
import id.overlogic.storify.data.source.local.preferences.AuthPreference
import id.overlogic.storify.data.source.local.preferences.dataStore
import id.overlogic.storify.data.source.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {

    fun injectApplication() = Application()

    fun injectAuth (context: Context): AuthRepository {
        val authPreference = AuthPreference.getInstance(context.dataStore)
        val user = runBlocking {
            authPreference.getSession().first()
        }
        ApiConfig.setToken(user.token)
        val apiService = ApiConfig.getApiService()
        return AuthRepository.getInstance(apiService, authPreference)
    }
    fun injectStory (context: Context): StoryRepository {
        val authPreference = AuthPreference.getInstance(context.dataStore)
        val user = runBlocking {
            authPreference.getSession().first()
        }
        val database = StoryDatabase.getDatabase(context)
        ApiConfig.setToken(user.token)
        val apiService = ApiConfig.getApiService()
        return StoryRepository.getInstance(apiService, database)
    }
}