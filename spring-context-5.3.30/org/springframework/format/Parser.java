/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.format;

import java.text.ParseException;
import java.util.Locale;

@FunctionalInterface
public interface Parser<T> {
    public T parse(String var1, Locale var2) throws ParseException;
}

