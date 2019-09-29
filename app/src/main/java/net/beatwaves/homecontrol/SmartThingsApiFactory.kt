package net.beatwaves.homecontrol

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.Duration

object SmartThingsApiFactory {

    // Creating Auth Interceptor to add auth headers to all requests
    private val authInterceptor = Interceptor { chain->

        val newRequest = chain.request()
            .newBuilder()
            .header("Authorization", "Bearer ${BuildConfig.SMART_THINGS_API_TOKEN}")
            .build()

        chain.proceed(newRequest)
    }

    // OkhttpClient for building http request url
    private val smartThingsClient = OkHttpClient().newBuilder()
        .callTimeout(Duration.ofSeconds(3))
        .addInterceptor(authInterceptor)
        .build()



    fun retrofit() : Retrofit = Retrofit.Builder()
        .client(smartThingsClient)
        .baseUrl("https://api.smartthings.com/devices/${BuildConfig.SMART_THINGS_DEVICE_ID}/")
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()


    val smartThingsApi : SmartThingsApi = retrofit().create(SmartThingsApi::class.java)

}