/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import org.apache.jackrabbit.spi.Name;

public interface ChildInfo {
    public Name getName();

    public String getUniqueID();

    public int getIndex();
}

