/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.apache.commons.io.FilenameUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.restore.stash;

import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStash;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashFactory;
import com.atlassian.confluence.impl.backuprestore.restore.stash.ImportedObjectsStashImpl;
import com.atlassian.confluence.impl.backuprestore.restore.stash.StashObjectsReaderImpl;
import com.atlassian.confluence.impl.backuprestore.restore.stash.StashObjectsSerialiser;
import com.atlassian.confluence.impl.backuprestore.restore.stash.StashObjectsWriterImpl;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import java.io.File;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImportedObjectsStashFactoryImpl
implements ImportedObjectsStashFactory {
    private static final Logger log = LoggerFactory.getLogger(ImportedObjectsStashFactoryImpl.class);
    public static final int FIRST_ITERATION = 0;
    private final StashObjectsSerialiser stashObjectsSerialiser;
    private final ConfluenceDirectories confluenceDirectories;

    public ImportedObjectsStashFactoryImpl(@Nonnull StashObjectsSerialiser stashObjectsSerialiser, @Nonnull ConfluenceDirectories confluenceDirectories) {
        this.stashObjectsSerialiser = stashObjectsSerialiser;
        this.confluenceDirectories = confluenceDirectories;
    }

    @Override
    public ImportedObjectsStash createStash(String name) {
        return this.createStash(name, 0);
    }

    @Override
    public ImportedObjectsStash createStash(String name, int iteration) {
        File stashFile = this.getStashFile(name, iteration);
        log.debug("Stash file: {}", (Object)stashFile.getAbsolutePath());
        StashObjectsWriterImpl writer = new StashObjectsWriterImpl(this.stashObjectsSerialiser, stashFile);
        StashObjectsReaderImpl reader = new StashObjectsReaderImpl(writer, this.stashObjectsSerialiser, stashFile);
        return new ImportedObjectsStashImpl(name, iteration, writer, reader);
    }

    private File getStashFile(String name, int iteration) {
        String tempDir = this.confluenceDirectories.getTempDirectory().toAbsolutePath().toString();
        String fileName = name + "." + iteration + "." + UUID.randomUUID();
        return new File(tempDir, FilenameUtils.getName((String)fileName));
    }
}

