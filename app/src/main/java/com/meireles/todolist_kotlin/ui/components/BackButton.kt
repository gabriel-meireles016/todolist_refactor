package com.meireles.todolist_kotlin.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**Reusable component of a back-page arrow.*/
@Composable
fun BackButton(onCancel: () -> Unit = {}) {
    IconButton(
        modifier = Modifier.padding(all = 8.dp),
        onClick = { onCancel() },
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = Color.White,
            containerColor = Color.Transparent
        )
    ) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
    }
}


@Preview
@Composable
private fun BackButtonPreview() {
    BackButton()
}