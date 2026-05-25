package com.example.blackjack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
class InstruccionesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MaterialTheme{
            Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF2C2C2C))
                        .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Reglas",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFC107),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = """
                                1. El objetivo del juego es conseguir 21 puntos o estar más cerca de 21 que el crupier 
                                
                                2. Si te pasas de 21 puntos, pierdes automáticamente la partida.
                                
                                3. Las cartas del 2 al 10 valen su número exacto. Las figuras (J, Q, K) valen 10 puntos cada una.
                                
                                4. El As (A) puede valer 11 puntos o 1 punto, dependiendo de lo que más te convenga para no pasarte del límite.
                                
                                5. Al inicio, tanto tú como el crupier reciben 2 cartas. Puedes 'Pedir Carta' o 'Plantarse'.
                                
                                6. El crupier está obligado a pedir cartas hasta alcanzar al menos 17 puntos.
                                
                                7. Gana quien tenga la puntuación más alta sin pasarse.
                            """.trimIndent(),
                        color = Color.White,
                        fontSize = 18.sp,
                        lineHeight = 28.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { finish() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Volver al Menu", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        }
    }
}
