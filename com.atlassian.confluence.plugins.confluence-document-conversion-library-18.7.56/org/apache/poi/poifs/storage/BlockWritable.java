/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.storage;

import java.io.IOException;
import java.io.OutputStream;

public interface BlockWritable {
    public void writeBlocks(OutputStream var1) throws IOException;
}

