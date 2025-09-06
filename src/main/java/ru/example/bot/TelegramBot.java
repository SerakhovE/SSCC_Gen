package ru.example.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TelegramBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        // Берем из переменной окружения, если нет - дефолтное значение
        return System.getenv("BOT_USERNAME") != null ?
                System.getenv("BOT_USERNAME") : "SSCC_gen_bot";
    }

    @Override
    public String getBotToken() {
        // Берем из переменной окружения
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            // Обрабатываем команды
            if (messageText.equals("/start")) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("Привет! Введите АДРЕС ЯЧЕЙКИ в формате 0000-00-00 (например: 2024-15-01)" +
                        "\nили в формате 0000 00 00 (например: 2024 15 01)" +
                        "\nили в формате 1111223 (например: 2024151) \nИ я сгенерирую Штрихкод!");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
            // Проверяем формат даты
            else if (isValidDateFormat(messageText)) {
                String result = processDate(messageText);
                try {
                    // Генерируем и отправляем штрихкод
                    sendBarcode(chatId, messageText, result); // Передаем оба значения
                } catch (Exception e) {
                    // Если ошибка генерации - отправляем текст
                    SendMessage errorMessage = new SendMessage();
                    errorMessage.setChatId(chatId);
                    errorMessage.setText("✅ Формат правильный!\nРезультат: " + result +
                            "\n❌ Ошибка генерации штрихкода");
                    try {
                        execute(errorMessage);
                    } catch (TelegramApiException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            // Если сообщение не распознано
            else {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText("❌ Неверный формат! (пример: 3009-07-03 или 3009 07 03 или 3009073)");
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Метод для отправки штрихкода (теперь принимает оригинальный текст и результат)
    private void sendBarcode(long chatId, String originalText, String result) throws IOException, TelegramApiException {
        // Генерируем изображение штрихкода
        byte[] barcodeImage = generateBarcodeImage(result);

        // Создаем сообщение с фото (в подписи используем оригинальный текст пользователя)
        SendPhoto photo = new SendPhoto();
        photo.setChatId(String.valueOf(chatId));
        photo.setPhoto(new InputFile(new ByteArrayInputStream(barcodeImage), "barcode.png"));
        if (originalText.contains("-")) {
            photo.setCaption("✅   S" + originalText + "   ✅"); // Только то, что ввел пользователь
        } else if (originalText.contains(" ")) {
            photo.setCaption("✅   S" + originalText.replace(" ", "-") + "   ✅");
        } else {
            photo.setCaption("✅   S" + originalText.substring(0, 4) + "-" + originalText.substring(4, 6) + "-0" + originalText.charAt(6) + "   ✅");
        }

        // Отправляем изображение
        execute(photo);
    }

    // Метод для генерации штрихкода
    private byte[] generateBarcodeImage(String data) throws IOException {
        Code128Bean barcodeGenerator = new Code128Bean();

        // Настройки штрихкода
        barcodeGenerator.setHeight(25); // высота
        barcodeGenerator.setModuleWidth(0.3); // ширина модуля
        barcodeGenerator.setQuietZone(10); // отступы

        // Добавляем вертикальные отступы (сверху и снизу)
        barcodeGenerator.setVerticalQuietZone(2.0); // отступы сверху и снизу

        // ОТКЛЮЧАЕМ отображение текста под штрихкодом
        barcodeGenerator.setFontSize(0); // размер шрифта 0

        // Создаем выходной поток для изображения
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BitmapCanvasProvider canvas = new BitmapCanvasProvider(
                outputStream, "image/png", 300, BufferedImage.TYPE_BYTE_BINARY, false, 0);

        // Генерируем штрихкод
        barcodeGenerator.generateBarcode(canvas, data);
        canvas.finish();

        return outputStream.toByteArray();
    }

    // Метод для проверки формата
    private boolean isValidDateFormat(String date) {
        String regex1 = "^\\d{4}-\\d{2}-\\d{2}$";
        String regex2 = "^\\d{7}$";
        String regex3 = "^\\d{4} \\d{2} \\d{2}$";
        if (date.matches(regex1)) {
            return true;
        } else if (date.matches(regex3)) {
            return true;
        }
        return date.matches(regex2);
    }

    // Метод для обработки данных
    private String processDate(String date) {
        String[] parts;
        int row;
        int cell;
        int stage;
        if (date.contains("-")) {
            parts = date.split("-");
            row = Integer.parseInt(parts[0]);
            cell = Integer.parseInt(parts[1]);
            stage = Integer.parseInt(parts[2]);
        } else if (date.contains(" ")) {
            parts = date.split(" ");
            row = Integer.parseInt(parts[0]);
            cell = Integer.parseInt(parts[1]);
            stage = Integer.parseInt(parts[2]);
        } else {
            parts = new String[3];
            parts[0] = date.substring(0, 4); // первые 4 символа
            parts[1] = date.substring(4, 6); // следующие 2 символа  
            parts[2] = date.substring(6);    // последний символ
            row = Integer.parseInt(parts[0]);
            cell = Integer.parseInt(parts[1]);
            stage = Integer.parseInt(parts[2]);
        }

        StringBuilder result = new StringBuilder("92");

        // Умножаем ряд на 4 и в hex
        String hexRow = Integer.toHexString(row * 4);
        if (hexRow.length() == 3) {
            result.append("0");
        }
        result.append(hexRow);

        // Ячейка в hex с ведущим нулем
        String hexCell = Integer.toHexString(cell);
        if (hexCell.length() == 1) {
            result.append("0");
        }
        result.append(hexCell);

        // Добавляем остальные части
        result.append("1");
        String hexStage = Integer.toHexString(stage);
        result.append(hexStage);
        result.append("WRH");

        return result.toString().toUpperCase();
    }

}



