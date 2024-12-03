/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore
 *  io.atlassian.util.concurrent.ThreadFactories
 *  javax.annotation.Nonnull
 *  org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator
 *  org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore
 *  org.apache.commons.compress.parallel.ScatterGatherBackingStore
 *  org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier
 *  org.apache.commons.io.FilenameUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.backuprestore.backup.container;

import com.atlassian.confluence.backuprestore.exception.BackupRestoreException;
import com.atlassian.confluence.impl.backuprestore.backup.container.BackupContainerWriter;
import com.atlassian.confluence.impl.backuprestore.backup.container.BackupContainerWriterFactory;
import com.atlassian.confluence.impl.backuprestore.backup.container.PluginDataWriter;
import com.atlassian.confluence.impl.backuprestore.backup.container.StreamZipWriter;
import com.atlassian.confluence.impl.backuprestore.backup.container.XmlBackupContainerWriter;
import com.atlassian.confluence.impl.pages.attachments.filesystem.AttachmentDataFileSystem;
import com.atlassian.confluence.internal.pages.persistence.AttachmentDaoInternal;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.dc.filestore.impl.filesystem.FilesystemFileStore;
import io.atlassian.util.concurrent.ThreadFactories;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nonnull;
import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.apache.commons.compress.parallel.FileBasedScatterGatherBackingStore;
import org.apache.commons.compress.parallel.ScatterGatherBackingStore;
import org.apache.commons.compress.parallel.ScatterGatherBackingStoreSupplier;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackupContainerWriterFactoryImpl
implements BackupContainerWriterFactory {
    private static final Logger log = LoggerFactory.getLogger(BackupContainerWriterFactoryImpl.class);
    private static final int MIN_NUM_OF_ZIP_THREADS = Integer.getInteger("confluence.backup-restore.min-num-of-zip-threads", 6);
    private static final Integer NUM_OF_ZIP_THREADS = Integer.getInteger("confluence.backup.num-of-zip-threads", Runtime.getRuntime().availableProcessors());
    private final ConfluenceDirectories confluenceDirectories;
    private final AttachmentDataFileSystem attachmentDataFileSystem;
    private final AttachmentDaoInternal attachmentDaoInternal;

    public BackupContainerWriterFactoryImpl(@Nonnull ConfluenceDirectories confluenceDirectories, @Nonnull AttachmentDataFileSystem attachmentDataFileSystem, @Nonnull AttachmentDaoInternal attachmentDaoInternal) {
        this.confluenceDirectories = confluenceDirectories;
        this.attachmentDataFileSystem = attachmentDataFileSystem;
        this.attachmentDaoInternal = attachmentDaoInternal;
    }

    @Override
    public BackupContainerWriter createBackupContainerWriter(String exportContainerFileName) throws BackupRestoreException, IOException {
        String tempDir = this.confluenceDirectories.getTempDirectory().toAbsolutePath().toString();
        File tempDirectory = new File(tempDir);
        if (!tempDirectory.exists() && !tempDirectory.mkdirs()) {
            throw new IOException("Couldn't create export directory " + tempDirectory.getAbsolutePath());
        }
        try {
            File outputFile = BackupContainerWriterFactoryImpl.getCanonicalFilePath(tempDir, FilenameUtils.getName((String)exportContainerFileName)).asJavaFile();
            ParallelScatterZipCreator scatterZipCreator = new ParallelScatterZipCreator(Executors.newFixedThreadPool(Math.max(NUM_OF_ZIP_THREADS, MIN_NUM_OF_ZIP_THREADS), ThreadFactories.namedThreadFactory((String)"backuprestore-parallelScatterZip")), (ScatterGatherBackingStoreSupplier)new ConfluenceBackingStoreSupplier(tempDir, FilenameUtils.getName((String)exportContainerFileName)));
            StreamZipWriter streamZipWriter = new StreamZipWriter(new FileOutputStream(outputFile), scatterZipCreator);
            PluginDataWriter pluginDataWriter = new PluginDataWriter(streamZipWriter);
            return new XmlBackupContainerWriter(outputFile, streamZipWriter, this.attachmentDataFileSystem, pluginDataWriter, this.attachmentDaoInternal);
        }
        catch (IOException e) {
            throw new BackupRestoreException(e);
        }
    }

    static FilesystemPath getCanonicalFilePath(String tempDir, String backupFileName) {
        Path basePath = Path.of(tempDir, new String[0]);
        FilesystemPath fileBasePath = FilesystemFileStore.forPath((Path)basePath);
        return fileBasePath.path(new String[]{backupFileName});
    }

    private static class ConfluenceBackingStoreSupplier
    implements ScatterGatherBackingStoreSupplier {
        final AtomicInteger storeNum = new AtomicInteger(0);
        final String backupFileName;
        final String tempDir;

        public ConfluenceBackingStoreSupplier(String tempDir, String backupFileName) {
            this.tempDir = tempDir;
            this.backupFileName = backupFileName;
        }

        public ScatterGatherBackingStore get() throws IOException {
            FilesystemPath canonicalFilePath = BackupContainerWriterFactoryImpl.getCanonicalFilePath(this.tempDir, this.backupFileName + ".temp" + this.storeNum.incrementAndGet());
            return new FileBasedScatterGatherBackingStore(canonicalFilePath.asJavaFile());
        }
    }
}

