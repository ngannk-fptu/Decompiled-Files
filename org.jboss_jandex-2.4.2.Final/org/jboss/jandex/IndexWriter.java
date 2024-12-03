/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.IOException;
import java.io.OutputStream;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexWriterImpl;
import org.jboss.jandex.IndexWriterV1;
import org.jboss.jandex.IndexWriterV2;
import org.jboss.jandex.UnsupportedVersion;

public final class IndexWriter {
    private final OutputStream out;

    public IndexWriter(OutputStream out) {
        this.out = out;
    }

    public int write(Index index) throws IOException {
        return this.write(index, 10);
    }

    @Deprecated
    public int write(Index index, byte version) throws IOException {
        return this.write(index, version & 0xFF);
    }

    public int write(Index index, int version) throws IOException {
        IndexWriterImpl writer = this.getWriter(version);
        if (writer == null) {
            throw new UnsupportedVersion("Can't write index version " + version + "; this IndexWriter only supports index versions " + 1 + "-" + 3 + "," + 6 + "-" + 10);
        }
        return writer.write(index, version);
    }

    private IndexWriterImpl getWriter(int version) {
        if (version >= 1 && version <= 3) {
            return new IndexWriterV1(this.out);
        }
        if (version >= 6 && version <= 10) {
            return new IndexWriterV2(this.out);
        }
        return null;
    }
}

