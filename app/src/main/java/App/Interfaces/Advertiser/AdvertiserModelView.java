package App.Interfaces.Advertiser;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdvertiserModelView extends ViewModel {

    private final MutableLiveData<String> mText;

    public AdvertiserModelView() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Advertiser fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}