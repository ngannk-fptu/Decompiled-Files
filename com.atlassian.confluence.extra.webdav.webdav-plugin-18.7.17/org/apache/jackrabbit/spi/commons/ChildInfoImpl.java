/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons;

import java.io.Serializable;
import org.apache.jackrabbit.spi.ChildInfo;
import org.apache.jackrabbit.spi.Name;

public class ChildInfoImpl
implements ChildInfo,
Serializable {
    private final Name name;
    private final String uniqueId;
    private final int index;

    public ChildInfoImpl(Name name, String uniqueId, int index) {
        this.name = name;
        this.uniqueId = uniqueId;
        this.index = index;
    }

    @Override
    public Name getName() {
        return this.name;
    }

    @Override
    public String getUniqueID() {
        return this.uniqueId;
    }

    @Override
    public int getIndex() {
        return this.index;
    }
}

