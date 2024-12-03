/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io.ole2;

import com.twelvemonkeys.io.SeekableInputStream;
import com.twelvemonkeys.io.ole2.CompoundDocument;
import com.twelvemonkeys.io.ole2.CorruptDocumentException;
import java.io.DataInput;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public final class Entry
implements Comparable<Entry> {
    String name;
    byte type;
    byte nodeColor;
    int prevDId;
    int nextDId;
    int rootNodeDId;
    long createdTimestamp;
    long modifiedTimestamp;
    int startSId;
    int streamSize;
    CompoundDocument document;
    Entry parent;
    SortedSet<Entry> children;
    public static final int LENGTH = 128;
    static final int EMPTY = 0;
    static final int USER_STORAGE = 1;
    static final int USER_STREAM = 2;
    static final int LOCK_BYTES = 3;
    static final int PROPERTY = 4;
    static final int ROOT_STORAGE = 5;
    private static final SortedSet<Entry> NO_CHILDREN = Collections.unmodifiableSortedSet(new TreeSet());

    private Entry() {
    }

    static Entry readEntry(DataInput dataInput) throws IOException {
        Entry entry = new Entry();
        entry.read(dataInput);
        return entry;
    }

    private void read(DataInput dataInput) throws IOException {
        byte[] byArray = new byte[64];
        dataInput.readFully(byArray);
        short s = dataInput.readShort();
        this.name = new String(byArray, 0, s - 2, Charset.forName("UTF-16LE"));
        this.type = dataInput.readByte();
        this.nodeColor = dataInput.readByte();
        this.prevDId = dataInput.readInt();
        this.nextDId = dataInput.readInt();
        this.rootNodeDId = dataInput.readInt();
        if (dataInput.skipBytes(20) != 20) {
            throw new CorruptDocumentException();
        }
        this.createdTimestamp = CompoundDocument.toJavaTimeInMillis(dataInput.readLong());
        this.modifiedTimestamp = CompoundDocument.toJavaTimeInMillis(dataInput.readLong());
        this.startSId = dataInput.readInt();
        this.streamSize = dataInput.readInt();
        dataInput.readInt();
    }

    public boolean isRoot() {
        return this.type == 5;
    }

    public boolean isDirectory() {
        return this.type == 1;
    }

    public boolean isFile() {
        return this.type == 2;
    }

    public String getName() {
        return this.name;
    }

    public SeekableInputStream getInputStream() throws IOException {
        if (!this.isFile()) {
            return null;
        }
        return this.document.getInputStreamForSId(this.startSId, this.streamSize);
    }

    public long length() {
        if (!this.isFile()) {
            return 0L;
        }
        return this.streamSize;
    }

    public long created() {
        return this.createdTimestamp;
    }

    public long lastModified() {
        return this.modifiedTimestamp;
    }

    public Entry getParentEntry() {
        return this.parent;
    }

    public Entry getChildEntry(String string) throws IOException {
        if (this.isFile() || this.rootNodeDId == -1) {
            return null;
        }
        Entry entry = new Entry();
        entry.name = string;
        entry.parent = this;
        SortedSet<Entry> sortedSet = this.getChildEntries().tailSet(entry);
        return sortedSet.first();
    }

    public SortedSet<Entry> getChildEntries() throws IOException {
        if (this.children == null) {
            this.children = this.isFile() || this.rootNodeDId == -1 ? NO_CHILDREN : Collections.unmodifiableSortedSet(this.document.getEntries(this.rootNodeDId, this));
        }
        return this.children;
    }

    public String toString() {
        return "\"" + this.name + "\" (" + (this.isFile() ? "Document" : (this.isDirectory() ? "Directory" : "Root")) + (this.parent != null ? ", parent: \"" + this.parent.getName() + "\"" : "") + (this.isFile() ? "" : ", children: " + (this.children != null ? String.valueOf(this.children.size()) : "(unknown)")) + ", SId=" + this.startSId + ", length=" + this.streamSize + ")";
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof Entry)) {
            return false;
        }
        Entry entry = (Entry)object;
        return this.name.equals(entry.name) && (this.parent == entry.parent || this.parent != null && this.parent.equals(entry.parent));
    }

    public int hashCode() {
        return this.name.hashCode() ^ this.startSId;
    }

    @Override
    public int compareTo(Entry entry) {
        if (this == entry) {
            return 0;
        }
        int n = this.name.length() - entry.name.length();
        if (n != 0) {
            return n;
        }
        return this.name.compareTo(entry.name);
    }
}

