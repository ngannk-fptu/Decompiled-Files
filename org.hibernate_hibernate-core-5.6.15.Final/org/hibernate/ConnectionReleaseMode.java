/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import java.util.Locale;
import org.hibernate.internal.util.StringHelper;

public enum ConnectionReleaseMode {
    AFTER_STATEMENT,
    BEFORE_TRANSACTION_COMPLETION,
    AFTER_TRANSACTION,
    ON_CLOSE;


    public static ConnectionReleaseMode parse(String name) {
        return ConnectionReleaseMode.valueOf(name.toUpperCase(Locale.ROOT));
    }

    public static ConnectionReleaseMode interpret(Object setting) {
        if (setting == null) {
            return null;
        }
        if (setting instanceof ConnectionReleaseMode) {
            return (ConnectionReleaseMode)((Object)setting);
        }
        String value = setting.toString();
        if (StringHelper.isEmpty(value)) {
            return null;
        }
        if (value.equalsIgnoreCase("auto")) {
            return null;
        }
        return ConnectionReleaseMode.parse(value);
    }
}

