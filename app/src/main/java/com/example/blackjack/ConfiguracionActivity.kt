package com.example.blackjack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blackjack.data.ConfigManager
class ConfiguracionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF2C2C2C))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Configuracion", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(32.dp))

                    // Seleccion de color
                    Text("Color de la mesa", fontSize = 20.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { ConfigManager.colorFondo.value = Color(0xFF1B5E20) }, // Verde
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20))
                        ) { Text("Verde") }

                        Button(
                            onClick = { ConfigManager.colorFondo.value = Color(0xFF2C2C2C) }, // Gris
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                        ) { Text("Gris") }

                        Button(
                            onClick = { ConfigManager.colorFondo.value = Color(0xFF0D47A1) }, // Azul
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
                        ) { Text("Azul") }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // Boton de regreso
                    Button(
                        onClick = { finish() },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
                    ) {
                        Text("Guardar y volver", fontSize = 20.sp, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Cambio de meta de puntos
                    Text("Puntaje Meta", fontSize = 20.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { ConfigManager.metaPuntos.value = 11 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (ConfigManager.metaPuntos.value == 11) Color(0xFFE91E63) else Color.Gray
                            )
                        ) { Text("A 11") }

                        Button(
                            onClick = { ConfigManager.metaPuntos.value = 21 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (ConfigManager.metaPuntos.value == 21) Color(0xFF4CAF50) else Color.Gray
                            )
                        ) { Text("A 21") }

                        Button(
                            onClick = { ConfigManager.metaPuntos.value = 25 },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (ConfigManager.metaPuntos.value == 25) Color(0xFFFF9800) else Color.Gray
                            )
                        ) { Text("A 25") }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
