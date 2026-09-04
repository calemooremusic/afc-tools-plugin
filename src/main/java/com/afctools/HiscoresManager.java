package com.afctools;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Slf4j
@Singleton
public class HiscoresManager
{
    // Updated with your new deployment URL
    private static final String API_URL = "https://script.google.com/macros/s/AKfycbxb83KvV90OQyPPJQMOZXpo2Cn2nukkfqc_L7I7Xwemp44QilM-2LJAW5TsFN2kTrBqPQ/exec";

    @Inject
    private OkHttpClient okHttpClient;

    @Inject
    private Gson gson;

    public void submitLapCount(String username, int laps)
    {
        HttpUrl url = HttpUrl.parse(API_URL).newBuilder()
                .addQueryParameter("action", "submit")
                .addQueryParameter("username", username.replace("\u00A0", " "))
                .addQueryParameter("laps", String.valueOf(laps))
                .build();

        Request request = new Request.Builder().url(url).build();

        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                log.debug("Failed to submit lap count", e);
            }

            @Override
            public void onResponse(Call call, Response response)
            {
                response.close();
            }
        });
    }

    public void fetchHiscores(Consumer<List<HiscoreEntry>> callback)
    {
        HttpUrl url = HttpUrl.parse(API_URL).newBuilder()
                .addQueryParameter("action", "get")
                .build();

        Request request = new Request.Builder().url(url).build();

        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                log.debug("Failed to fetch hiscores", e);
                callback.accept(null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                try
                {
                    if (!response.isSuccessful() || response.body() == null)
                    {
                        callback.accept(null);
                        return;
                    }
                    String responseData = response.body().string();
                    List<HiscoreEntry> hiscores = gson.fromJson(responseData, new TypeToken<List<HiscoreEntry>>(){}.getType());
                    callback.accept(hiscores);
                }
                catch (Exception e)
                {
                    log.error("Error parsing hiscores JSON", e);
                    callback.accept(null);
                }
                finally
                {
                    response.close();
                }
            }
        });
    }

    @Data
    public static class HiscoreEntry
    {
        private final String username;
        private final int laps;
    }
}