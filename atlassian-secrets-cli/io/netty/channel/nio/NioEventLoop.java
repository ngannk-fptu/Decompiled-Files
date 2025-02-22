/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.EventLoopException;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.nio.NioTask;
import io.netty.channel.nio.SelectedSelectionKeySet;
import io.netty.channel.nio.SelectedSelectionKeySetSelector;
import io.netty.util.IntSupplier;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.ReflectionUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public final class NioEventLoop
extends SingleThreadEventLoop {
    private static final InternalLogger logger;
    private static final int CLEANUP_INTERVAL = 256;
    private static final boolean DISABLE_KEY_SET_OPTIMIZATION;
    private static final int MIN_PREMATURE_SELECTOR_RETURNS = 3;
    private static final int SELECTOR_AUTO_REBUILD_THRESHOLD;
    private final IntSupplier selectNowSupplier = new IntSupplier(){

        @Override
        public int get() throws Exception {
            return NioEventLoop.this.selectNow();
        }
    };
    private Selector selector;
    private Selector unwrappedSelector;
    private SelectedSelectionKeySet selectedKeys;
    private final SelectorProvider provider;
    private static final long AWAKE = -1L;
    private static final long NONE = Long.MAX_VALUE;
    private final AtomicLong nextWakeupNanos = new AtomicLong(-1L);
    private final SelectStrategy selectStrategy;
    private volatile int ioRatio = 50;
    private int cancelledKeys;
    private boolean needsToSelectAgain;

    NioEventLoop(NioEventLoopGroup parent, Executor executor, SelectorProvider selectorProvider, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler, EventLoopTaskQueueFactory taskQueueFactory, EventLoopTaskQueueFactory tailTaskQueueFactory) {
        super(parent, executor, false, NioEventLoop.newTaskQueue(taskQueueFactory), NioEventLoop.newTaskQueue(tailTaskQueueFactory), rejectedExecutionHandler);
        this.provider = ObjectUtil.checkNotNull(selectorProvider, "selectorProvider");
        this.selectStrategy = ObjectUtil.checkNotNull(strategy, "selectStrategy");
        SelectorTuple selectorTuple = this.openSelector();
        this.selector = selectorTuple.selector;
        this.unwrappedSelector = selectorTuple.unwrappedSelector;
    }

    private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {
        if (queueFactory == null) {
            return NioEventLoop.newTaskQueue0(DEFAULT_MAX_PENDING_TASKS);
        }
        return queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
    }

    private SelectorTuple openSelector() {
        AbstractSelector unwrappedSelector;
        try {
            unwrappedSelector = this.provider.openSelector();
        }
        catch (IOException e) {
            throw new ChannelException("failed to open a new selector", e);
        }
        if (DISABLE_KEY_SET_OPTIMIZATION) {
            return new SelectorTuple(unwrappedSelector);
        }
        Object maybeSelectorImplClass = AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                try {
                    return Class.forName("sun.nio.ch.SelectorImpl", false, PlatformDependent.getSystemClassLoader());
                }
                catch (Throwable cause) {
                    return cause;
                }
            }
        });
        if (!(maybeSelectorImplClass instanceof Class) || !((Class)maybeSelectorImplClass).isAssignableFrom(unwrappedSelector.getClass())) {
            if (maybeSelectorImplClass instanceof Throwable) {
                Throwable t = (Throwable)maybeSelectorImplClass;
                logger.trace("failed to instrument a special java.util.Set into: {}", (Object)unwrappedSelector, (Object)t);
            }
            return new SelectorTuple(unwrappedSelector);
        }
        final Class selectorImplClass = (Class)maybeSelectorImplClass;
        final SelectedSelectionKeySet selectedKeySet = new SelectedSelectionKeySet();
        Object maybeException = AccessController.doPrivileged(new PrivilegedAction<Object>(){

            @Override
            public Object run() {
                try {
                    Throwable cause;
                    Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
                    Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
                    if (PlatformDependent.javaVersion() >= 9 && PlatformDependent.hasUnsafe()) {
                        long selectedKeysFieldOffset = PlatformDependent.objectFieldOffset(selectedKeysField);
                        long publicSelectedKeysFieldOffset = PlatformDependent.objectFieldOffset(publicSelectedKeysField);
                        if (selectedKeysFieldOffset != -1L && publicSelectedKeysFieldOffset != -1L) {
                            PlatformDependent.putObject(unwrappedSelector, selectedKeysFieldOffset, selectedKeySet);
                            PlatformDependent.putObject(unwrappedSelector, publicSelectedKeysFieldOffset, selectedKeySet);
                            return null;
                        }
                    }
                    if ((cause = ReflectionUtil.trySetAccessible(selectedKeysField, true)) != null) {
                        return cause;
                    }
                    cause = ReflectionUtil.trySetAccessible(publicSelectedKeysField, true);
                    if (cause != null) {
                        return cause;
                    }
                    selectedKeysField.set(unwrappedSelector, selectedKeySet);
                    publicSelectedKeysField.set(unwrappedSelector, selectedKeySet);
                    return null;
                }
                catch (NoSuchFieldException e) {
                    return e;
                }
                catch (IllegalAccessException e) {
                    return e;
                }
            }
        });
        if (maybeException instanceof Exception) {
            this.selectedKeys = null;
            Exception e = (Exception)maybeException;
            logger.trace("failed to instrument a special java.util.Set into: {}", (Object)unwrappedSelector, (Object)e);
            return new SelectorTuple(unwrappedSelector);
        }
        this.selectedKeys = selectedKeySet;
        logger.trace("instrumented a special java.util.Set into: {}", (Object)unwrappedSelector);
        return new SelectorTuple(unwrappedSelector, new SelectedSelectionKeySetSelector(unwrappedSelector, selectedKeySet));
    }

    public SelectorProvider selectorProvider() {
        return this.provider;
    }

    @Override
    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return NioEventLoop.newTaskQueue0(maxPendingTasks);
    }

    private static Queue<Runnable> newTaskQueue0(int maxPendingTasks) {
        return maxPendingTasks == Integer.MAX_VALUE ? PlatformDependent.newMpscQueue() : PlatformDependent.newMpscQueue(maxPendingTasks);
    }

    public void register(final SelectableChannel ch, final int interestOps, final NioTask<?> task) {
        ObjectUtil.checkNotNull(ch, "ch");
        if (interestOps == 0) {
            throw new IllegalArgumentException("interestOps must be non-zero.");
        }
        if ((interestOps & ~ch.validOps()) != 0) {
            throw new IllegalArgumentException("invalid interestOps: " + interestOps + "(validOps: " + ch.validOps() + ')');
        }
        ObjectUtil.checkNotNull(task, "task");
        if (this.isShutdown()) {
            throw new IllegalStateException("event loop shut down");
        }
        if (this.inEventLoop()) {
            this.register0(ch, interestOps, task);
        } else {
            try {
                this.submit(new Runnable(){

                    @Override
                    public void run() {
                        NioEventLoop.this.register0(ch, interestOps, task);
                    }
                }).sync();
            }
            catch (InterruptedException ignore) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void register0(SelectableChannel ch, int interestOps, NioTask<?> task) {
        try {
            ch.register(this.unwrappedSelector, interestOps, task);
        }
        catch (Exception e) {
            throw new EventLoopException("failed to register a channel", e);
        }
    }

    public int getIoRatio() {
        return this.ioRatio;
    }

    public void setIoRatio(int ioRatio) {
        if (ioRatio <= 0 || ioRatio > 100) {
            throw new IllegalArgumentException("ioRatio: " + ioRatio + " (expected: 0 < ioRatio <= 100)");
        }
        this.ioRatio = ioRatio;
    }

    public void rebuildSelector() {
        if (!this.inEventLoop()) {
            this.execute(new Runnable(){

                @Override
                public void run() {
                    NioEventLoop.this.rebuildSelector0();
                }
            });
            return;
        }
        this.rebuildSelector0();
    }

    @Override
    public int registeredChannels() {
        return this.selector.keys().size() - this.cancelledKeys;
    }

    @Override
    public Iterator<Channel> registeredChannelsIterator() {
        assert (this.inEventLoop());
        final Set<SelectionKey> keys = this.selector.keys();
        if (keys.isEmpty()) {
            return SingleThreadEventLoop.ChannelsReadOnlyIterator.empty();
        }
        return new Iterator<Channel>(){
            final Iterator<SelectionKey> selectionKeyIterator;
            Channel next;
            boolean isDone;
            {
                this.selectionKeyIterator = ObjectUtil.checkNotNull(keys, "selectionKeys").iterator();
            }

            @Override
            public boolean hasNext() {
                if (this.isDone) {
                    return false;
                }
                Channel cur = this.next;
                if (cur == null) {
                    this.next = this.nextOrDone();
                    cur = this.next;
                    return cur != null;
                }
                return true;
            }

            @Override
            public Channel next() {
                if (this.isDone) {
                    throw new NoSuchElementException();
                }
                Channel cur = this.next;
                if (cur == null && (cur = this.nextOrDone()) == null) {
                    throw new NoSuchElementException();
                }
                this.next = this.nextOrDone();
                return cur;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove");
            }

            private Channel nextOrDone() {
                Iterator<SelectionKey> it = this.selectionKeyIterator;
                while (it.hasNext()) {
                    Object attachment;
                    SelectionKey key = it.next();
                    if (!key.isValid() || !((attachment = key.attachment()) instanceof AbstractNioChannel)) continue;
                    return (AbstractNioChannel)attachment;
                }
                this.isDone = true;
                return null;
            }
        };
    }

    private void rebuildSelector0() {
        int nChannels;
        block11: {
            SelectorTuple newSelectorTuple;
            Selector oldSelector = this.selector;
            if (oldSelector == null) {
                return;
            }
            try {
                newSelectorTuple = this.openSelector();
            }
            catch (Exception e) {
                logger.warn("Failed to create a new Selector.", e);
                return;
            }
            nChannels = 0;
            for (SelectionKey key : oldSelector.keys()) {
                Object a = key.attachment();
                try {
                    if (!key.isValid() || key.channel().keyFor(newSelectorTuple.unwrappedSelector) != null) continue;
                    int interestOps = key.interestOps();
                    key.cancel();
                    SelectionKey newKey = key.channel().register(newSelectorTuple.unwrappedSelector, interestOps, a);
                    if (a instanceof AbstractNioChannel) {
                        ((AbstractNioChannel)a).selectionKey = newKey;
                    }
                    ++nChannels;
                }
                catch (Exception e) {
                    logger.warn("Failed to re-register a Channel to the new Selector.", e);
                    if (a instanceof AbstractNioChannel) {
                        AbstractNioChannel ch = (AbstractNioChannel)a;
                        ch.unsafe().close(ch.unsafe().voidPromise());
                        continue;
                    }
                    NioTask task = (NioTask)a;
                    NioEventLoop.invokeChannelUnregistered(task, key, e);
                }
            }
            this.selector = newSelectorTuple.selector;
            this.unwrappedSelector = newSelectorTuple.unwrappedSelector;
            try {
                oldSelector.close();
            }
            catch (Throwable t) {
                if (!logger.isWarnEnabled()) break block11;
                logger.warn("Failed to close the old Selector.", t);
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("Migrated " + nChannels + " channel(s) to the new Selector.");
        }
    }

    /*
     * Exception decompiling
     */
    @Override
    protected void run() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [15[SWITCH], 17[CASE]], but top level block is 3[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private boolean unexpectedSelectorWakeup(int selectCnt) {
        if (Thread.interrupted()) {
            if (logger.isDebugEnabled()) {
                logger.debug("Selector.select() returned prematurely because Thread.currentThread().interrupt() was called. Use NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop.");
            }
            return true;
        }
        if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 && selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
            logger.warn("Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.", (Object)selectCnt, (Object)this.selector);
            this.rebuildSelector();
            return true;
        }
        return false;
    }

    private static void handleLoopException(Throwable t) {
        logger.warn("Unexpected exception in the selector loop.", t);
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private void processSelectedKeys() {
        if (this.selectedKeys != null) {
            this.processSelectedKeysOptimized();
        } else {
            this.processSelectedKeysPlain(this.selector.selectedKeys());
        }
    }

    @Override
    protected void cleanup() {
        try {
            this.selector.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close a selector.", e);
        }
    }

    void cancel(SelectionKey key) {
        key.cancel();
        ++this.cancelledKeys;
        if (this.cancelledKeys >= 256) {
            this.cancelledKeys = 0;
            this.needsToSelectAgain = true;
        }
    }

    private void processSelectedKeysPlain(Set<SelectionKey> selectedKeys) {
        if (selectedKeys.isEmpty()) {
            return;
        }
        Iterator<SelectionKey> i = selectedKeys.iterator();
        while (true) {
            SelectionKey k = i.next();
            Object a = k.attachment();
            i.remove();
            if (a instanceof AbstractNioChannel) {
                this.processSelectedKey(k, (AbstractNioChannel)a);
            } else {
                NioTask task = (NioTask)a;
                NioEventLoop.processSelectedKey(k, task);
            }
            if (!i.hasNext()) break;
            if (!this.needsToSelectAgain) continue;
            this.selectAgain();
            selectedKeys = this.selector.selectedKeys();
            if (selectedKeys.isEmpty()) break;
            i = selectedKeys.iterator();
        }
    }

    private void processSelectedKeysOptimized() {
        for (int i = 0; i < this.selectedKeys.size; ++i) {
            SelectionKey k = this.selectedKeys.keys[i];
            this.selectedKeys.keys[i] = null;
            Object a = k.attachment();
            if (a instanceof AbstractNioChannel) {
                this.processSelectedKey(k, (AbstractNioChannel)a);
            } else {
                NioTask task = (NioTask)a;
                NioEventLoop.processSelectedKey(k, task);
            }
            if (!this.needsToSelectAgain) continue;
            this.selectedKeys.reset(i + 1);
            this.selectAgain();
            i = -1;
        }
    }

    private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
        AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();
        if (!k.isValid()) {
            NioEventLoop eventLoop;
            try {
                eventLoop = ch.eventLoop();
            }
            catch (Throwable ignored) {
                return;
            }
            if (eventLoop == this) {
                unsafe.close(unsafe.voidPromise());
            }
            return;
        }
        try {
            int readyOps = k.readyOps();
            if ((readyOps & 8) != 0) {
                int ops = k.interestOps();
                k.interestOps(ops &= 0xFFFFFFF7);
                unsafe.finishConnect();
            }
            if ((readyOps & 4) != 0) {
                unsafe.forceFlush();
            }
            if ((readyOps & 0x11) != 0 || readyOps == 0) {
                unsafe.read();
            }
        }
        catch (CancelledKeyException ignored) {
            unsafe.close(unsafe.voidPromise());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void processSelectedKey(SelectionKey k, NioTask<SelectableChannel> task) {
        int state = 0;
        try {
            task.channelReady(k.channel(), k);
            state = 1;
        }
        catch (Exception e) {
            try {
                k.cancel();
                NioEventLoop.invokeChannelUnregistered(task, k, e);
                state = 2;
            }
            catch (Throwable throwable) {
                switch (state) {
                    case 0: {
                        k.cancel();
                        NioEventLoop.invokeChannelUnregistered(task, k, null);
                        break;
                    }
                    case 1: {
                        if (k.isValid()) break;
                        NioEventLoop.invokeChannelUnregistered(task, k, null);
                        break;
                    }
                }
                throw throwable;
            }
            switch (state) {
                case 0: {
                    k.cancel();
                    NioEventLoop.invokeChannelUnregistered(task, k, null);
                    break;
                }
                case 1: {
                    if (!k.isValid()) {
                        NioEventLoop.invokeChannelUnregistered(task, k, null);
                        break;
                    }
                }
            }
        }
        switch (state) {
            case 0: {
                k.cancel();
                NioEventLoop.invokeChannelUnregistered(task, k, null);
                break;
            }
            case 1: {
                if (!k.isValid()) {
                    NioEventLoop.invokeChannelUnregistered(task, k, null);
                }
                break;
            }
        }
    }

    private void closeAll() {
        this.selectAgain();
        Set<SelectionKey> keys = this.selector.keys();
        ArrayList<AbstractNioChannel> channels = new ArrayList<AbstractNioChannel>(keys.size());
        for (SelectionKey k : keys) {
            Object a = k.attachment();
            if (a instanceof AbstractNioChannel) {
                channels.add((AbstractNioChannel)a);
                continue;
            }
            k.cancel();
            NioTask task = (NioTask)a;
            NioEventLoop.invokeChannelUnregistered(task, k, null);
        }
        for (AbstractNioChannel ch : channels) {
            ch.unsafe().close(ch.unsafe().voidPromise());
        }
    }

    private static void invokeChannelUnregistered(NioTask<SelectableChannel> task, SelectionKey k, Throwable cause) {
        try {
            task.channelUnregistered(k.channel(), cause);
        }
        catch (Exception e) {
            logger.warn("Unexpected exception while running NioTask.channelUnregistered()", e);
        }
    }

    @Override
    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop && this.nextWakeupNanos.getAndSet(-1L) != -1L) {
            this.selector.wakeup();
        }
    }

    @Override
    protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
        return deadlineNanos < this.nextWakeupNanos.get();
    }

    @Override
    protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
        return deadlineNanos < this.nextWakeupNanos.get();
    }

    Selector unwrappedSelector() {
        return this.unwrappedSelector;
    }

    int selectNow() throws IOException {
        return this.selector.selectNow();
    }

    private int select(long deadlineNanos) throws IOException {
        if (deadlineNanos == Long.MAX_VALUE) {
            return this.selector.select();
        }
        long timeoutMillis = NioEventLoop.deadlineToDelayNanos(deadlineNanos + 995000L) / 1000000L;
        return timeoutMillis <= 0L ? this.selector.selectNow() : this.selector.select(timeoutMillis);
    }

    private void selectAgain() {
        this.needsToSelectAgain = false;
        try {
            this.selector.selectNow();
        }
        catch (Throwable t) {
            logger.warn("Failed to update SelectionKeys.", t);
        }
    }

    static {
        int selectorAutoRebuildThreshold;
        logger = InternalLoggerFactory.getInstance(NioEventLoop.class);
        DISABLE_KEY_SET_OPTIMIZATION = SystemPropertyUtil.getBoolean("io.netty.noKeySetOptimization", false);
        if (PlatformDependent.javaVersion() < 7) {
            String key = "sun.nio.ch.bugLevel";
            String bugLevel = SystemPropertyUtil.get("sun.nio.ch.bugLevel");
            if (bugLevel == null) {
                try {
                    AccessController.doPrivileged(new PrivilegedAction<Void>(){

                        @Override
                        public Void run() {
                            System.setProperty("sun.nio.ch.bugLevel", "");
                            return null;
                        }
                    });
                }
                catch (SecurityException e) {
                    logger.debug("Unable to get/set System Property: sun.nio.ch.bugLevel", e);
                }
            }
        }
        if ((selectorAutoRebuildThreshold = SystemPropertyUtil.getInt("io.netty.selectorAutoRebuildThreshold", 512)) < 3) {
            selectorAutoRebuildThreshold = 0;
        }
        SELECTOR_AUTO_REBUILD_THRESHOLD = selectorAutoRebuildThreshold;
        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.noKeySetOptimization: {}", (Object)DISABLE_KEY_SET_OPTIMIZATION);
            logger.debug("-Dio.netty.selectorAutoRebuildThreshold: {}", (Object)SELECTOR_AUTO_REBUILD_THRESHOLD);
        }
    }

    private static final class SelectorTuple {
        final Selector unwrappedSelector;
        final Selector selector;

        SelectorTuple(Selector unwrappedSelector) {
            this.unwrappedSelector = unwrappedSelector;
            this.selector = unwrappedSelector;
        }

        SelectorTuple(Selector unwrappedSelector, Selector selector) {
            this.unwrappedSelector = unwrappedSelector;
            this.selector = selector;
        }
    }
}

