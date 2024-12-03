/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Comparator;

public class TemporalAmountComparator
implements Comparator<TemporalAmount> {
    @Override
    public int compare(TemporalAmount o1, TemporalAmount o2) {
        int result = 0;
        if (!o1.getClass().equals(o2.getClass())) {
            boolean o2datebased;
            boolean o1datebased = o1.getUnits().stream().anyMatch(u -> u.isDateBased());
            if (o1datebased != (o2datebased = o2.getUnits().stream().anyMatch(u -> u.isDateBased()))) {
                result = o1datebased ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }
        } else {
            if (o1 instanceof Period && o2 instanceof Period) {
                Period p1 = (Period)o1;
                Period p2 = (Period)o2;
                result = p1.isNegative() != p2.isNegative() ? (p1.isNegative() ? Integer.MIN_VALUE : Integer.MAX_VALUE) : (p1.getYears() != p2.getYears() ? p1.getYears() - p2.getYears() : (p1.getMonths() != p2.getMonths() ? p1.getMonths() - p2.getMonths() : p1.getDays() - p2.getDays()));
                if (p1.isNegative()) {
                    return -result;
                }
                return result;
            }
            result = Duration.from(o1).compareTo(Duration.from(o2));
        }
        return result;
    }
}

