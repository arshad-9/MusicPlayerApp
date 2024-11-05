package ars.example.musicplayerapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ars.example.musicplayerapp.databinding.SongItemLayoutBinding

import com.bumptech.glide.Glide

class songListAdaptor(private val context: Context,private val list :List<Data>):RecyclerView.Adapter<songListAdaptor.songListViewHolder>() {

    inner class songListViewHolder(val binding: SongItemLayoutBinding):RecyclerView.ViewHolder(binding.root){}
    private var onclick = { data :Data ->
        val intent  = Intent(context,PlayerActivity::class.java)
        intent.putExtra(Constants.PREV,data.preview)
        intent.putExtra(Constants.ALBUM_SONGS,data.album.tracklist)
        intent.putExtra(Constants.FIRST_SONG_TITLE,data.title_short)
        intent.putExtra(Constants.ARTIST,data.artist.name)
        intent.putExtra(Constants.POSTER,data.album.cover_big)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

        context.startActivity(intent)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): songListAdaptor.songListViewHolder {
       return songListViewHolder(SongItemLayoutBinding.inflate(LayoutInflater.from(parent.context)
           ,parent,false))
    }

    override fun onBindViewHolder(holder: songListAdaptor.songListViewHolder, position: Int) {
        val model  = list[position]
        val x = holder.binding
        x.songTitle.text = model.title_short
        x.songArtist.text = model.artist.name
        Glide.with(context).load(model.album.cover)
            .placeholder(R.drawable.baseline_play_circle_24)
            .into(x.songImage)
        holder.itemView.setOnClickListener {
            onclick.invoke(model)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}