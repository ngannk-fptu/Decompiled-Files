/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import org.apache.jackrabbit.spi.ItemId;
import org.apache.jackrabbit.spi.Path;

public interface ItemInfo {
    public ItemId getId();

    public boolean denotesNode();

    public Path getPath();
}

