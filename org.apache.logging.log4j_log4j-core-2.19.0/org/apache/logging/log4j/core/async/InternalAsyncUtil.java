/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.message.AsynchronouslyFormattable
 *  org.apache.logging.log4j.message.Message
 */
package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.apache.logging.log4j.message.Message;

public class InternalAsyncUtil {
    public static Message makeMessageImmutable(Message msg) {
        if (msg != null && !InternalAsyncUtil.canFormatMessageInBackground(msg)) {
            msg.getFormattedMessage();
        }
        return msg;
    }

    private static boolean canFormatMessageInBackground(Message message) {
        return Constants.FORMAT_MESSAGES_IN_BACKGROUND || message.getClass().isAnnotationPresent(AsynchronouslyFormattable.class);
    }
}

