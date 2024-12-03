/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.engine.messageinterpolation;

import java.util.Locale;

public class LocalizedMessage {
    private final String message;
    private final Locale locale;
    private final int hashCode;

    public LocalizedMessage(String message, Locale locale) {
        this.message = message;
        this.locale = locale;
        this.hashCode = this.buildHashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LocalizedMessage that = (LocalizedMessage)o;
        if (!this.message.equals(that.message)) {
            return false;
        }
        return this.locale.equals(that.locale);
    }

    public int hashCode() {
        return this.hashCode;
    }

    private int buildHashCode() {
        int result = this.message.hashCode();
        result = 31 * result + this.locale.hashCode();
        return result;
    }
}

