package com.example.geoquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.geoquiz.ui.theme.GeoQuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeoQuizTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF6200EE)
                ) { innerPadding ->
                    GeoQuizScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// ==========================================
// СПИСОК ВОПРОСОВ
// ==========================================
private val questionBank = listOf(
    Question("Canberra is the capital of Australia.", true),
    Question("The Pacific Ocean is larger than the Atlantic Ocean.", true),
    Question("The Suez Canal connects the Red Sea and the Indian Ocean.", false),
    Question("The source of the Nile River is in Egypt.", false),
    Question("The Amazon River is the longest river in the Americas.", true),
    Question("Lake Baikal is the world's oldest and deepest freshwater lake.", true)
)

// ==========================================
// ОСНОВНОЙ ЭКРАН
// ==========================================
@Composable
fun GeoQuizScreen(modifier: Modifier = Modifier) {

    // ========== СОСТОЯНИЯ ==========
    var currentIndex by remember { mutableIntStateOf(0) }
    var correctAnswers by remember { mutableIntStateOf(0) }
    var hasAnswered by remember { mutableStateOf(false) }
    var showResultDialog by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf("") }

    val currentQuestion = questionBank[currentIndex]
    val isLastQuestion = currentIndex == questionBank.size - 1

    // ========== UI ==========
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ─── ЗАГОЛОВОК ───
        Text(
            text = "GeoQuiz",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // ─── ВОПРОС (ИСПРАВЛЕНО: lineHeight!) ───
        Text(
            text = currentQuestion.textResId,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 28.sp,  // ★ ИСПРАВЛЕНО: было 28.dp → теперь 28.sp
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ─── КНОПКИ TRUE / FALSE ───
        if (!hasAnswered) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                Button(
                    onClick = {
                        if (currentQuestion.answer == true) correctAnswers++
                        hasAnswered = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3700B3),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.width(120.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(text = "TRUE", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (currentQuestion.answer == false) correctAnswers++
                        hasAnswered = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3700B3),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.width(120.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(text = "FALSE", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            // Показываем результат ответа
            val isCorrect = when {
                currentIndex == 0 -> true   // Canberra - true
                currentIndex == 1 -> true   // Pacific > Atlantic - true
                currentIndex == 2 -> false  // Suez Canal - false
                currentIndex == 3 -> false  // Nile source - false
                currentIndex == 4 -> true   // Amazon - true
                currentIndex == 5 -> true   // Baikal - true
                else -> false
            }

            Text(
                text = if (isCorrect) "✓ Correct!" else "✗ Wrong!",
                color = if (isCorrect) Color.Green else Color.Red,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // ─── КНОПКА NEXT (ИСПРАВЛЕНО: убран OutlinedButtonDefaults!) ───
        if (hasAnswered && !isLastQuestion) {
            OutlinedButton(
                onClick = {
                    currentIndex++
                    hasAnswered = false
                },
                // ★ ИСПРАВЛЕНО: убраны colors с OutlinedButtonDefaults!
                modifier = Modifier.width(140.dp)
            ) {
                Text(text = "NEXT >", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // ─── ПОКАЗ РЕЗУЛЬТАТОВ НА ПОСЛЕДНЕМ ВОПРОСЕ ───
        if (hasAnswered && isLastQuestion) {
            resultText = "Викторина завершена!\nПравильных ответов: $correctAnswers из ${questionBank.size}"
            showResultDialog = true
        }
    }

    // ─── ДИАЛОГ РЕЗУЛЬТАТОВ ───
    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text("🎉 Результаты") },
            text = {
                Text(
                    text = resultText,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResultDialog = false
                        currentIndex = 0
                        correctAnswers = 0
                        hasAnswered = false
                    }
                ) {
                    Text("Начать заново")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showResultDialog = false
                }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

// ==========================================
// PREVIEW (ИСПРАВЛЕН ИМПОРТ!)
// ==========================================
@Preview(showBackground = true)
@Composable
fun GeoQuizPreview() {
    GeoQuizTheme {
        GeoQuizScreen()
    }
}