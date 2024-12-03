/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.upgrader.LuceneIndexUpgrader
 *  com.atlassian.util.profiling.Ticker
 *  com.atlassian.util.profiling.Timer
 *  io.atlassian.util.concurrent.ThreadFactories
 *  io.atlassian.util.concurrent.ThreadFactories$Type
 *  javax.annotation.concurrent.GuardedBy
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.index.DirectoryReader
 *  org.apache.lucene.index.IndexCommit
 *  org.apache.lucene.index.IndexDeletionPolicy
 *  org.apache.lucene.index.IndexFormatTooOldException
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.IndexUpgrader
 *  org.apache.lucene.index.IndexWriter
 *  org.apache.lucene.index.IndexWriterConfig
 *  org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy
 *  org.apache.lucene.index.LiveIndexWriterConfig
 *  org.apache.lucene.index.SnapshotDeletionPolicy
 *  org.apache.lucene.search.IndexSearcher
 *  org.apache.lucene.search.ReferenceManager$RefreshListener
 *  org.apache.lucene.search.SearcherFactory
 *  org.apache.lucene.search.SearcherLifetimeManager
 *  org.apache.lucene.search.SearcherLifetimeManager$PruneByAge
 *  org.apache.lucene.search.SearcherLifetimeManager$Pruner
 *  org.apache.lucene.search.SearcherManager
 *  org.apache.lucene.store.Directory
 *  org.apache.lucene.store.FSDirectory
 *  org.apache.lucene.store.IOContext
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.bonnie.upgrader.LuceneIndexUpgrader;
import com.atlassian.confluence.internal.search.v2.lucene.BonnieConstants;
import com.atlassian.confluence.internal.search.v2.lucene.DirectoryUtil;
import com.atlassian.confluence.internal.search.v2.lucene.ILuceneConnection;
import com.atlassian.confluence.internal.search.v2.lucene.InstrumentedIndexSearcher;
import com.atlassian.confluence.internal.search.v2.lucene.InstrumentedIndexWriter;
import com.atlassian.confluence.internal.search.v2.lucene.LoggingReentrantLock;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneAccessor;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneConnectionClosedException;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneException;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneIndexMetrics;
import com.atlassian.confluence.internal.search.v2.lucene.SearchTokenExpiredException;
import com.atlassian.confluence.internal.search.v2.lucene.SearcherInitialisation;
import com.atlassian.util.profiling.Ticker;
import com.atlassian.util.profiling.Timer;
import io.atlassian.util.concurrent.ThreadFactories;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import javax.annotation.concurrent.GuardedBy;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexFormatTooOldException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexUpgrader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.LiveIndexWriterConfig;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ReferenceManager;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherLifetimeManager;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LuceneConnection
implements ILuceneConnection,
LuceneAccessor {
    private static final Logger log = LoggerFactory.getLogger(LuceneConnection.class);
    private static final SearcherInitialisation NOOP_SEARCHER_INITIALISATION = searcher -> {};
    private static final String TRACKED_SEARCHERS_PRUNE_DELAY = "atlassian.indexing.tracked.searchers.prune.delay";
    private static final String TRACKED_SEARCHERS_MAX_AGE = "atlassian.indexing.tracked.searchers.max.age";
    private final Analyzer analyzerForIndexing;
    private final ILuceneConnection.Configuration configuration;
    private final SearcherInitialisation searcherInitialisation;
    private final Lock indexLock = new LoggingReentrantLock("indexLock");
    private final ReadWriteLock searcherLifetimeManagerLock = new ReentrantReadWriteLock();
    private final AtomicBoolean indexClosed = new AtomicBoolean(false);
    private final AtomicBoolean batchMode = new AtomicBoolean(false);
    private final LuceneIndexMetrics metrics;
    private final Timer getNumDocsMetric;
    private final Timer optimizeMetric;
    private final Timer withSearchMetric;
    private final Timer withWriterMetric;
    private final Timer withBatchUpdateMetric;
    private final Timer commitAndRefreshMetric;
    private final Timer commitMetric;
    private final Timer truncateIndexMetric;
    private final Timer snapShotIndexMetric;
    private IndexWriter writer;
    private SnapshotDeletionPolicy snapshotter;
    private SearcherManager searcherManager;
    private SearcherLifetimeManager searcherLifetimeManager;
    private ScheduledExecutorService scheduledExecutorService;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public LuceneConnection(Directory directory, Analyzer analyzer, ILuceneConnection.Configuration configuration, SearcherInitialisation searcherInitialisation, LuceneIndexMetrics metrics) {
        this.analyzerForIndexing = Objects.requireNonNull(analyzer);
        this.configuration = Objects.requireNonNull(configuration);
        this.searcherInitialisation = Objects.requireNonNull(searcherInitialisation);
        this.metrics = Objects.requireNonNull(metrics);
        this.getNumDocsMetric = metrics.timer("LuceneConnection.GetNumDocs");
        this.optimizeMetric = metrics.timer("LuceneConnection.Optimize");
        this.withSearchMetric = metrics.timer("LuceneConnection.WithSearch");
        this.withWriterMetric = metrics.timer("LuceneConnection.WithWriter");
        this.withBatchUpdateMetric = metrics.timer("LuceneConnection.WithBatchUpdate");
        this.commitAndRefreshMetric = metrics.timer("LuceneConnection.CommitAndRefresh");
        this.commitMetric = metrics.timer("LuceneConnection.Commit");
        this.truncateIndexMetric = metrics.timer("LuceneConnection.TruncateIndex");
        this.snapShotIndexMetric = metrics.timer("LuceneConnection.SnapshotIndex");
        this.indexLock.lock();
        try {
            this.setUpWriterAndSearcher(directory);
            this.scheduleIndexSearcherPruneJob(configuration);
        }
        finally {
            this.indexLock.unlock();
        }
    }

    public LuceneConnection(Directory directory, Analyzer analyzer, ILuceneConnection.Configuration configuration, SearcherInitialisation searcherInitialisation) {
        this(directory, analyzer, configuration, searcherInitialisation, new LuceneIndexMetrics());
    }

    public LuceneConnection(Directory directory, Analyzer analyzer, ILuceneConnection.Configuration configuration) {
        this(directory, analyzer, configuration, NOOP_SEARCHER_INITIALISATION);
    }

    public LuceneConnection(Directory directory, Analyzer analyzer) {
        this(directory, analyzer, DEFAULT_CONFIGURATION);
    }

    public LuceneConnection(File path, Analyzer analyzer, ILuceneConnection.Configuration configuration, SearcherInitialisation searcherInitialisation, LuceneIndexMetrics metrics) {
        this(DirectoryUtil.getDirectory(path), analyzer, configuration, searcherInitialisation, metrics);
    }

    public LuceneConnection(File path, Analyzer analyzer, ILuceneConnection.Configuration configuration, SearcherInitialisation searcherInitialisation) {
        this(DirectoryUtil.getDirectory(path), analyzer, configuration, searcherInitialisation);
    }

    public LuceneConnection(File path, Analyzer analyzer, ILuceneConnection.Configuration configuration) {
        this(DirectoryUtil.getDirectory(path), analyzer, configuration);
    }

    public LuceneConnection(File path, Analyzer analyzer) {
        this(path, analyzer, DEFAULT_CONFIGURATION);
    }

    private static SearcherLifetimeManager.PruneByAge createPruneByAge(double maxAgeSecs) {
        return new SearcherLifetimeManager.PruneByAge(maxAgeSecs);
    }

    @Override
    public int getNumDocs() {
        try (Ticker ignored = this.getNumDocsMetric.start(new String[0]);){
            int n = (Integer)this.withReader(IndexReader::numDocs);
            return n;
        }
    }

    private SearcherManager newSearcherManager() throws IOException {
        final SearcherManager newSearchManager = new SearcherManager(this.writer, true, new SearcherFactory(){

            public IndexSearcher newSearcher(IndexReader reader) throws IOException {
                InstrumentedIndexSearcher indexSearcher = new InstrumentedIndexSearcher(reader, LuceneConnection.this.metrics);
                LuceneConnection.this.searcherInitialisation.initialise(indexSearcher);
                return indexSearcher;
            }
        });
        newSearchManager.addListener(new ReferenceManager.RefreshListener(){

            public void beforeRefresh() throws IOException {
            }

            public void afterRefresh(boolean didRefresh) throws IOException {
                if (didRefresh) {
                    IndexSearcher indexSearcher = (IndexSearcher)newSearchManager.acquire();
                    try {
                        LuceneConnection.this.record(indexSearcher);
                    }
                    finally {
                        newSearchManager.release((Object)indexSearcher);
                    }
                }
            }
        });
        return newSearchManager;
    }

    @GuardedBy(value="indexLock")
    private void scheduleIndexSearcherPruneJob(ILuceneConnection.Configuration configuration) {
        long indexSearcherPruneDelay = Long.getLong(TRACKED_SEARCHERS_PRUNE_DELAY, configuration.getIndexSearcherPruneDelay());
        long indexSearcherMaxAge = Long.getLong(TRACKED_SEARCHERS_MAX_AGE, configuration.getIndexSearcherMaxAge());
        log.info("Starting the scheduled service for the prune job of {}", (Object)this.writer.getDirectory());
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(ThreadFactories.named((String)("lucene-searchers-pruner-" + this.writer.getDirectory())).type(ThreadFactories.Type.DAEMON).uncaughtExceptionHandler((t, e) -> log.error("Error thread {} abruptly terminated due to an uncaught exception", (Object)t.getName(), (Object)e)).build());
        this.scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                this.searcherLifetimeManager.prune((SearcherLifetimeManager.Pruner)LuceneConnection.createPruneByAge(configuration.getIndexSearcherMaxAge()));
            }
            catch (Throwable e) {
                log.error("Error pruning IndexSearchers.", e);
            }
        }, indexSearcherMaxAge, indexSearcherPruneDelay, TimeUnit.SECONDS);
    }

    @Override
    public void optimize() throws LuceneException {
        try (Ticker ignored = this.optimizeMetric.start(new String[0]);){
            this.withWriter(writer -> writer.forceMerge(1, true));
        }
    }

    @Override
    public void close() throws LuceneException {
        this.assertNotClosed();
        try {
            log.info("Closing connection to {}", (Object)this.writer.getDirectory());
            this.closeWriter();
            this.searcherManager.close();
        }
        catch (IOException e) {
            throw new LuceneException(e);
        }
        this.shutdownScheduledExecutorService();
        this.closeSearcherLifetimeManager();
    }

    private void shutdownScheduledExecutorService() {
        this.scheduledExecutorService.shutdown();
        try {
            if (!this.scheduledExecutorService.awaitTermination(60L, TimeUnit.SECONDS)) {
                this.scheduledExecutorService.shutdownNow();
                if (!this.scheduledExecutorService.awaitTermination(60L, TimeUnit.SECONDS)) {
                    log.debug("Pool did not terminate");
                }
            }
        }
        catch (InterruptedException ie) {
            this.scheduledExecutorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void closeSearcherLifetimeManager() {
        this.searcherLifetimeManagerLock.writeLock().lock();
        try {
            this.searcherLifetimeManager.close();
        }
        catch (IOException e) {
            log.debug("Error closing searcherLifetimeManager", (Throwable)e);
        }
        finally {
            this.searcherLifetimeManagerLock.writeLock().unlock();
        }
    }

    @Override
    public void closeWriter() throws LuceneException {
        this.assertNotClosed();
        try {
            this.writer.close();
            this.indexClosed.set(true);
        }
        catch (IOException e) {
            throw new LuceneException(e);
        }
    }

    private void assertNotClosed() throws LuceneException {
        if (this.indexClosed.get()) {
            throw new LuceneConnectionClosedException("Cannot operate on closed IndexWriter");
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    public void withSearch(ILuceneConnection.SearcherAction action) throws LuceneException {
        this.assertNotClosed();
        IndexSearcher indexSearcher = null;
        try (Ticker ignored = this.withSearchMetric.start(new String[0]);){
            indexSearcher = (IndexSearcher)this.searcherManager.acquire();
            action.perform(indexSearcher);
        }
        catch (IOException e) {
            try {
                throw new LuceneException(e);
            }
            catch (Throwable throwable) {
                try {
                    if (indexSearcher == null) throw throwable;
                    this.searcherManager.release(indexSearcher);
                    throw throwable;
                }
                catch (IOException e2) {
                    throw new LuceneException(e2);
                }
            }
        }
        try {
            if (indexSearcher == null) return;
            this.searcherManager.release((Object)indexSearcher);
            return;
        }
        catch (IOException e) {
            throw new LuceneException(e);
        }
    }

    /*
     * Loose catch block
     */
    @Override
    public <T> T withSearcher(ILuceneConnection.SearcherWithTokenAction<T> action) throws LuceneException {
        T t;
        Ticker ignored;
        IndexSearcher indexSearcher;
        block16: {
            this.assertNotClosed();
            indexSearcher = null;
            ignored = this.withSearchMetric.start(new String[0]);
            indexSearcher = (IndexSearcher)this.searcherManager.acquire();
            long searchToken = this.record(indexSearcher);
            t = action.perform(indexSearcher, searchToken);
            if (ignored == null) break block16;
            ignored.close();
        }
        try {
            if (indexSearcher != null) {
                this.searcherManager.release((Object)indexSearcher);
            }
        }
        catch (IOException e) {
            throw new LuceneException(e);
        }
        return t;
        {
            catch (Throwable throwable) {
                try {
                    try {
                        if (ignored != null) {
                            try {
                                ignored.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException e) {
                        throw new LuceneException(e);
                    }
                }
                catch (Throwable throwable3) {
                    try {
                        if (indexSearcher != null) {
                            this.searcherManager.release(indexSearcher);
                        }
                    }
                    catch (IOException e) {
                        throw new LuceneException(e);
                    }
                    throw throwable3;
                }
            }
        }
    }

    private long record(IndexSearcher indexSearcher) throws LuceneException {
        long searchToken;
        this.searcherLifetimeManagerLock.readLock().lock();
        try {
            searchToken = this.searcherLifetimeManager.record(indexSearcher);
        }
        catch (IOException e) {
            throw new LuceneException(e);
        }
        finally {
            this.searcherLifetimeManagerLock.readLock().unlock();
        }
        return searchToken;
    }

    @Override
    public <T> T withSearcher(long searchToken, ILuceneConnection.SearcherWithTokenAction<T> action) throws SearchTokenExpiredException {
        this.assertNotClosed();
        if (searchToken <= 0L) {
            throw new IllegalArgumentException("searchToken must be greater than 0");
        }
        IndexSearcher indexSearcher = this.searcherLifetimeManager.acquire(searchToken);
        if (indexSearcher == null) {
            throw new SearchTokenExpiredException(searchToken);
        }
        try {
            T t;
            block17: {
                Ticker ignored = this.withSearchMetric.start(new String[0]);
                try {
                    t = action.perform(indexSearcher, searchToken);
                    if (ignored == null) break block17;
                }
                catch (Throwable throwable) {
                    try {
                        if (ignored != null) {
                            try {
                                ignored.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    catch (IOException e) {
                        throw new LuceneException(e);
                    }
                }
                ignored.close();
            }
            return t;
        }
        finally {
            try {
                this.searcherLifetimeManager.release(indexSearcher);
            }
            catch (IOException e) {
                throw new LuceneException(e);
            }
        }
    }

    @Override
    public Object withReader(ILuceneConnection.ReaderAction action) throws LuceneException {
        AtomicReference result = new AtomicReference();
        this.withSearch(searcher -> result.set(action.perform(searcher.getIndexReader())));
        return result.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void withWriter(ILuceneConnection.WriterAction action) throws LuceneException {
        this.indexLock.lock();
        try (Ticker ignored = this.withWriterMetric.start(new String[0]);){
            this.assertNotClosed();
            this.configureIndexWriter(this.writer, this.configuration);
            try {
                action.perform(this.writer);
            }
            catch (IOException e) {
                throw new LuceneException(e);
            }
            this.commitAndRefreshSearcher();
        }
        finally {
            this.indexLock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void withBatchUpdate(ILuceneConnection.BatchUpdateAction action) {
        this.indexLock.lock();
        try (Ticker ignored = this.withBatchUpdateMetric.start(new String[0]);){
            this.assertNotClosed();
            this.batchMode.set(true);
            try {
                action.perform();
            }
            catch (Exception e) {
                throw new LuceneException(e);
            }
            finally {
                this.batchMode.set(false);
            }
            this.commitAndRefreshSearcher();
        }
        finally {
            this.indexLock.unlock();
        }
    }

    @GuardedBy(value="indexLock")
    private void commitAndRefreshSearcher() {
        if (this.batchMode.get()) {
            return;
        }
        try (Ticker ignored = this.commitAndRefreshMetric.start(new String[0]);){
            try (Ticker ignored2 = this.commitMetric.start(new String[0]);){
                this.writer.commit();
            }
            this.searcherManager.maybeRefreshBlocking();
        }
        catch (IOException e) {
            throw new LuceneException("Error refreshing index searcher", e);
        }
    }

    @GuardedBy(value="indexLock")
    private void configureIndexWriter(IndexWriter indexWriter, ILuceneConnection.Configuration configuration) {
        LiveIndexWriterConfig config = indexWriter.getConfig();
        config.setUseCompoundFile(configuration.isCompoundIndexFileFormat());
        if (this.batchMode.get()) {
            config.setMaxBufferedDocs(configuration.getBatchMaxBufferedDocs());
        } else {
            config.setMaxBufferedDocs(configuration.getInteractiveMaxBufferedDocs());
        }
    }

    @Override
    public void truncateIndex() throws LuceneException {
        try (Ticker ignored = this.truncateIndexMetric.start(new String[0]);){
            this.withWriter(IndexWriter::deleteAll);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void snapshot(Directory destDir) throws IOException {
        this.indexLock.lock();
        try (Ticker ignored = this.snapShotIndexMetric.start(new String[0]);){
            this.assertNotClosed();
            IndexCommit snapshot = this.snapshotter.snapshot();
            try {
                for (String fileName : snapshot.getFileNames()) {
                    snapshot.getDirectory().copy(destDir, fileName, fileName, IOContext.DEFAULT);
                }
            }
            finally {
                this.snapshotter.release(snapshot);
            }
        }
        finally {
            this.indexLock.unlock();
        }
    }

    @Override
    public void reset(Runnable resetAction) throws LuceneException {
        this.indexLock.lock();
        try {
            this.assertNotClosed();
            log.info("Resetting the connection to {}", (Object)this.writer.getDirectory());
            Directory indexDirectory = this.writer.getDirectory();
            this.close();
            if (resetAction != null) {
                resetAction.run();
            }
            this.setUpWriterAndSearcher(indexDirectory);
            this.scheduleIndexSearcherPruneJob(this.configuration);
        }
        finally {
            this.indexLock.unlock();
        }
    }

    @GuardedBy(value="indexLock")
    private void setUpWriterAndSearcher(Directory directory) throws LuceneException {
        log.info("Setting up the writer and searcher of {}", (Object)directory);
        try {
            this.ensureLockOnDirectory(directory);
            this.ensureCorrectIndexFormat(directory);
            this.ensureIndexExists(directory);
            IndexWriterConfig config = new IndexWriterConfig(BonnieConstants.LUCENE_VERSION, this.analyzerForIndexing);
            config.setIndexDeletionPolicy((IndexDeletionPolicy)new SnapshotDeletionPolicy((IndexDeletionPolicy)new KeepOnlyLastCommitDeletionPolicy()));
            this.writer = new InstrumentedIndexWriter(directory, config, this.metrics);
            this.snapshotter = (SnapshotDeletionPolicy)this.writer.getConfig().getIndexDeletionPolicy();
            this.searcherLifetimeManager = new SearcherLifetimeManager();
            this.searcherManager = this.newSearcherManager();
            this.indexClosed.set(false);
        }
        catch (IOException e) {
            throw new LuceneException(e);
        }
    }

    @GuardedBy(value="indexLock")
    private void ensureIndexExists(Directory directory) throws IOException {
        if (!DirectoryReader.indexExists((Directory)directory)) {
            new IndexWriter(directory, new IndexWriterConfig(BonnieConstants.LUCENE_VERSION, null)).close();
        }
    }

    @GuardedBy(value="indexLock")
    private void ensureCorrectIndexFormat(Directory directory) throws IOException {
        if (!(directory instanceof FSDirectory)) {
            log.info("Expect FSDirectory. Skip index format check");
            return;
        }
        this.upgradeIndexIfNecessary(((FSDirectory)directory).getDirectory(), directory);
    }

    @GuardedBy(value="indexLock")
    private void ensureLockOnDirectory(Directory directory) throws IOException {
        if (IndexWriter.isLocked((Directory)directory)) {
            log.warn("Forcing unlock of locked index directory: " + directory);
            IndexWriter.unlock((Directory)directory);
        }
    }

    @GuardedBy(value="indexLock")
    private void upgradeIndexIfNecessary(File directoryPath, Directory directory) throws IOException {
        try {
            new IndexWriter(directory, new IndexWriterConfig(BonnieConstants.LUCENE_VERSION, null)).close();
        }
        catch (IndexFormatTooOldException e) {
            log.info("Detected old index format. Attempting an upgrade.");
            this.upgradeIndexToLucene36(directoryPath);
            this.upgradeIndexToCurrentLuceneVersion(directory);
        }
    }

    @GuardedBy(value="indexLock")
    private void upgradeIndexToCurrentLuceneVersion(Directory directory) throws IOException {
        log.info("Upgrading index to " + BonnieConstants.LUCENE_VERSION.name());
        IndexUpgrader indexUpgrader = new IndexUpgrader(directory, BonnieConstants.LUCENE_VERSION);
        indexUpgrader.upgrade();
        log.info("Index upgraded to " + BonnieConstants.LUCENE_VERSION.name());
    }

    @GuardedBy(value="indexLock")
    private void upgradeIndexToLucene36(File directory) throws IOException {
        log.info("Upgrading index to Lucene 3.6");
        LuceneIndexUpgrader indexUpgrader = LuceneIndexUpgrader.create((File)directory);
        indexUpgrader.upgrade();
        log.info("Index upgraded to Lucene 3.6");
    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return this.scheduledExecutorService;
    }

    @Override
    public Analyzer getAnalyzer() {
        return this.writer.getAnalyzer();
    }

    @Override
    public void execute(Consumer<IndexWriter> writerAction) {
        this.withWriter(writerAction::accept);
    }

    @Override
    public SearcherManager getSearcherManager() {
        return this.searcherManager;
    }

    @Override
    public SearcherLifetimeManager getSearcherLifetimeManager() {
        return this.searcherLifetimeManager;
    }

    @Override
    public Path getPath() {
        Directory directory = this.writer.getDirectory();
        if (directory instanceof FSDirectory) {
            return ((FSDirectory)directory).getDirectory().toPath();
        }
        return null;
    }
}

