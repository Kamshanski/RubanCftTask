package edu.kamshanski.rubancfttask.model

import android.content.Context
import com.google.gson.GsonBuilder
import edu.kamshanski.rubancfttask.model.web.CbrApi
import edu.kamshanski.rubancfttask.model.utils.LocalDateTimeJsonConverter
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/** Retrofit convenient access and managing object */
object RetrofitClient {
    /** Client object */
    @Volatile private var retrofit: Retrofit? = null
    /** Url of cbr api */
    val BASE_URL = "https://www.cbr-xml-daily.ru/"

    /** @return Retrofit client with Gson converter */
    private fun client(context: Context): Retrofit {
        if (retrofit == null) {
            synchronized(this) {
                if (retrofit == null) {
                    val gson = GsonBuilder()
                        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeJsonConverter())
                        .create()
                    retrofit = Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .client(getOkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build()
                }
            }
        }

        return retrofit!!
    }

    private fun getOkHttpClient() : OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15000, TimeUnit.MILLISECONDS)
            .writeTimeout(15000, TimeUnit.MILLISECONDS)
            .readTimeout(15000, TimeUnit.MILLISECONDS)
            .followRedirects(true)
            .callTimeout(15000, TimeUnit.MILLISECONDS)
            .build()

    }

    fun getCbrApi(application: Context): CbrApi = client(application).create(CbrApi::class.java)

}
