package com.example.blackjack.viewmodels

import androidx.lifecycle.ViewModel
import com.example.blackjack.data.Carta
import com.example.blackjack.data.ConfigManager
import com.example.blackjack.data.HistorialManager
import com.example.blackjack.data.Partida
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class JuegoViewModel : ViewModel() {

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

        val meta = ConfigManager.metaPuntos.value

        // Si la meta es 11, solo damos 1 carta para que no pierdan al instante
        if (meta == 11) {
            _manoJugador.value = listOf(mazo.removeAt(mazo.lastIndex))
            _manoCrupier.value = listOf(mazo.removeAt(mazo.lastIndex))
        } else {
            // Para 21 y 25, damos las 2 cartas
            _manoJugador.value = listOf(mazo.removeAt(mazo.lastIndex), mazo.removeAt(mazo.lastIndex))
            _manoCrupier.value = listOf(mazo.removeAt(mazo.lastIndex), mazo.removeAt(mazo.lastIndex))
        }

        AlInicio21()
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

        val meta = ConfigManager.metaPuntos.value
        // Si el jugador se pasa de la meta, pierde
        if (calcularPuntos(_manoJugador.value) > meta) {
            finalizarJuego("Crupier")
        }
    }

    fun plantarse() {
        if (_juegoTerminado.value) return

        val meta = ConfigManager.metaPuntos.value
        val limiteCrupier = meta - 4 // Se planta a 4 puntos de la meta

        var puntosCrupier = calcularPuntos(_manoCrupier.value)
        val manoActualCrupier = _manoCrupier.value.toMutableList()

        while (puntosCrupier < limiteCrupier) {
            manoActualCrupier.add(mazo.removeAt(mazo.lastIndex))
            puntosCrupier = calcularPuntos(manoActualCrupier)
        }
        _manoCrupier.value = manoActualCrupier

        val puntosJugador = calcularPuntos(_manoJugador.value)

        val ganador = when {
            puntosCrupier > meta -> "Jugador"
            puntosJugador > puntosCrupier -> "Jugador"
            puntosCrupier > puntosJugador -> "Crupier"
            else -> "Empate"
        }
        finalizarJuego(ganador)
    }

    fun calcularPuntos(mano: List<Carta>): Int {
        var total = 0
        var cantidadDeAses = 0
        val meta = ConfigManager.metaPuntos.value

        for (carta in mano) {
            total += carta.puntosBasicos
            if (carta.valor == "A") cantidadDeAses++
        }

        // Si nos pasamos de la meta y tenemos un As, le restamos 10 puntos
        while (total > meta && cantidadDeAses > 0) {
            total -= 10
            cantidadDeAses--
        }
        return total
    }

    private fun AlInicio21() {
        val meta = ConfigManager.metaPuntos.value
        val puntosJugador = calcularPuntos(_manoJugador.value)
        val puntosCrupier = calcularPuntos(_manoCrupier.value)

        if (puntosJugador == meta && puntosCrupier == meta) finalizarJuego("Empate")
        else if (puntosJugador == meta) finalizarJuego("Jugador")
        else if (puntosCrupier == meta) finalizarJuego("Crupier")
    }
    private fun finalizarJuego(ganador: String) {
        _juegoTerminado.value = true
        _mensajeFinal.value = if (ganador == "Empate") "Empate!" else "Ganador: $ganador"

        // Preparamos los datos para guardarlos en nuestra Lista
        val combinacion = if (ganador == "Jugador") {
            val puntos = calcularPuntos(_manoJugador.value)
            val textoCartas = _manoJugador.value.joinToString { "${it.valor}${it.palo}" }
            "$textoCartas ($puntos puntos)"
        } else if (ganador == "Crupier") {
            val puntos = calcularPuntos(_manoCrupier.value)
            val textoCartas = _manoCrupier.value.joinToString { "${it.valor}${it.palo}" }
            "$textoCartas ($puntos puntos)"
        } else {
            val puntos = calcularPuntos(_manoJugador.value)
            "Empate por $puntos puntos"
        }

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = sdf.format(Date())

        // Lo guardamos directamente en la memoria
        HistorialManager.partidasGuardadas.add(
            Partida(ganador = ganador, fechaHora = fecha, combinacionGanadora = combinacion)
        )
    }
}