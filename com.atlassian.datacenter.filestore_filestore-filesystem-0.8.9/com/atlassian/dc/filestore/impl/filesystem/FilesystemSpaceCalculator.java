/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.DataSize
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.dc.filestore.impl.filesystem;

import com.atlassian.dc.filestore.api.DataSize;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class FilesystemSpaceCalculator {
    private static final Logger log = LoggerFactory.getLogger(FilesystemSpaceCalculator.class);
    private final FileStore fileStore;

    static Optional<FilesystemSpaceCalculator> createFor(Path path) {
        try {
            return Optional.of(Files.getFileStore(path)).map(FilesystemSpaceCalculator::new);
        }
        catch (IOException e) {
            log.error("Failed to determine filesystem for path {}", (Object)path, (Object)e);
            return Optional.empty();
        }
    }

    private FilesystemSpaceCalculator(FileStore fileStore) {
        this.fileStore = fileStore;
    }

    Optional<DataSize> getAvailableSpace() {
        return this.getSpace(FileStore::getUsableSpace);
    }

    Optional<DataSize> getTotalSpace() {
        return this.getSpace(FileStore::getTotalSpace);
    }

    private Optional<DataSize> getSpace(SpaceFunction spaceFunction) {
        try {
            return Optional.of(spaceFunction.calculate(this.fileStore)).map(DataSize::ofBytes);
        }
        catch (IOException e) {
            log.error("Failed to determine available space on filesystem {}", (Object)this.fileStore, (Object)e);
            return Optional.empty();
        }
    }

    @FunctionalInterface
    private static interface SpaceFunction {
        public long calculate(FileStore var1) throws IOException;
    }
}

