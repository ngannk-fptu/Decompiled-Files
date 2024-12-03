/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.filesystem;

import org.apache.poi.poifs.filesystem.DirectoryEntry;

public interface Entry {
    public String getName();

    public boolean isDirectoryEntry();

    public boolean isDocumentEntry();

    public DirectoryEntry getParent();

    public boolean delete();

    public boolean renameTo(String var1);
}

