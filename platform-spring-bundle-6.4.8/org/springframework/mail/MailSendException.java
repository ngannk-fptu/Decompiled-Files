/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.mail;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.mail.MailException;
import org.springframework.util.ObjectUtils;

public class MailSendException
extends MailException {
    private final transient Map<Object, Exception> failedMessages;
    @Nullable
    private final Exception[] messageExceptions;

    public MailSendException(String msg) {
        this(msg, null);
    }

    public MailSendException(String msg, @Nullable Throwable cause) {
        super(msg, cause);
        this.failedMessages = new LinkedHashMap<Object, Exception>();
        this.messageExceptions = null;
    }

    public MailSendException(@Nullable String msg, @Nullable Throwable cause, Map<Object, Exception> failedMessages) {
        super(msg, cause);
        this.failedMessages = new LinkedHashMap<Object, Exception>(failedMessages);
        this.messageExceptions = failedMessages.values().toArray(new Exception[0]);
    }

    public MailSendException(Map<Object, Exception> failedMessages) {
        this(null, null, failedMessages);
    }

    public final Map<Object, Exception> getFailedMessages() {
        return this.failedMessages;
    }

    public final Exception[] getMessageExceptions() {
        return this.messageExceptions != null ? this.messageExceptions : new Exception[]{};
    }

    @Override
    @Nullable
    public String getMessage() {
        if (ObjectUtils.isEmpty(this.messageExceptions)) {
            return super.getMessage();
        }
        StringBuilder sb = new StringBuilder();
        String baseMessage = super.getMessage();
        if (baseMessage != null) {
            sb.append(baseMessage).append(". ");
        }
        sb.append("Failed messages: ");
        for (int i2 = 0; i2 < this.messageExceptions.length; ++i2) {
            Exception subEx = this.messageExceptions[i2];
            sb.append(subEx.toString());
            if (i2 >= this.messageExceptions.length - 1) continue;
            sb.append("; ");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        if (ObjectUtils.isEmpty(this.messageExceptions)) {
            return super.toString();
        }
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("; message exceptions (").append(this.messageExceptions.length).append(") are:");
        for (int i2 = 0; i2 < this.messageExceptions.length; ++i2) {
            Exception subEx = this.messageExceptions[i2];
            sb.append('\n').append("Failed message ").append(i2 + 1).append(": ");
            sb.append(subEx);
        }
        return sb.toString();
    }

    @Override
    public void printStackTrace(PrintStream ps) {
        if (ObjectUtils.isEmpty(this.messageExceptions)) {
            super.printStackTrace(ps);
        } else {
            ps.println(super.toString() + "; message exception details (" + this.messageExceptions.length + ") are:");
            for (int i2 = 0; i2 < this.messageExceptions.length; ++i2) {
                Exception subEx = this.messageExceptions[i2];
                ps.println("Failed message " + (i2 + 1) + ":");
                subEx.printStackTrace(ps);
            }
        }
    }

    @Override
    public void printStackTrace(PrintWriter pw) {
        if (ObjectUtils.isEmpty(this.messageExceptions)) {
            super.printStackTrace(pw);
        } else {
            pw.println(super.toString() + "; message exception details (" + this.messageExceptions.length + ") are:");
            for (int i2 = 0; i2 < this.messageExceptions.length; ++i2) {
                Exception subEx = this.messageExceptions[i2];
                pw.println("Failed message " + (i2 + 1) + ":");
                subEx.printStackTrace(pw);
            }
        }
    }
}

