package ars.example.musicplayerapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import retrofit2.http.Url

interface retrofitInterface {

    @GET("search")
    @Headers(
             "x-rapidapi-key: ${Constants.API_KEY}",
             "x-rapidapi-host: deezerdevs-deezer.p.rapidapi.com")
    fun getSongTracks(@Query("q")query:String): Call<result>

    @GET
    fun getRelatedTracks(@Url url:String):Call<trackResult>
}