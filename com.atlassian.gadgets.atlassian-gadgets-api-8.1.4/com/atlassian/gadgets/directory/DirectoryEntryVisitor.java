/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gadgets.directory;

import com.atlassian.gadgets.directory.Directory;

public interface DirectoryEntryVisitor<T> {
    public T visit(Directory.OpenSocialDirectoryEntry var1);

    public T visit(Directory.DashboardDirectoryEntry var1);
}

