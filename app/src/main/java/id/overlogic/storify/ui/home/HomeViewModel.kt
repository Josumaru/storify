package id.overlogic.storify.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.paging.cachedIn
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import id.overlogic.storify.data.repository.StoryRepository
import id.overlogic.storify.data.source.local.entity.StoryEntity

class HomeViewModel(application: Application, private val storyRepository: StoryRepository) :
    AndroidViewModel(application) {

    val result: LiveData<PagingData<StoryEntity>> =
        storyRepository.getPageStory().cachedIn(viewModelScope)
}
