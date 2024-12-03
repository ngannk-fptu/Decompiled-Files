/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dc.filestore.api.compat;

import java.io.File;
import java.nio.file.Path;

public interface FilesystemAccess {
    public Path asJavaPath();

    default public File asJavaFile() {
        return this.asJavaPath().toFile();
    }

    @Deprecated
    default public Path getFilesystemPath() {
        return this.asJavaPath();
    }

    public static class AccessDenied
    extends RuntimeException {
        public AccessDenied(Path path) {
            super("Accedd denied to filesystem path " + path);
        }
    }
}

