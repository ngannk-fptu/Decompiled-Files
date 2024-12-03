/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.util.function;

import org.osgi.annotation.versioning.ConsumerType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@ConsumerType
public interface Function<T, R> {
    public R apply(T var1);
}

