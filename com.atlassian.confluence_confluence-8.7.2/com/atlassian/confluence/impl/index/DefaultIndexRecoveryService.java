/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.index.IndexRecoverer
 *  com.atlassian.confluence.api.model.journal.JournalIdentifier
 *  com.atlassian.confluence.api.service.index.IndexRecoveryService
 *  com.atlassian.confluence.internal.search.v2.lucene.DirectoryUtil
 *  com.atlassian.core.util.FileUtils
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.atlassian.event.Event
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.events.PluginFrameworkResumingEvent
 *  com.atlassian.plugin.event.events.PluginModuleEnabledEvent
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.atlassian.util.profiling.Ticker
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Joiner
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Throwables
 *  com.google.common.io.Files
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.lucene.index.DirectoryReader
 *  org.apache.lucene.store.Directory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.impl.index;

import com.atlassian.confluence.api.model.index.IndexRecoverer;
import com.atlassian.confluence.api.model.journal.JournalIdentifier;
import com.atlassian.confluence.api.service.index.IndexRecoveryService;
import com.atlassian.confluence.cluster.ClusterConfigurationHelper;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.cluster.ReIndexingScopeThreadLocal;
import com.atlassian.confluence.event.events.admin.ReindexFinishedEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapper;
import com.atlassian.confluence.event.events.cluster.ClusterEventWrapperService;
import com.atlassian.confluence.event.events.cluster.ClusterIndexRequestEvent;
import com.atlassian.confluence.event.events.cluster.ClusterIndexResponseEvent;
import com.atlassian.confluence.event.events.cluster.ClusterIndexSnapshotCreatedEvent;
import com.atlassian.confluence.event.events.cluster.ClusterIndexSnapshotRequestEvent;
import com.atlassian.confluence.event.events.cluster.ClusterIndexSnapshotRequestReceivedEvent;
import com.atlassian.confluence.event.events.plugin.PluginFrameworkStartedEvent;
import com.atlassian.confluence.impl.journal.JournalDao;
import com.atlassian.confluence.impl.journal.JournalEntry;
import com.atlassian.confluence.impl.journal.JournalStateStore;
import com.atlassian.confluence.impl.util.concurrent.ConfluenceExecutors;
import com.atlassian.confluence.internal.index.event.ReIndexFinishedAnalyticsEvent;
import com.atlassian.confluence.internal.index.event.ReIndexStartedAnalyticsEvent;
import com.atlassian.confluence.internal.index.lucene.LuceneIndexHelper;
import com.atlassian.confluence.internal.search.queue.AbstractJournalIndexTaskQueue;
import com.atlassian.confluence.internal.search.v2.lucene.DirectoryUtil;
import com.atlassian.confluence.plugin.descriptor.IndexRecovererModuleDescriptor;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.confluence.util.profiling.TimedAnalytics;
import com.atlassian.confluence.util.zip.FileUnzipper;
import com.atlassian.core.util.FileUtils;
import com.atlassian.dc.filestore.api.FileStore;
import com.atlassian.event.Event;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.events.PluginFrameworkResumingEvent;
import com.atlassian.plugin.event.events.PluginModuleEnabledEvent;
import com.atlassian.util.concurrent.ThreadFactories;
import com.atlassian.util.profiling.Ticker;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class DefaultIndexRecoveryService
implements IndexRecoveryService,
InitializingBean,
DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DefaultIndexRecoveryService.class);
    private static final String SNAPSHOT_FILE_PREFIX = "IndexSnapshot";
    private static final String SNAPSHOT_FILE_NAME = "Disaster_Recovery_Backup";
    private static final String SNAPSHOT_DIRECTORY = "index-snapshots";
    private static final String SNAPSHOT_FILE_WAIT_TIME = "confluence.cluster.snapshot.file.wait.time";
    private static final String INDEX_RECOVERY_GENERATION_SYSTEM_PROPERTY = "confluence.cluster.index.recovery.generation.timeout";
    private static final String INDEX_RECOVERY_QUERY_SYSTEM_PROPERTY = "confluence.cluster.index.recovery.query.timeout";
    private static final int INDEX_RECOVERY_NUM_ATTEMPTS = Integer.getInteger("confluence.cluster.index.recovery.num.attempts", 1);
    private final ExecutorService generateIndexRecoveryThreadPool = ConfluenceExecutors.wrap(new ThreadPoolExecutor(0, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), ThreadFactories.namedThreadFactory((String)"generateIndexRecovery")));
    private final JournalStateStore journalStateStore;
    private final JournalDao journalDao;
    private final FileStore.Path sharedHome;
    private final ClusterManager clusterManager;
    private final ClusterConfigurationHelper clusterConfigurationHelper;
    private final EventPublisher eventPublisher;
    private final ClusterEventWrapperService clusterEventWrapperService;
    private final PluginAccessor pluginAccessor;
    private final ConfluenceDirectories confluenceDirectories;
    private final BootstrapManager bootstrapManager;
    private final SynchronousQueue<ClusterIndexResponseEvent> indexRequestQueue = new SynchronousQueue();
    private final SynchronousQueue<ClusterIndexSnapshotCreatedEvent> indexSnapshotQueue = new SynchronousQueue();
    private final Function<File, Optional<Long>> directoryVersionGetter;
    private static final Duration waitTime = Duration.ofSeconds(Long.getLong("confluence.cluster.index.recovery.generation.timeout", 120L));
    private static final Duration shortWaitTime = Duration.ofSeconds(Long.getLong("confluence.cluster.index.recovery.query.timeout", 10L));
    private static final Duration eventWaitTime = Duration.ofSeconds(5L);
    private static final Duration indexSnapshotWaitTime = Duration.ofSeconds(Long.getLong("confluence.cluster.snapshot.file.wait.time", 120L));
    private final AtomicBoolean pluginSystemLateStartupTriggered;
    private final AtomicReference<ReIndexStartedAnalyticsEvent> reIndexStartedEventHolder = new AtomicReference();
    private final AtomicReference<ReIndexFinishedAnalyticsEvent> reIndexFinishedEventHolder = new AtomicReference();

    public DefaultIndexRecoveryService(JournalStateStore journalStateStore, JournalDao journalDao, FileStore.Path sharedHome, ClusterManager clusterManager, EventPublisher eventPublisher, ClusterEventWrapperService clusterEventWrapperService, PluginAccessor pluginAccessor, ClusterConfigurationHelper clusterConfigurationHelper, ConfluenceDirectories confluenceDirectories, BootstrapManager bootstrapManager) {
        this(journalStateStore, journalDao, sharedHome, clusterManager, eventPublisher, clusterEventWrapperService, pluginAccessor, clusterConfigurationHelper, confluenceDirectories, bootstrapManager, DefaultIndexRecoveryService.defaultDirectoryVersionGetter());
    }

    static Function<File, Optional<Long>> defaultDirectoryVersionGetter() {
        return file -> {
            try {
                DirectoryReader directoryReader = DirectoryReader.open((Directory)DirectoryUtil.getDirectory((File)file));
                Optional<Long> id = Optional.of(directoryReader.getVersion());
                log.info("index recovery for {} directory version is {}", (Object)file.getName(), id);
                directoryReader.close();
                return id;
            }
            catch (IOException e) {
                log.warn("Index could not be read", (Throwable)e);
                return Optional.empty();
            }
        };
    }

    @VisibleForTesting
    DefaultIndexRecoveryService(JournalStateStore journalStateStore, JournalDao journalDao, FileStore.Path sharedHome, ClusterManager clusterManager, EventPublisher eventPublisher, ClusterEventWrapperService clusterEventWrapperService, PluginAccessor pluginAccessor, ClusterConfigurationHelper clusterConfigurationHelper, ConfluenceDirectories confluenceDirectories, BootstrapManager bootstrapManager, Function<File, Optional<Long>> directoryVersionGetter) {
        this.journalStateStore = (JournalStateStore)Preconditions.checkNotNull((Object)journalStateStore);
        this.journalDao = (JournalDao)Preconditions.checkNotNull((Object)journalDao);
        this.sharedHome = sharedHome;
        this.clusterManager = (ClusterManager)Preconditions.checkNotNull((Object)clusterManager);
        this.clusterConfigurationHelper = (ClusterConfigurationHelper)Preconditions.checkNotNull((Object)clusterConfigurationHelper);
        this.eventPublisher = (EventPublisher)Preconditions.checkNotNull((Object)eventPublisher);
        this.clusterEventWrapperService = (ClusterEventWrapperService)Preconditions.checkNotNull((Object)clusterEventWrapperService);
        this.pluginAccessor = (PluginAccessor)Preconditions.checkNotNull((Object)pluginAccessor);
        this.confluenceDirectories = (ConfluenceDirectories)Preconditions.checkNotNull((Object)confluenceDirectories);
        this.bootstrapManager = (BootstrapManager)Preconditions.checkNotNull((Object)bootstrapManager);
        this.directoryVersionGetter = (Function)Preconditions.checkNotNull(directoryVersionGetter);
        this.pluginSystemLateStartupTriggered = new AtomicBoolean(false);
    }

    private File indexPath() {
        return this.confluenceDirectories.getLuceneIndexDirectory().toFile();
    }

    private File tempDir() {
        return this.confluenceDirectories.getTempDirectory().toFile();
    }

    public boolean isIndexRecoveryRequired(JournalIdentifier journalId, String indexDirName) {
        boolean nonOrFirstVersion;
        File indexDir;
        if (!this.bootstrapManager.isSetupComplete()) {
            log.info("index recovery for {} not required, boostrap not complete", (Object)journalId.getJournalName());
            return false;
        }
        if (!this.clusterConfigurationHelper.isClusterHomeConfigured()) {
            log.info("index recovery for {} not required, not clustered", (Object)journalId.getJournalName());
            return false;
        }
        File file = indexDir = StringUtils.isEmpty((CharSequence)indexDirName) ? this.indexPath() : new File(this.indexPath(), indexDirName);
        if (!indexDir.exists()) {
            log.warn("index recovery for {} required! index dir {} did not exist", (Object)journalId.getJournalName(), (Object)indexDir.getName());
            return true;
        }
        long journalEntryId = this.journalStateStore.getMostRecentId(journalId);
        if (journalEntryId == 0L) {
            log.warn("index recovery required! journalEntryId for {} was 0", (Object)journalId.getJournalName());
            return true;
        }
        JournalEntry entry = this.journalDao.findEntry(journalEntryId);
        if (entry == null) {
            log.warn("index recovery required! journal entry {} not found for for {}", (Object)journalEntryId, (Object)journalId.getJournalName());
            return true;
        }
        Optional<Long> directoryVersion = this.directoryVersionGetter.apply(indexDir);
        boolean bl = nonOrFirstVersion = !directoryVersion.isPresent() || directoryVersion.get() == 1L;
        if (nonOrFirstVersion) {
            log.warn("index recovery for {} required, index has never been written to", (Object)journalId.getJournalName());
        } else {
            log.info("index recovery for {} not required, index exist and journalEntryId is {}", (Object)journalId.getJournalName(), (Object)journalEntryId);
        }
        return nonOrFirstVersion;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean recoverIndex(JournalIdentifier journalId, String indexDirName) {
        if (!this.clusterManager.isClustered()) {
            return false;
        }
        if (this.clusterManager.getClusterInformation().getMemberCount() <= 1) {
            log.debug("Cannot recover index because this is the only node in the cluster");
            return false;
        }
        ClusterNodeInformation nodeInfo = this.clusterManager.getThisNodeInformation();
        if (nodeInfo == null) {
            log.warn("Cannot get cluster node info from the cluster manager for this node");
            return false;
        }
        DefaultIndexRecoveryService defaultIndexRecoveryService = this;
        synchronized (defaultIndexRecoveryService) {
            boolean bl;
            block23: {
                ClusterIndexSnapshotCreatedEvent snapshotCreatedEvent;
                Ticker t;
                block21: {
                    boolean bl2;
                    block22: {
                        ClusterIndexResponseEvent indexResponseEvent;
                        block19: {
                            boolean bl3;
                            block20: {
                                t = TimedAnalytics.timedAnalytics().start("confluence.profiling.startup.index.restore.node-snapshot");
                                try {
                                    ClusterIndexRequestEvent indexRequestEvent = new ClusterIndexRequestEvent(this, nodeInfo.getAnonymizedNodeIdentifier(), journalId, BuildInformation.INSTANCE.getBuildNumber(), indexDirName);
                                    this.clusterManager.publishEventImmediately(this.clusterEventWrapperService.wrap(this.clusterManager.getThisNodeInformation(), indexRequestEvent));
                                    indexResponseEvent = this.indexRequestQueue.poll(shortWaitTime.toSeconds(), TimeUnit.SECONDS);
                                    if (indexResponseEvent != null) break block19;
                                    log.warn("Timeout waiting for index recovery, no other nodes responded to offer their index snapshot within {} seconds. To increase this, modify the system property 'confluence.cluster.index.recovery.query.timeout' where the time unit is in seconds.", (Object)shortWaitTime.toSeconds());
                                    bl3 = false;
                                    if (t == null) break block20;
                                }
                                catch (Throwable throwable) {
                                    try {
                                        if (t != null) {
                                            try {
                                                t.close();
                                            }
                                            catch (Throwable throwable2) {
                                                throwable.addSuppressed(throwable2);
                                            }
                                        }
                                        throw throwable;
                                    }
                                    catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        return false;
                                    }
                                }
                                t.close();
                            }
                            return bl3;
                        }
                        this.requestIndexSnapshot(indexResponseEvent);
                        snapshotCreatedEvent = this.indexSnapshotQueue.poll(waitTime.toSeconds(), TimeUnit.SECONDS);
                        if (snapshotCreatedEvent != null) break block21;
                        log.warn("Timeout waiting for index recovery, did not receive index snapshot within {} seconds. To increase this, modify the system property 'confluence.cluster.index.recovery.generation.timeout' where the time unit is in seconds.", (Object)waitTime.toSeconds());
                        bl2 = false;
                        if (t == null) break block22;
                        t.close();
                    }
                    return bl2;
                }
                bl = this.consumeIndexSnapshot(journalId, snapshotCreatedEvent);
                if (t == null) break block23;
                t.close();
            }
            return bl;
        }
    }

    public boolean recoverIndexFromSharedHome(JournalIdentifier journalId, String indexDirName) {
        log.info("recovering index {} from shared home", (Object)journalId.getJournalName());
        if (!this.clusterConfigurationHelper.isClusterHomeConfigured()) {
            log.warn("recovering index from shared home unavailable for non-clustered confluence");
            return false;
        }
        File indexDir = new File(this.indexPath(), indexDirName);
        FileStore.Path sharedHomeIndexDir = this.getSharedHomeIndexDirectory();
        Optional<Long> journalEntryId = this.getIndexJournalEntryId(journalId);
        if (!journalEntryId.isPresent()) {
            return false;
        }
        JournalEntry entry = this.journalDao.findEntry(journalEntryId.get());
        if (entry == null) {
            log.warn("unable to find entry {} in the journal, journal_id is not up to date", (Object)journalEntryId.get());
            return false;
        }
        String zipFileName = Joiner.on((String)"_").join((Object)SNAPSHOT_FILE_PREFIX, (Object)journalId.getJournalName(), new Object[]{journalEntryId.get()}).concat(".zip");
        FileStore.Path sharedHomeZipFile = sharedHomeIndexDir.path(new String[]{zipFileName});
        if (!sharedHomeZipFile.tryFileExists()) {
            log.warn("No index snapshot found in the shared home with the filename {}", (Object)zipFileName);
            return false;
        }
        try (Ticker t = TimedAnalytics.timedAnalytics().start("confluence.profiling.startup.index.restore.shared-home");){
            File zipFile = new File(indexDir, zipFileName);
            Runnable runBeforeReset = () -> this.restoreIndex(indexDir, sharedHomeZipFile, zipFile, journalId, (Long)journalEntryId.get());
            this.getIndexRecoverer(indexDirName).reset(runBeforeReset);
            boolean bl = true;
            return bl;
        }
    }

    public boolean createIndexBackup(JournalIdentifier journalId, String indexDirName, IndexRecoverer indexRecoverer) {
        if (this.isIndexRecoveryRequired(journalId, indexDirName)) {
            return false;
        }
        long mostRecentId = this.journalStateStore.getMostRecentId(journalId);
        Optional<Long> oldJournalId = this.getIndexJournalEntryId(journalId);
        if (oldJournalId.map(v -> v >= mostRecentId).orElse(false).booleanValue()) {
            log.info("existing index snapshot back up is up to date, it is not necessary to create a new index backup");
            return true;
        }
        File tempIndexFile = new File(this.tempDir(), Joiner.on((String)"_").join((Object)SNAPSHOT_FILE_PREFIX, (Object)indexDirName, new Object[]{SNAPSHOT_FILE_NAME}));
        String zipFileName = Joiner.on((String)"_").join((Object)SNAPSHOT_FILE_PREFIX, (Object)journalId.getJournalName(), new Object[]{mostRecentId}).concat(".zip");
        this.createIndex(indexDirName, indexRecoverer, tempIndexFile, zipFileName);
        if (!this.writeIndexJournalEntryId(mostRecentId, journalId)) {
            this.deleteIndex(zipFileName);
        } else if (oldJournalId.isPresent()) {
            String oldZipFileName = Joiner.on((String)"_").join((Object)SNAPSHOT_FILE_PREFIX, (Object)journalId.getJournalName(), new Object[]{oldJournalId.get()}).concat(".zip");
            this.deleteIndex(oldZipFileName);
        }
        return true;
    }

    @EventListener
    public void handleStartingEvent(PluginFrameworkResumingEvent event) {
        this.triggerIndexRecovererModuleDescriptors();
        this.pluginSystemLateStartupTriggered.set(true);
    }

    @EventListener
    public void onPluginFrameworkStartedEvent(PluginFrameworkStartedEvent ignored) {
        Optional.ofNullable(this.reIndexStartedEventHolder.getAndSet(null)).ifPresent(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
        Optional.ofNullable(this.reIndexFinishedEventHolder.getAndSet(null)).ifPresent(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
    }

    @EventListener
    public void handleModuleEnableEvent(PluginModuleEnabledEvent event) {
        if (this.pluginSystemLateStartupTriggered.get() && IndexRecovererModuleDescriptor.class.isInstance(event.getModule())) {
            this.triggerIndexRecovererModuleDescriptors();
        }
    }

    @VisibleForTesting
    protected void triggerIndexRecovererModuleDescriptors() {
        List indexRecoverers = this.pluginAccessor.getEnabledModuleDescriptorsByClass(IndexRecovererModuleDescriptor.class);
        block2: for (IndexRecovererModuleDescriptor indexRecoverer : indexRecoverers) {
            try {
                JournalIdentifier journalId = indexRecoverer.getJournalId();
                String indexDirName = indexRecoverer.getIndexDirName();
                String indexName = indexRecoverer.getIndexName();
                if (!this.isIndexRecoveryRequired(journalId, indexDirName)) continue;
                log.warn("Index recovery is required for {} index, starting now", (Object)indexName);
                if (this.recoverIndexFromSharedHome(journalId, indexDirName)) {
                    log.warn("{} index recovered from shared home directory", (Object)indexName);
                    continue;
                }
                for (int i = 0; i < INDEX_RECOVERY_NUM_ATTEMPTS; ++i) {
                    if (this.recoverIndex(journalId, indexDirName)) {
                        log.warn("{} index recovery complete", (Object)indexName);
                        continue block2;
                    }
                    log.warn("{} failed to recover, attempt {}/{}", new Object[]{indexName, i + 1, INDEX_RECOVERY_NUM_ATTEMPTS});
                }
                log.warn("Could not recover {} index, the system will attempt to do a full re-index", (Object)indexName);
                ReIndexingScopeThreadLocal.withScope(ReIndexingScopeThreadLocal.ReIndexingScope.LOCALLY, () -> indexRecoverer.getModule().reindex());
                if (INDEX_RECOVERY_NUM_ATTEMPTS != 0) continue;
                this.reIndexStartedEventHolder.set(ReIndexStartedAnalyticsEvent.newIndexRecoveryReIndexStartedEvent());
            }
            catch (Exception e) {
                log.error("Exception when handling index recovery for module descriptor {}", (Object)indexRecoverer.getCompleteKey(), (Object)e);
            }
        }
    }

    @EventListener
    public void onReIndexFinishedEvent(ReindexFinishedEvent ignored) {
        if (INDEX_RECOVERY_NUM_ATTEMPTS == 0) {
            this.reIndexFinishedEventHolder.set(ReIndexFinishedAnalyticsEvent.newIndexRecoveryReIndexFinishedEvent(LuceneIndexHelper.sizeOfIndexMB(Paths.get(this.getIndexRecovererModuleDescriptor(r -> r.getJournalId().equals((Object)AbstractJournalIndexTaskQueue.CONTENT_JOURNAL_ID)).getIndexDirName(), new String[0])), LuceneIndexHelper.sizeOfIndexMB(Paths.get(this.getIndexRecovererModuleDescriptor(r -> r.getJournalId().equals((Object)AbstractJournalIndexTaskQueue.CHANGE_JOURNAL_ID)).getIndexDirName(), new String[0]))));
        }
    }

    @EventListener
    public void handleEvent(ClusterEventWrapper eventWrapper) throws Exception {
        ClusterIndexSnapshotCreatedEvent event;
        Event wrappedEvent = eventWrapper.getEvent();
        if (wrappedEvent instanceof ClusterIndexRequestEvent) {
            this.indexRecoveryResponse((ClusterIndexRequestEvent)wrappedEvent);
        } else if (wrappedEvent instanceof ClusterIndexResponseEvent) {
            ClusterIndexResponseEvent event2 = (ClusterIndexResponseEvent)wrappedEvent;
            if (this.isReceiverNode(event2.getReceiverNodeId())) {
                this.indexRequestQueue.offer(event2, eventWaitTime.toMillis(), TimeUnit.MILLISECONDS);
            }
        } else if (wrappedEvent instanceof ClusterIndexSnapshotRequestEvent) {
            this.generateIndexRecovery((ClusterIndexSnapshotRequestEvent)wrappedEvent);
        } else if (wrappedEvent instanceof ClusterIndexSnapshotCreatedEvent && this.isReceiverNode((event = (ClusterIndexSnapshotCreatedEvent)wrappedEvent).getReceiverNodeId())) {
            this.indexSnapshotQueue.offer(event, eventWaitTime.toMillis(), TimeUnit.MILLISECONDS);
        }
    }

    private FileStore.Path getSharedHomeIndexDirectory() {
        return this.sharedHome.path(new String[]{SNAPSHOT_DIRECTORY});
    }

    private Optional<Long> getIndexJournalEntryId(JournalIdentifier journal) {
        try {
            FileStore.Path journalIdFile = this.getSharedHomeIndexDirectory().path(new String[]{Joiner.on((String)"_").join((Object)SNAPSHOT_FILE_PREFIX, (Object)journal.getJournalName(), new Object[]{"journal", "id"})});
            log.info("will read latest journalId from file {}", (Object)journalIdFile);
            if (!journalIdFile.tryFileExists()) {
                log.warn("unable to find journal_id file for {}", (Object)journal.getJournalName());
                return Optional.empty();
            }
            String firstLine = (String)journalIdFile.fileReader().read(is -> new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).readLine());
            return Optional.of(Long.parseLong(firstLine));
        }
        catch (IOException e) {
            log.warn("Problems reading the index journal id file, cannot recover index", (Throwable)e);
            return Optional.empty();
        }
    }

    private boolean writeIndexJournalEntryId(long id, JournalIdentifier journal) {
        try {
            FileStore.Path sharedHomeIndexDir = this.getSharedHomeIndexDirectory();
            String journalEntryIdFileName = Joiner.on((String)"_").join((Object)SNAPSHOT_FILE_PREFIX, (Object)journal.getJournalName(), new Object[]{"journal", "id"});
            FileStore.Path journalIdFile = sharedHomeIndexDir.path(new String[]{journalEntryIdFileName});
            journalIdFile.fileWriter().write(Long.toString(id).getBytes(StandardCharsets.UTF_8));
            return true;
        }
        catch (IOException e) {
            log.warn("Problems writing the index journal id file, cannot recover index", (Throwable)e);
            return false;
        }
    }

    private void indexRecoveryResponse(ClusterIndexRequestEvent event) {
        String buildNumber = BuildInformation.INSTANCE.getBuildNumber();
        if (!buildNumber.equals(event.getBuildNumber())) {
            log.warn("This node does not have a matching build number, do not respond to index snapshot request");
            return;
        }
        if (this.isIndexRecoveryRequired(event.getJournalId(), event.getIndexDirName())) {
            log.warn("This node appears to have an out of date index / non-usable index, do not respond to index snapshot request");
            return;
        }
        ClusterNodeInformation nodeInfo = this.clusterManager.getThisNodeInformation();
        if (nodeInfo == null) {
            log.warn("Cannot get cluster node info from the cluster manager for this node");
            return;
        }
        log.info("This node responded that it can provide index snapshot");
        this.eventPublisher.publish((Object)new ClusterIndexResponseEvent(this, nodeInfo.getAnonymizedNodeIdentifier(), event.getNodeId(), event.getJournalId(), event.getIndexDirName()));
    }

    private void requestIndexSnapshot(ClusterIndexResponseEvent event) {
        ClusterIndexSnapshotRequestEvent indexRequestEvent = new ClusterIndexSnapshotRequestEvent(this, event.getReceiverNodeId(), event.getSenderNodeId(), event.getJournalId(), event.getIndexDirName());
        this.clusterManager.publishEventImmediately(this.clusterEventWrapperService.wrap(this.clusterManager.getThisNodeInformation(), indexRequestEvent));
    }

    private void generateIndexRecovery(ClusterIndexSnapshotRequestEvent event) throws Exception {
        if (!this.isReceiverNode(event.getReceiverNodeId())) {
            return;
        }
        this.eventPublisher.publish((Object)new ClusterIndexSnapshotRequestReceivedEvent(this, event.getReceiverNodeId(), event.getSenderNodeId()));
        Runnable generateIndexRecoveryTask = () -> this.createIndexRecoveryTask(event);
        this.generateIndexRecoveryThreadPool.submit(generateIndexRecoveryTask);
    }

    private void createIndexRecoveryTask(ClusterIndexSnapshotRequestEvent requestEvent) {
        File tempIndexFile = new File(this.tempDir(), Joiner.on((String)"_").join((Object)SNAPSHOT_FILE_PREFIX, (Object)requestEvent.getIndexDirName(), new Object[]{requestEvent.getSenderNodeId()}));
        long mostRecentId = this.journalStateStore.getMostRecentId(requestEvent.getJournalId());
        Object[] zipFileNameParts = new String[]{SNAPSHOT_FILE_PREFIX, requestEvent.getSenderNodeId(), requestEvent.getReceiverNodeId(), "" + mostRecentId};
        String zipFileName = Joiner.on((String)"_").join(zipFileNameParts).concat(".zip");
        this.createIndex(requestEvent.getIndexDirName(), this.getIndexRecoverer(requestEvent.getIndexDirName()), tempIndexFile, zipFileName);
        this.eventPublisher.publish((Object)new ClusterIndexSnapshotCreatedEvent(this, requestEvent.getReceiverNodeId(), requestEvent.getSenderNodeId(), mostRecentId, requestEvent.getIndexDirName(), zipFileName));
    }

    private void createIndex(String indexDirName, IndexRecoverer indexRecoverer, File tempIndexSnapshot, String zipFileName) {
        File indexDir = new File(this.indexPath(), indexDirName);
        tempIndexSnapshot.mkdir();
        File indexZip = new File(indexDir, zipFileName);
        if (indexZip.exists()) {
            indexZip.delete();
        }
        FileStore.Path sharedHomeIndexDir = this.getSharedHomeIndexDirectory();
        FileStore.Path sharedIndexPath = sharedHomeIndexDir.path(new String[]{zipFileName});
        try {
            log.info("requesting connection to create new snapshot for index dir {}", (Object)indexDirName);
            indexRecoverer.snapshot(tempIndexSnapshot);
            log.info("creating zipfile {}", (Object)zipFileName);
            FileUtils.createZipFile((File)tempIndexSnapshot, (File)indexZip);
            log.info("moving zipfile {} to shared index path {}", (Object)zipFileName, (Object)sharedIndexPath);
            sharedIndexPath.fileWriter().write(outputStream -> com.google.common.io.Files.copy((File)indexZip, (OutputStream)outputStream));
            Files.delete(indexZip.toPath());
        }
        catch (Exception e) {
            log.error("unable to create new index snapshot from index dir {}", (Object)indexDirName, (Object)e);
            throw Throwables.propagate((Throwable)e);
        }
        finally {
            FileUtils.deleteDir((File)tempIndexSnapshot);
            indexZip.delete();
        }
    }

    private void deleteIndex(String oldZipFileName) {
        FileStore.Path sharedHomeIndexDir = this.getSharedHomeIndexDirectory();
        FileStore.Path index = sharedHomeIndexDir.path(new String[]{oldZipFileName});
        try {
            index.deleteFile();
        }
        catch (IOException e) {
            log.warn("Failed to delete old snapshot file at {}", (Object)index, (Object)e);
        }
    }

    private boolean consumeIndexSnapshot(JournalIdentifier journalId, ClusterIndexSnapshotCreatedEvent event) {
        File indexDir = new File(this.indexPath(), event.getIndexDirName());
        String zipFileName = event.getIndexSnapshotFilename();
        FileStore.Path sharedHomeZipFile = this.getSharedHomeIndexDirectory().path(new String[]{zipFileName});
        File zipFile = new File(indexDir, zipFileName);
        try {
            for (long waiting = indexSnapshotWaitTime.toSeconds(); waiting >= 0L; --waiting) {
                if (sharedHomeZipFile.tryFileExists()) {
                    Runnable runBeforeReset = () -> this.restoreIndex(indexDir, sharedHomeZipFile, zipFile, journalId, event.getJournalEntryId());
                    this.getIndexRecoverer(event.getIndexDirName()).reset(runBeforeReset);
                    try {
                        sharedHomeZipFile.deleteFile();
                    }
                    catch (IOException e) {
                        log.warn("Failed to delete index snapshot at {}", (Object)sharedHomeZipFile, (Object)e);
                    }
                    return true;
                }
                Thread.sleep(1000L);
            }
            log.warn(String.format("Index snapshot hasn't appeared in the shared home within %d seconds. Unable to consume.", indexSnapshotWaitTime.toSeconds()));
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Exception waiting for index snapshot zip to become available: {}", (Object)e.getMessage());
        }
        return false;
    }

    private void restoreIndex(File indexDir, FileStore.Path sharedHomeZipFile, File zipFile, JournalIdentifier journalId, long journalEntryId) {
        File[] indexFiles = indexDir.listFiles();
        if (indexFiles != null) {
            for (File file : indexFiles) {
                if (!file.isFile()) continue;
                file.delete();
            }
        }
        try {
            sharedHomeZipFile.fileReader().consume(is -> Files.copy(is, zipFile.toPath(), new CopyOption[0]));
            try (Ticker t = TimedAnalytics.timedAnalytics().start("confluence.profiling.startup.index.restore.unzip");){
                new FileUnzipper(zipFile, indexDir).unzip();
            }
            this.journalStateStore.setMostRecentId(journalId, journalEntryId);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            zipFile.delete();
        }
    }

    protected IndexRecovererModuleDescriptor getIndexRecovererModuleDescriptor(Predicate<IndexRecovererModuleDescriptor> predicate) {
        List indexRecoverers = this.pluginAccessor.getEnabledModuleDescriptorsByClass(IndexRecovererModuleDescriptor.class).stream().filter(predicate).collect(Collectors.toList());
        if (indexRecoverers.isEmpty()) {
            throw new RuntimeException("No index recoverer satisfies the predicate. Make sure your plugin has implemented <index-recoverer> module");
        }
        if (indexRecoverers.size() > 1) {
            log.warn("More than one index recoverer available for {} index. Returning the first one", (Object)((IndexRecovererModuleDescriptor)indexRecoverers.get(0)).getIndexName());
        }
        return (IndexRecovererModuleDescriptor)indexRecoverers.get(0);
    }

    private IndexRecoverer getIndexRecoverer(String indexDirName) {
        return this.getIndexRecovererModuleDescriptor(r -> indexDirName.equals(r.getIndexDirName())).getModule();
    }

    private boolean isReceiverNode(String receiverNodeId) {
        ClusterNodeInformation nodeInfo = this.clusterManager.getThisNodeInformation();
        return nodeInfo != null && nodeInfo.getAnonymizedNodeIdentifier().equals(receiverNodeId);
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }
}

