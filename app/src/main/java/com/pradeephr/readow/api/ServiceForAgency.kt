package com.pradeephr.readow.api



import com.pradeephr.readow.model.RssAgency
import com.pradeephr.readow.model.RssNamesandLinks
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL="https://rss-flash-default-rtdb.firebaseio.com/rssurl/"
private const val XMLTOJSON_URL="https://www.toptal.com/developers/"

public var NEWS_URL=""

interface RetrofitHelper {


    @GET("agency.json")
    fun getAgencyList(): Call<RssAgency>


    @GET("{res}.json")
    fun getSelectedAgencyNamesandLinks(@Path("res") res:String):Call<RssNamesandLinks>








}

object ServiceForAgency{
    val retrofitInstance:RetrofitHelper

    init {
        val retrofit=Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofitInstance=retrofit.create(RetrofitHelper::class.java)
    }


}



