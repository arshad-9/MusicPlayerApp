package ars.example.musicplayerapp

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.recyclerview.widget.LinearLayoutManager
import ars.example.musicplayerapp.databinding.ActivityPlayerBinding
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlayerActivity : AppCompatActivity() {

    private var binding :ActivityPlayerBinding? = null
    private lateinit var  serviceInstance :SongService
    private lateinit var serviceIntent: Intent
    private var songsList:ArrayList<String> = arrayListOf()
    private var songsTitleList = arrayListOf<String>()

    private val notificationPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){}
    private val serviceConnection = object :ServiceConnection{

        override fun onServiceConnected(name: ComponentName?, serviceBinder: IBinder?) {
            serviceInstance = (serviceBinder as SongService.Mybinder).getSongservice()
            updatePreference(serviceInstance)
            Log.d("Updation","Yes its updating")


        }
        override fun onServiceDisconnected(name: ComponentName?) {}
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        serviceIntent = Intent(this,SongService::class.java)

         startService(serviceIntent)
        val x =bindService(serviceIntent,serviceConnection, Context.BIND_AUTO_CREATE)
        if(x){
            Log.d("Updation","pxpxpx its updating")
        }else{
            Log.d("Updation","nxnxnx its updating")
        }
         notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)

       onBackPressedDispatcher.addCallback(object :OnBackPressedCallback(true){
           override fun handleOnBackPressed() {
               val intent = Intent(this@PlayerActivity,MainActivity::class.java)
               intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
               startActivity(intent)
                finish()
           }
       })
    }



    private fun updatePreference(service: SongService){
        val pref  =  getSharedPreferences(Constants.SHEARED_PREFERENCES, MODE_PRIVATE)
        if(intent.hasExtra(Constants.ALBUM_SONGS) && intent.hasExtra(Constants.PREV)
            && intent.hasExtra(Constants.FIRST_SONG_TITLE) && intent.hasExtra(Constants.ARTIST)
            && intent.hasExtra(Constants.POSTER)){
            val albumSongs = intent.getStringExtra(Constants.ALBUM_SONGS)!!
            val firstSong = intent.getStringExtra(Constants.PREV)!!
            val firstTitle  = intent.getStringExtra(Constants.FIRST_SONG_TITLE)!!
            val artist = intent.getStringExtra(Constants.ARTIST)
            val poster = intent.getStringExtra(Constants.POSTER)


            val editor  = pref.edit()
            editor.putString(Constants.PREV,firstSong)
            editor.putString(Constants.FIRST_SONG_TITLE,firstTitle)
            editor.putString((Constants.ARTIST),artist)
            editor.putString(Constants.POSTER,poster)
            editor.apply()
            retrofitServiceCall(albumSongs,firstSong,firstTitle,service)
        }
        else{
            val albumSongstitle = pref.getString(Constants.ALBUM_SONGS
                ,"")!!.replace("[",
                "").replace("]",
                "").split(",")
                songsTitleList = ArrayList(albumSongstitle)

            val albumSongs = pref.getString(Constants.SONG_LIST
                ,"")!!.replace("[",
                "").replace("]",
                "").split(",")
            songsList = ArrayList(albumSongs)
            setUpUi(service)
        }



    }

    private fun retrofitServiceCall(songList:String ,firstSong:String,firstTitle:String
    ,service: SongService){
        val retrofitServiceResult = retrofitService().getService(retrofitInterface::class.java)
        Log.d("Tfg",songList)
            val result = retrofitServiceResult.getRelatedTracks(songList)

            result.enqueue(object :Callback<trackResult>{
                override fun onResponse(call: Call<trackResult>, response: Response<trackResult>) {
                    if(response.isSuccessful){
                        songsList.add(firstSong)
                        songsTitleList.add(firstTitle)
                        val list = response.body()!!.data
                        for(i in list){
                            songsList.add(i.preview)
                            songsTitleList.add(i.title_short)

                        }

                        Log.d("Tfg",songsTitleList.toString())

                            val pref  =  getSharedPreferences(Constants.SHEARED_PREFERENCES, MODE_PRIVATE)
                            val editor  = pref.edit()
                            editor.putString(Constants.ALBUM_SONGS,songsTitleList.toString())
                            editor.putString(Constants.SONG_LIST,songsList.toString())
                            editor.apply()
//                           service.setUpSongs(songsList)
                            setUpUi(service)

                    }
                    else{

                        Toast.makeText(this@PlayerActivity
                            ,"Get the Song First "
                            ,Toast.LENGTH_LONG).show()

                    }
                    }


                override fun onFailure(call: Call<trackResult>, t: Throwable) {
                    Toast.makeText(this@PlayerActivity
                        ,"Something went wrong ${t.message} "
                        ,Toast.LENGTH_LONG).show()

                    Log.d("TEST",t.message.toString())
                }

            })



    }
    @OptIn(UnstableApi::class)
    private fun setUpUi(service: SongService){
        val dialog = dialogs(this)
        val pref = getSharedPreferences(Constants.SHEARED_PREFERENCES, MODE_PRIVATE)
        val artist = pref.getString(Constants.ARTIST,"")!!
        val poster  = pref.getString(Constants.POSTER,"")!!
        binding?.artistNameText?.text =artist
        Glide.with(this).load(poster)
            .placeholder(R.drawable.baseline_play_circle_24)
            .fitCenter().into(binding?.poster!!)



        val player  = service.player!!


        binding?.playerView?.player = player
        binding?.playerView?.showController()
        binding?.playerView?.controllerHideOnTouch = false
        binding?.playerView?.controllerShowTimeoutMs = 0
        binding?.playerView?.setControllerVisibilityListener(
            PlayerView.ControllerVisibilityListener { binding?.playerView?.showController() })
          player.addListener(object: Player.Listener{

           override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
               if(player.currentMediaItemIndex < songsTitleList.size && player.currentMediaItemIndex>=0){
                      binding?.songTitleText?.text = songsTitleList[player.currentMediaItemIndex]
                   }
               super.onMediaItemTransition(mediaItem, reason)
           }

              override fun onPlaybackStateChanged(playbackState: Int) {
                  when(playbackState){
                      Player.STATE_READY->{
                          if(player.currentMediaItemIndex <  songsTitleList.size && player.currentMediaItemIndex>=0){
                                 binding?.songTitleText?.text = songsTitleList[player.currentMediaItemIndex]
                               }
                                 playPauseSetting(player)
                                 if(dialog.isActive){
                                     if(!(isFinishing || isDestroyed)){
                                         dialog.stopDialog()
                                     }

                                 }
                               }

                      Player.STATE_BUFFERING->{
                          if(!(isFinishing || isDestroyed)){
                              dialog.showDialog()
                          }
                          }
                  }
                  super.onPlaybackStateChanged(playbackState)
              }
       })
        val function = { index :Int->
            player.seekTo(index,0L)
        }

        binding?.albumTracks?.adapter = albumAdaptor(this,songsTitleList,artist,poster,function
            ,player.currentMediaItemIndex)

        binding?.albumTracks?.layoutManager = LinearLayoutManager(this,
            LinearLayoutManager.VERTICAL,false)

        if(player.currentMediaItemIndex < songsTitleList.size && player.currentMediaItemIndex>=0){
            binding?.songTitleText?.text = songsTitleList[player.currentMediaItemIndex]
        }


        if((!(player.hasNextMediaItem() || player.hasPreviousMediaItem())
          && intent.hasExtra(Constants.CHANGE)) || !intent.hasExtra(Constants.CHANGE)){
            Log.d("TAGG","Hello")
            service.setUpSongs(songsList,songsTitleList,poster,artist)
            Log.d("TAGG",songsList.toString())
        }

       playPauseSetting(player)

        binding?.playPause?.setOnClickListener {
            if(player.isPlaying){
               binding?.playPause?.setImageDrawable(AppCompatResources.getDrawable(this
                   ,R.drawable.baseline_play_circle_24))
                player.pause()

            }else{
                binding?.playPause?.setImageDrawable(AppCompatResources.getDrawable(this
                    ,R.drawable.baseline_pause_circle_24))
                player.play()
            }
        }
    }


    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }

    private fun playPauseSetting(player:ExoPlayer){
        if(player.isPlaying){
            binding?.playPause?.setImageDrawable(AppCompatResources.getDrawable(this@PlayerActivity
                ,R.drawable.baseline_pause_circle_24))

        }else{
            binding?.playPause?.setImageDrawable(AppCompatResources.getDrawable(this@PlayerActivity
                ,R.drawable.baseline_play_circle_24))

        }
    }


}