/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.util.IO
 *  org.eclipse.jetty.util.annotation.ManagedAttribute
 *  org.eclipse.jetty.util.annotation.ManagedOperation
 *  org.eclipse.jetty.util.component.ContainerLifeCycle
 *  org.eclipse.jetty.util.component.Dumpable
 *  org.eclipse.jetty.util.component.DumpableCollection
 *  org.eclipse.jetty.util.statistic.SampleStatistic
 *  org.eclipse.jetty.util.thread.AutoLock
 *  org.eclipse.jetty.util.thread.ExecutionStrategy
 *  org.eclipse.jetty.util.thread.ExecutionStrategy$Producer
 *  org.eclipse.jetty.util.thread.Scheduler$Task
 *  org.eclipse.jetty.util.thread.strategy.AdaptiveExecutionStrategy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.eclipse.jetty.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.SelectorManager;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.statistic.SampleStatistic;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.strategy.AdaptiveExecutionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagedSelector
extends ContainerLifeCycle
implements Dumpable {
    private static final Logger LOG = LoggerFactory.getLogger(ManagedSelector.class);
    private static final boolean FORCE_SELECT_NOW;
    private final AutoLock _lock = new AutoLock();
    private final AtomicBoolean _started = new AtomicBoolean(false);
    private boolean _selecting;
    private final SelectorManager _selectorManager;
    private final int _id;
    private final ExecutionStrategy _strategy;
    private Selector _selector;
    private Deque<SelectorUpdate> _updates = new ArrayDeque<SelectorUpdate>();
    private Deque<SelectorUpdate> _updateable = new ArrayDeque<SelectorUpdate>();
    private final SampleStatistic _keyStats = new SampleStatistic();

    public ManagedSelector(SelectorManager selectorManager, int id) {
        this._selectorManager = selectorManager;
        this._id = id;
        SelectorProducer producer = new SelectorProducer();
        Executor executor = selectorManager.getExecutor();
        this._strategy = new AdaptiveExecutionStrategy((ExecutionStrategy.Producer)producer, executor);
        this.addBean(this._strategy, true);
    }

    public Selector getSelector() {
        return this._selector;
    }

    protected void doStart() throws Exception {
        super.doStart();
        this._selector = this._selectorManager.newSelector();
        this._selectorManager.execute(() -> ((ExecutionStrategy)this._strategy).produce());
        Start start = new Start();
        this.submit(start);
        start._started.await();
    }

    protected void doStop() throws Exception {
        if (this._started.compareAndSet(true, false) && this._selector != null) {
            CloseConnections closeConnections = new CloseConnections();
            this.submit(closeConnections);
            closeConnections._complete.await();
            StopSelector stopSelector = new StopSelector();
            this.submit(stopSelector);
            stopSelector._stopped.await();
        }
        super.doStop();
    }

    @ManagedAttribute(value="Total number of keys", readonly=true)
    public int getTotalKeys() {
        Selector selector = this._selector;
        return selector == null ? 0 : selector.keys().size();
    }

    @ManagedAttribute(value="Average number of selected keys", readonly=true)
    public double getAverageSelectedKeys() {
        return this._keyStats.getMean();
    }

    @ManagedAttribute(value="Maximum number of selected keys", readonly=true)
    public long getMaxSelectedKeys() {
        return this._keyStats.getMax();
    }

    @ManagedAttribute(value="Total number of select() calls", readonly=true)
    public long getSelectCount() {
        return this._keyStats.getCount();
    }

    @ManagedOperation(value="Resets the statistics", impact="ACTION")
    public void resetStats() {
        this._keyStats.reset();
    }

    protected int nioSelect(Selector selector, boolean now) throws IOException {
        return now ? selector.selectNow() : selector.select();
    }

    protected int select(Selector selector) throws IOException {
        try {
            int selected = this.nioSelect(selector, false);
            if (selected == 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Selector {} woken with none selected", (Object)selector);
                }
                if (Thread.interrupted() && !this.isRunning()) {
                    throw new ClosedSelectorException();
                }
                if (FORCE_SELECT_NOW) {
                    selected = this.nioSelect(selector, true);
                }
            }
            return selected;
        }
        catch (ClosedSelectorException x) {
            throw x;
        }
        catch (Throwable x) {
            this.handleSelectFailure(selector, x);
            return 0;
        }
    }

    protected void handleSelectFailure(Selector selector, Throwable failure) throws IOException {
        LOG.info("Caught select() failure, trying to recover: {}", (Object)failure.toString());
        if (LOG.isDebugEnabled()) {
            LOG.debug("", failure);
        }
        Selector newSelector = this._selectorManager.newSelector();
        for (SelectionKey oldKey : selector.keys()) {
            SelectableChannel channel = oldKey.channel();
            int interestOps = ManagedSelector.safeInterestOps(oldKey);
            if (interestOps >= 0) {
                try {
                    Object attachment = oldKey.attachment();
                    SelectionKey newKey = channel.register(newSelector, interestOps, attachment);
                    if (attachment instanceof Selectable) {
                        ((Selectable)attachment).replaceKey(newKey);
                    }
                    oldKey.cancel();
                    if (!LOG.isDebugEnabled()) continue;
                    LOG.debug("Transferred {} iOps={} att={}", new Object[]{channel, interestOps, attachment});
                }
                catch (Throwable t) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Could not transfer {}", (Object)channel, (Object)t);
                    }
                    IO.close((Closeable)channel);
                }
                continue;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Invalid interestOps for {}", (Object)channel);
            }
            IO.close((Closeable)channel);
        }
        IO.close((Closeable)selector);
        this._selector = newSelector;
    }

    protected void onSelectFailed(Throwable cause) {
    }

    public int size() {
        Selector s = this._selector;
        if (s == null) {
            return 0;
        }
        Set<SelectionKey> keys = s.keys();
        if (keys == null) {
            return 0;
        }
        return keys.size();
    }

    public void submit(SelectorUpdate update) {
        this.submit(update, false);
    }

    private void submit(SelectorUpdate update, boolean lazy) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Queued change lazy={} {} on {}", new Object[]{lazy, update, this});
        }
        Selector selector = null;
        try (AutoLock l = this._lock.lock();){
            this._updates.offer(update);
            if (this._selecting && !lazy) {
                selector = this._selector;
                this._selecting = false;
            }
        }
        if (selector != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Wakeup on submit {}", (Object)this);
            }
            selector.wakeup();
        }
    }

    private void wakeup() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Wakeup {}", (Object)this);
        }
        Selector selector = null;
        try (AutoLock l = this._lock.lock();){
            if (this._selecting) {
                selector = this._selector;
                this._selecting = false;
            }
        }
        if (selector != null) {
            selector.wakeup();
        }
    }

    private void execute(Runnable task) {
        block2: {
            try {
                this._selectorManager.execute(task);
            }
            catch (RejectedExecutionException x) {
                if (!(task instanceof Closeable)) break block2;
                IO.close((Closeable)((Closeable)((Object)task)));
            }
        }
    }

    private void processConnect(SelectionKey key, Connect connect) {
        SelectableChannel channel = key.channel();
        try {
            key.attach(connect.attachment);
            boolean connected = this._selectorManager.doFinishConnect(channel);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Connected {} {}", (Object)connected, (Object)channel);
            }
            if (connected) {
                if (!connect.timeout.cancel()) {
                    throw new SocketTimeoutException("Concurrent Connect Timeout");
                }
            } else {
                throw new ConnectException();
            }
            key.interestOps(0);
            this.execute(new CreateEndPoint(connect, key));
        }
        catch (Throwable x) {
            connect.failed(x);
        }
    }

    protected void endPointOpened(EndPoint endPoint) {
        this._selectorManager.endPointOpened(endPoint);
    }

    protected void endPointClosed(EndPoint endPoint) {
        this._selectorManager.endPointClosed(endPoint);
    }

    void createEndPoint(SelectableChannel channel, SelectionKey selectionKey) throws IOException {
        EndPoint endPoint = this._selectorManager.newEndPoint(channel, this, selectionKey);
        Object context = selectionKey.attachment();
        Connection connection = this._selectorManager.newConnection(channel, endPoint, context);
        endPoint.setConnection(connection);
        this.submit(selector -> {
            SelectionKey key = selectionKey;
            if (key.selector() != selector && (key = channel.keyFor(selector)) != null && endPoint instanceof Selectable) {
                ((Selectable)((Object)endPoint)).replaceKey(key);
            }
            if (key != null) {
                key.attach(endPoint);
            }
        }, true);
        endPoint.onOpen();
        this.endPointOpened(endPoint);
        this._selectorManager.connectionOpened(connection, context);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Created {}", (Object)endPoint);
        }
    }

    void destroyEndPoint(EndPoint endPoint, Throwable cause) {
        this.wakeup();
        this.execute(new DestroyEndPoint(endPoint, cause));
    }

    private int getActionSize() {
        try (AutoLock l = this._lock.lock();){
            int n = this._updates.size();
            return n;
        }
    }

    static int safeReadyOps(SelectionKey selectionKey) {
        try {
            return selectionKey.readyOps();
        }
        catch (Throwable x) {
            LOG.trace("IGNORED", x);
            return -1;
        }
    }

    static int safeInterestOps(SelectionKey selectionKey) {
        try {
            return selectionKey.interestOps();
        }
        catch (Throwable x) {
            LOG.trace("IGNORED", x);
            return -1;
        }
    }

    public void dump(Appendable out, String indent) throws IOException {
        Selector selector = this._selector;
        if (selector != null && selector.isOpen()) {
            ArrayList<SelectorUpdate> updates;
            DumpKeys dump = new DumpKeys();
            String updatesAt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now());
            try (AutoLock l = this._lock.lock();){
                updates = new ArrayList<SelectorUpdate>(this._updates);
                this._updates.addFirst(dump);
                this._selecting = false;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("wakeup on dump {}", (Object)this);
            }
            selector.wakeup();
            List<String> keys = dump.get(5L, TimeUnit.SECONDS);
            String keysAt = DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(ZonedDateTime.now());
            if (keys == null) {
                keys = Collections.singletonList("No dump keys retrieved");
            }
            this.dumpObjects(out, indent, new Object[]{new DumpableCollection("updates @ " + updatesAt, updates), new DumpableCollection("keys @ " + keysAt, keys)});
        } else {
            this.dumpObjects(out, indent, new Object[0]);
        }
    }

    public String toString() {
        Selector selector = this._selector;
        return String.format("%s id=%s keys=%d selected=%d updates=%d", super.toString(), this._id, selector != null && selector.isOpen() ? selector.keys().size() : -1, selector != null && selector.isOpen() ? selector.selectedKeys().size() : -1, this.getActionSize());
    }

    static {
        String property = System.getProperty("org.eclipse.jetty.io.forceSelectNow");
        FORCE_SELECT_NOW = property != null ? Boolean.parseBoolean(property) : (property = System.getProperty("os.name")) != null && property.toLowerCase(Locale.ENGLISH).contains("windows");
    }

    private class SelectorProducer
    implements ExecutionStrategy.Producer {
        private Set<SelectionKey> _keys = Collections.emptySet();
        private Iterator<SelectionKey> _cursor = Collections.emptyIterator();

        private SelectorProducer() {
        }

        public Runnable produce() {
            do {
                Runnable task;
                if ((task = this.processSelected()) != null) {
                    return task;
                }
                this.processUpdates();
                this.updateKeys();
            } while (this.select());
            return null;
        }

        private void processUpdates() {
            int updates;
            try (AutoLock l = ManagedSelector.this._lock.lock();){
                Deque<SelectorUpdate> updates2 = ManagedSelector.this._updates;
                ManagedSelector.this._updates = ManagedSelector.this._updateable;
                ManagedSelector.this._updateable = updates2;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("updateable {}", (Object)ManagedSelector.this._updateable.size());
            }
            Selector selector = ManagedSelector.this._selector;
            for (SelectorUpdate update : ManagedSelector.this._updateable) {
                if (selector == null) break;
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("update {}", (Object)update);
                    }
                    update.update(selector);
                }
                catch (Throwable x) {
                    LOG.warn("Cannot update selector {}", (Object)ManagedSelector.this, (Object)x);
                }
            }
            ManagedSelector.this._updateable.clear();
            try (AutoLock l = ManagedSelector.this._lock.lock();){
                updates = ManagedSelector.this._updates.size();
                boolean bl = ManagedSelector.this._selecting = updates == 0;
                if (ManagedSelector.this._selecting) {
                    selector = null;
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("updates {}", (Object)updates);
            }
            if (selector != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("wakeup on updates {}", (Object)this);
                }
                selector.wakeup();
            }
        }

        private boolean select() {
            block14: {
                try {
                    int updates;
                    Selector selector = ManagedSelector.this._selector;
                    if (selector == null) break block14;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Selector {} waiting with {} keys", (Object)selector, (Object)selector.keys().size());
                    }
                    int selected = ManagedSelector.this.select(selector);
                    selector = ManagedSelector.this._selector;
                    if (selector == null) break block14;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Selector {} woken up from select, {}/{}/{} selected", new Object[]{selector, selected, selector.selectedKeys().size(), selector.keys().size()});
                    }
                    try (AutoLock l = ManagedSelector.this._lock.lock();){
                        ManagedSelector.this._selecting = false;
                        updates = ManagedSelector.this._updates.size();
                    }
                    this._keys = selector.selectedKeys();
                    int selectedKeys = this._keys.size();
                    if (selectedKeys > 0) {
                        ManagedSelector.this._keyStats.record((long)selectedKeys);
                    }
                    Iterator<Object> iterator = this._cursor = selectedKeys > 0 ? this._keys.iterator() : Collections.emptyIterator();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Selector {} processing {} keys, {} updates", new Object[]{selector, selectedKeys, updates});
                    }
                    return true;
                }
                catch (Throwable x) {
                    IO.close((Closeable)ManagedSelector.this._selector);
                    ManagedSelector.this._selector = null;
                    if (ManagedSelector.this.isRunning()) {
                        LOG.warn("Fatal select() failure", x);
                        ManagedSelector.this.onSelectFailed(x);
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.warn("select() failure", x);
                    }
                    LOG.warn("select() failure {}", (Object)x.toString());
                }
            }
            return false;
        }

        private Runnable processSelected() {
            while (this._cursor.hasNext()) {
                SelectionKey key = this._cursor.next();
                Object attachment = key.attachment();
                SelectableChannel channel = key.channel();
                if (key.isValid()) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("selected {} {} {} ", new Object[]{ManagedSelector.safeReadyOps(key), key, attachment});
                    }
                    try {
                        if (attachment instanceof Selectable) {
                            Runnable task = ((Selectable)attachment).onSelected();
                            if (task == null) continue;
                            return task;
                        }
                        if (key.isConnectable()) {
                            ManagedSelector.this.processConnect(key, (Connect)attachment);
                            continue;
                        }
                        throw new IllegalStateException("key=" + key + ", att=" + attachment + ", iOps=" + ManagedSelector.safeInterestOps(key) + ", rOps=" + ManagedSelector.safeReadyOps(key));
                    }
                    catch (CancelledKeyException x) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Ignoring cancelled key for channel {}", (Object)channel);
                        }
                        IO.close((Closeable)(attachment instanceof EndPoint ? (EndPoint)attachment : channel));
                        continue;
                    }
                    catch (Throwable x) {
                        LOG.warn("Could not process key for channel {}", (Object)channel, (Object)x);
                        IO.close((Closeable)(attachment instanceof EndPoint ? (EndPoint)attachment : channel));
                        continue;
                    }
                }
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Selector loop ignoring invalid key for channel {}", (Object)channel);
                }
                IO.close((Closeable)(attachment instanceof EndPoint ? (EndPoint)attachment : channel));
            }
            return null;
        }

        private void updateKeys() {
            for (SelectionKey key : this._keys) {
                Object attachment = key.attachment();
                if (!(attachment instanceof Selectable)) continue;
                ((Selectable)attachment).updateKey();
            }
            this._keys.clear();
        }

        public String toString() {
            return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
        }
    }

    private class Start
    implements SelectorUpdate {
        private final CountDownLatch _started = new CountDownLatch(1);

        private Start() {
        }

        @Override
        public void update(Selector selector) {
            ManagedSelector.this._started.set(true);
            this._started.countDown();
        }
    }

    public static interface SelectorUpdate {
        public void update(Selector var1);
    }

    private class CloseConnections
    implements SelectorUpdate {
        private final Set<Closeable> _closed;
        private final CountDownLatch _complete = new CountDownLatch(1);

        private CloseConnections() {
            this(null);
        }

        private CloseConnections(Set<Closeable> closed) {
            this._closed = closed;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void update(Selector selector) {
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Closing {} connections on {}", (Object)selector.keys().size(), (Object)ManagedSelector.this);
                }
                for (SelectionKey key : selector.keys()) {
                    if (key == null || !key.isValid()) continue;
                    Closeable closeable = null;
                    Object attachment = key.attachment();
                    if (attachment instanceof EndPoint) {
                        EndPoint endPoint = (EndPoint)attachment;
                        Connection connection = endPoint.getConnection();
                        closeable = Objects.requireNonNullElse(connection, endPoint);
                    }
                    if (closeable == null) continue;
                    if (this._closed == null) {
                        IO.close(closeable);
                        continue;
                    }
                    if (this._closed.contains(closeable)) continue;
                    this._closed.add(closeable);
                    IO.close((Closeable)closeable);
                }
            }
            finally {
                this._complete.countDown();
            }
        }
    }

    private class StopSelector
    implements SelectorUpdate {
        private final CountDownLatch _stopped = new CountDownLatch(1);

        private StopSelector() {
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void update(Selector selector) {
            try {
                for (SelectionKey key : selector.keys()) {
                    Object attachment;
                    if (key == null || !((attachment = key.attachment()) instanceof Closeable)) continue;
                    IO.close((Closeable)((Closeable)attachment));
                }
                ManagedSelector.this._selector = null;
                IO.close((Closeable)selector);
            }
            finally {
                this._stopped.countDown();
            }
        }
    }

    public static interface Selectable {
        public Runnable onSelected();

        public void updateKey();

        public void replaceKey(SelectionKey var1);
    }

    class Connect
    implements SelectorUpdate,
    Runnable {
        private final AtomicBoolean failed = new AtomicBoolean();
        private final SelectableChannel channel;
        private final Object attachment;
        private final Scheduler.Task timeout;

        Connect(SelectableChannel channel, Object attachment) {
            this.channel = channel;
            this.attachment = attachment;
            long timeout = ManagedSelector.this._selectorManager.getConnectTimeout();
            this.timeout = timeout > 0L ? ManagedSelector.this._selectorManager.getScheduler().schedule((Runnable)this, timeout, TimeUnit.MILLISECONDS) : null;
        }

        @Override
        public void update(Selector selector) {
            try {
                this.channel.register(selector, 8, this);
            }
            catch (Throwable x) {
                this.failed(x);
            }
        }

        @Override
        public void run() {
            if (ManagedSelector.this._selectorManager.isConnectionPending(this.channel)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Channel {} timed out while connecting, closing it", (Object)this.channel);
                }
                this.failed(new SocketTimeoutException("Connect Timeout"));
            }
        }

        public void failed(Throwable failure) {
            if (this.failed.compareAndSet(false, true)) {
                if (this.timeout != null) {
                    this.timeout.cancel();
                }
                IO.close((Closeable)this.channel);
                ManagedSelector.this._selectorManager.connectionFailed(this.channel, failure, this.attachment);
            }
        }

        public String toString() {
            return String.format("Connect@%x{%s,%s}", this.hashCode(), this.channel, this.attachment);
        }
    }

    private final class CreateEndPoint
    implements Runnable {
        private final Connect _connect;
        private final SelectionKey _key;

        private CreateEndPoint(Connect connect, SelectionKey key) {
            this._connect = connect;
            this._key = key;
        }

        @Override
        public void run() {
            try {
                ManagedSelector.this.createEndPoint(this._connect.channel, this._key);
            }
            catch (Throwable failure) {
                IO.close((Closeable)this._connect.channel);
                LOG.warn("Could not create EndPoint {}: {}", (Object)this._connect.channel, (Object)String.valueOf(failure));
                if (LOG.isDebugEnabled()) {
                    LOG.debug("", failure);
                }
                this._connect.failed(failure);
            }
        }

        public String toString() {
            return String.format("%s@%x{%s}", this.getClass().getSimpleName(), this.hashCode(), this._connect);
        }
    }

    private class DestroyEndPoint
    implements Runnable,
    Closeable {
        private final EndPoint endPoint;
        private final Throwable cause;

        private DestroyEndPoint(EndPoint endPoint, Throwable cause) {
            this.endPoint = endPoint;
            this.cause = cause;
        }

        @Override
        public void run() {
            Connection connection;
            if (LOG.isDebugEnabled()) {
                LOG.debug("Destroyed {}", (Object)this.endPoint);
            }
            if ((connection = this.endPoint.getConnection()) != null) {
                ManagedSelector.this._selectorManager.connectionClosed(connection, this.cause);
            }
            ManagedSelector.this.endPointClosed(this.endPoint);
        }

        @Override
        public void close() {
            this.run();
        }
    }

    private static class DumpKeys
    implements SelectorUpdate {
        private final CountDownLatch latch = new CountDownLatch(1);
        private List<String> keys;

        private DumpKeys() {
        }

        @Override
        public void update(Selector selector) {
            Set<SelectionKey> selectorKeys = selector.keys();
            ArrayList<String> list = new ArrayList<String>(selectorKeys.size());
            for (SelectionKey key : selectorKeys) {
                if (key == null) continue;
                list.add(String.format("SelectionKey@%x{i=%d}->%s", key.hashCode(), ManagedSelector.safeInterestOps(key), key.attachment()));
            }
            this.keys = list;
            this.latch.countDown();
        }

        public List<String> get(long timeout, TimeUnit unit) {
            try {
                this.latch.await(timeout, unit);
            }
            catch (InterruptedException x) {
                LOG.trace("IGNORED", (Throwable)x);
            }
            return this.keys;
        }
    }

    class Accept
    implements SelectorUpdate,
    Runnable,
    Closeable {
        private final SelectableChannel channel;
        private final Object attachment;
        private SelectionKey key;

        Accept(SelectableChannel channel, Object attachment) {
            this.channel = channel;
            this.attachment = attachment;
            ManagedSelector.this._selectorManager.onAccepting(channel);
        }

        @Override
        public void close() {
            if (LOG.isDebugEnabled()) {
                LOG.debug("closed accept of {}", (Object)this.channel);
            }
            IO.close((Closeable)this.channel);
        }

        @Override
        public void update(Selector selector) {
            block2: {
                try {
                    this.key = this.channel.register(selector, 0, this.attachment);
                    ManagedSelector.this.execute(this);
                }
                catch (Throwable x) {
                    IO.close((Closeable)this.channel);
                    ManagedSelector.this._selectorManager.onAcceptFailed(this.channel, x);
                    if (!LOG.isDebugEnabled()) break block2;
                    LOG.debug("Could not register channel after accept {}", (Object)this.channel, (Object)x);
                }
            }
        }

        @Override
        public void run() {
            try {
                ManagedSelector.this.createEndPoint(this.channel, this.key);
                ManagedSelector.this._selectorManager.onAccepted(this.channel);
            }
            catch (Throwable x) {
                this.failed(x);
            }
        }

        protected void failed(Throwable failure) {
            IO.close((Closeable)this.channel);
            if (LOG.isDebugEnabled()) {
                LOG.warn("Could not accept {}", (Object)this.channel, (Object)failure);
            } else {
                LOG.warn("Could not accept {}: {}", (Object)this.channel, (Object)String.valueOf(failure));
            }
            ManagedSelector.this._selectorManager.onAcceptFailed(this.channel, failure);
        }

        public String toString() {
            return String.format("%s@%x[%s]", this.getClass().getSimpleName(), this.hashCode(), this.channel);
        }
    }

    class Acceptor
    implements SelectorUpdate,
    Selectable,
    Closeable {
        private final SelectableChannel _channel;
        private SelectionKey _key;

        Acceptor(SelectableChannel channel) {
            this._channel = channel;
        }

        @Override
        public void update(Selector selector) {
            try {
                this._key = this._channel.register(selector, 16, this);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("{} acceptor={}", (Object)this, (Object)this._channel);
                }
            }
            catch (Throwable x) {
                IO.close((Closeable)this._channel);
                LOG.warn("Unable to register OP_ACCEPT on selector for {}", (Object)this._channel, (Object)x);
            }
        }

        @Override
        public Runnable onSelected() {
            SelectableChannel channel = null;
            try {
                while ((channel = ManagedSelector.this._selectorManager.doAccept(this._channel)) != null) {
                    ManagedSelector.this._selectorManager.accepted(channel);
                }
            }
            catch (Throwable x) {
                LOG.warn("Accept failed for channel {}", (Object)channel, (Object)x);
                IO.close((Closeable)channel);
            }
            return null;
        }

        @Override
        public void updateKey() {
        }

        @Override
        public void replaceKey(SelectionKey newKey) {
            this._key = newKey;
        }

        @Override
        public void close() throws IOException {
            ManagedSelector.this.submit(selector -> this._key.cancel());
        }
    }
}

