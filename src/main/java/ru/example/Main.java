package ru.example;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.example.bot.TelegramBot;

public class Main {
    public static void main(String[] args) {
        try {
            // Создаем экземпляр API телеграма
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            // Регистрируем нашего бота
            botsApi.registerBot(new TelegramBot());
            System.out.println("Бот запущен и готов к работе!");

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}