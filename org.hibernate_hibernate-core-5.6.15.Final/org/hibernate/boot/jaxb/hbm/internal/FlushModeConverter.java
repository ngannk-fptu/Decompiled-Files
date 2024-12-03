/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.internal;

import java.util.Locale;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;

public class FlushModeConverter {
    public static FlushMode fromXml(String name) {
        if (name == null) {
            return null;
        }
        if ("never".equalsIgnoreCase(name)) {
            return FlushMode.MANUAL;
        }
        if ("auto".equalsIgnoreCase(name)) {
            return FlushMode.AUTO;
        }
        if ("always".equalsIgnoreCase(name)) {
            return FlushMode.ALWAYS;
        }
        throw new HibernateException("Unrecognized flush mode : " + name);
    }

    public static String toXml(FlushMode mode) {
        if (mode == null) {
            return null;
        }
        if (mode == FlushMode.MANUAL) {
            return "never";
        }
        return mode.name().toLowerCase(Locale.ENGLISH);
    }
}

