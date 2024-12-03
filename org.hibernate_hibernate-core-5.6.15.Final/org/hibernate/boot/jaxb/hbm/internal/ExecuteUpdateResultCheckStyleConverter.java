/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.internal;

import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;

public class ExecuteUpdateResultCheckStyleConverter {
    public static ExecuteUpdateResultCheckStyle fromXml(String name) {
        return ExecuteUpdateResultCheckStyle.fromExternalName(name);
    }

    public static String toXml(ExecuteUpdateResultCheckStyle style) {
        return style.externalName();
    }
}

