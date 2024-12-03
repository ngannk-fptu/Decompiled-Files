/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dc.filestore.impl.filesystem;

import java.nio.file.Path;
import java.util.Optional;

class FileStorePathTraversalException
extends RuntimeException {
    private FileStorePathTraversalException(Path root, Path path) {
        super(String.format("Path '%s' is not a subpath of root '%s'", path, root));
    }

    static Path sanitise(Path root, Path path) {
        return Optional.of(path.normalize()).filter(p -> p.startsWith(root)).orElseThrow(() -> new FileStorePathTraversalException(root, path));
    }
}

