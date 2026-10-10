package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChairDao {
  @Query("SELECT * FROM chairs ORDER BY createdAt DESC")
  fun getAllChairs(): Flow<List<ChairEntity>>

  @Query("SELECT * FROM chairs WHERE committee = :committee ORDER BY createdAt DESC")
  fun getChairsByCommittee(committee: String): Flow<List<ChairEntity>>

  @Query("SELECT * FROM chairs WHERE id = :id LIMIT 1")
  suspend fun getChairById(id: String): ChairEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertChair(chair: ChairEntity)

  @Query("DELETE FROM chairs WHERE id = :id")
  suspend fun deleteChair(id: String)
}

@Dao
interface DelegateDao {
  @Query("SELECT * FROM delegates ORDER BY registeredAt DESC")
  fun getAllDelegates(): Flow<List<DelegateEntity>>

  @Query("SELECT * FROM delegates WHERE committee = :committee ORDER BY country ASC")
  fun getDelegatesByCommittee(committee: String): Flow<List<DelegateEntity>>

  @Query("SELECT * FROM delegates WHERE id = :id LIMIT 1")
  suspend fun getDelegateById(id: String): DelegateEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDelegate(delegate: DelegateEntity)

  @Query("UPDATE delegates SET rollCallStatus = :status WHERE id = :id")
  suspend fun updateRollCall(id: String, status: String)

  @Query("DELETE FROM delegates WHERE id = :id")
  suspend fun deleteDelegate(id: String)

  @Query("SELECT COUNT(*) FROM delegates WHERE committee = :committee")
  suspend fun countDelegatesInCommittee(committee: String): Int
}

@Dao
interface CommitteeDao {
  @Query("SELECT * FROM committees")
  fun getAllCommittees(): Flow<List<CommitteeEntity>>

  @Query("SELECT * FROM committees WHERE id = :id LIMIT 1")
  fun getCommitteeById(id: String): Flow<CommitteeEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCommittees(committees: List<CommitteeEntity>)

  @Update
  suspend fun updateCommittee(committee: CommitteeEntity)

  @Query("UPDATE committees SET currentGslSpeaker = :speaker WHERE id = :id")
  suspend fun updateCurrentSpeaker(id: String, speaker: String?)

  @Query("UPDATE committees SET sessionStatus = :status WHERE id = :id")
  suspend fun updateSessionStatus(id: String, status: String)
}

@Dao
interface SessionActionDao {
  @Query("SELECT * FROM session_actions WHERE committeeId = :committeeId ORDER BY timestamp DESC LIMIT 50")
  fun getActionsByCommittee(committeeId: String): Flow<List<SessionActionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAction(action: SessionActionEntity)
}
