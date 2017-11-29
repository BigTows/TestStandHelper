package ru.uniteller.teststandhelper.util;

import com.intellij.openapi.diagnostic.Logger;

public class LogHelper {
    private static final Logger LOG = Logger.getInstance(LogHelper.class);
    private boolean debug = true;
    private Class clazz;

    public LogHelper(Class clazz) {
        this.clazz = clazz;
    }


    /**
     * Вывод INFO сообщение
     * @param message Сообщение с информацией
     */
    public void i(String message) {
        LOG.info(getMessageWithPrefix(message));
    }

    /**
     * Вывод сообщения об ошибке
     *
     * @param message Сообщение содержащие информацию об ошибке
     */
    public void e(String message) {
        LOG.error(getMessageWithPrefix(message));
    }

    /**
     * Вывод DEBUG сообщения
     * @param message Сообщение с информацией
     */
    public void d(String message) {
        if (debug)
            LOG.info(getMessageWithPrefix(message));
    }

    private String getMessageWithPrefix(String message) {
        return clazz.getSimpleName() + ": " + message;
    }

}
