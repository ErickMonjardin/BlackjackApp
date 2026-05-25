package com.example.blackjack.data

data class Carta(
    val palo: String, //Corazon, diamante, treboles y picas
    val valor: String, //Numero de las cartas
    val puntosBasicos: Int
)