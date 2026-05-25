package com.example.blackjack.data

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color

object ConfigManager {
    var colorFondo = mutableStateOf(Color(0xFF2C2C2C))

    //Cambio de meta de puntos
    var metaPuntos = mutableStateOf(21)
}