/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.bind.v2.schemagen;

import java.text.MessageFormat;
import java.util.ResourceBundle;

enum Messages {
    ANONYMOUS_TYPE_CYCLE;

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

