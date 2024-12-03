/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.transform.rfc5545;

public interface Rfc5545Rule<T> {
    public void applyTo(T var1);

    public Class<T> getSupportedType();
}

