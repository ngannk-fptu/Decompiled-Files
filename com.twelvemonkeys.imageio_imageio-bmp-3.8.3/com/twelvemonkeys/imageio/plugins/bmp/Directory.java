/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.plugins.bmp;

import com.twelvemonkeys.imageio.plugins.bmp.DirectoryEntry;
import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

class Directory {
    private final List<DirectoryEntry> entries;

    private Directory(int n) {
        this.entries = Arrays.asList(new DirectoryEntry[n]);
    }

    public static Directory read(int n, int n2, DataInput dataInput) throws IOException {
        Directory directory = new Directory(n2);
        directory.readEntries(n, dataInput);
        return directory;
    }

    private void readEntries(int n, DataInput dataInput) throws IOException {
        for (int i = 0; i < this.entries.size(); ++i) {
            this.entries.set(i, DirectoryEntry.read(n, dataInput));
        }
    }

    public DirectoryEntry getEntry(int n) {
        return this.entries.get(n);
    }

    public int count() {
        return this.entries.size();
    }

    public String toString() {
        return String.format("%s%s", this.getClass().getSimpleName(), this.entries);
    }
}

