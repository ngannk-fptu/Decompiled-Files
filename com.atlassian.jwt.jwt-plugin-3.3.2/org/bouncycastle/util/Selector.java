/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Selector<T>
extends Cloneable {
    public boolean match(T var1);

    public Object clone();
}

