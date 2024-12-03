/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl.fpx;

class SSDirectoryEntry {
    int index;
    String name;
    long size;
    long startSector;
    long SIDLeftSibling;
    long SIDRightSibling;
    long SIDChild;

    public SSDirectoryEntry(int index, String name, long size, long startSector, long SIDLeftSibling, long SIDRightSibling, long SIDChild) {
        this.name = name;
        this.index = index;
        this.size = size;
        this.startSector = startSector;
        this.SIDLeftSibling = SIDLeftSibling;
        this.SIDRightSibling = SIDRightSibling;
        this.SIDChild = SIDChild;
    }

    public String getName() {
        return this.name;
    }

    public long getSize() {
        return this.size;
    }

    public long getStartSector() {
        return this.startSector;
    }

    public long getSIDLeftSibling() {
        return this.SIDLeftSibling;
    }

    public long getSIDRightSibling() {
        return this.SIDRightSibling;
    }

    public long getSIDChild() {
        return this.SIDChild;
    }
}

