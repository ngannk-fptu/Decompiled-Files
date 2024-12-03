/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import net.fortuna.ical4j.model.WeekDay;
import net.fortuna.ical4j.util.CompatibilityHints;

public class WeekDayList
extends ArrayList<WeekDay>
implements Serializable {
    private static final long serialVersionUID = 1243262497035300445L;

    public WeekDayList() {
    }

    public WeekDayList(WeekDay ... weekDays) {
        this.addAll(Arrays.asList(weekDays));
    }

    public WeekDayList(int initialCapacity) {
        super(initialCapacity);
    }

    public WeekDayList(String aString) {
        boolean outlookCompatibility = CompatibilityHints.isHintEnabled("ical4j.compatibility.outlook");
        StringTokenizer t = new StringTokenizer(aString, ",");
        while (t.hasMoreTokens()) {
            if (outlookCompatibility) {
                this.add(new WeekDay(t.nextToken().replaceAll(" ", "")));
                continue;
            }
            this.add(new WeekDay(t.nextToken()));
        }
    }

    @Override
    public final String toString() {
        return this.stream().map(WeekDay::toString).collect(Collectors.joining(","));
    }
}

