package ge.jvash.flowers.ui.featured

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ge.jvash.flowers.R

class FeaturedListAdapter(var list: ArrayList<FlowerItem>) : RecyclerView.Adapter<FlowerItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.flower_list_item, parent, false)
        return FlowerItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlowerItemViewHolder, position: Int) {
        val item = list[position]
        holder.number.text = item.number
        holder.description.text = item.description
        holder.price.text = item.price
        holder.title.text = item.title
        holder.quantity.setText("1")
    }

    override fun getItemCount(): Int {
        return list.size
    }

}

class FlowerItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var picture = itemView.findViewById<ImageView>(R.id.flower_picture)
    var number = itemView.findViewById<TextView>(R.id.flower_seller_number)
    var description = itemView.findViewById<TextView>(R.id.flower_description)
    var price = itemView.findViewById<TextView>(R.id.price)
    var title = itemView.findViewById<TextView>(R.id.flower_seller_name)
    var quantity = itemView.findViewById<EditText>(R.id.quantityEditText)
}