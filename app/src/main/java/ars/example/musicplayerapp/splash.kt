package ars.example.musicplayerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import ars.example.musicplayerapp.databinding.ActivitySplashBinding

class splash : AppCompatActivity() {
    private var binding:ActivitySplashBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

       if(intent.hasExtra(Constants.WHICH_ACTIVITY)){
           Handler(Looper.getMainLooper()).postDelayed(
               { val intent  = Intent(this,PlayerActivity::class.java)
                   intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                   startActivity(intent)
                   finish()
               }
               ,1200L)

       }else{
           Handler(Looper.getMainLooper()).postDelayed(
             {
                 val intent  = Intent(this,MainActivity::class.java)
                 intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                 startActivity(intent)
                 finish()
             }
             ,1200L)
       }


    }
}