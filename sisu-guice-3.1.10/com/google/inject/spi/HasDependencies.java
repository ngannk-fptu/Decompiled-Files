/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.spi;

import com.google.inject.spi.Dependency;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface HasDependencies {
    public Set<Dependency<?>> getDependencies();
}

