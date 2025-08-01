package com.nexusnova.aartiapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nexusnova.aartiapp.R
import com.nexusnova.aartiapp.data.local.AartiEntity
import com.squareup.picasso.Picasso

class AartiAdapter(
    private val items: List<AartiEntity>,
    private val onClick: (AartiEntity) -> Unit
) : RecyclerView.Adapter<AartiAdapter.VH>() {

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val img: ImageView    = v.findViewById(R.id.ivAartiImage)
        private val title: TextView   = v.findViewById(R.id.tvAartiTitle)
        private val prime: TextView   = v.findViewById(R.id.tvAartiPrime)
        private val desc: TextView    = v.findViewById(R.id.tvAartiDescription)
        private val mp3: TextView     = v.findViewById(R.id.tvAartiMp3Url)

        init {
            v.setOnClickListener {
                adapterPosition.takeIf { it != RecyclerView.NO_POSITION }?.let { pos ->
                    onClick(items[pos])
                }
            }
        }

        fun bind(a: AartiEntity) {
            title.text = a.title
            prime.text = "Prime: ${a.prime}"
            desc.text  = a.description
            mp3.text   = a.mp3URL
            Picasso.get()
                .load(a.imageURL)
                .placeholder(R.drawable.ic_launcher_background)
                .into(img)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_aarti, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
