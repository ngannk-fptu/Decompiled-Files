/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.pkix.util;

import java.util.Locale;
import org.bouncycastle.pkix.util.ErrorBundle;

public class LocalizedException
extends Exception {
    protected ErrorBundle message;
    private Throwable cause;

    public LocalizedException(ErrorBundle message) {
        super(message.getText(Locale.getDefault()));
        this.message = message;
    }

    public LocalizedException(ErrorBundle message, Throwable throwable) {
        super(message.getText(Locale.getDefault()));
        this.message = message;
        this.cause = throwable;
    }

    public ErrorBundle getErrorMessage() {
        return this.message;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

