/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.directory.Directory$Entry
 *  com.atlassian.gadgets.directory.Directory$EntryScope
 */
package com.atlassian.gadgets.directory.internal;

import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.directory.Directory;
import java.net.URI;

public interface DirectoryEntryProvider {
    public Iterable<Directory.Entry<?>> entries(GadgetRequestContext var1, Directory.EntryScope var2);

    public boolean contains(URI var1);
}

