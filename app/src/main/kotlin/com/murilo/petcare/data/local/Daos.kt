package com.murilo.petcare.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {

    @Query("SELECT * FROM pets WHERE shared = 0 ORDER BY name")
    fun observeMyPets(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE shared = 1 ORDER BY name")
    fun observeSharedPets(): Flow<List<PetEntity>>

    @Query("SELECT * FROM pets WHERE id = :petId")
    fun observePet(petId: String): Flow<PetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(pets: List<PetEntity>)

    @Query("DELETE FROM pets WHERE shared = :shared")
    suspend fun clear(shared: Boolean)

    @Query("DELETE FROM pets WHERE id = :petId")
    suspend fun delete(petId: String)

    @Transaction
    suspend fun replaceAll(shared: Boolean, pets: List<PetEntity>) {
        clear(shared)
        upsertAll(pets)
    }
}

@Dao
interface HealthRecordDao {

    @Query("SELECT * FROM health_records WHERE petId = :petId ORDER BY date DESC")
    fun observeByPet(petId: String): Flow<List<HealthRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(records: List<HealthRecordEntity>)

    @Query("DELETE FROM health_records WHERE petId = :petId")
    suspend fun clearForPet(petId: String)

    @Transaction
    suspend fun replaceForPet(petId: String, records: List<HealthRecordEntity>) {
        clearForPet(petId)
        upsertAll(records)
    }
}

@Dao
interface SupplyDao {

    @Query("SELECT * FROM supplies ORDER BY name")
    fun observeAll(): Flow<List<SupplyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(supplies: List<SupplyEntity>)

    @Query("DELETE FROM supplies")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(supplies: List<SupplyEntity>) {
        clear()
        upsertAll(supplies)
    }
}

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun observeAll(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(expenses: List<ExpenseEntity>)

    @Query("DELETE FROM expenses")
    suspend fun clear()

    @Transaction
    suspend fun replaceAll(expenses: List<ExpenseEntity>) {
        clear()
        upsertAll(expenses)
    }
}