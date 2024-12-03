/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.stream.Collectors;
import net.fortuna.ical4j.util.Numbers;

public class NumberList
extends ArrayList<Integer>
implements Serializable {
    private static final long serialVersionUID = -1667481795613729889L;
    private final int minValue;
    private final int maxValue;
    private final boolean allowsNegativeValues;

    public NumberList() {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }

    public NumberList(int minValue, int maxValue, boolean allowsNegativeValues) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.allowsNegativeValues = allowsNegativeValues;
    }

    public NumberList(String aString) {
        this(aString, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }

    public NumberList(String aString, int minValue, int maxValue, boolean allowsNegativeValues) {
        this(minValue, maxValue, allowsNegativeValues);
        StringTokenizer t = new StringTokenizer(aString, ",");
        while (t.hasMoreTokens()) {
            int value = Numbers.parseInt(t.nextToken());
            this.add(value);
        }
    }

    @Override
    public final boolean add(Integer aNumber) {
        int abs = aNumber;
        if ((abs >> 31 | -abs >>> 31) < 0) {
            if (!this.allowsNegativeValues) {
                throw new IllegalArgumentException("Negative value not allowed: " + aNumber);
            }
            abs = Math.abs(abs);
        }
        if (abs < this.minValue || abs > this.maxValue) {
            throw new IllegalArgumentException("Value not in range [" + this.minValue + ".." + this.maxValue + "]: " + aNumber);
        }
        return super.add(aNumber);
    }

    @Override
    public final String toString() {
        return this.stream().map(Object::toString).collect(Collectors.joining(","));
    }
}

