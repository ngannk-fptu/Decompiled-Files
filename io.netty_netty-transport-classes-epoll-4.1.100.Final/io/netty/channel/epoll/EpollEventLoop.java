/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.EventLoopTaskQueueFactory
 *  io.netty.channel.SelectStrategy
 *  io.netty.channel.SingleThreadEventLoop
 *  io.netty.channel.SingleThreadEventLoop$ChannelsReadOnlyIterator
 *  io.netty.channel.unix.FileDescriptor
 *  io.netty.channel.unix.IovArray
 *  io.netty.util.IntSupplier
 *  io.netty.util.collection.IntObjectHashMap
 *  io.netty.util.collection.IntObjectMap
 *  io.netty.util.concurrent.RejectedExecutionHandler
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.PlatformDependent
 *  io.netty.util.internal.SystemPropertyUtil
 *  io.netty.util.internal.logging.InternalLogger
 *  io.netty.util.internal.logging.InternalLoggerFactory
 */
package io.netty.channel.epoll;

import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.EventLoopTaskQueueFactory;
import io.netty.channel.SelectStrategy;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventArray;
import io.netty.channel.epoll.Native;
import io.netty.channel.epoll.NativeDatagramPacketArray;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.IovArray;
import io.netty.util.IntSupplier;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.concurrent.RejectedExecutionHandler;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;

