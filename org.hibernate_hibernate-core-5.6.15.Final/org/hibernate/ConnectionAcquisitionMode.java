/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate;

import org.hibernate.internal.util.StringHelper;

public enum ConnectionAcquisitionMode {
    IMMEDIATELY,
    AS_NEEDED;


    public static ConnectionAcquisitionMode interpret(String value) {
        if (value != null && ("immediate".equalsIgnoreCase(value) || "immediately".equalsIgnoreCase(value))) {
            return IMMEDIATELY;
        }
        return AS_NEEDED;
    }

    public static ConnectionAcquisitionMode interpret(Object setting) {
        if (setting == null) {
            return null;
        }
        if (setting instanceof ConnectionAcquisitionMode) {
            return (ConnectionAcquisitionMode)((Object)setting);
        }
        String value = setting.toString();
        if (StringHelper.isEmpty(value)) {
            return null;
        }
        return ConnectionAcquisitionMode.interpret(value);
    }
}

