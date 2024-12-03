/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckReturnValue
 */
package com.atlassian.dc.filestore.api.compat;

import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.dc.filestore.api.compat.FilesystemAccess;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.CheckReturnValue;

public interface FilesystemPath
extends FileStore.Path,
FilesystemAccess {
    @CheckReturnValue
    public Pruner deleteFileAndPrune() throws IOException;

    @Override
    public FilesystemPath path(String ... var1);

    public Stream<FilesystemPath> getFileDescendents() throws IOException;

    public Optional<FilesystemPath> getParent();

    public static interface Pruner {
        public void untilReach(FilesystemPath var1);
    }
}

