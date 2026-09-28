package com.meireles.todolist_kotlin.ui.screens.add

/**Immutable state of the Add/Edit task screen.*/
data class AddUiState(
    /**Indicates an ongoing operation.*/
    val isLoading: Boolean = false,
    /**Differentiates the screen mode:
     *
     * false -> creating a new task
     *
     * true -> editing an existing task
     * */
    val isEditing: Boolean = false,
    /**Task ID.*/
    val id: Int? = null,
    /**Editable title field in the form.*/
    val title: String = "",
    /**Editable description field in the form.*/
    val description: String = "",
    /**Metadata created by repository/data layer*/
    val createdAt: Long? = null,
    /**Task completion status.*/
    val isCompleted: Boolean = false,
    /**General form validation flag (typical: required title).*/
    val isValid: Boolean = false,
    /**Error message displayed in the UI.*/
    val error: String? = null,
    /**Event/state of "successfully saved". The UI observes and navigates back when this is true.*/
    val isSaved: Boolean = false
)
