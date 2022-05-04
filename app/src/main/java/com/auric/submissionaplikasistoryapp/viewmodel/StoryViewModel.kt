package com.auric.submissionaplikasistoryapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.auric.submissionaplikasistoryapp.model.FileUploadResponse
import com.auric.submissionaplikasistoryapp.model.ListStoryItem
import com.auric.submissionaplikasistoryapp.model.repository.StoryRepository
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(private val storyRepository: StoryRepository) :
    ViewModel() {

    val allStory: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStoriesPaging().cachedIn(viewModelScope)

    val dataStoryWithLocation: LiveData<List<ListStoryItem>> = storyRepository.listStory

    val isLoading: LiveData<Boolean> = storyRepository.loading

    val responseUpload : LiveData<FileUploadResponse> = storyRepository.uploadResponse

    fun getStoryWithLocation(token: String) {
        storyRepository.getListStoryItem(token)
    }

    fun uploadStory(
        token: String,
        description: String,
        imgFile: File,
        location: LatLng
    ) {
        storyRepository.uploadImage(token, description, imgFile,location)
    }

}