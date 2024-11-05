package ars.example.musicplayerapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ars.example.musicplayerapp.databinding.SongItemLayoutBinding
import com.bumptech.glide.Glide

class albumAdaptor(private var context: Context, private val list :ArrayList<String>
                  ,private var artistName:String
                  ,private val photo:String
                  ,private val function :((Int)->Unit),
    private val currentPosition:Int):RecyclerView.Adapter<albumAdaptor.albumViewHolder>() {

    inner class albumViewHolder(val binding:SongItemLayoutBinding):RecyclerView.ViewHolder(binding.root){}
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): albumAdaptor.albumViewHolder {
        return albumViewHolder(SongItemLayoutBinding.inflate(LayoutInflater.from(parent.context)
            ,parent,false))
    }

    override fun onBindViewHolder(holder: albumAdaptor.albumViewHolder, position: Int) {
         val model = list[position]
         val x = holder.binding

        x.songTitle.text =model
        x.songArtist.text = artistName
        Glide.with(context)
            .load(photo)
            .placeholder(R.drawable.baseline_play_circle_24)
            .fitCenter()
            .into(x.songImage)
        holder.itemView.setOnClickListener {
            function.invoke(position)
        }

    }



    override fun getItemCount(): Int {
        return list.size
    }

}