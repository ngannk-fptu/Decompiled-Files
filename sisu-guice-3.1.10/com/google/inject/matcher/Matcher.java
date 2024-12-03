/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.matcher;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Matcher<T> {
    public boolean matches(T var1);

    public Matcher<T> and(Matcher<? super T> var1);

    public Matcher<T> or(Matcher<? super T> var1);
}

