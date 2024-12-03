/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.dc.filestore.api.compat.FilesystemPath
 *  com.atlassian.plugin.PluginAccessor
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.index.lucene.snapshot;

import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.impl.journal.JournalStateStore;
import com.atlassian.confluence.internal.index.lucene.LuceneIndexHelper;
import com.atlassian.confluence.internal.index.lucene.snapshot.IndexSnapshotError;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshot;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshotException;
import com.atlassian.confluence.internal.index.lucene.snapshot.LuceneIndexSnapshotManager;
import com.atlassian.confluence.plugin.descriptor.IndexRecovererModuleDescriptor;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.util.zip.FileUnzipper;
import com.atlassian.dc.filestore.api.compat.FilesystemPath;
import com.atlassian.plugin.PluginAccessor;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLuceneIndexSnapshotManager
implements LuceneIndexSnapshotManager {
    static final String INDEX_TEMP_DIRECTORY_SUFFIX = "_rstr_tmp";
    private static final Logger log = LoggerFactory.getLogger(DefaultLuceneIndexSnapshotManager.class);
    private static final String SNAPSHOT_DIRECTORY_NAME = "index-snapshots";
    private static final String SNAPSHOT_FILE_NAME_PREFIX = "IndexSnapshot_";
    private static final String SNAPSHOT_TEMP_FILE_NAME_PREFIX = "index_snapshot_tmp_";
    private final PluginAccessor pluginAccessor;
    private final BootstrapManager bootstrapManager;
    private final FilesystemPath sharedHome;
    private final JournalStateStore journalStateStore;
    private final Function<Path, Optional<Long>> indexVersionReader;

    public DefaultLuceneIndexSnapshotManager(PluginAccessor pluginAccessor, BootstrapManager bootstrapManager, FilesystemPath sharedHome, JournalStateStore journalStateStore) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.bootstrapManager = Objects.requireNonNull(bootstrapManager);
        this.sharedHome = Objects.requireNonNull(sharedHome);
        this.journalStateStore = Objects.requireNonNull(journalStateStore);
        this.indexVersionReader = LuceneIndexHelper::getIndexVersion;
    }

    public DefaultLuceneIndexSnapshotManager(PluginAccessor pluginAccessor, BootstrapManager bootstrapManager, FilesystemPath sharedHome, JournalStateStore journalStateStore, Function<Path, Optional<Long>> indexVersionReader) {
        this.pluginAccessor = Objects.requireNonNull(pluginAccessor);
        this.bootstrapManager = Objects.requireNonNull(bootstrapManager);
        this.sharedHome = Objects.requireNonNull(sharedHome);
        this.journalStateStore = Objects.requireNonNull(journalStateStore);
        this.indexVersionReader = Objects.requireNonNull(indexVersionReader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public LuceneIndexSnapshot create(JournalIdentifier journalIdentifier) {
        Path zippedSnapshot;
        Path snapshotTmpDir;
        long currentIndexSize;
        Objects.requireNonNull(journalIdentifier);
        log.info("Creating a new snapshot for index {}", (Object)journalIdentifier.getJournalName());
        Optional<IndexRecovererModuleDescriptor> recoverer = this.getRecovererDescriptor(journalIdentifier);
        if (!recoverer.isPresent()) {
            throw new LuceneIndexSnapshotException(String.format("There is no recoverer module for index %s. Cannot create snapshot.", journalIdentifier.getJournalName()), IndexSnapshotError.UNKNOWN_INDEX);
        }
        Path tempRootDir = Paths.get(this.bootstrapManager.getFilePathProperty("struts.multipart.saveDir"), new String[0]);
        if (!Files.exists(tempRootDir, new LinkOption[0])) {
            try {
                Files.createDirectories(tempRootDir, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new LuceneIndexSnapshotException("Cannot create temp directory to create snapshots", IndexSnapshotError.NOT_WRITABLE_LOCAL_HOME, e);
            }
        }
        try {
            currentIndexSize = LuceneIndexHelper.sizeOfIndex(this.getIndexDirectory(recoverer.get()));
        }
        catch (IOException e) {
            throw new LuceneIndexSnapshotException("Cannot obtain size of index " + recoverer.get().getIndexName(), e);
        }
        this.hasEnoughSpaceOrFail(tempRootDir, currentIndexSize * 2L, "Not enough space to create index snapshot locally", IndexSnapshotError.NOT_ENOUGH_DISK_SPACE_LOCAL_HOME);
        this.hasEnoughSpaceOrFail(this.sharedHome.asJavaPath(), currentIndexSize, "Not enough space to create index snapshot in shared home", IndexSnapshotError.NOT_ENOUGH_DISK_SPACE_SHARED_HOME);
        try {
            snapshotTmpDir = Files.createTempDirectory(tempRootDir, SNAPSHOT_TEMP_FILE_NAME_PREFIX, new FileAttribute[0]);
            zippedSnapshot = Files.createTempFile(tempRootDir, SNAPSHOT_TEMP_FILE_NAME_PREFIX, ".zip", new FileAttribute[0]);
        }
        catch (IOException ioe) {
            throw new LuceneIndexSnapshotException("Cannot create temporary files to hold index snapshot", IndexSnapshotError.NOT_WRITABLE_LOCAL_HOME, ioe);
        }
        try {
            long currentJournalEntryId = this.journalStateStore.getMostRecentId(journalIdentifier);
            try {
                recoverer.get().getModule().snapshot(snapshotTmpDir.toFile());
            }
            catch (IOException ioe) {
                throw new LuceneIndexSnapshotException(String.format("Error creating snapshot for index %s", journalIdentifier.getJournalName()), ioe);
            }
            try {
                com.atlassian.core.util.FileUtils.createZipFile((File)snapshotTmpDir.toFile(), (File)zippedSnapshot.toFile());
            }
            catch (Exception e) {
                throw new LuceneIndexSnapshotException("Cannot compress index snapshot", e);
            }
            this.moveSnapshotToSharedHome(zippedSnapshot, this.getSnapshotRoot(), this.getSnapshotFileName(journalIdentifier, currentJournalEntryId));
            log.info("A new snapshot has been created for index {} with journalEntryId of {}", (Object)journalIdentifier.getJournalName(), (Object)currentJournalEntryId);
            LuceneIndexSnapshot luceneIndexSnapshot = new LuceneIndexSnapshot(journalIdentifier, currentJournalEntryId);
            return luceneIndexSnapshot;
        }
        finally {
            try {
                Files.deleteIfExists(zippedSnapshot);
            }
            catch (IOException ioe) {
                log.error("Failed to cleanup temp snapshot file at {}", (Object)zippedSnapshot.toAbsolutePath(), (Object)ioe);
            }
            com.atlassian.core.util.FileUtils.deleteDir((File)snapshotTmpDir.toFile());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void restore(LuceneIndexSnapshot snapshot) {
        long snapshotSize;
        long currentIndexSize;
        Optional<Path> snapshotFile;
        Objects.requireNonNull(snapshot);
        log.info("Restoring index snapshot {}", (Object)snapshot);
        Optional<IndexRecovererModuleDescriptor> recoverer = this.getRecovererDescriptor(snapshot.getJournalIdentifier());
        if (!recoverer.isPresent()) {
            throw new LuceneIndexSnapshotException(String.format("There is no recoverer module for index %s. Cannot restore snapshot.", snapshot.getJournalIdentifier().getJournalName()), IndexSnapshotError.UNKNOWN_INDEX);
        }
        Path indexDirectory = this.getIndexDirectory(recoverer.get());
        if (!Files.exists(indexDirectory, new LinkOption[0])) {
            try {
                Files.createDirectories(indexDirectory, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new LuceneIndexSnapshotException("Error creating index directory", IndexSnapshotError.NOT_WRITABLE_LOCAL_HOME, e);
            }
        }
        if (!(snapshotFile = this.getFile(snapshot)).isPresent()) {
            throw new LuceneIndexSnapshotException(String.format("Snapshot %s not found", snapshot), IndexSnapshotError.SNAPSHOT_NOT_EXIST);
        }
        try {
            currentIndexSize = LuceneIndexHelper.sizeOfIndex(indexDirectory);
        }
        catch (IOException e) {
            log.warn("Cannot determine size of index " + recoverer.get().getIndexName(), (Throwable)e);
            currentIndexSize = 0L;
        }
        try {
            snapshotSize = Files.size(snapshotFile.get());
        }
        catch (IOException e) {
            throw new LuceneIndexSnapshotException("Cannot determine size of index snapshot " + snapshot, e);
        }
        this.hasEnoughSpaceOrFail(indexDirectory, Math.max(currentIndexSize, snapshotSize), "Not enough space to restore snapshot", IndexSnapshotError.NOT_ENOUGH_DISK_SPACE_LOCAL_HOME);
        Path indexTempDirectory = indexDirectory.resolveSibling(recoverer.get().getIndexDirName() + INDEX_TEMP_DIRECTORY_SUFFIX);
        if (Files.exists(indexTempDirectory, new LinkOption[0])) {
            log.debug("Index temp directory {} already exists, deleting it", (Object)indexTempDirectory.toAbsolutePath());
            com.atlassian.core.util.FileUtils.deleteDir((File)indexTempDirectory.toFile());
        }
        try {
            log.debug("Extracting index snapshot {} into local home", (Object)snapshot);
            try {
                new FileUnzipper(snapshotFile.get().toFile(), indexTempDirectory.toFile()).unzip();
            }
            catch (IOException e) {
                throw new LuceneIndexSnapshotException("Error extracting snapshot to local home", e);
            }
            if (!this.indexVersionReader.apply(indexTempDirectory).isPresent()) {
                throw new LuceneIndexSnapshotException(String.format("Invalid index snapshot %s", snapshot));
            }
            recoverer.get().getModule().reset(() -> {
                try {
                    log.info("Replacing index {} with snapshot {}", (Object)((IndexRecovererModuleDescriptor)recoverer.get()).getIndexName(), (Object)snapshot);
                    LuceneIndexHelper.replaceIndexDirectory(indexDirectory, indexTempDirectory);
                    log.info("Setting journalEntryId of index {} to that of snapshot {}", (Object)((IndexRecovererModuleDescriptor)recoverer.get()).getIndexName(), (Object)snapshot);
                    this.journalStateStore.setMostRecentId(snapshot.getJournalIdentifier(), snapshot.getJournalEntryId());
                }
                catch (IOException e) {
                    throw new LuceneIndexSnapshotException("Error replacing existing index with new snapshot", e);
                }
            });
            log.info("Snapshot {} has been successfully restored", (Object)snapshot);
        }
        finally {
            com.atlassian.core.util.FileUtils.deleteDir((File)indexTempDirectory.toFile());
        }
    }

    @Override
    public List<LuceneIndexSnapshot> findForJournal(JournalIdentifier journalIdentifier) {
        Objects.requireNonNull(journalIdentifier);
        ArrayList<LuceneIndexSnapshot> snapshots = new ArrayList<LuceneIndexSnapshot>();
        Comparator<LuceneIndexSnapshot> snapshotJournalIdComparator = Comparator.comparingLong(LuceneIndexSnapshot::getJournalEntryId).reversed();
        Pattern snapshotFileNamePattern = Pattern.compile(Pattern.quote(SNAPSHOT_FILE_NAME_PREFIX + journalIdentifier.getJournalName()) + "_([0-9]+)\\.zip");
        try (Stream<Path> snapshotPaths = Files.list(this.getSnapshotRoot());){
            snapshotPaths.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).map(Path::getFileName).filter(Objects::nonNull).forEach(snapshotFileName -> {
                Matcher snapshotMatcher = snapshotFileNamePattern.matcher(snapshotFileName.toString());
                if (snapshotMatcher.matches()) {
                    snapshots.add(new LuceneIndexSnapshot(journalIdentifier, Long.parseLong(snapshotMatcher.group(1))));
                }
            });
        }
        catch (IOException e) {
            log.warn("Error iterating snapshot directory: {}", (Object)e.getMessage());
            log.debug("Full stack trace of the problem", (Throwable)e);
        }
        snapshots.sort(snapshotJournalIdComparator);
        return snapshots;
    }

    @Override
    public Optional<LuceneIndexSnapshot> find(JournalIdentifier journalIdentifier, long journalEntryId) {
        Objects.requireNonNull(journalIdentifier);
        return this.getSnapshotFile(journalIdentifier, journalEntryId).map(snapshotFile -> new LuceneIndexSnapshot(journalIdentifier, journalEntryId));
    }

    @Override
    public Optional<LuceneIndexSnapshot> find(JournalIdentifier journalIdentifier, long journalEntryId, long timeoutMs) throws InterruptedException {
        Objects.requireNonNull(journalIdentifier);
        long deadline = System.currentTimeMillis() + timeoutMs;
        do {
            Optional<LuceneIndexSnapshot> snapshot;
            if ((snapshot = this.find(journalIdentifier, journalEntryId)).isPresent()) {
                return snapshot;
            }
            log.debug("Index snapshot of index {} with journalEntryId {} not found. Trying again in 10 seconds", (Object)journalIdentifier.getJournalName(), (Object)journalEntryId);
            Thread.sleep(10000L);
        } while (System.currentTimeMillis() < deadline);
        log.debug("Index snapshot of index {} with journalEntryId {} not found after {} seconds. Giving up.", new Object[]{journalIdentifier.getJournalName(), journalEntryId, timeoutMs / 1000L});
        return Optional.empty();
    }

    @Override
    public void delete(LuceneIndexSnapshot snapshot) throws IOException {
        Objects.requireNonNull(snapshot);
        log.debug("Deleting snapshot {}", (Object)snapshot);
        Optional<Path> snapshotFile = this.getSnapshotFile(snapshot.getJournalIdentifier(), snapshot.getJournalEntryId());
        if (snapshotFile.isPresent()) {
            Files.delete(snapshotFile.get());
        }
    }

    @Override
    public Optional<Path> getFile(LuceneIndexSnapshot snapshot) {
        Objects.requireNonNull(snapshot);
        return this.getSnapshotFile(snapshot.getJournalIdentifier(), snapshot.getJournalEntryId());
    }

    private Optional<Path> getSnapshotFile(JournalIdentifier journalIdentifier, long journalEntryId) {
        Path snapshotFile = this.getSnapshotRoot().resolve(this.getSnapshotFileName(journalIdentifier, journalEntryId));
        return Files.exists(snapshotFile, new LinkOption[0]) ? Optional.of(snapshotFile) : Optional.empty();
    }

    private void hasEnoughSpaceOrFail(Path target, long requiredFreeSpace, String errorMessage, IndexSnapshotError errorCode) {
        try {
            long availableSpace = Files.getFileStore(target.toRealPath(new LinkOption[0])).getUsableSpace();
            if (availableSpace < requiredFreeSpace) {
                throw new LuceneIndexSnapshotException(String.format(errorMessage + ". Target directory: %s. Available space: %s. Required free space: %s", target.toAbsolutePath(), FileUtils.byteCountToDisplaySize((long)availableSpace), FileUtils.byteCountToDisplaySize((long)requiredFreeSpace)));
            }
        }
        catch (IOException e) {
            throw new LuceneIndexSnapshotException("Cannot get usable space of directory " + target.toAbsolutePath(), errorCode, e);
        }
    }

    private void moveSnapshotToSharedHome(Path zippedSnapshot, Path snapshotRoot, String snapshotFileName) {
        if (!Files.exists(snapshotRoot, new LinkOption[0])) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Creating snapshot directory at {}", (Object)snapshotRoot.toAbsolutePath());
                }
                Files.createDirectories(snapshotRoot, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new LuceneIndexSnapshotException("Cannot create snapshot directory", IndexSnapshotError.NOT_WRITABLE_SHARED_HOME, e);
            }
        }
        Path snapshotFile = snapshotRoot.resolve(snapshotFileName);
        try {
            Files.move(zippedSnapshot, snapshotFile, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ioe) {
            throw new LuceneIndexSnapshotException("Cannot store new snapshot under shared home", IndexSnapshotError.NOT_WRITABLE_SHARED_HOME, ioe);
        }
    }

    private Optional<IndexRecovererModuleDescriptor> getRecovererDescriptor(JournalIdentifier journalIdentifier) {
        return this.pluginAccessor.getEnabledModuleDescriptorsByClass(IndexRecovererModuleDescriptor.class).stream().filter(recover -> recover.getJournalId().equals((Object)journalIdentifier)).findFirst();
    }

    private Path getSnapshotRoot() {
        return this.sharedHome.asJavaPath().resolve(SNAPSHOT_DIRECTORY_NAME);
    }

    private String getSnapshotFileName(JournalIdentifier journalIdentifier, long journalEntryId) {
        return String.format("IndexSnapshot_%s_%d.zip", journalIdentifier.getJournalName(), journalEntryId);
    }

    private Path getIndexDirectory(IndexRecovererModuleDescriptor indexRecoverer) {
        Path indexDirectory = Paths.get(this.bootstrapManager.getFilePathProperty("lucene.index.dir"), new String[0]);
        return indexDirectory.resolve(indexRecoverer.getIndexDirName());
    }
}

