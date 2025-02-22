/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.message.Message
 *  org.apache.logging.log4j.util.StringBuilderFormattable
 */
package org.apache.logging.log4j.core.impl;

import java.util.Arrays;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.StringBuilderFormattable;

public final class MementoMessage
implements Message,
StringBuilderFormattable {
    private final String formattedMessage;
    private final String format;
    private final Object[] parameters;

    public MementoMessage(String formattedMessage, String format, Object[] parameters) {
        this.formattedMessage = formattedMessage;
        this.format = format;
        this.parameters = parameters;
    }

    public String getFormattedMessage() {
        return this.formattedMessage;
    }

    public String getFormat() {
        return this.format;
    }

    public Object[] getParameters() {
        return this.parameters;
    }

    public Throwable getThrowable() {
        return null;
    }

    public void formatTo(StringBuilder buffer) {
        buffer.append(this.formattedMessage);
    }

    public String toString() {
        return "MementoMessage{formattedMessage='" + this.formattedMessage + '\'' + ", format='" + this.format + '\'' + ", parameters=" + Arrays.toString(this.parameters) + '}';
    }
}