public class EpollEventLoop
extends SingleThreadEventLoop {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(EpollEventLoop.class);
    private static final long EPOLL_WAIT_MILLIS_THRESHOLD = SystemPropertyUtil.getLong((String)"io.netty.channel.epoll.epollWaitThreshold", (long)10L);
    private FileDescriptor epollFd;
    private FileDescriptor eventFd;
    private FileDescriptor timerFd;
    private final IntObjectMap<AbstractEpollChannel> channels = new IntObjectHashMap(4096);
    private final boolean allowGrowing;
    private final EpollEventArray events;
    private IovArray iovArray;
    private NativeDatagramPacketArray datagramPacketArray;
    private final SelectStrategy selectStrategy;
    private final IntSupplier selectNowSupplier = new IntSupplier(){

        public int get() throws Exception {
            return EpollEventLoop.this.epollWaitNow();
        }
    };
    private static final long AWAKE = -1L;
    private static final long NONE = Long.MAX_VALUE;
    private final AtomicLong nextWakeupNanos = new AtomicLong(-1L);
    private boolean pendingWakeup;
    private volatile int ioRatio = 50;
    private static final long MAX_SCHEDULED_TIMERFD_NS = 999999999L;

    EpollEventLoop(EventLoopGroup parent, Executor executor, int maxEvents, SelectStrategy strategy, RejectedExecutionHandler rejectedExecutionHandler, EventLoopTaskQueueFactory taskQueueFactory, EventLoopTaskQueueFactory tailTaskQueueFactory) {
        super(parent, executor, false, EpollEventLoop.newTaskQueue(taskQueueFactory), EpollEventLoop.newTaskQueue(tailTaskQueueFactory), rejectedExecutionHandler);
        this.selectStrategy = (SelectStrategy)ObjectUtil.checkNotNull((Object)strategy, (String)"strategy");
        if (maxEvents == 0) {
            this.allowGrowing = true;
            this.events = new EpollEventArray(4096);
        } else {
            this.allowGrowing = false;
            this.events = new EpollEventArray(maxEvents);
        }
        this.openFileDescriptors();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void openFileDescriptors() {
        boolean success = false;
        FileDescriptor epollFd = null;
        FileDescriptor eventFd = null;
        FileDescriptor timerFd = null;
        try {
            this.epollFd = epollFd = Native.newEpollCreate();
            this.eventFd = eventFd = Native.newEventFd();
            try {
                Native.epollCtlAdd(epollFd.intValue(), eventFd.intValue(), Native.EPOLLIN | Native.EPOLLET);
            }
            catch (IOException e) {
                throw new IllegalStateException("Unable to add eventFd filedescriptor to epoll", e);
            }
            this.timerFd = timerFd = Native.newTimerFd();
            try {
                Native.epollCtlAdd(epollFd.intValue(), timerFd.intValue(), Native.EPOLLIN | Native.EPOLLET);
            }
            catch (IOException e) {
                throw new IllegalStateException("Unable to add timerFd filedescriptor to epoll", e);
            }
            success = true;
        }
        finally {
            if (!success) {
                if (epollFd != null) {
                    try {
                        epollFd.close();
                    }
                    catch (Exception exception) {}
                }
                if (eventFd != null) {
                    try {
                        eventFd.close();
                    }
                    catch (Exception exception) {}
                }
                if (timerFd != null) {
                    try {
                        timerFd.close();
                    }
                    catch (Exception exception) {}
                }
            }
        }
    }

    private static Queue<Runnable> newTaskQueue(EventLoopTaskQueueFactory queueFactory) {
        if (queueFactory == null) {
            return EpollEventLoop.newTaskQueue0(DEFAULT_MAX_PENDING_TASKS);
        }
        return queueFactory.newTaskQueue(DEFAULT_MAX_PENDING_TASKS);
    }

    IovArray cleanIovArray() {
        if (this.iovArray == null) {
            this.iovArray = new IovArray();
        } else {
            this.iovArray.clear();
        }
        return this.iovArray;
    }

    NativeDatagramPacketArray cleanDatagramPacketArray() {
        if (this.datagramPacketArray == null) {
            this.datagramPacketArray = new NativeDatagramPacketArray();
        } else {
            this.datagramPacketArray.clear();
        }
        return this.datagramPacketArray;
    }

    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop && this.nextWakeupNanos.getAndSet(-1L) != -1L) {
            Native.eventFdWrite(this.eventFd.intValue(), 1L);
        }
    }

    protected boolean beforeScheduledTaskSubmitted(long deadlineNanos) {
        return deadlineNanos < this.nextWakeupNanos.get();
    }

    protected boolean afterScheduledTaskSubmitted(long deadlineNanos) {
        return deadlineNanos < this.nextWakeupNanos.get();
    }

    void add(AbstractEpollChannel ch) throws IOException {
        assert (this.inEventLoop());
        int fd = ch.socket.intValue();
        Native.epollCtlAdd(this.epollFd.intValue(), fd, ch.flags);
        AbstractEpollChannel old = (AbstractEpollChannel)((Object)this.channels.put(fd, (Object)ch));
        assert (old == null || !old.isOpen());
    }

    void modify(AbstractEpollChannel ch) throws IOException {
        assert (this.inEventLoop());
        Native.epollCtlMod(this.epollFd.intValue(), ch.socket.intValue(), ch.flags);
    }

    void remove(AbstractEpollChannel ch) throws IOException {
        assert (this.inEventLoop());
        int fd = ch.socket.intValue();
        AbstractEpollChannel old = (AbstractEpollChannel)((Object)this.channels.remove(fd));
        if (old != null && old != ch) {
            this.channels.put(fd, (Object)old);
            assert (!ch.isOpen());
        } else if (ch.isOpen()) {
            Native.epollCtlDel(this.epollFd.intValue(), fd);
        }
    }

    protected Queue<Runnable> newTaskQueue(int maxPendingTasks) {
        return EpollEventLoop.newTaskQueue0(maxPendingTasks);
    }

    private static Queue<Runnable> newTaskQueue0(int maxPendingTasks) {
        return maxPendingTasks == Integer.MAX_VALUE ? PlatformDependent.newMpscQueue() : PlatformDependent.newMpscQueue((int)maxPendingTasks);
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

    public int registeredChannels() {
        return this.channels.size();
    }

    public Iterator<Channel> registeredChannelsIterator() {
        assert (this.inEventLoop());
        IntObjectMap<AbstractEpollChannel> ch = this.channels;
        if (ch.isEmpty()) {
            return SingleThreadEventLoop.ChannelsReadOnlyIterator.empty();
        }
        return new SingleThreadEventLoop.ChannelsReadOnlyIterator((Iterable)ch.values());
    }

    private long epollWait(long deadlineNanos) throws IOException {
        if (deadlineNanos == Long.MAX_VALUE) {
            return Native.epollWait(this.epollFd, this.events, this.timerFd, Integer.MAX_VALUE, 0, EPOLL_WAIT_MILLIS_THRESHOLD);
        }
        long totalDelay = EpollEventLoop.deadlineToDelayNanos((long)deadlineNanos);
        int delaySeconds = (int)Math.min(totalDelay / 1000000000L, Integer.MAX_VALUE);
        int delayNanos = (int)Math.min(totalDelay - (long)delaySeconds * 1000000000L, 999999999L);
        return Native.epollWait(this.epollFd, this.events, this.timerFd, delaySeconds, delayNanos, EPOLL_WAIT_MILLIS_THRESHOLD);
    }

    private int epollWaitNoTimerChange() throws IOException {
        return Native.epollWait(this.epollFd, this.events, false);
    }

    private int epollWaitNow() throws IOException {
        return Native.epollWait(this.epollFd, this.events, true);
    }

    private int epollBusyWait() throws IOException {
        return Native.epollBusyWait(this.epollFd, this.events);
    }

    private int epollWaitTimeboxed() throws IOException {
        return Native.epollWait(this.epollFd, this.events, 1000);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Unable to fully structure code
     */
    protected void run() {
        prevDeadlineNanos = 0x7FFFFFFFFFFFFFFFL;
        block33: while (true) {
            try {
                strategy = this.selectStrategy.calculateStrategy(this.selectNowSupplier, this.hasTasks());
                switch (strategy) {
                    ** case -2:
lbl7:
                    // 1 sources

                    continue block33;
                    case -3: {
                        strategy = this.epollBusyWait();
                        ** GOTO lbl34
                    }
                    case -1: {
                        if (!this.pendingWakeup) ** GOTO lbl18
                        strategy = this.epollWaitTimeboxed();
                        if (strategy != 0) ** GOTO lbl34
                        EpollEventLoop.logger.warn("Missed eventfd write (not seen after > 1 second)");
                        this.pendingWakeup = false;
                        if (this.hasTasks()) ** GOTO lbl34
lbl18:
                        // 2 sources

                        if ((curDeadlineNanos = this.nextScheduledTaskDeadlineNanos()) == -1L) {
                            curDeadlineNanos = 0x7FFFFFFFFFFFFFFFL;
                        }
                        this.nextWakeupNanos.set(curDeadlineNanos);
                        try {
                            if (!this.hasTasks()) {
                                if (curDeadlineNanos == prevDeadlineNanos) {
                                    strategy = this.epollWaitNoTimerChange();
                                } else {
                                    result = this.epollWait(curDeadlineNanos);
                                    strategy = Native.epollReady(result);
                                    prevDeadlineNanos = Native.epollTimerWasUsed(result) != false ? curDeadlineNanos : 0x7FFFFFFFFFFFFFFFL;
                                }
                            }
                        }
                        finally {
                            if (this.nextWakeupNanos.get() == -1L || this.nextWakeupNanos.getAndSet(-1L) == -1L) {
                                this.pendingWakeup = true;
                            }
                        }
                    }
lbl34:
                    // 6 sources

                    default: {
                        ioRatio = this.ioRatio;
                        if (ioRatio == 100) {
                            try {
                                if (strategy <= 0 || !this.processReady(this.events, strategy)) ** GOTO lbl58
                                prevDeadlineNanos = 0x7FFFFFFFFFFFFFFFL;
                            }
                            finally {
                                this.runAllTasks();
                            }
                        } else if (strategy > 0) {
                            ioStartTime = System.nanoTime();
                            try {
                                if (!this.processReady(this.events, strategy)) ** GOTO lbl58
                                prevDeadlineNanos = 0x7FFFFFFFFFFFFFFFL;
                            }
                            finally {
                                ioTime = System.nanoTime() - ioStartTime;
                                this.runAllTasks(ioTime * (long)(100 - ioRatio) / (long)ioRatio);
                            }
                        } else {
                            this.runAllTasks(0L);
                        }
lbl58:
                        // 5 sources

                        if (!this.allowGrowing || strategy != this.events.length()) continue block33;
                        this.events.increase();
                        continue block33;
                    }
                }
            }
            catch (Error e) {
                throw e;
            }
            catch (Throwable t) {
                this.handleLoopException(t);
                continue;
            }
            finally {
                try {
                    if (!this.isShuttingDown()) continue;
                    this.closeAll();
                    if (!this.confirmShutdown()) continue;
                    break;
                }
                catch (Error e) {
                    throw e;
                }
                catch (Throwable t) {
                    this.handleLoopException(t);
                }
                continue;
            }
            break;
        }
    }

    void handleLoopException(Throwable t) {
        logger.warn("Unexpected exception in the selector loop.", t);
        try {
            Thread.sleep(1000L);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
    }

    private void closeAll() {
        AbstractEpollChannel[] localChannels;
        for (AbstractEpollChannel ch : localChannels = this.channels.values().toArray(new AbstractEpollChannel[0])) {
            ch.unsafe().close(ch.unsafe().voidPromise());
        }
    }

    private boolean processReady(EpollEventArray events, int ready) {
        boolean timerFired = false;
        for (int i = 0; i < ready; ++i) {
            int fd = events.fd(i);
            if (fd == this.eventFd.intValue()) {
                this.pendingWakeup = false;
                continue;
            }
            if (fd == this.timerFd.intValue()) {
                timerFired = true;
                continue;
            }
            long ev = events.events(i);
            AbstractEpollChannel ch = (AbstractEpollChannel)((Object)this.channels.get(fd));
            if (ch != null) {
                AbstractEpollChannel.AbstractEpollUnsafe unsafe = (AbstractEpollChannel.AbstractEpollUnsafe)ch.unsafe();
                if ((ev & (long)(Native.EPOLLERR | Native.EPOLLOUT)) != 0L) {
                    unsafe.epollOutReady();
                }
                if ((ev & (long)(Native.EPOLLERR | Native.EPOLLIN)) != 0L) {
                    unsafe.epollInReady();
                }
                if ((ev & (long)Native.EPOLLRDHUP) == 0L) continue;
                unsafe.epollRdHupReady();
                continue;
            }
            try {
                Native.epollCtlDel(this.epollFd.intValue(), fd);
                continue;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return timerFired;
    }

    protected void cleanup() {
        try {
            this.closeFileDescriptors();
        }
        finally {
            if (this.iovArray != null) {
                this.iovArray.release();
                this.iovArray = null;
            }
            if (this.datagramPacketArray != null) {
                this.datagramPacketArray.release();
                this.datagramPacketArray = null;
            }
            this.events.free();
        }
    }

    public void closeFileDescriptors() {
        block8: while (this.pendingWakeup) {
            try {
                int count = this.epollWaitTimeboxed();
                if (count == 0) break;
                for (int i = 0; i < count; ++i) {
                    if (this.events.fd(i) != this.eventFd.intValue()) continue;
                    this.pendingWakeup = false;
                    continue block8;
                }
            }
            catch (IOException count) {
            }
        }
        try {
            this.eventFd.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close the event fd.", (Throwable)e);
        }
        try {
            this.timerFd.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close the timer fd.", (Throwable)e);
        }
        try {
            this.epollFd.close();
        }
        catch (IOException e) {
            logger.warn("Failed to close the epoll fd.", (Throwable)e);
        }
    }

    static {
        Epoll.ensureAvailability();
    }
}

