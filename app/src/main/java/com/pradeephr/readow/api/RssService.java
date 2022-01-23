package com.pradeephr.readow.api;

import com.pradeephr.readow.model.RssFeed;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RssService {
    String BASE_URL="https://timesofindia.indiatimes.com/rssfeeds/";

    Retrofit retrofit=new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();

    @GET
    Call<RssFeed> getFeed(@Url String url);
}