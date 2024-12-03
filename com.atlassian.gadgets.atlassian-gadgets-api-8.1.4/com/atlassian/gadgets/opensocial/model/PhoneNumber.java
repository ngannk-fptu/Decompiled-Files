/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.opensocial.model;

import net.jcip.annotations.Immutable;

@Immutable
public final class PhoneNumber {
    private final String number;

    public PhoneNumber(String number) {
        if (number == null) {
            throw new NullPointerException("number parameter to PhoneNumber must not be null");
        }
        this.number = number;
    }

    public String value() {
        return this.number;
    }

    public String toString() {
        return this.number;
    }

    public static PhoneNumber valueOf(String number) {
        return new PhoneNumber(number);
    }

    public boolean equals(Object obj) {
        return obj instanceof PhoneNumber && this.number.equals(((PhoneNumber)obj).value());
    }

    public int hashCode() {
        return this.number.hashCode();
    }
}

