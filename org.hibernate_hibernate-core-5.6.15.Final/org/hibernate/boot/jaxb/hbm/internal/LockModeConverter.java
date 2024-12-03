/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.internal;

import org.hibernate.LockMode;

public class LockModeConverter {
    public static LockMode fromXml(String name) {
        return LockMode.fromExternalForm(name);
    }

    public static String toXml(LockMode lockMode) {
        return lockMode.toExternalForm();
    }
}

