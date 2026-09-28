package com.meireles.todolist_kotlin.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.meireles.todolist_kotlin.data.repositories.TaskCounts
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    /**Inserts a new task and returns generated rowId.*/
    @Insert
    suspend fun create(taskEntity: TaskEntity) : Long

    /**Get all tasks with an optional filter and returns a reactive list (Flow)*/
    @Query("SELECT * FROM task WHERE (:isCompleted IS NULL OR isCompleted = :isCompleted) ORDER BY id")
    fun getAll(isCompleted: Boolean?): Flow<List<TaskEntity>>
    // Flow makes access more reactive by implementing changes automatically.

    /**Remove a task by id.*/
    @Query("DELETE FROM task WHERE id = :id")
    suspend fun delete(id: Int)

    /**Update a task.*/
    @Update
    suspend fun update(taskEntity: TaskEntity)

    /**Search a taks by id and returns null if there isn't this task.*/
    @Query("SELECT * FROM task WHERE id = :id LIMIT 1")
    fun getById(id: Int): Flow<TaskEntity?>

    /**Toggle conclusion status.*/
    @Query("UPDATE task SET isCompleted = NOT isCompleted WHERE id = :id")
    suspend fun toggleStatus(id: Int)

    /**Return total, done and progess task counts.*/
    @Query("""
        SELECT
            COUNT(*) AS total,
            COALESCE(SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END), 0) AS completed,
            COALESCE(SUM(CASE WHEN isCompleted = 0 THEN 1 ELSE 0 END),0) AS inProgress
        FROM task
    """)
    fun getNumber(): Flow<TaskCounts>
    // Flow makes access more reactive by implementing changes automatically.

}


