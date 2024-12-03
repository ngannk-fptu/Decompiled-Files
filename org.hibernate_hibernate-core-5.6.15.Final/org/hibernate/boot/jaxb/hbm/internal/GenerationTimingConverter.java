/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.jaxb.hbm.internal;

import java.util.Locale;
import org.hibernate.tuple.GenerationTiming;

public class GenerationTimingConverter {
    public static GenerationTiming fromXml(String name) {
        return GenerationTiming.parseFromName(name);
    }

    public static String toXml(GenerationTiming generationTiming) {
        return null == generationTiming ? null : generationTiming.name().toLowerCase(Locale.ENGLISH);
    }
}

