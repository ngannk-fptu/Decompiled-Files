/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.atlassian.gadgets.opensocial.model;

import net.jcip.annotations.Immutable;

@Immutable
public final class Address {
    private final String address;

    public Address(String address) {
        if (address == null) {
            throw new NullPointerException("address parameter to Address must not be null");
        }
        this.address = address.intern();
    }

    public String value() {
        return this.address;
    }

    public String toString() {
        return this.address;
    }

    public static Address valueOf(String address) {
        return new Address(address);
    }

    public boolean equals(Object obj) {
        return obj instanceof Address && this.address.equals(((Address)obj).value());
    }

    public int hashCode() {
        return this.address.hashCode();
    }
}

