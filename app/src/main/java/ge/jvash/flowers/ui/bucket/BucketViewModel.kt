package ge.jvash.flowers.ui.bucket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BucketViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is bucket Fragment"
    }
    val text: LiveData<String> = _text
}