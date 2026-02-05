package dev.simplevisuals.util;

/**
 * Заглушка для Discord RPC, чтобы проект собирался даже без внешней библиотеки.
 * Все методы сейчас ничего не делают, но публичный API сохранён.
 */
public class DiscordRichPresenceUtil {
    public static String state;

    public static synchronized void discordrpc() {
        startDiscord(null);
    }

    public static synchronized void startDiscord(String applicationId) {
        // no-op: отключён Discord RPC
    }

    public static synchronized void shutdownDiscord() {
        // no-op
    }
}
