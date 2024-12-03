/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.time;

import com.thoughtworks.xstream.converters.time.AbstractChronoLocalDateConverter;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ThaiBuddhistChronology;
import java.time.chrono.ThaiBuddhistDate;
import java.time.chrono.ThaiBuddhistEra;
import java.util.Collections;

public class ThaiBuddhistDateConverter
extends AbstractChronoLocalDateConverter<ThaiBuddhistEra> {
    @Override
    public boolean canConvert(Class type) {
        return ThaiBuddhistDate.class == type;
    }

    @Override
    public Object fromString(String str) {
        return this.parseChronoLocalDate(str, "Thai Buddhist", Collections.singleton(ThaiBuddhistChronology.INSTANCE));
    }

    @Override
    protected ChronoLocalDate chronoLocalDateOf(ThaiBuddhistEra era, int prolepticYear, int month, int dayOfMonth) {
        return ThaiBuddhistDate.of(prolepticYear, month, dayOfMonth);
    }

    @Override
    protected ThaiBuddhistEra eraOf(String id) {
        return ThaiBuddhistEra.valueOf(id);
    }
}

