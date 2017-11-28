package ru.uniteller.teststandhelper.util;

import com.intellij.openapi.diagnostic.Logger;

public class LogHelper {
    private static final Logger LOG = Logger.getInstance(LogHelper.class);
    private boolean debug = true;
    private Class clazz;

    public LogHelper(Class clazz) {
        this.clazz = clazz;
    }


    public void i(String message) {
        LOG.info(getMessageWithPrefix(message));
    }

    public void e(String message) {
        LOG.error(getMessageWithPrefix(message));
    }

    private String getMessageWithPrefix(String message) {
        return clazz.getSimpleName() + ": " + message;
    }

}
