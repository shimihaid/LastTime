package com.example.lasttime.data.repository

import com.example.lasttime.data.local.dao.ActivityDao
import com.example.lasttime.data.local.entity.ActivityEntity
import com.example.lasttime.domain.model.Activity
import com.example.lasttime.domain.model.ActivityNotFoundException
import com.example.lasttime.domain.model.ActivityRules
import com.example.lasttime.domain.model.InvalidActivityException
import com.example.lasttime.domain.model.MarkOutcome
import com.example.lasttime.domain.model.calculateDaysSince
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class ActivityRepository(private val dao: ActivityDao) {

    fun observeActivities(): Flow<List<Activity>> =
        dao.observeActivities().map { list ->
            val today = LocalDate.now()
            list.map { it.toDomain(today) }
        }

    fun observeHistory(activityId: Long): Flow<List<LocalDate>> =
        dao.observeHistory(activityId).map { list -> list.map { it.performedAt } }

    suspend fun addActivity(name: String, performedAt: LocalDate): Result<Long> = safeCall {
        ActivityRules.validateName(name)?.let { throw InvalidActivityException(it) }
        if (performedAt.isAfter(LocalDate.now())) {
            throw InvalidActivityException("A data não pode estar no futuro.")
        }
        dao.insertActivityWithHistory(name.trim(), performedAt)
    }

    suspend fun markPerformedToday(activityId: Long): Result<MarkOutcome> = safeCall {
        val outcome = dao.markPerformed(activityId, LocalDate.now())
        if (outcome == MarkOutcome.NOT_FOUND) throw ActivityNotFoundException()
        outcome
    }

    suspend fun deleteActivity(activityId: Long): Result<Unit> = safeCall {
        if (dao.deleteActivity(activityId) == 0) throw ActivityNotFoundException()
    }

    private fun ActivityEntity.toDomain(today: LocalDate) = Activity(
        id = id,
        name = name,
        lastPerformedAt = lastPerformedAt,
        daysSinceLastPerformed = calculateDaysSince(lastPerformedAt, today)
    )
}
