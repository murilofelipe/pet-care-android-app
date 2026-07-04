package com.murilo.petcare.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        PetEntity::class,
        HealthRecordEntity::class,
        SupplyEntity::class,
        ExpenseEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class PetCareDatabase : RoomDatabase() {
    abstract fun petDao(): PetDao
    abstract fun healthRecordDao(): HealthRecordDao
    abstract fun supplyDao(): SupplyDao
    abstract fun expenseDao(): ExpenseDao
}