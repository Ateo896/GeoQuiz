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
import com.example.geoquiz.ui.theme.GeoQuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeoQuizTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF6200EE) // Фиолетовый фон как на скриншоте
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
// СПИСОК ВОПРОСОВ (из задания)
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
// ОСНОВНОЙ ЭКРАН ВИКТОРИНЫ
// ==========================================
@Composable
fun GeoQuizScreen(modifier: Modifier = Modifier) {

    // ========== СОСТОЯНИЕ ==========

    // Индекс текущего вопроса (0-5)
    var currentIndex by remember { mutableIntStateOf(0) }

    // Счётчик правильных ответов
    var correctAnswers by remember { mutableIntStateOf(0) }

    // Был ли дан ответ на текущий вопрос?
    var hasAnswered by remember { mutableStateOf(false) }

    // Показывать ли диалог с результатами?
    var showResultDialog by remember { mutableStateOf(false) }

    // Текст результата для диалога
    var resultText by remember { mutableStateOf("") }

    // Текущий вопрос
    val currentQuestion = questionBank[currentIndex]

    // Это последний вопрос?
    val isLastQuestion = currentIndex == questionBank.size - 1

    // ========== UI ==========

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ─── ЗАГОЛОВОК "GeoQuiz" ───
        Text(
            text = "GeoQuiz",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // ─── КАРТОЧКА С ВОПРОСОМ ───
        Text(
            text = currentQuestion.textResId,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.9f),
            textAlign = TextAlign.Center,
            lineHeight = 28.dp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ─── КНОПКИ TRUE / FALSE ───
        // ★ Условие видимости: если НЕ отвечали → видны, если отвечали → скрыты!
        if (!hasAnswered) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
                // Кнопка TRUE
                Button(
                    onClick = {
                        checkAnswer(userAnswer = true,
                            correctAnswer = currentQuestion.answer,
                            onCorrect = { correctAnswers++ },
                            onAnswered = { hasAnswered = true })
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3700B3), // Темно-фиолетовый
                        contentColor = Color.White
                    ),
                    modifier = Modifier.width(120.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(text = "TRUE", fontWeight = FontWeight.Bold)
                }

                // Кнопка FALSE
                Button(
                    onClick = {
                        checkAnswer(userAnswer = false,
                            correctAnswer = currentQuestion.answer,
                            onCorrect = { correctAnswers++ },
                            onAnswered = { hasAnswered = true })
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
            // Когда кнопки скрыты — показываем пустое пространство или сообщение
            Text(
                text = if (currentQuestion.answer == true) "✓ Correct!" else "✗ Wrong!",
                color = if (currentQuestion.answer == true) Color.Green else Color.Red,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // ─── КНОПКА NEXT ───
        // ★ Условие видимости:
        //   1. Должен быть дан ответ (hasAnswered == true)
        //   2. НЕ должен быть последним вопросом (isLastQuestion == false)
        if (hasAnswered && !isLastQuestion) {
            OutlinedButton(
                onClick = {
                    // Переход к следующему вопросу
                    currentIndex++
                    hasAnswered = false // Сбросить флаг ответа
                },
                colors = OutlinedButtonDefaults.colors(
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 2.dp,
                    color = Color.White
                ),
                modifier = Modifier.width(140.dp)
            ) {
                Text(text = "NEXT >", fontWeight = FontWeight.Bold)
            }
        }

        // ★ Если это последний вопрос и дан ответ → показать результаты!
        if (hasAnswered && isLastQuestion) {
            // Автоматически показываем диалог с результатами
            resultText = "Викторина завершена!\nПравильных ответов: $correctAnswers из ${questionBank.size}"
            showResultDialog = true
        }
    }

    // ─── ДИАЛОГ С РЕЗУЛЬТАТАМИ ───
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
                        // Сброс викторины на начало
                        currentIndex = 0
                        correctAnswers = 0
                        hasAnswered = false
                    }
                ) {
                    Text("Начать заново")
                }
            },
            dismissButton = {
                TextButton(onClick = { /* Закрыть приложение */ }) {
                    Text("Закрыть")
                }
            }
        )
    }
}

// ==========================================
// ВСПОМОГАТЕЛЬНАЯ ФУНКЦИЯ ПРОВЕРКИ ОТВЕТА
// ==========================================
private fun checkAnswer(
    userAnswer: Boolean,
    correctAnswer: Boolean,
    onCorrect: () -> Unit,
    onAnswered: () -> Unit
) {
    if (userAnswer == correctAnswer) {
        onCorrect() // Увеличиваем счётчик правильных
    }
    onAnswered() // Помечаем, что ответ дан
}

// ==========================================
// PREVIEW
// ==========================================
@Preview(showBackground = true)
@Composable
fun GeoQuizPreview() {
    GeoQuizTheme {
        GeoQuizScreen()
    }
}