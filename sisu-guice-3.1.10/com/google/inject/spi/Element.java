/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.Binder;
import com.google.inject.spi.ElementVisitor;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Element {
    public Object getSource();

    public <T> T acceptVisitor(ElementVisitor<T> var1);

    public void applyTo(Binder var1);
}

