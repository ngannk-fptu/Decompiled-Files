/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import org.apache.jackrabbit.spi.Path;

public interface ItemId {
    public boolean denotesNode();

    public String getUniqueID();

    public Path getPath();
}

