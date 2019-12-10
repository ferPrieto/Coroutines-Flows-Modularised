package prieto.fernando.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import prieto.fernando.core.presentation.BaseViewModel
import prieto.fernando.presentation.model.CompanyInfoUiModel
import prieto.fernando.presentation.model.LaunchUiModel
import timber.log.Timber
import javax.inject.Inject

interface MainViewModelInputs {
    fun launches()
    fun companyInfo()
}

class MainViewModel @Inject constructor(
    private val getLaunches: GetLaunches,
    private val getCompanyInfo: GetCompanyInfo
) : BaseViewModel(), MainViewModelInputs {
    private val launchUiModelRetrieved: MutableLiveData<List<LaunchUiModel>> = MutableLiveData()
    private val companyInfoUiModelRetrieved: MutableLiveData<CompanyInfoUiModel> = MutableLiveData()
    private val loading: MutableLiveData<Boolean> = MutableLiveData()

    fun loading(): LiveData<Boolean> = loading
    fun onLaunchesUiModelRetrieved(): LiveData<List<LaunchUiModel>> = launchUiModelRetrieved
    fun onCompanyInfoUiModelRetrieved(): LiveData<CompanyInfoUiModel> = companyInfoUiModelRetrieved

    override fun launches() {
        getLaunches.execute()
            .compose(schedulerProvider.doOnIoObserveOnMainSingle())
            .doOnSubscribe { loading.postValue(true) }
            .doFinally { loading.postValue(false) }
            .subscribe({ launchesUiModel ->
                launchUiModelRetrieved.postValue(launchesUiModel)
            }, { throwable ->
                Timber.d(throwable)
            }).also { subscriptions.add(it) }
    }

    override fun companyInfo() {
        getCompanyInfo.execute()
            .compose(schedulerProvider.doOnIoObserveOnMainSingle())
            .doOnSubscribe { loading.postValue(true) }
            .doFinally { loading.postValue(false) }
            .subscribe({ companyInfo ->
                companyInfoUiModelRetrieved.postValue(companyInfo)
            }, { throwable ->
                Timber.d(throwable)
            }).also { subscriptions.add(it) }
    }
}