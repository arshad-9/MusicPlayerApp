package ars.example.musicplayerapp

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class retrofitService {

     val client = OkHttpClient.Builder().build()

     val retfitBuilder  = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun<T>getService(service:Class<T>):T{
        return retfitBuilder.create(service)
    }
}