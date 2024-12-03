/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.bind.api;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages {
    ARGUMENT_CANT_BE_NULL;

    private static final ResourceBundle rb;

    public String toString() {
        return this.format(new Object[0]);
    }

    public String format(Object ... args) {
        return MessageFormat.format(rb.getString(this.name()), args);
    }

    static {
        rb = ResourceBundle.getBundle(Messages.class.getName());
    }
}

