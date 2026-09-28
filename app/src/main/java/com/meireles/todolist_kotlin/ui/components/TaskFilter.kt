package com.meireles.todolist_kotlin.ui.components

/**Enum class representing the three possible filter types used in the UI:
 * ALL, ACTIVE and COMPLETED.
 * */
enum class TaskFilter { ALL, ACTIVE, COMPLETED }

/**Extension function that cycles between the three filters.
 * It is used when the user taps the filter chip in the AppBar.
 * */
fun TaskFilter.next(): TaskFilter = when (this) {
    TaskFilter.ALL -> TaskFilter.ACTIVE
    TaskFilter.ACTIVE -> TaskFilter.COMPLETED
    TaskFilter.COMPLETED -> TaskFilter.ALL
}

/**Function that converts the filter to a Boolean? value used in the repository/DAO, making
 * it easier to use filters in the database.
 * */
fun TaskFilter.toBooleanOrNull(): Boolean? = when (this) {
    TaskFilter.ALL -> null
    TaskFilter.ACTIVE -> false
    TaskFilter.COMPLETED -> true
}