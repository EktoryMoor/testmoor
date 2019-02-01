/*
 * Copyright (c) 2019.
 * @autor Kate Moor
 */

package moor.example.testmoor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Собственно БОТ
 */
@Component
@EnableAutoConfiguration
public class Bot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    /**
     * имя бота
     */
    @Value("${bot.name}")
    private String name;
    /**
     * токен бота
     */
    @Value("${bot.token}")
    private String token;
    /**
     * ID текущего собеседника
     */
    private String currentChatId;

    /**
     * Конструктор бота для запуска под прокси
     *
     * @param botOptions объект с данными прокси
     */
    public Bot(DefaultBotOptions botOptions) {
        super(botOptions);
    }

    /**
     * Метод для приема сообщений
     *
     * @param update Содержит сообщение от пользователя
     */
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            String chatId = update.getMessage().getChatId().toString();
            currentChatId = chatId;
            switch (message) {
                case "hello":
                    sendMessageBot(chatId, "Привет!");
                    break;
                case "/help":
                    SendMessage msg = new SendMessage();
                    msg.setChatId(chatId);
                    msg.setText("Бот калькуляции. Умеет обрабатывать и отвечать результатом " +
                            "на выражение вида: 4 + 4 * 2 или (4 + 4) * 2");
                    // для красоты - формируем меню с возможными командами
                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                    keyboardMarkup.setResizeKeyboard(true);
                    List keyboard = new ArrayList<>();
                    KeyboardRow row = new KeyboardRow();
                    row.add("hello");
                    row.add("(2 + 9 / ( 6 - 3 ) ) * 5,5");
                    keyboard.add(row);
                    keyboardMarkup.setKeyboard(keyboard);
                    msg.setReplyMarkup(keyboardMarkup);
                    try {
                        execute(msg);
                    } catch (TelegramApiException e) {
                        logger.error(Arrays.toString(e.getStackTrace()));
                    }
                    break;
                default:
                    sendMessageBot(chatId, Calculator.calculate(message));
            }

        }

    }

    /**
     * Формирование простого сообщения и его отправка
     *
     * @param chatId  id чата
     * @param message строка сообщения
     */
    public synchronized void sendMessageBot(String chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            logger.error(Arrays.toString(e.getStackTrace()));
        }
    }

    /**
     * Возвращает имя бота, требуется для регистрации на сервере Telegram
     *
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return name;
    }

    /**
     * Возвращает токен бота, требуется для регистрации на сервере Telegram
     *
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return token;
    }
    /**
     * Событие при закрытии сессии с ботом
     *
     */
    @Override
    public void onClosing()  {
        sendMessageBot(currentChatId, "До свидания.");
        super.onClosing();
    }

    /**
     * Информация о запуске бота
     *
     */
    @PostConstruct
    public void start() {
        logger.info("API бота username: {}, token: {} запущено.", name, token);
    }
}
