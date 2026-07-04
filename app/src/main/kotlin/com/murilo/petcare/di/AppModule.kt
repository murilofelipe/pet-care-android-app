package com.murilo.petcare.di

import android.content.Context
import androidx.room.Room
import com.murilo.petcare.BuildConfig
import com.murilo.petcare.data.auth.AuthInterceptor
import com.murilo.petcare.data.local.ExpenseDao
import com.murilo.petcare.data.local.HealthRecordDao
import com.murilo.petcare.data.local.PetCareDatabase
import com.murilo.petcare.data.local.PetDao
import com.murilo.petcare.data.local.SupplyDao
import com.murilo.petcare.data.remote.PetCareApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                }
            }
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun providePetCareApi(retrofit: Retrofit): PetCareApi = retrofit.create(PetCareApi::class.java)

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PetCareDatabase =
        Room.databaseBuilder(context, PetCareDatabase::class.java, "petcare.db")
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()

    @Provides
    fun providePetDao(db: PetCareDatabase): PetDao = db.petDao()

    @Provides
    fun provideHealthRecordDao(db: PetCareDatabase): HealthRecordDao = db.healthRecordDao()

    @Provides
    fun provideSupplyDao(db: PetCareDatabase): SupplyDao = db.supplyDao()

    @Provides
    fun provideExpenseDao(db: PetCareDatabase): ExpenseDao = db.expenseDao()
}