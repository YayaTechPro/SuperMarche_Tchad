package com.supermarche.tchad

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.supermarche.tchad.databinding.ItemProductBinding

// Shows a list of products as cards.
// showActions = true  -> edit and delete buttons are visible (Products screen)
// showActions = false -> read-only list (Dashboard and Alerts screens)
class ProductAdapter(
    private var products: List<Product>,
    private val showActions: Boolean,
    private val onEdit: (Product) -> Unit = {},
    private val onDelete: (Product) -> Unit = {}
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    // Holds the views of one card
    class ViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        val context = holder.itemView.context
        val b = holder.binding

        b.tvName.text = product.name
        b.tvCategory.text = Utils.categoryLabel(context, product.category)
        b.tvPrice.text = Utils.formatPrice(context, product.price)
        b.tvQuantity.text = context.getString(R.string.quantity_label, product.quantity)
        b.tvInitial.text = product.name.take(1).uppercase()

        // Pick the text and colors that match the stock status
        val (statusText, strongColor, lightColor) = when (product.status()) {
            StockStatus.IN_STOCK -> Triple(R.string.status_in_stock, R.color.green, R.color.green_light)
            StockStatus.LOW_STOCK -> Triple(R.string.status_low, R.color.orange, R.color.orange_light)
            StockStatus.OUT_OF_STOCK -> Triple(R.string.status_out, R.color.red, R.color.red_light)
        }
        val strong = ContextCompat.getColor(context, strongColor)
        val light = ContextCompat.getColor(context, lightColor)

        b.tvStatus.setText(statusText)
        b.tvStatus.setTextColor(strong)
        b.tvStatus.backgroundTintList = ColorStateList.valueOf(light)
        b.tvInitial.setTextColor(strong)
        b.tvInitial.backgroundTintList = ColorStateList.valueOf(light)

        // Edit and delete buttons
        b.layoutActions.visibility = if (showActions) View.VISIBLE else View.GONE
        b.btnEdit.setOnClickListener { onEdit(product) }
        b.btnDelete.setOnClickListener { onDelete(product) }
    }

    // Replaces the list and refreshes the screen
    fun updateList(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }
}
