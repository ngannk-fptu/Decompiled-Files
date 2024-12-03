/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.tar;

import java.io.IOException;
import org.apache.tools.tar.TarConstants;
import org.apache.tools.tar.TarUtils;

public class TarArchiveSparseEntry
implements TarConstants {
    private boolean isExtended;

    public TarArchiveSparseEntry(byte[] headerBuf) throws IOException {
        int offset = 0;
        this.isExtended = TarUtils.parseBoolean(headerBuf, offset += 504);
    }

    public boolean isExtended() {
        return this.isExtended;
    }
}

