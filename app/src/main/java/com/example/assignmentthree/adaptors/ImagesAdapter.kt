package com.example.assignmentthree.adaptors


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.assignmentthree.R

import com.squareup.picasso.Picasso


class ImagesAdapter(val context: Context, private val list: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemListener: OnItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_image, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = list[position]
        if (holder is MyViewHolder) {
            val img = holder.itemView.findViewById<AppCompatImageView>(R.id.item_img_view)
            Picasso.get()
                .load(item)
                .resize(122, 82)
                .placeholder(R.drawable.user_images)
                .into(img)



            holder.itemView.setOnClickListener {
                onItemListener?.onClick(position, item)
            }
        }


    }

    fun setOnClickListener(onItemListener: OnItemListener) {
        this.onItemListener = onItemListener
    }

    interface OnItemListener {
        fun onClick(position: Int, item: String)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}