/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core._DelayedConversionToString;

public class _DelayedGetMessage
extends _DelayedConversionToString {
    public _DelayedGetMessage(Throwable exception) {
        super(exception);
    }

    @Override
    protected String doConversion(Object obj) {
        String message = ((Throwable)obj).getMessage();
        return message == null || message.length() == 0 ? "[No exception message]" : message;
    }
}

