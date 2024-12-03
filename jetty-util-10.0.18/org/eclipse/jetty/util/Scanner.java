/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.IncludeExcludeSet;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scanner
extends ContainerLifeCycle {
    public static final int DEFAULT_SCAN_DEPTH = 1;
    public static final int MAX_SCAN_DEPTH = Integer.MAX_VALUE;
    private static final Logger LOG = LoggerFactory.getLogger(Scanner.class);
    private static final AtomicInteger SCANNER_IDS = new AtomicInteger();
    private int _scanInterval;
    private final AtomicInteger _scanCount = new AtomicInteger(0);
    private final List<Listener> _listeners = new CopyOnWriteArrayList<Listener>();
    private Map<Path, MetaData> _prevScan;
    private FilenameFilter _filter;
    private final Map<Path, IncludeExcludeSet<PathMatcher, Path>> _scannables = new ConcurrentHashMap<Path, IncludeExcludeSet<PathMatcher, Path>>();
    private boolean _autoStartScanning = true;
    private boolean _scanningStarted = false;
    private boolean _reportExisting = true;
    private boolean _reportDirs = true;
    private Scheduler.Task _task;
    private final Scheduler _scheduler;
    private int _scanDepth = 1;
    private final LinkOption[] _linkOptions;

    public Scanner() {
        this(null);
    }

    public Scanner(Scheduler scheduler) {
        this(scheduler, true);
    }

    public Scanner(Scheduler scheduler, boolean reportRealPaths) {
        LinkOption[] linkOptionArray;
        this._scheduler = scheduler == null ? new ScheduledExecutorScheduler("Scanner-" + SCANNER_IDS.getAndIncrement(), true, 1) : scheduler;
        this.addBean(this._scheduler);
        if (reportRealPaths) {
            linkOptionArray = new LinkOption[]{};
        } else {
            LinkOption[] linkOptionArray2 = new LinkOption[1];
            linkOptionArray = linkOptionArray2;
            linkOptionArray2[0] = LinkOption.NOFOLLOW_LINKS;
        }
        this._linkOptions = linkOptionArray;
    }

    public int getScanInterval() {
        return this._scanInterval;
    }

    public void setScanInterval(int scanInterval) {
        if (this.isRunning()) {
            throw new IllegalStateException("Scanner started");
        }
        this._scanInterval = scanInterval;
    }

    public void setScanDirs(List<File> dirs) {
        if (this.isRunning()) {
            throw new IllegalStateException("Scanner started");
        }
        this._scannables.clear();
        if (dirs == null) {
            return;
        }
        for (File f : dirs) {
            if (f.isDirectory()) {
                this.addDirectory(f.toPath());
                continue;
            }
            this.addFile(f.toPath());
        }
    }

    public void addFile(Path path) {
        if (this.isRunning()) {
            throw new IllegalStateException("Scanner started");
        }
        if (path == null) {
            throw new IllegalStateException("Null path");
        }
        try {
            Path real = path.toRealPath(this._linkOptions);
            if (!Files.exists(real, new LinkOption[0]) || Files.isDirectory(real, new LinkOption[0])) {
                throw new IllegalStateException("Not file or doesn't exist: " + path);
            }
            this._scannables.putIfAbsent(real, new IncludeExcludeSet(PathMatcherSet.class));
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public IncludeExcludeSet<PathMatcher, Path> addDirectory(Path p) {
        if (this.isRunning()) {
            throw new IllegalStateException("Scanner started");
        }
        if (p == null) {
            throw new IllegalStateException("Null path");
        }
        try {
            Path real = p.toRealPath(this._linkOptions);
            if (!Files.exists(real, new LinkOption[0]) || !Files.isDirectory(real, new LinkOption[0])) {
                throw new IllegalStateException("Not directory or doesn't exist: " + p);
            }
            IncludeExcludeSet<PathMatcher, Path> includesExcludes = new IncludeExcludeSet<PathMatcher, Path>(PathMatcherSet.class);
            IncludeExcludeSet prev = this._scannables.putIfAbsent(real, includesExcludes);
            if (prev != null) {
                includesExcludes = prev;
            }
            return includesExcludes;
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Deprecated
    public void setFilenameFilter(FilenameFilter filter) {
        this._filter = filter;
    }

    @Deprecated
    public FilenameFilter getFilenameFilter() {
        return this._filter;
    }

    public Set<Path> getScannables() {
        return Collections.unmodifiableSet(this._scannables.keySet());
    }

    public int getScanDepth() {
        return this._scanDepth;
    }

    public void setScanDepth(int scanDepth) {
        if (this.isRunning()) {
            throw new IllegalStateException("Scanner started");
        }
        this._scanDepth = scanDepth;
    }

    public boolean isAutoStartScanning() {
        return this._autoStartScanning;
    }

    public void setAutoStartScanning(boolean autostart) {
        this._autoStartScanning = autostart;
    }

    public void setReportExistingFilesOnStartup(boolean reportExisting) {
        if (this.isRunning()) {
            throw new IllegalStateException("Scanner started");
        }
        this._reportExisting = reportExisting;
    }

    public boolean getReportExistingFilesOnStartup() {
        return this._reportExisting;
    }

    public void setReportDirs(boolean dirs) {
        if (this.isRunning()) {
            throw new IllegalStateException("Scanner started");
        }
        this._reportDirs = dirs;
    }

    public boolean getReportDirs() {
        return this._reportDirs;
    }

    public void addListener(Listener listener) {
        if (listener == null) {
            return;
        }
        this._listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        if (listener == null) {
            return;
        }
        this._listeners.remove(listener);
    }

    @Override
    public void doStart() throws Exception {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Scanner start: autoStartScanning={}, reportExists={}, depth={}, rprtDirs={}, interval={}, filter={}, scannables={}", new Object[]{this.isAutoStartScanning(), this._reportExisting, this._scanDepth, this._reportDirs, this._scanInterval, this._filter, this._scannables});
        }
        super.doStart();
        if (this.isAutoStartScanning()) {
            this.startScanning();
        }
    }

    public void startScanning() {
        if (!this.isRunning()) {
            throw new IllegalStateException("Scanner not started");
        }
        if (this._scanningStarted) {
            return;
        }
        this._scanningStarted = true;
        if (LOG.isDebugEnabled()) {
            LOG.debug("{}.startup()", (Object)this.getClass().getSimpleName());
        }
        if (this._reportExisting) {
            this.scan();
            this.scan();
        } else {
            this._prevScan = this.scanFiles();
        }
        this.schedule();
    }

    private void schedule() {
        if (this.isRunning() && this.getScanInterval() > 0) {
            this._task = this._scheduler.schedule(new ScanTask(), 1010L * (long)this.getScanInterval(), TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void doStop() throws Exception {
        Scheduler.Task task = this._task;
        this._task = null;
        if (task != null) {
            task.cancel();
        }
        this._scanningStarted = false;
    }

    public void reset() {
        if (!this.isStopped()) {
            throw new IllegalStateException("Not stopped");
        }
        this._scannables.clear();
        this._prevScan = null;
    }

    public boolean exists(String path) {
        for (Path p : this._scannables.keySet()) {
            if (!p.resolve(path).toFile().exists()) continue;
            return true;
        }
        return false;
    }

    public void nudge() {
        if (!this.isRunning()) {
            throw new IllegalStateException("Scanner not running");
        }
        this.scan(Callback.NOOP);
    }

    public void scan(Callback complete) {
        Scheduler scheduler = this._scheduler;
        if (!this.isRunning() || scheduler == null) {
            complete.failed(new IllegalStateException("Scanner not running"));
            return;
        }
        scheduler.schedule(() -> {
            try {
                this.scan();
                complete.succeeded();
            }
            catch (Throwable t) {
                complete.failed(t);
            }
        }, 0L, TimeUnit.MILLISECONDS);
    }

    void scan() {
        int cycle = this._scanCount.incrementAndGet();
        this.reportScanStart(cycle);
        Map<Path, MetaData> currentScan = this.scanFiles();
        this.reportDifferences(currentScan, this._prevScan == null ? Collections.emptyMap() : Collections.unmodifiableMap(this._prevScan));
        this._prevScan = currentScan;
        this.reportScanEnd(cycle);
    }

    private Map<Path, MetaData> scanFiles() {
        HashMap<Path, MetaData> currentScan = new HashMap<Path, MetaData>();
        for (Map.Entry<Path, IncludeExcludeSet<PathMatcher, Path>> entry : this._scannables.entrySet()) {
            try {
                Files.walkFileTree(entry.getKey(), EnumSet.allOf(FileVisitOption.class), this._scanDepth, new Visitor(entry.getKey(), entry.getValue(), currentScan));
            }
            catch (IOException e) {
                LOG.warn("Error scanning files.", (Throwable)e);
            }
        }
        return currentScan;
    }

    private void reportDifferences(Map<Path, MetaData> currentScan, Map<Path, MetaData> oldScan) {
        HashMap<Path, Notification> changes = new HashMap<Path, Notification>();
        HashSet<Path> oldScanKeys = new HashSet<Path>(oldScan.keySet());
        oldScanKeys.removeAll(currentScan.keySet());
        for (Path path : oldScanKeys) {
            changes.put(path, Notification.REMOVED);
        }
        for (Map.Entry entry : currentScan.entrySet()) {
            MetaData current = (MetaData)entry.getValue();
            MetaData previous = oldScan.get(entry.getKey());
            if (previous == null) {
                current._status = Status.ADDED;
                continue;
            }
            if (current.isModified(previous)) {
                if (previous._status == Status.ADDED) {
                    current._status = Status.ADDED;
                    continue;
                }
                current._status = Status.CHANGED;
                continue;
            }
            if (previous._status == Status.ADDED) {
                changes.put((Path)entry.getKey(), Notification.ADDED);
            } else if (previous._status == Status.CHANGED) {
                changes.put((Path)entry.getKey(), Notification.CHANGED);
            }
            current._status = Status.STABLE;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("scanned {}", this._scannables.keySet());
        }
        block7: for (Map.Entry entry : changes.entrySet()) {
            switch ((Notification)((Object)entry.getValue())) {
                case ADDED: {
                    this.reportAddition((Path)entry.getKey());
                    continue block7;
                }
                case CHANGED: {
                    this.reportChange((Path)entry.getKey());
                    continue block7;
                }
                case REMOVED: {
                    this.reportRemoval((Path)entry.getKey());
                    continue block7;
                }
            }
            LOG.warn("Unknown file change: {}", entry.getValue());
        }
        this.reportBulkChanges(changes.keySet());
    }

    private void warn(Object listener, Path path, Throwable th) {
        LOG.warn("{} failed on '{}'", new Object[]{listener, path, th});
    }

    private void reportAddition(Path path) {
        for (Listener l : this._listeners) {
            try {
                if (!(l instanceof DiscreteListener)) continue;
                ((DiscreteListener)l).pathAdded(path);
            }
            catch (Throwable e) {
                this.warn(l, path, e);
            }
        }
    }

    private void reportRemoval(Path path) {
        for (Listener l : this._listeners) {
            try {
                if (!(l instanceof DiscreteListener)) continue;
                ((DiscreteListener)l).pathRemoved(path);
            }
            catch (Throwable e) {
                this.warn(l, path, e);
            }
        }
    }

    private void reportChange(Path path) {
        if (path == null) {
            return;
        }
        for (Listener l : this._listeners) {
            try {
                if (!(l instanceof DiscreteListener)) continue;
                ((DiscreteListener)l).pathChanged(path);
            }
            catch (Throwable e) {
                this.warn(l, path, e);
            }
        }
    }

    private void reportBulkChanges(Set<Path> paths) {
        if (paths == null || paths.isEmpty()) {
            return;
        }
        for (Listener l : this._listeners) {
            try {
                if (!(l instanceof BulkListener)) continue;
                ((BulkListener)l).pathsChanged(paths);
            }
            catch (Throwable e) {
                LOG.warn("{} failed on '{}'", new Object[]{l, paths, e});
            }
        }
    }

    private void reportScanStart(int cycle) {
        for (Listener listener : this._listeners) {
            try {
                if (!(listener instanceof ScanCycleListener)) continue;
                ((ScanCycleListener)listener).scanStarted(cycle);
            }
            catch (Exception e) {
                LOG.warn("{} failed on scan start for cycle {}", new Object[]{listener, cycle, e});
            }
        }
    }

    private void reportScanEnd(int cycle) {
        for (Listener listener : this._listeners) {
            try {
                if (!(listener instanceof ScanCycleListener)) continue;
                ((ScanCycleListener)listener).scanEnded(cycle);
            }
            catch (Exception e) {
                LOG.warn("{} failed on scan end for cycle {}", new Object[]{listener, cycle, e});
            }
        }
    }

    static class PathMatcherSet
    extends HashSet<PathMatcher>
    implements Predicate<Path> {
        PathMatcherSet() {
        }

        @Override
        public boolean test(Path p) {
            for (PathMatcher pm : this) {
                if (!pm.matches(p)) continue;
                return true;
            }
            return false;
        }
    }

    private class ScanTask
    implements Runnable {
        private ScanTask() {
        }

        @Override
        public void run() {
            Scanner.this.scan();
            Scanner.this.schedule();
        }
    }

    private class Visitor
    implements FileVisitor<Path> {
        Map<Path, MetaData> scanInfoMap;
        IncludeExcludeSet<PathMatcher, Path> rootIncludesExcludes;
        Path root;

        private Visitor(Path root, IncludeExcludeSet<PathMatcher, Path> rootIncludesExcludes, Map<Path, MetaData> scanInfoMap) {
            this.root = root;
            this.rootIncludesExcludes = rootIncludesExcludes;
            this.scanInfoMap = scanInfoMap;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            if (!Files.exists(dir, new LinkOption[0])) {
                return FileVisitResult.SKIP_SUBTREE;
            }
            dir = dir.toRealPath(Scanner.this._linkOptions);
            File f = dir.toFile();
            if (Scanner.this._reportDirs && !this.scanInfoMap.containsKey(dir)) {
                boolean accepted = false;
                if (this.rootIncludesExcludes != null && !this.rootIncludesExcludes.isEmpty()) {
                    accepted = this.rootIncludesExcludes.test(dir);
                } else if (Scanner.this._filter == null || Scanner.this._filter.accept(f.getParentFile(), f.getName())) {
                    accepted = true;
                }
                if (accepted) {
                    this.scanInfoMap.put(dir, new MetaData(f.lastModified(), f.isDirectory() ? 0L : f.length()));
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("scan accepted dir {} mod={}", (Object)f, (Object)f.lastModified());
                    }
                }
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
            if (!Files.exists(path = path.toRealPath(Scanner.this._linkOptions), new LinkOption[0])) {
                return FileVisitResult.CONTINUE;
            }
            File f = path.toFile();
            boolean accepted = false;
            if (f.isFile() || f.isDirectory() && Scanner.this._reportDirs && !this.scanInfoMap.containsKey(path)) {
                if (this.rootIncludesExcludes != null && !this.rootIncludesExcludes.isEmpty()) {
                    accepted = this.rootIncludesExcludes.test(path);
                } else if (Scanner.this._filter == null || Scanner.this._filter.accept(f.getParentFile(), f.getName())) {
                    accepted = true;
                }
            }
            if (accepted) {
                this.scanInfoMap.put(path, new MetaData(f.lastModified(), f.isDirectory() ? 0L : f.length()));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("scan accepted {} mod={}", (Object)f, (Object)f.lastModified());
                }
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            LOG.warn("FileVisit failed: {}", (Object)file, (Object)exc);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }

    static enum Notification {
        ADDED,
        CHANGED,
        REMOVED;

    }

    private static class MetaData {
        final long _lastModified;
        final long _size;
        Status _status;

        public MetaData(long lastModified, long size) {
            this._lastModified = lastModified;
            this._size = size;
        }

        public boolean isModified(MetaData m) {
            return m._lastModified != this._lastModified || m._size != this._size;
        }

        public String toString() {
            return "[lm=" + this._lastModified + ",sz=" + this._size + ",s=" + this._status + "]";
        }
    }

    private static enum Status {
        ADDED,
        CHANGED,
        REMOVED,
        STABLE;

    }

    public static interface Listener {
    }

    public static interface DiscreteListener
    extends Listener {
        default public void pathChanged(Path path) throws Exception {
            path.toString();
            this.fileChanged(path.toString());
        }

        default public void pathAdded(Path path) throws Exception {
            this.fileAdded(path.toString());
        }

        default public void pathRemoved(Path path) throws Exception {
            this.fileRemoved(path.toString());
        }

        public void fileChanged(String var1) throws Exception;

        public void fileAdded(String var1) throws Exception;

        public void fileRemoved(String var1) throws Exception;
    }

    public static interface BulkListener
    extends Listener {
        default public void pathsChanged(Set<Path> paths) throws Exception {
            this.filesChanged(paths.stream().map(Path::toString).collect(Collectors.toSet()));
        }

        public void filesChanged(Set<String> var1) throws Exception;
    }

    public static interface ScanCycleListener
    extends Listener {
        default public void scanStarted(int cycle) throws Exception {
        }

        default public void scanEnded(int cycle) throws Exception {
        }
    }
}

