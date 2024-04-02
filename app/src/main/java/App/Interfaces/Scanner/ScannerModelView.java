package App.Interfaces.Scanner;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScannerModelView extends ViewModel {

    private final MutableLiveData<String> mText;

    public ScannerModelView() {
        mText = new MutableLiveData<>();
        mText.setValue("This is scanner fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}