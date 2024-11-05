package ars.example.musicplayerapp

import android.app.Notification

import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.app.PendingIntent
import android.app.Service

import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.drawable.Drawable

import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.app.ServiceCompat
import androidx.core.app.ServiceCompat.startForeground
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import androidx.media3.ui.PlayerNotificationManager.NotificationListener
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

class SongService: Service() {
    private val tag = "TAG"
    private var artist :String? = ""
    private var titleList = arrayListOf<String>()
    private var poster:String = ""
    inner class Mybinder: Binder(){
        fun getSongservice() = this@SongService
    }
    private val binder  = Mybinder()
    var player : ExoPlayer? = null
    override fun onRebind(intent: Intent?) {
        Log.d(tag,"On Rebind Called")
        super.onRebind(intent)
    }
    private fun createNotification():Notification {
          return  NotificationCompat.Builder(this).apply {
                setContentTitle("Songs")
                setOngoing(true)
                setSmallIcon(R.drawable.baseline_play_circle_24)
                setContentText("Music is playing")
            }.build()

    }

    fun setUpSongs(list:ArrayList<String>,titles:ArrayList<String>,cover:String,singer:String){
        if (player != null && (player!!.hasNextMediaItem() || player!!.hasPreviousMediaItem())) {
            player?.clearMediaItems()
            titleList.clear()
            poster = ""
            artist =""
        }
        titleList = titles
        poster = cover
        artist = singer
        for(i in list){
            val media = MediaItem.fromUri(i)
            player?.addMediaItem(media)
        }
        player?.prepare()

        player?.play()
        player?.playWhenReady = true

    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(tag,"On StartCommand Called ")
        if(player==null){
            player  = ExoPlayer.Builder(this).build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                newNotification()}
            else {
                startForeground(10,createNotification())
            }



        }

        return START_STICKY
    }
    override fun onBind(intent: Intent?): IBinder? {
        Log.d(tag,"On Bind Called ")
        if(player==null){
            player  = ExoPlayer.Builder(this).build()
        }


        return binder
    }


    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(tag,"On Unbind Called ")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(tag,"On Destroy Called ")
        super.onDestroy()
    }

    @OptIn(UnstableApi::class)
    override fun onTaskRemoved(rootIntent: Intent?) {
        if(player!= null){
            player?.stop()
            player?.release()
            player = null
         if(notificationManager!= null){
             notificationManager?.setPlayer(null)
             notificationManager = null
         }
        }

        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

@OptIn(UnstableApi::class)
private fun newNotification(){
    val mediaadaptor  =  object :PlayerNotificationManager.MediaDescriptionAdapter{
        override fun getCurrentContentTitle(player: Player): CharSequence {
          return titleList[player.currentMediaItemIndex]
        }


        override fun createCurrentContentIntent(player: Player): PendingIntent? {

            val intent  = if( player.isPlaying){
               val intent = Intent(applicationContext,splash::class.java)
               intent.putExtra(Constants.WHICH_ACTIVITY,"Player")
                intent
            }else{
                val intent = Intent(applicationContext,splash::class.java)
                intent
            }
            return  PendingIntent.getActivity(applicationContext,0,intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return artist
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {

            Glide.with(this@SongService).asBitmap()
                .load(poster)
                .into(object:CustomTarget<Bitmap>(){
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        callback.onBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?)=Unit
                })
            return null
        }
    }
    val notificationListener = object :NotificationListener{


        override fun onNotificationPosted(
            notificationId: Int,
            notification: Notification,
            ongoing: Boolean
        ) {

              startForeground(this@SongService,notificationId,notification,
               if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                   ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK}else{0})
            super.onNotificationPosted(notificationId, notification, ongoing)
        }
    }


        notificationManager  = PlayerNotificationManager.Builder(this,10,
        Constants.CHANNEL_ID).apply {
        setChannelImportance(IMPORTANCE_DEFAULT)
        setSmallIconResourceId(R.drawable.vinyl)
        setChannelDescriptionResourceId(R.string.CHANNEL_DESCRIPTION)
        setNextActionIconResourceId(R.drawable.baseline_skip_next_24)
        setPreviousActionIconResourceId(R.drawable.baseline_skip_previous_24)
        setPlayActionIconResourceId(R.drawable.baseline_play_circle_24)
        setPauseActionIconResourceId(R.drawable.baseline_pause_circle_24)
        setChannelNameResourceId(R.string.CHANNEL_NAME)
        setMediaDescriptionAdapter(mediaadaptor)
        setNotificationListener(notificationListener)

    }.build()

    if(player==null){
        Log.d("NOT", "Starting foreground with notification ID 10")
    }
    notificationManager?.apply{setPlayer(player)
   setPriority(PRIORITY_DEFAULT)
   setUseRewindAction(false)
   setUseFastForwardAction(false)
    }



}
private var notificationManager :PlayerNotificationManager? = null

}


