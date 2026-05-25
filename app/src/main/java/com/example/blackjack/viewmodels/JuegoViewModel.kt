package com.example.blackjack.viewmodels

import androidx.lifecycle.ViewModel
import com.example.blackjack.data.Carta
import com.example.blackjack.data.HistorialManager
import com.example.blackjack.data.Partida
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JuegoViewModel : ViewModel() { // <-- ¡Adiós DAO y base de datos!

    private val palos = listOf("♥", "♦", "♣", "♠")
    private val valores = listOf("2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A")

    private var mazo = mutableListOf<Carta>()

    // Estados para la interfaz grafica (lo que tiene que escuchar)
    private val _manoJugador = MutableStateFlow<List<Carta>>(emptyList())
    val manoJugador: StateFlow<List<Carta>> = _manoJugador.asStateFlow()

    private val _manoCrupier = MutableStateFlow<List<Carta>>(emptyList())
    val manoCrupier: StateFlow<List<Carta>> = _manoCrupier.asStateFlow()

    private val _juegoTerminado = MutableStateFlow(false)
    val juegoTerminado: StateFlow<Boolean> = _juegoTerminado.asStateFlow()

    private val _mensajeFinal = MutableStateFlow("")
    val mensajeFinal: StateFlow<String> = _mensajeFinal.asStateFlow()

    init {
        iniciarNuevaPartida()
    }

    fun iniciarNuevaPartida() {
        _juegoTerminado.value = false
        _mensajeFinal.value = ""
        crearYBarajear()

        // Repartimos 2 cartas a cada uno al empezar
        _manoJugador.value = listOf(mazo.removeAt(mazo.lastIndex), mazo.removeAt(mazo.lastIndex))
        _manoCrupier.value = listOf(mazo.removeAt(mazo.lastIndex), mazo.removeAt(mazo.lastIndex))

        AlIncio21()
    }

    private fun crearYBarajear() {
        mazo.clear()
        for (palo in palos) {
            for (valor in valores) {
                val puntos = when (valor) {
                    "A" -> 11 // Inicialmente le damos valor de 11
                    "J", "Q", "K" -> 10
                    else -> valor.toInt()
                }
                mazo.add(Carta(palo, valor, puntos))
            }
        }
        mazo.shuffle() // Revolvemos las 52 cartas
    }

    fun pedirCarta() {
        if (_juegoTerminado.value) return

        val nuevaMano = _manoJugador.value.toMutableList()
        nuevaMano.add(mazo.removeAt(mazo.lastIndex))
        _manoJugador.value = nuevaMano

        // Si el jugador se pasa de 21, pierde en automatico
        if (calcularPuntos(_manoJugador.value) > 21) {
            finalizarJuego("Crupier")
        }
    }

    fun plantarse() {
        if (_juegoTerminado.value) return

        var puntosCrupier = calcularPuntos(_manoCrupier.value)
        val manoActualCrupier = _manoCrupier.value.toMutableList()

        // Crupier saca cartas obligatoriamente hasta tener 17 o mas
        while (puntosCrupier < 17) {
            manoActualCrupier.add(mazo.removeAt(mazo.lastIndex))
            puntosCrupier = calcularPuntos(manoActualCrupier)
        }
        _manoCrupier.value = manoActualCrupier

        val puntosJugador = calcularPuntos(_manoJugador.value)

        // Evaluamos quien gano
        val ganador = when {
            puntosCrupier > 21 -> "Jugador" // Si el crupier se pasa
            puntosJugador > puntosCrupier -> "Jugador"
            puntosCrupier > puntosJugador -> "Crupier"
            else -> "Empate"
        }
        finalizarJuego(ganador)
    }

    // Logica para contar puntos y cambiar el valor del As
    fun calcularPuntos(mano: List<Carta>): Int {
        var total = 0
        var cantidadDeAses = 0

        for (carta in mano) {
            total += carta.puntosBasicos
            if (carta.valor == "A") cantidadDeAses++
        }

        // Si nos pasamos de 21 y tenemos un As, le restamos 10 puntos
        while (total > 21 && cantidadDeAses > 0) {
            total -= 10
            cantidadDeAses--
        }
        return total
    }

    private fun AlIncio21() {
        val puntosJugador = calcularPuntos(_manoJugador.value)
        val puntosCrupier = calcularPuntos(_manoCrupier.value)

        if (puntosJugador == 21 && puntosCrupier == 21) finalizarJuego("Empate")
        else if (puntosJugador == 21) finalizarJuego("Jugador")
        else if (puntosCrupier == 21) finalizarJuego("Crupier")
    }

    private fun finalizarJuego(ganador: String) {
        _juegoTerminado.value = true
        _mensajeFinal.value = if (ganador == "Empate") "Empate!" else "Ganador: $ganador"

        // Preparamos los datos para guardarlos en nuestra Lista
        val combinacion = if (ganador == "Jugador") {
            _manoJugador.value.joinToString { "${it.valor}${it.palo}" }
        } else if (ganador == "Crupier") {
            _manoCrupier.value.joinToString { "${it.valor}${it.palo}" }
        } else {
            "Empate"
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = sdf.format(Date())

        // Lo guardamos directamente en la memoria
        HistorialManager.partidasGuardadas.add(
            Partida(ganador = ganador, fechaHora = fecha, combinacionGanadora = combinacion)
        )
    }
}