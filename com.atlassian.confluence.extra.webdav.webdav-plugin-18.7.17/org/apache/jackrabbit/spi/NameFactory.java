/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import org.apache.jackrabbit.spi.Name;

public interface NameFactory {
    public Name create(String var1, String var2) throws IllegalArgumentException;

    public Name create(String var1) throws IllegalArgumentException;
}

