package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    ChairEntity::class,
    DelegateEntity::class,
    CommitteeEntity::class,
    SessionActionEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class DelegateXDatabase : RoomDatabase() {
  abstract fun chairDao(): ChairDao
  abstract fun delegateDao(): DelegateDao
  abstract fun committeeDao(): CommitteeDao
  abstract fun sessionActionDao(): SessionActionDao

  companion object {
    @Volatile
    private var INSTANCE: DelegateXDatabase? = null

    fun getInstance(context: Context): DelegateXDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          DelegateXDatabase::class.java,
          "delegatex_database"
        ).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
