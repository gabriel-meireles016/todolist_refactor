package com.meireles.todolist_kotlin.navigation

import kotlinx.serialization.Serializable

// Creating UI route references, centralizing them here and with a specific type.

/**
 * Define type-safe UI routes for the App.
 *
 * Each destination is represented by a sealed type, enabling exhaustive 'when'
 * expressions and centralized route management.
 *
 * The '@Serializable' annotation allows safe argument passing via JSON, avoiding fragile string
 * concatenations.
 * */
@Serializable
sealed interface UIRoute{

    /**Home screen.*/
    @Serializable
    data object Home: UIRoute

    /**Add-Task screen.*/
    @Serializable
    data object Add: UIRoute

    /**Edit-Task screen, requires a task identifier.*/
    @Serializable
    data class Edit(val taskId: Int): UIRoute

}