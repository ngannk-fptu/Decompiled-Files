/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.io;

import java.io.CharConversionException;
import java.util.Locale;
import org.apache.xerces.util.MessageFormatter;

public final class MalformedByteSequenceException
extends CharConversionException {
    static final long serialVersionUID = 8436382245048328739L;
    private MessageFormatter fFormatter;
    private Locale fLocale;
    private String fDomain;
    private String fKey;
    private Object[] fArguments;
    private String fMessage;

    public MalformedByteSequenceException(MessageFormatter messageFormatter, Locale locale, String string, String string2, Object[] objectArray) {
        this.fFormatter = messageFormatter;
        this.fLocale = locale;
        this.fDomain = string;
        this.fKey = string2;
        this.fArguments = objectArray;
    }

    public String getDomain() {
        return this.fDomain;
    }

    public String getKey() {
        return this.fKey;
    }

    public Object[] getArguments() {
        return this.fArguments;
    }

    @Override
    public synchronized String getMessage() {
        if (this.fMessage == null) {
            this.fMessage = this.fFormatter.formatMessage(this.fLocale, this.fKey, this.fArguments);
            this.fFormatter = null;
            this.fLocale = null;
        }
        return this.fMessage;
    }
}

