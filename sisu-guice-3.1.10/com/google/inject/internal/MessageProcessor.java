/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal;

import com.google.inject.Guice;
import com.google.inject.internal.AbstractProcessor;
import com.google.inject.internal.Errors;
import com.google.inject.spi.Message;
import java.util.logging.Level;
import java.util.logging.Logger;

final class MessageProcessor
extends AbstractProcessor {
    private static final Logger logger = Logger.getLogger(Guice.class.getName());

    MessageProcessor(Errors errors) {
        super(errors);
    }

    public Boolean visit(Message message) {
        if (message.getCause() != null) {
            String rootMessage = MessageProcessor.getRootMessage(message.getCause());
            logger.log(Level.INFO, "An exception was caught and reported. Message: " + rootMessage, message.getCause());
        }
        this.errors.addMessage(message);
        return true;
    }

    public static String getRootMessage(Throwable t) {
        Throwable cause = t.getCause();
        return cause == null ? t.toString() : MessageProcessor.getRootMessage(cause);
    }
}

