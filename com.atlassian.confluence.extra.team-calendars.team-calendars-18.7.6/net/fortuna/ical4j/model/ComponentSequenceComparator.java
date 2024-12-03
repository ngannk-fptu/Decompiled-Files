/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.util.Comparator;
import java.util.Optional;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.Sequence;

public class ComponentSequenceComparator
implements Comparator<Component> {
    @Override
    public int compare(Component o1, Component o2) {
        Sequence sequence2;
        int retVal = 0;
        Sequence defaultSequence = new Sequence(0);
        Sequence sequence1 = Optional.ofNullable((Sequence)o1.getProperty("SEQUENCE")).orElse(defaultSequence);
        retVal = sequence1.compareTo(sequence2 = Optional.ofNullable((Sequence)o2.getProperty("SEQUENCE")).orElse(defaultSequence));
        if (retVal == 0) {
            DtStamp defaultDtStamp = new DtStamp(new DateTime(0L));
            DtStamp dtStamp1 = Optional.ofNullable((DtStamp)o1.getProperty("DTSTAMP")).orElse(defaultDtStamp);
            DtStamp dtStamp2 = Optional.ofNullable((DtStamp)o2.getProperty("DTSTAMP")).orElse(defaultDtStamp);
            retVal = dtStamp1.compareTo(dtStamp2);
        }
        return retVal;
    }
}

