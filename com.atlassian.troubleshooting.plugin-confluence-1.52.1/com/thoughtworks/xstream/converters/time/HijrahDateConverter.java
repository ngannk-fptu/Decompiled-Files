/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.time;

import com.thoughtworks.xstream.converters.time.AbstractChronoLocalDateConverter;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.HijrahChronology;
import java.time.chrono.HijrahDate;
import java.time.chrono.HijrahEra;
import java.util.HashSet;
import java.util.Set;

public class HijrahDateConverter
extends AbstractChronoLocalDateConverter<HijrahEra> {
    private final Set<Chronology> hijrahChronologies = new HashSet<Chronology>();

    public HijrahDateConverter() {
        Set<Chronology> chronologies = Chronology.getAvailableChronologies();
        for (Chronology chronology : chronologies) {
            if (!(chronology instanceof HijrahChronology)) continue;
            this.hijrahChronologies.add(chronology);
        }
    }

    @Override
    public boolean canConvert(Class type) {
        return HijrahDate.class == type;
    }

    @Override
    public Object fromString(String str) {
        return this.parseChronoLocalDate(str, "Hijrah", this.hijrahChronologies);
    }

    @Override
    protected ChronoLocalDate chronoLocalDateOf(HijrahEra era, int prolepticYear, int month, int dayOfMonth) {
        return era != null ? HijrahDate.of(prolepticYear, month, dayOfMonth) : null;
    }

    @Override
    protected HijrahEra eraOf(String id) {
        return HijrahEra.valueOf(id);
    }
}

