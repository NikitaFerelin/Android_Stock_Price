package com.ferelin.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.StoragePath
import com.ferelin.core.domain.repository.AuthUserStateRepository
import com.ferelin.core.domain.usecase.*
import com.ferelin.core.permission.PermissionManager
import com.ferelin.core.storage.AppStorageManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsStateUi(
  val isUserAuthenticated: Boolean = false,
  val downloadLce: LceState = LceState.None,
  val showDataCleared: Boolean = false,
  val showPathError: Boolean = false,
  val requestPermissions: Boolean = false,
  val requestPath: Boolean = false
)

class SettingsViewModel @Inject constructor(
  private val notifyPriceUseCase: NotifyPriceUseCase,
  private val storagePathUseCase: StoragePathUseCase,
  private val downloadProjectUseCase: DownloadProjectUseCase,
  private val permissionManager: PermissionManager,
  private val storageManager: AppStorageManager,
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val searchRequestsUseCase: SearchRequestsUseCase,
  private val authUseCase: AuthUseCase,
  private val dispatchersProvider: DispatchersProvider,
  authUserStateRepository: AuthUserStateRepository
) : ViewModel() {
  private val viewModelState = MutableStateFlow(SettingsStateUi())
  val uiState = viewModelState.asStateFlow()

  private val permissionsGranted = MutableStateFlow(permissionManager.writeExternalStorage())
  private val requestDownloadCode = MutableSharedFlow<Unit>()

  init {
    requestDownloadCode
      .combine(
        flow = permissionsGranted,
        transform = { _, isGranted ->
          viewModelState.update { it.copy(requestPermissions = !isGranted) }
          isGranted
        }
      )
      .filter { it }
      .combine(
        flow = storagePathUseCase.storagePath,
        transform = { _, storagePath ->
          viewModelState.update { it.copy(requestPath = !storagePath.isValid) }
          storagePath
        }
      )
      .filter { it.isValid }
      .onEach(this::tryDownloadSourceCode)
      .launchIn(viewModelScope)

    downloadProjectUseCase.downloadLce
      .onEach(this::onDownloadLce)
      .launchIn(viewModelScope)

    authUserStateRepository.userAuthenticated
      .distinctUntilChanged()
      .onEach(this::onAuthenticate)
      .launchIn(viewModelScope)
  }


  fun onPermissionsGranted() {
    permissionsGranted.value = permissionManager.writeExternalStorage()
  }

  fun onStoragePathSelected(path: String, authority: String) {
    viewModelScope.launch(dispatchersProvider.IO) {
      storagePathUseCase.setStoragePath(path, authority)
    }
  }

  fun onLogOutClick() {
    viewModelScope.launch(dispatchersProvider.IO) {
      authUseCase.logOut()
    }
  }

  fun onClearDataClick() {
    viewModelScope.launch(dispatchersProvider.IO) {
      searchRequestsUseCase.eraseAll()
      favouriteCompanyUseCase.eraseCache()
      viewModelState.update { it.copy(showDataCleared = true) }
    }
  }

  fun onDownloadCodeClick() {
    viewModelScope.launch {
      requestDownloadCode.emit(Unit)
    }
  }

  private fun onDownloadLce(lceState: LceState) {
    viewModelState.update { it.copy(downloadLce = lceState) }
  }

  private fun onAuthenticate(isAuthenticated: Boolean) {
    viewModelState.update { it.copy(isUserAuthenticated = isAuthenticated) }
  }

  private suspend fun tryDownloadSourceCode(storagePath: StoragePath) {
    viewModelScope.launch(dispatchersProvider.IO) {
      val destinationFile = storageManager.buildDownloadFile(
        treePath = storagePath.path,
        pathAuthority = storagePath.authority,
        fileName = DOWNLOAD_FILE_NAME
      )
      downloadProjectUseCase.download(destinationFile)
    }
  }
}

internal val StoragePath.isValid: Boolean
  get() = this.path.isNotEmpty() && this.authority.isNotEmpty()

internal const val DOWNLOAD_FILE_NAME = "Stock-Price"