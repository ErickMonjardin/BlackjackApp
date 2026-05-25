package com.example.blackjack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.blackjack.data.ConfigManager
import com.example.blackjack.viewmodels.JuegoViewModel

class JuegoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // Instanciamos el ViewModel
                val viewModel: JuegoViewModel = viewModel()

                // Escuchamos los estados del juego
                val manoJugador by viewModel.manoJugador.collectAsState()
                val manoCrupier by viewModel.manoCrupier.collectAsState()
                val juegoTerminado by viewModel.juegoTerminado.collectAsState()
                val mensajeFinal by viewModel.mensajeFinal.collectAsState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(ConfigManager.colorFondo.value) // Fondo oscuro
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Zona del crupier
                    Text("Crupier", fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        manoCrupier.forEach { carta ->
                            CartaUI(
                                texto = "${carta.valor}${carta.palo}",
                                oculta = !juegoTerminado
                            )
                        }
                    }
                    if (juegoTerminado) {
                        Text("Puntos: ${viewModel.calcularPuntos(manoCrupier)}", color = Color.White, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Mensajes y boton de reset
                    if (juegoTerminado) {
                        Text(mensajeFinal, fontSize = 32.sp, color = Color(0xFFFFC107), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.iniciarNuevaPartida() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4))
                        ) {
                            Text("Nueva Partida", fontSize = 20.sp)
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Zona del jugador
                    Text("Jugador", fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.Center) {
                        manoJugador.forEach { carta ->
                            CartaUI(texto = "${carta.valor}${carta.palo}", oculta = false)
                        }
                    }
                    Text("Puntos: ${viewModel.calcularPuntos(manoJugador)}", color = Color.White, fontSize = 20.sp)

                    Spacer(modifier = Modifier.height(32.dp))

                    // Botones de la mesa
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { viewModel.pedirCarta() },
                            enabled = !juegoTerminado,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Pedir Carta", fontSize = 18.sp)
                        }
                        Button(
                            onClick = { viewModel.plantarse() },
                            enabled = !juegoTerminado,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                        ) {
                            Text("Plantarse", fontSize = 18.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun CartaUI(texto: String, oculta: Boolean) {
    val esRoja = texto.contains("♥") || texto.contains("♦")

    Card(
        modifier = Modifier
            .padding(4.dp)
            .size(70.dp, 100.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = if (oculta) Color.Gray else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            if (oculta) {
                Text("❓", fontSize = 32.sp)
            } else {
                Text(
                    text = texto,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (esRoja) Color.Red else Color.Black
                )
            }
        }
    }
}