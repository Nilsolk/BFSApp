import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bfsapp.dao.entities.GraphEntity
import com.example.bfsapp.databinding.GraphItemBinding

class GraphListAdapter(
    private var items: List<GraphEntity>,
    private val onItemClick: (GraphEntity) -> Unit,
    private val onDeleteClick: (GraphEntity) -> Unit
) : RecyclerView.Adapter<GraphListAdapter.VH>() {

    inner class VH(val b: GraphItemBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(entity: GraphEntity) {
            b.graphName.text = entity.name
            b.root.setOnClickListener { onItemClick(entity) }
            b.deleteButton.setOnClickListener { onDeleteClick(entity) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = GraphItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun update(newItems: List<GraphEntity>) {
        items = newItems
        notifyDataSetChanged()
    }
}
