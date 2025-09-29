package ru.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.example.bot.TelegramBot;

import java.net.InetSocketAddress;
import java.io.IOException;
import java.io.OutputStream;

public class Main {
    public static void main(String[] args) {
        try {
            // Запуск Telegram бота
            TelegramBot bot = new TelegramBot();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);

            // HTTP сервер для веб-интерфейса
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Главная страница с твоим HTML
            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    String html = """
                            <!DOCTYPE html>
                            <html lang="ru">
                            <head>
                                <meta charset="UTF-8">
                                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                                <title>SSCC Cell Gen</title>
                                <script src="https://cdn.jsdelivr.net/npm/jsbarcode@3.11.5/dist/JsBarcode.all.min.js"></script>
                                <style>
                                    * {
                                        margin: 0;
                                        padding: 0;
                                        box-sizing: border-box;
                                    }
                            
                                    body {
                                        font-family: 'Arial', sans-serif;
                                        background: #DEDEDE;
                                        padding: 20px;
                                        min-height: 100vh;
                                        display: flex;
                                        justify-content: center;
                                        align-items: flex-start;
                                    }
                            
                                    .container {
                                        max-width: 400px;
                                        width: 100%;
                                        display: flex;
                                        flex-direction: column;
                                        align-items: center;
                                        gap: 20px;
                                    }
                            
                                    .header {
                                        background: #FFFFFF;
                                        padding: 14px;
                                        border-radius: 16px;
                                        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                                        text-align: center;
                                        margin-top: 18px;
                                        margin-bottom: 20px;
                                    }
                            
                                    .header h1 {
                                        color: #2C3E50;
                                        font-size: 20px;
                                        font-weight: bold;
                                    }
                            
                                    .card {
                                        background: #FFFFFF;
                                        padding: 15px;
                                        border-radius: 16px;
                                        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                                        width: 100%;
                                    }
                            
                                    .radio-group {
                                        display: flex;
                                        justify-content: center;
                                        gap: 10px;
                                        width: 100%;
                                    }
                            
                                    .radio-option {
                                        flex: 1;
                                        text-align: center;
                                    }
                            
                                    .radio-option input {
                                        display: none;
                                    }
                            
                                    .radio-option label {
                                        display: block;
                                        padding: 10px;
                                        background: #f8f9fa;
                                        border: 2px solid #e9ecef;
                                        border-radius: 8px;
                                        color: #2C3E50;
                                        cursor: pointer;
                                        transition: all 0.3s;
                                    }
                            
                                    .radio-option input:checked + label {
                                        background: #3498DB;
                                        color: white;
                                        border-color: #3498DB;
                                    }
                            
                                    .input-card {
                                        padding: 16px;
                                    }
                            
                                    #inputText {
                                        width: 100%;
                                        padding: 12px;
                                        font-size: 18px;
                                        text-align: center;
                                        border: none;
                                        outline: none;
                                        background: transparent;
                                        color: #2C3E50;
                                    }
                            
                                    #inputText::placeholder {
                                        color: #95a5a6;
                                    }
                            
                                    #generateButton {
                                        width: 160px;
                                        height: 55px;
                                        background: #3498DB;
                                        color: white;
                                        border: none;
                                        border-radius: 8px;
                                        font-size: 16px;
                                        cursor: pointer;
                                        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                                        transition: background 0.3s;
                                    }
                            
                                    #generateButton:hover {
                                        background: #2980B9;
                                    }
                            
                                    #generateButton:active {
                                        transform: scale(0.98);
                                    }
                            
                                    .barcode-card {
                                        height: 200px;
                                        display: flex;
                                        justify-content: center;
                                        align-items: center;
                                        box-shadow: 0 6px 10px rgba(0,0,0,0.15);
                                        overflow: hidden;
                                    }
                            
                                    #barcodeContainer {
                                        max-width: 100%;
                                        max-height: 100%;
                                        padding: 15px;
                                        text-align: center;
                                    }
                            
                                    #barcodeCanvas {
                                        max-width: 100%;
                                        height: auto;
                                    }
                            
                                    .result-card {
                                        background: #ECF0F1;
                                        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                                        display: none;
                                    }
                            
                                    #resultText {
                                        padding: 12px;
                                        font-size: 14px;
                                        text-align: center;
                                        color: #34495E;
                                        word-break: break-all;
                                    }
                            
                                    .error {
                                        color: #e74c3c;
                                        text-align: center;
                                        margin-top: 10px;
                                        display: none;
                                    }
                                </style>
                            </head>
                            <body>
                                <div class="container">
                                    <div class="header">
                                        <h1>SSCC Cell Gen</h1>
                                    </div>
                            
                                    <div class="card">
                                        <div class="radio-group">
                                            <div class="radio-option">
                                                <input type="radio" id="radioCell" name="barcodeType" checked>
                                                <label for="radioCell">ЯЧЕЙКА</label>
                                            </div>
                                            <div class="radio-option">
                                                <input type="radio" id="radioSscc" name="barcodeType">
                                                <label for="radioSscc">SSCC</label>
                                            </div>
                                        </div>
                                    </div>
                            
                                    <div class="card input-card">
                                        <input type="text" id="inputText" placeholder="Формат: 0000-00-00" maxlength="10">
                                    </div>
                            
                                    <button id="generateButton">Сгенерировать</button>
                            
                                    <div class="card barcode-card">
                                        <div id="barcodeContainer">
                                            <canvas id="barcodeCanvas" width="300" height="150"></canvas>
                                        </div>
                                    </div>
                            
                                    <div class="card result-card">
                                        <div id="resultText"></div>
                                    </div>
                            
                                    <div class="error" id="errorMessage"></div>
                                </div>
                            
                                <script>
                                    // Элементы DOM
                                    const inputText = document.getElementById('inputText');
                                    const generateButton = document.getElementById('generateButton');
                                    const barcodeCanvas = document.getElementById('barcodeCanvas');
                                    const resultText = document.getElementById('resultText');
                                    const resultCard = document.querySelector('.result-card');
                                    const errorMessage = document.getElementById('errorMessage');
                                    const radioCell = document.getElementById('radioCell');
                                    const radioSscc = document.getElementById('radioSscc');
                            
                                    // Убедимся что Canvas поддерживается
                                    function isCanvasSupported() {
                                        const elem = document.createElement('canvas');
                                        return !!(elem.getContext && elem.getContext('2d'));
                                    }
                            
                                    if (!isCanvasSupported()) {
                                        errorMessage.textContent = 'Ваш браузер не поддерживает генерацию штрихкодов';
                                        errorMessage.style.display = 'block';
                                    }
                            
                                    // Обновление поля ввода при переключении режимов
                                    function updateInputField() {
                                        if (radioCell.checked) {
                                            inputText.placeholder = 'Формат: 0000-00-00';
                                            inputText.maxLength = 10;
                                        } else {
                                            inputText.placeholder = 'Последние 7 цифр';
                                            inputText.maxLength = 7;
                                        }
                                        inputText.value = '';
                                        inputText.focus();
                            
                                        // Очищаем предыдущий результат
                                        clearBarcode();
                                    }
                            
                                    // Очистка штрихкода
                                    function clearBarcode() {
                                        const context = barcodeCanvas.getContext('2d');
                                        context.clearRect(0, 0, barcodeCanvas.width, barcodeCanvas.height);
                                        resultCard.style.display = 'none';
                                        errorMessage.style.display = 'none';
                                    }
                            
                                    // Обработчики переключения радио-кнопок
                                    radioCell.addEventListener('change', updateInputField);
                                    radioSscc.addEventListener('change', updateInputField);
                            
                                    // Автоматическое добавление тире для режима ЯЧЕЙКА
                                    inputText.addEventListener('input', function(e) {
                                        if (!radioCell.checked) return;
                            
                                        let value = this.value.replace(/-/g, '');
                                        if (value.length > 8) value = value.substring(0, 8);
                            
                                        let formattedValue = '';
                                        for (let i = 0; i < value.length; i++) {
                                            if (i === 4 || i === 6) formattedValue += '-';
                                            formattedValue += value[i];
                                        }
                            
                                        if (this.value !== formattedValue) {
                                            this.value = formattedValue;
                                        }
                                    });
                            
                                    // Твоя логика обработки даты
                                    function processDate(date) {
                                        const parts = date.split('-');
                                        const row = parseInt(parts[0]);
                                        const cell = parseInt(parts[1]);
                                        const stage = parseInt(parts[2]);
                            
                                        let result = "92";
                            
                                        let hexRow = (row * 4).toString(16).toUpperCase();
                                        if (hexRow.length === 3) result += "0";
                                        result += hexRow;
                            
                                        let hexCell = cell.toString(16).toUpperCase();
                                        if (hexCell.length === 1) result += "0";
                                        result += hexCell;
                            
                                        result += "1";
                                        result += stage.toString(16).toUpperCase();
                                        result += "WRH";
                            
                                        return result;
                                    }
                            
                                    // Обработка SSCC
                                    function processSscc(input) {
                                        return "0000300002300" + input;
                                    }
                            
                                    // Проверка формата даты
                                    function isValidDateFormat(date) {
                                        return /^\\d{4}-\\d{2}-\\d{2}$/.test(date);
                                    }
                            
                                    // Генерация штрихкода с улучшенной обработкой ошибок
                                    function generateBarcode() {
                                        try {
                                            clearBarcode();
                            
                                            const input = inputText.value.trim();
                            
                                            if (radioCell.checked) {
                                                const cleanText = input.replace(/-/g, '');
                                                if (cleanText.length === 8) {
                                                    const formattedText = cleanText.substring(0, 4) + '-' + 
                                                                         cleanText.substring(4, 6) + '-' + 
                                                                         cleanText.substring(6, 8);
                            
                                                    if (isValidDateFormat(formattedText)) {
                                                        const result = processDate(formattedText);
                                                        generateBarcodeImage(result);
                                                    } else {
                                                        showError('Неверный формат! Пример: 2024-15-01');
                                                    }
                                                } else {
                                                    showError('Введите все 8 цифр: 1111-22-33');
                                                }
                                            } else {
                                                const cleanText = input.replace(/\\D/g, '');
                                                if (cleanText.length === 7) {
                                                    const result = processSscc(cleanText);
                                                    generateBarcodeImage(result);
                                                } else {
                                                    showError('Неверный формат! Введите ровно 7 цифр');
                                                }
                                            }
                                        } catch (error) {
                                            showError('Ошибка при генерации: ' + error.message);
                                            console.error('Generation error:', error);
                                        }
                                    }
                            
                                    // Показать ошибку
                                    function showError(message) {
                                        errorMessage.textContent = message;
                                        errorMessage.style.display = 'block';
                                    }
                            
                                    // Генерация изображения штрихкода
                                    function generateBarcodeImage(data) {
                                        try {
                                            console.log('Generating barcode for:', data);
                            
                                            // Явно устанавливаем размеры canvas
                                            barcodeCanvas.width = 300;
                                            barcodeCanvas.height = 150;
                            
                                            // Генерируем штрихкод с простыми настройками
                                            JsBarcode(barcodeCanvas, data, {
                                                format: "CODE128",
                                                width: 2,
                                                height: 100,
                                                displayValue: false,
                                                margin: 10,
                                                background: "#ffffff",
                                                lineColor: "#000000"
                                            });
                            
                                            console.log('Barcode generated successfully');
                            
                                            // Показываем результат
                                            resultText.textContent = "Результат: " + data;
                                            resultCard.style.display = 'block';
                            
                                        } catch (error) {
                                            console.error('Barcode generation error:', error);
                                            showError('Ошибка генерации штрихкода: ' + error.message);
                                        }
                                    }
                            
                                    // Обработчик кнопки
                                    generateButton.addEventListener('click', generateBarcode);
                            
                                    // Обработчик Enter в поле ввода
                                    inputText.addEventListener('keypress', function(e) {
                                        if (e.key === 'Enter') {
                                            generateBarcode();
                                        }
                                    });
                            
                                    // Инициализация
                                    updateInputField();
                            
                                    // Тестовая генерация при загрузке
                                    setTimeout(() => {
                                        console.log('App initialized, testing barcode generation...');
                                    }, 1000);
                                    // Разрешаем ввод только цифр и тире
                                            function validateInput(e) {
                                                const key = e.key;
                                                const isDigit = /^\\d$/.test(key);
                                                const isDash = key === '-';
                                                const isBackspace = key === 'Backspace';
                                                const isDelete = key === 'Delete';
                                                const isArrow = key.startsWith('Arrow');
                                                const isTab = key === 'Tab';
                            
                                                // Разрешаем только цифры, тире и управляющие клавиши
                                                if (!isDigit && !isDash && !isBackspace && !isDelete && !isArrow && !isTab) {
                                                    e.preventDefault();
                                                    return;
                                                }
                            
                                                // Для режима ЯЧЕЙКА - автоматическое добавление тире
                                                if (radioCell.checked && isDigit) {
                                                    setTimeout(() => {
                                                        let value = inputText.value.replace(/-/g, '');
                                                        if (value.length > 8) value = value.substring(0, 8);
                            
                                                        let formattedValue = '';
                                                        for (let i = 0; i < value.length; i++) {
                                                            if (i === 4 || i === 6) formattedValue += '-';
                                                            formattedValue += value[i];
                                                        }
                            
                                                        if (inputText.value !== formattedValue) {
                                                            inputText.value = formattedValue;
                                                        }
                                                    }, 0);
                                                }
                                            }
                            
                                            // Обработчик вставки (paste) - очищаем от нецифровых символов
                                            function handlePaste(e) {
                                                e.preventDefault();
                                                let pastedText = (e.clipboardData || window.clipboardData).getData('text');
                            
                                                // Оставляем только цифры
                                                pastedText = pastedText.replace(/\\D/g, '');
                            
                                                if (radioCell.checked) {
                                                    // Для режима ЯЧЕЙКА форматируем как дату
                                                    if (pastedText.length > 8) pastedText = pastedText.substring(0, 8);
                                                    let formattedValue = '';
                                                    for (let i = 0; i < pastedText.length; i++) {
                                                        if (i === 4 || i === 6) formattedValue += '-';
                                                        formattedValue += pastedText[i];
                                                    }
                                                    inputText.value = formattedValue;
                                                } else {
                                                    // Для режима SSCC - только цифры
                                                    if (pastedText.length > 7) pastedText = pastedText.substring(0, 7);
                                                    inputText.value = pastedText;
                                                }
                                            }
                            
                                            // Добавь обработчики после существующего кода
                                            inputText.addEventListener('keydown', validateInput);
                                            inputText.addEventListener('paste', handlePaste);
                            
                                            // Также обнови функцию updateInputField для сброса значения
                                            function updateInputField() {
                                                if (radioCell.checked) {
                                                    inputText.placeholder = 'Формат: 0000-00-00';
                                                    inputText.maxLength = 10;
                                                } else {
                                                    inputText.placeholder = 'Последние 7 цифр';
                                                    inputText.maxLength = 7;
                                                }
                                                inputText.value = '';
                                                inputText.focus();
                            
                                                // Очищаем предыдущий результат
                                                clearBarcode();
                                            }
                                </script>
                            </body>
                            </html>
                            """;

                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                    exchange.sendResponseHeaders(200, html.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(html.getBytes());
                    }
                }
            });

            server.start();
            System.out.println("✅ Бот запущен! Веб-интерфейс: https://sscc-gen.onrender.com");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
