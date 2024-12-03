/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.jboss.jandex.Index;
import org.jboss.jandex.IndexReaderImpl;
import org.jboss.jandex.IndexReaderV1;
import org.jboss.jandex.IndexReaderV2;
import org.jboss.jandex.PackedDataInputStream;
import org.jboss.jandex.UnsupportedVersion;

public final class IndexReader {
    private static final int MAGIC = -1161945323;
    private PackedDataInputStream input;
    private int version = -1;
    private IndexReaderImpl reader;

    public IndexReader(InputStream input) {
        this.input = new PackedDataInputStream(new BufferedInputStream(input));
    }

    public Index read() throws IOException {
        if (this.version == -1) {
            this.readVersion();
        }
        return this.reader.read(this.version);
    }

    private void initReader(int version) throws IOException {
        IndexReaderImpl reader;
        if (version >= 2 && version <= 3) {
            reader = new IndexReaderV1(this.input);
        } else if (version >= 6 && version <= 10) {
            reader = new IndexReaderV2(this.input);
        } else {
            this.input.close();
            throw new UnsupportedVersion("Can't read index version " + version + "; this IndexReader only supports index versions " + 2 + "-" + 3 + "," + 6 + "-" + 10);
        }
        this.reader = reader;
    }

    public int getDataVersion() throws IOException {
        if (this.version == -1) {
            this.readVersion();
        }
        return this.reader.toDataVersion(this.version);
    }

    public int getIndexVersion() throws IOException {
        if (this.version == -1) {
            this.readVersion();
        }
        return this.version;
    }

    private void readVersion() throws IOException {
        if (this.input.readInt() != -1161945323) {
            this.input.close();
            throw new IllegalArgumentException("Not a jandex index");
        }
        this.version = this.input.readUnsignedByte();
        this.initReader(this.version);
    }
}

