package com.example.lasttime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.lasttime.data.local.entity.ActivityEntity
import com.example.lasttime.data.local.entity.ActivityHistoryEntity
import com.example.lasttime.domain.model.MarkOutcome
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
abstract class ActivityDao {

    // ---------- atividades ----------
    @Insert
    abstract suspend fun insertActivity(activity: ActivityEntity): Long

    // Mais tempo sem fazer aparece primeiro
    @Query("SELECT * FROM activities ORDER BY lastPerformedAt ASC, name ASC")
    abstract fun observeActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :id")
    abstract suspend fun getActivityById(id: Long): ActivityEntity?

    @Query("UPDATE activities SET lastPerformedAt = :date WHERE id = :id")
    abstract suspend fun updateLastPerformed(id: Long, date: LocalDate): Int

    @Query("DELETE FROM activities WHERE id = :id")
    abstract suspend fun deleteActivity(id: Long): Int
}
