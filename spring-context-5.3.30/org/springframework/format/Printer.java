/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format;

import java.util.Locale;

@FunctionalInterface
public interface Printer<T> {
    public String print(T var1, Locale var2);
}

