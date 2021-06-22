package ge.jvash.flowers.ui.bucket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ge.jvash.flowers.databinding.FragmentBucketBinding

class BucketFragment : Fragment() {

    private lateinit var bucketViewModel: BucketViewModel
    private var _binding: FragmentBucketBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bucketViewModel =
            ViewModelProvider(this).get(BucketViewModel::class.java)

        _binding = FragmentBucketBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textView: TextView = binding.textGallery
        bucketViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}