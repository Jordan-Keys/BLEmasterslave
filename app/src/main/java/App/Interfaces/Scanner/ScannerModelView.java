package App.Interfaces.Scanner;

import android.widget.ListView;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScannerModelView extends ViewModel {

    private final MutableLiveData<String> mText;

    public ScannerModelView() {
        mText = new MutableLiveData<>();
        mText.setValue("This is scanner fragment");
    }

    public void getText(ListView textView) {
        return;
    }
}