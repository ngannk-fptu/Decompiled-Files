/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.internal;

import java.util.Locale;
import org.hibernate.engine.OptimisticLockStyle;

public class OptimisticLockStyleConverter {
    public static OptimisticLockStyle fromXml(String name) {
        return OptimisticLockStyle.valueOf(name == null ? null : name.toUpperCase(Locale.ENGLISH));
    }

    public static String toXml(OptimisticLockStyle lockMode) {
        return lockMode == null ? null : lockMode.name().toLowerCase(Locale.ENGLISH);
    }
}

