/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.benryan.conversion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentDataTempFile
implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(AttachmentDataTempFile.class);
    private final Path file;

    public AttachmentDataTempFile(Path file) {
        this.file = file;
    }

    public Path getFile() {
        return this.file;
    }

    @Override
    public void close() throws Exception {
        log.debug("Deleting temp data file {}", (Object)this.file);
        try {
            Files.delete(this.file);
        }
        catch (IOException e) {
            log.warn("Failed to delete temporary attachment conversion file {}", (Object)this.file, (Object)e);
        }
    }
}

