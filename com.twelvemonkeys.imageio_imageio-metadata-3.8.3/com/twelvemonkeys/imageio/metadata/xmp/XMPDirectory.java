/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata.xmp;

import com.twelvemonkeys.imageio.metadata.AbstractCompoundDirectory;
import com.twelvemonkeys.imageio.metadata.Directory;
import java.util.Collection;

final class XMPDirectory
extends AbstractCompoundDirectory {
    private final String toolkit;

    public XMPDirectory(Collection<? extends Directory> collection, String string) {
        super(collection);
        this.toolkit = string;
    }

    String getWriterToolkit() {
        return this.toolkit;
    }

    @Override
    public boolean isReadOnly() {
        return super.isReadOnly();
    }
}

