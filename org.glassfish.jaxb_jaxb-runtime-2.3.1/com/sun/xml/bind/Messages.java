/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.bind;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages {
    FAILED_TO_INITIALE_DATATYPE_FACTORY;

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

