/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.config.bootstrap.AtlassianBootstrapManager
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection
 *  com.atlassian.confluence.upgrade.AbstractUpgradeTask
 *  com.atlassian.confluence.upgrade.UpgradeException
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.upgradetask;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.config.bootstrap.AtlassianBootstrapManager;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.impl.journal.JournalStateStore;
import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import com.atlassian.confluence.upgrade.AbstractUpgradeTask;
import com.atlassian.confluence.upgrade.UpgradeException;
import com.atlassian.confluence.upgrade.upgradetask.splitindex.FileInfo;
import com.atlassian.confluence.upgrade.upgradetask.splitindex.IndexInfo;
import com.atlassian.confluence.upgrade.upgradetask.splitindex.SplitIndexFileHelper;
import com.atlassian.confluence.upgrade.upgradetask.splitindex.SplitIndexPhase;
import com.atlassian.confluence.upgrade.upgradetask.splitindex.SplitIndexSnapshotManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.web.UrlBuilder;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SplitIndexUpgradeTask
extends AbstractUpgradeTask {
    public static final String BUILD_NUMBER = "8503";
    public static final String DESCRIPTION = "Migrates single shared index into split index for content and changes.";
    public static final String CONTENT = "content";
    public static final String CHANGE = "change";
    public static final URL KB_URL = UrlBuilder.createURL("https://confluence.atlassian.com/x/r3zmOw");
    private static final Logger log = LoggerFactory.getLogger(SplitIndexUpgradeTask.class);
    private static final String NOT_ENOUGH_SPACE = "johnson.message.split.index.insufficient.disk.free.space";
    private static final String NOT_AN_INDEX = "johnson.message.split.index.folder.not.an.index";
    private static final String CHANGES_EXISTS_NOT_LUCENE_DIRECTORY = "johnson.message.split.index.change.exists.not.lucene.directory";
    private static final String CHANGES_CANNOT_CREATE = "johnson.message.split.index.change.cannot.create";
    private static final ToLongFunction<File> USABLE_SPACE_FUNCTION = File::getUsableSpace;
    private static final boolean ATTEMPT_INDEX_RECOVERY = Integer.getInteger("confluence.cluster.index.recovery.num.attempts", 1) > 0;
    private final ILuceneConnection contentConnection;
    private final ILuceneConnection changesConnection;
    private final I18NBeanFactory i18NBeanFactory;
    private final JournalStateStore journalStateStore;
    private final JournalIdentifier contentJournalIdentifier;
    private final JournalIdentifier changeJournalIdentifier;
    private final SplitIndexSnapshotManager snapshotManager;
    private final ToLongFunction<File> freeSpaceFunction;

    public SplitIndexUpgradeTask(@NonNull ILuceneConnection contentConnection, @NonNull ILuceneConnection changesConnection, @NonNull I18NBeanFactory i18NBeanFactory, @NonNull JournalStateStore journalStateStore, @NonNull JournalIdentifier contentJournalIdentifier, @NonNull JournalIdentifier changeJournalIdentifier, @NonNull SplitIndexSnapshotManager snapshotManager) {
        this(contentConnection, changesConnection, i18NBeanFactory, journalStateStore, contentJournalIdentifier, changeJournalIdentifier, snapshotManager, null);
    }

    SplitIndexUpgradeTask(@NonNull ILuceneConnection contentConnection, @NonNull ILuceneConnection changesConnection, @NonNull I18NBeanFactory i18NBeanFactory, @NonNull JournalStateStore journalStateStore, @NonNull JournalIdentifier contentJournalIdentifier, @NonNull JournalIdentifier changeJournalIdentifier, @NonNull SplitIndexSnapshotManager snapshotManager, @Nullable ToLongFunction<File> function) {
        this.contentConnection = Objects.requireNonNull(contentConnection);
        this.changesConnection = Objects.requireNonNull(changesConnection);
        this.i18NBeanFactory = Objects.requireNonNull(i18NBeanFactory);
        this.journalStateStore = Objects.requireNonNull(journalStateStore);
        this.contentJournalIdentifier = Objects.requireNonNull(contentJournalIdentifier);
        this.changeJournalIdentifier = Objects.requireNonNull(changeJournalIdentifier);
        this.snapshotManager = Objects.requireNonNull(snapshotManager);
        this.freeSpaceFunction = function == null ? USABLE_SPACE_FUNCTION : function;
    }

    private File getContentIndexFile() {
        AtlassianBootstrapManager bootstrapManager = BootstrapUtils.getBootstrapManager();
        String root = bootstrapManager.getFilePathProperty("lucene.index.dir");
        log.info("Confluence index is located in {}", (Object)root);
        return new File(root);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void validate() throws Exception {
        log.info("Split index validation task started.");
        File content = this.getContentIndexFile();
        SplitIndexFileHelper contentManager = new SplitIndexFileHelper(content, CONTENT);
        try {
            boolean needToRestoreSnapshot = this.snapshotManager.needsSnapshotRestoration(content);
            boolean hasSuitableContentSnapshot = this.snapshotManager.hasSuitableContentSnapshot();
            if (ATTEMPT_INDEX_RECOVERY && needToRestoreSnapshot && hasSuitableContentSnapshot) {
                long estimatedRestoredSize = this.snapshotManager.estimatedUnpackedLatestSnapshotSizeBytes();
                boolean hasSuitableChangeSnapshot = this.snapshotManager.hasSuitableChangeSnapshot();
                long coefficient = hasSuitableChangeSnapshot ? 1L : 3L;
                long requiredSpace = coefficient * estimatedRestoredSize;
                long usableSpace = this.freeSpaceFunction.applyAsLong(content);
                log.info("We are attempting to restore the snapshot. Checking if there is enough disk space. SnapshotSize={}, RequiredSpace={}, AvailableSpace={}", new Object[]{estimatedRestoredSize, requiredSpace, usableSpace});
                this.checkEnoughSpaceOnDisk(requiredSpace, usableSpace);
                log.info("There is enough disk space. There is nothing else to validate.");
                return;
            }
            if (!contentManager.getIndex().exists() || !SplitIndexFileHelper.hasFiles(content)) {
                log.info("Index folders were removed or emptied, forcing full reindex rather than split index");
                return;
            }
            Map<String, FileInfo> sourceMap = contentManager.collectIndexFileInfos();
            if (log.isDebugEnabled()) {
                sourceMap.forEach((key, fi) -> log.debug("file {}, size {}, absolute path: {}", new Object[]{fi.getName(), fi.getSize(), fi.getFile().getAbsolutePath()}));
            }
            long total = SplitIndexFileHelper.getTotalSize(sourceMap.values());
            long required = 3L * total;
            long usable = this.freeSpaceFunction.applyAsLong(content);
            this.checkEnoughSpaceOnDisk(required, usable);
            this.validateChanges(new File(contentManager.getIndex(), CHANGE));
            if (SplitIndexFileHelper.hasFiles(content)) {
                IndexInfo contentInfo;
                if (!SplitIndexFileHelper.isLuceneIndex(content)) {
                    log.info("Configured main index at {} is not a valid lucene index and contains non-index files. Upgrade cannot continue, please remove the files or configure the index to point to a valid index.", (Object)content.getAbsolutePath());
                    String files = sourceMap.keySet().stream().map(key -> String.format("   - %s", key)).collect(Collectors.joining("\n"));
                    log.info("Invalid index files: \n{}", (Object)files);
                    this.raiseJohnsonFatal(NOT_AN_INDEX, content.getAbsolutePath());
                }
                if ((contentInfo = contentManager.refreshIndexInfo()).isOldVersion()) {
                    log.info("Configured main index at {} is an older file format Lucene index. Upgrade task will remove the index and force a full reindex.", (Object)content.getAbsolutePath());
                } else if (contentInfo.isValid()) {
                    log.info("Found upgradable index at {}, can proceed to split index", (Object)content.getAbsolutePath());
                }
            }
        }
        finally {
            log.info("Split index validation completed.");
        }
    }

    private void checkEnoughSpaceOnDisk(long requiredSpace, long usableSpace) throws UpgradeException {
        if (usableSpace <= requiredSpace) {
            String usableFormatted = SplitIndexFileHelper.bytesToHumanReadable(usableSpace);
            String requiredFormatted = SplitIndexFileHelper.bytesToHumanReadable(requiredSpace);
            log.error("Not enough diskspace, required: {}, usable: {}", (Object)requiredFormatted, (Object)usableFormatted);
            this.raiseJohnsonFatal(NOT_ENOUGH_SPACE, usableFormatted, requiredFormatted);
        }
    }

    private void validateChanges(File changes) throws UpgradeException {
        if (changes.exists()) {
            if (!changes.isDirectory()) {
                log.error("{} exists but is not a directory, cannot continue", (Object)changes.getAbsolutePath());
                this.raiseJohnsonFatal(CHANGES_EXISTS_NOT_LUCENE_DIRECTORY, changes);
            }
            SplitIndexFileHelper changesManager = new SplitIndexFileHelper(changes, CHANGE);
            IndexInfo changesInfo = changesManager.refreshIndexInfo();
            if (SplitIndexFileHelper.hasFiles(changes) && !changesInfo.isValid()) {
                log.error("{} exists and has files, but is not a valid lucene index, cannot continue with splitting index", (Object)changes.getAbsolutePath());
                this.raiseJohnsonFatal(CHANGES_EXISTS_NOT_LUCENE_DIRECTORY, changes.getAbsolutePath());
            }
        }
    }

    public void doUpgrade() throws UpgradeException {
        log.info("Starting the upgrade task with acquiring write locks on Lucene connections");
        try {
            log.info("Locks have been acquired successfully");
            this.restoreSnapshotIfNeeded();
            AtomicBoolean successfullyUpgraded = new AtomicBoolean();
            this.contentConnection.reset(() -> this.changesConnection.reset(() -> {
                try {
                    boolean success = this.upgradeUnderConnectionWriteLock();
                    successfullyUpgraded.set(success);
                    log.info("Releasing connection write locks and resetting the connections");
                }
                catch (UpgradeException e) {
                    throw new RuntimeException(e);
                }
            }));
            if (successfullyUpgraded.get()) {
                this.takeNewSnapshotIfNeeded();
            }
        }
        catch (RuntimeException e) {
            if (e.getCause() instanceof UpgradeException) {
                throw (UpgradeException)e.getCause();
            }
            throw e;
        }
    }

    private void takeNewSnapshotIfNeeded() {
        try {
            File content = this.getContentIndexFile();
            if (this.snapshotManager.shouldTakeNewSnapshot(content)) {
                log.info("Taking a new snapshot of an index for other nodes");
                this.snapshotManager.takeNewSnapshot();
            } else {
                log.info("Skipping creation of new snapshots");
            }
        }
        catch (RuntimeException e) {
            log.error("There was an error creating a snapshot at the end of split index upgrade task", (Throwable)e);
        }
    }

    private void restoreSnapshotIfNeeded() {
        log.info("Split index upgrade task started.");
        File content = this.getContentIndexFile();
        boolean needToRestoreSnapshot = this.snapshotManager.needsSnapshotRestoration(content);
        boolean hasSuitableContentSnapshot = this.snapshotManager.hasSuitableContentSnapshot();
        if (ATTEMPT_INDEX_RECOVERY && needToRestoreSnapshot && hasSuitableContentSnapshot) {
            log.info("Restoring the latest content snapshot");
            this.snapshotManager.restoreLatestContentSnapshot();
            SplitIndexFileHelper contentManager = new SplitIndexFileHelper(content, CONTENT);
            if (contentManager.refreshIndexInfo().hasChanges()) {
                log.info("Content snapshot contains change documents. Proceeding with the upgrade task.");
            } else if (this.snapshotManager.hasSuitableChangeSnapshot()) {
                log.info("Restoring changes snapshot");
                this.snapshotManager.restoreLatestChangeSnapshot();
            } else {
                log.info("Content snapshot doesn't have any changes and we don't have available changes snapshot. We are going to truncate all indexes, so that index recovery is triggered.");
                contentManager.overrideWithEmptyIndex();
                File changes = new File(content, CHANGE);
                if (changes.exists() && changes.isDirectory()) {
                    new SplitIndexFileHelper(changes, CHANGE).overrideWithEmptyIndex();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean upgradeUnderConnectionWriteLock() throws UpgradeException {
        log.info("Starting the upgrade task under connections lock.");
        File content = this.getContentIndexFile();
        File changes = new File(content, CHANGE);
        if (!SplitIndexFileHelper.hasFiles(content)) {
            log.info("Split index upgrade task cancelled as a the index folder is empty which will force a complete Reindex");
            return true;
        }
        SplitIndexFileHelper contentManager = new SplitIndexFileHelper(content, CONTENT);
        try {
            SplitIndexFileHelper changesManager;
            Map<String, FileInfo> sourceMap = contentManager.collectIndexFileInfos();
            SplitIndexPhase phase = contentManager.getPhase();
            IndexInfo contentInfo = contentManager.refreshIndexInfo();
            if (phase == null) {
                phase = this.resolvePhase(contentInfo, changes);
            }
            if (phase == SplitIndexPhase.CREATE_CHANGE_FOLDER) {
                log.info("creating change folder");
                contentManager.setPhase(phase);
                this.createChanges(changes, content.getAbsolutePath());
                if (contentInfo.isValid() && contentInfo.hasDocuments()) {
                    phase = SplitIndexPhase.COPY_FILES;
                } else {
                    log.info("No existing documents in index '{}', creating empty index", (Object)content.getAbsolutePath());
                    changesManager = new SplitIndexFileHelper(changes, CHANGE);
                    changesManager.createEmptyIndexIfNone();
                    phase = SplitIndexPhase.DONE;
                }
            }
            if (phase == SplitIndexPhase.COPY_FILES) {
                log.info("copying files");
                contentManager.setPhase(phase);
                changesManager = new SplitIndexFileHelper(changes, CHANGE);
                changesManager.purgeAllFiles();
                SplitIndexFileHelper.copyFilesToFolder(sourceMap.values(), changes);
                long mostRecentId = this.journalStateStore.getMostRecentId(this.contentJournalIdentifier);
                this.journalStateStore.setMostRecentId(this.changeJournalIdentifier, mostRecentId);
                log.info("{} most recent journal id is {}, copied to {}", new Object[]{this.contentJournalIdentifier.getJournalName(), mostRecentId, this.changeJournalIdentifier.getJournalName()});
                phase = SplitIndexPhase.PURGE_CONTENT_FROM_CHANGES;
            }
            if (phase == SplitIndexPhase.PURGE_CONTENT_FROM_CHANGES) {
                log.info("purging content from changes index");
                contentManager.setPhase(phase);
                changesManager = new SplitIndexFileHelper(changes, CHANGE);
                changesManager.purgeContent();
                phase = SplitIndexPhase.PURGE_CHANGES_FROM_CONTENT;
            }
            if (phase == SplitIndexPhase.PURGE_CHANGES_FROM_CONTENT) {
                log.info("purging changes from context index");
                contentManager.setPhase(phase);
                contentManager.purgeChanges();
                phase = SplitIndexPhase.DONE;
            }
            if (phase == SplitIndexPhase.REMOVE_OLD_INDEX) {
                log.info("preparing index for full reindex");
                contentManager.purgeAllFiles();
                phase = SplitIndexPhase.DONE;
            }
            if (phase == SplitIndexPhase.DONE) {
                contentManager.removePhase();
                boolean bl = true;
                return bl;
            }
            if (phase == SplitIndexPhase.ABORTED) {
                contentManager.removePhase();
                log.info("Split index migration has been aborted, please check the logs and screen for errors");
                boolean bl = false;
                return bl;
            }
            log.warn("Somehow the upgrade task ended in unknown phase: {}", (Object)phase);
            boolean bl = false;
            return bl;
        }
        finally {
            log.info("Split index upgrade task completed.");
        }
    }

    private void createChanges(@NonNull File changes, @NonNull String root) throws UpgradeException {
        if (!changes.exists() && !changes.mkdirs()) {
            log.error("Unable to create changes subfolder at {}, aborting split index.", (Object)root);
            this.raiseJohnsonFatal(CHANGES_CANNOT_CREATE, root);
        }
    }

    @VisibleForTesting
    SplitIndexPhase resolvePhase(IndexInfo contentInfo, File changes) {
        if (!contentInfo.isValid()) {
            if (contentInfo.isOldVersion()) {
                log.info("Detected older version of lucene index in content folder, force full reindex");
            } else if (contentInfo.isEmptyFolder()) {
                log.info("Detected empty folder instead of lucene index, force full reindex");
            } else {
                log.info("Detected invalid index, force full reindex");
            }
            return SplitIndexPhase.REMOVE_OLD_INDEX;
        }
        if (!changes.exists()) {
            log.info("No changes index detected, starting split index at create change folder phase");
            return SplitIndexPhase.CREATE_CHANGE_FOLDER;
        }
        if (!SplitIndexFileHelper.isLuceneIndex(changes)) {
            log.info("Discovered incomplete split index process, Empty folder at changes location, can continue at copy files phase");
            return SplitIndexPhase.COPY_FILES;
        }
        SplitIndexFileHelper changesManager = new SplitIndexFileHelper(changes, CHANGE);
        IndexInfo changesInfo = changesManager.refreshIndexInfo();
        if (!changesInfo.hasDocuments() || !changesInfo.hasChanges()) {
            if (!contentInfo.hasChanges()) {
                log.info("Changes index is empty, no changes in content index, process is done");
                return SplitIndexPhase.DONE;
            }
            log.warn("Changes index is empty. We should start the procedure by copying main index.");
            return SplitIndexPhase.COPY_FILES;
        }
        if (changesInfo.hasContent()) {
            log.info("Discovered incomplete split index process, restarting at purge content from change index phase");
            return SplitIndexPhase.PURGE_CONTENT_FROM_CHANGES;
        }
        if (contentInfo.hasChanges()) {
            log.info("Discovered incomplete split index process, restarting at purge changes from content index phase");
            return SplitIndexPhase.PURGE_CHANGES_FROM_CONTENT;
        }
        return SplitIndexPhase.DONE;
    }

    private void raiseJohnsonFatal(@NonNull String i18NKey, Object ... args) throws UpgradeException {
        String message = args != null && args.length > 0 ? this.i18NBeanFactory.getI18NBean().getText(i18NKey, args) : this.i18NBeanFactory.getI18NBean().getText(i18NKey);
        throw new UpgradeException(message, KB_URL, true);
    }

    public String getShortDescription() {
        return DESCRIPTION;
    }

    public String getBuildNumber() {
        return BUILD_NUMBER;
    }

    public boolean runOnSpaceImport() {
        return false;
    }

    public boolean breaksBackwardCompatibility() {
        return false;
    }
}

