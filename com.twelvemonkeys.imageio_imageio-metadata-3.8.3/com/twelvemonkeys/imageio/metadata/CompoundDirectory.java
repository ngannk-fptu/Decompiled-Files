/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata;

import com.twelvemonkeys.imageio.metadata.Directory;

public interface CompoundDirectory
extends Directory {
    public Directory getDirectory(int var1);

    public int directoryCount();
}

