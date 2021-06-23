package ge.jvash.flowers.ui.featured

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ge.jvash.flowers.R
import ge.jvash.flowers.databinding.FragmentFeaturedBinding

class FeaturedFragment : Fragment() {

    private lateinit var featuredViewModel: FeaturedViewModel
    private lateinit var myView: View
    private lateinit var adapter: FeaturedListAdapter
    private var _binding: FragmentFeaturedBinding? = null
    private lateinit var logOutButton: Button
    private lateinit var db: DatabaseReference
    private val flowers = listOf("Tita", "Endzela", "Zalim Istanbul", "Mayvali", "Ucnobi")

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseDatabase.getInstance().getReference("FlowerInfo")
        val list = arrayListOf<FlowerItem>()
        for (i in 1..15){
            list.add(FlowerItem(i.toString(),flowers[(0..4).random().toInt()], "null", "+995" + (500000000..599999999).random()
                .toString(), "Flower Number: $i", (1..150).random().toString() + "$"))
        }
        myView = inflater.inflate(R.layout.fragment_featured, container, false)
        val rvToday = myView.findViewById<RecyclerView>(R.id.flower_featured)
        adapter = FeaturedListAdapter(list)
        updateData()
        rvToday.adapter = adapter
        return myView
    }

    private fun updateData() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}