package com.nexusnova.aartiapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nexusnova.aartiapp.R
import com.nexusnova.aartiapp.data.local.CategoryEntity
import com.squareup.picasso.Picasso

class CategoryAdapter(
    private val items: List<CategoryEntity>,
    private val onClick: (CategoryEntity) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.ivCategory)
        val title: TextView = view.findViewById(R.id.tvCategory)

        init {
            view.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onClick(items[pos])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val cat = items[position]
        holder.title.text = cat.title
        Picasso.get().load(cat.imageUrl).into(holder.img)
    }

    override fun getItemCount() = items.size
}
