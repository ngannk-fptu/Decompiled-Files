/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 */
package com.atlassian.confluence.impl.filestore;

import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import java.util.Optional;

public interface HomePathPlaceholderResolver {
    public Optional<FilesystemPath> resolveFileStorePlaceHolders(String var1);
}

