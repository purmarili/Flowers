package ge.jvash.flowers.ui.my_products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyProductsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is my products Fragment"
    }
    val text: LiveData<String> = _text
}