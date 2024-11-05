package ars.example.musicplayerapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager


import ars.example.musicplayerapp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private var binding:ActivityMainBinding? = null
    private lateinit var dialog :dialogs
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.searchNBar?.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
              return if(!query.isNullOrEmpty()){
                  dialog = dialogs(this@MainActivity)
                  dialog.showDialog()
                   retrofitCall(query)
                   true }
                else{ false }
            }
            override fun onQueryTextChange(newText: String?): Boolean { return true } })

         setSupportActionBar(binding?.toolbar)
        supportActionBar?.title ="Search"

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.memu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.backbutton){
            val intent = Intent(this@MainActivity,PlayerActivity::class.java)
            intent.putExtra(Constants.CHANGE,true)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun retrofitCall(query:String){
        val service  = retrofitService().getService(retrofitInterface::class.java)

             val x = service.getSongTracks(query)
             x.enqueue(object:Callback<result>{
                 override fun onResponse(call: Call<result>, response: Response<result>) {
                     if(response.isSuccessful){
                         val result = response.body()!!.data
                         binding?.songs?.adapter = songListAdaptor(this@MainActivity,result)
                         binding?.songs?.layoutManager =LinearLayoutManager(this@MainActivity
                             ,LinearLayoutManager.VERTICAL,false)
                         binding?.placeholerImage?.visibility = View.GONE
                         binding?.sometext?.visibility = View.GONE
                         binding?.songs?.visibility = View.VISIBLE

                     }
                     else{ Toast.makeText(this@MainActivity,"Not found ",Toast.LENGTH_LONG).show() }
                     dialog.stopDialog()
                 }

                 override fun onFailure(call: Call<result>, t: Throwable) {
                     Toast.makeText(this@MainActivity,"Something went wrong",Toast.LENGTH_LONG).show()
                     dialog.stopDialog()}

             })


           }

    }
