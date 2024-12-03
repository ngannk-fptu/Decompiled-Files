/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 *  javax.xml.ws.Holder
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.api.pipe;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.Cancelable;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.ComponentRegistry;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Engine;
import com.sun.xml.ws.api.pipe.FiberContextSwitchInterceptor;
import com.sun.xml.ws.api.pipe.NextAction;
import com.sun.xml.ws.api.pipe.ThrowableContainerPropertySet;
import com.sun.xml.ws.api.pipe.Tube;
import com.sun.xml.ws.api.pipe.TubeCloner;
import com.sun.xml.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.ws.api.server.Container;
import com.sun.xml.ws.api.server.ContainerResolver;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceException;

public final class Fiber
implements Runnable,
Cancelable,
ComponentRegistry {
    private final List<Listener> _listeners = new ArrayList<Listener>();
    private Tube[] conts = new Tube[16];
    private int contsSize;
    private Tube next;
    private Packet packet;
    private Throwable throwable;
    public final Engine owner;
    private volatile int suspendedCount = 0;
    private volatile boolean isInsideSuspendCallbacks = false;
    private boolean synchronous;
    private boolean interrupted;
    private final int id;
    private List<FiberContextSwitchInterceptor> interceptors;
    @Nullable
    private ClassLoader contextClassLoader;
    @Nullable
    private CompletionCallback completionCallback;
    private boolean isDeliverThrowableInPacket = false;
    private Thread currentThread;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = this.lock.newCondition();
    private volatile boolean isCanceled;
    private boolean started;
    private boolean startedSync;
    private static final PlaceholderTube PLACEHOLDER = new PlaceholderTube();
    private static final ThreadLocal<Fiber> CURRENT_FIBER = new ThreadLocal();
    private static final AtomicInteger iotaGen = new AtomicInteger();
    private static final Logger LOGGER = Logger.getLogger(Fiber.class.getName());
    private static final ReentrantLock serializedExecutionLock = new ReentrantLock();
    public static volatile boolean serializeExecution = Boolean.getBoolean(Fiber.class.getName() + ".serialize");
    private final Set<Component> components = new CopyOnWriteArraySet<Component>();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addListener(Listener listener) {
        List<Listener> list = this._listeners;
        synchronized (list) {
            if (!this._listeners.contains(listener)) {
                this._listeners.add(listener);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeListener(Listener listener) {
        List<Listener> list = this._listeners;
        synchronized (list) {
            this._listeners.remove(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    List<Listener> getCurrentListeners() {
        List<Listener> list = this._listeners;
        synchronized (list) {
            return new ArrayList<Listener>(this._listeners);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void clearListeners() {
        List<Listener> list = this._listeners;
        synchronized (list) {
            this._listeners.clear();
        }
    }

    public void setDeliverThrowableInPacket(boolean isDeliverThrowableInPacket) {
        this.isDeliverThrowableInPacket = isDeliverThrowableInPacket;
    }

    Fiber(Engine engine) {
        this.owner = engine;
        this.id = iotaGen.incrementAndGet();
        if (Fiber.isTraceEnabled()) {
            LOGGER.log(Level.FINE, "{0} created", this.getName());
        }
        this.contextClassLoader = Thread.currentThread().getContextClassLoader();
    }

    public void start(@NotNull Tube tubeline, @NotNull Packet request, @Nullable CompletionCallback completionCallback) {
        this.start(tubeline, request, completionCallback, false);
    }

    private void dumpFiberContext(String desc) {
        if (Fiber.isTraceEnabled()) {
            String action = null;
            String msgId = null;
            if (this.packet != null) {
                for (SOAPVersion sv : SOAPVersion.values()) {
                    for (AddressingVersion av : AddressingVersion.values()) {
                        action = this.packet.getMessage() != null ? AddressingUtils.getAction(this.packet.getMessage().getHeaders(), av, sv) : null;
                        String string = msgId = this.packet.getMessage() != null ? AddressingUtils.getMessageID(this.packet.getMessage().getHeaders(), av, sv) : null;
                        if (action != null || msgId != null) break;
                    }
                    if (action != null || msgId != null) break;
                }
            }
            String actionAndMsgDesc = action == null && msgId == null ? "NO ACTION or MSG ID" : "'" + action + "' and msgId '" + msgId + "'";
            String tubeDesc = this.next != null ? this.next.toString() + ".processRequest()" : this.peekCont() + ".processResponse()";
            LOGGER.log(Level.FINE, "{0} {1} with {2} and ''current'' tube {3} from thread {4} with Packet: {5}", new Object[]{this.getName(), desc, actionAndMsgDesc, tubeDesc, Thread.currentThread().getName(), this.packet != null ? this.packet.toShortString() : null});
        }
    }

    public void start(@NotNull Tube tubeline, @NotNull Packet request, @Nullable CompletionCallback completionCallback, boolean forceSync) {
        this.next = tubeline;
        this.packet = request;
        this.completionCallback = completionCallback;
        if (forceSync) {
            this.startedSync = true;
            this.dumpFiberContext("starting (sync)");
            this.run();
        } else {
            this.started = true;
            this.dumpFiberContext("starting (async)");
            this.owner.addRunnable(this);
        }
    }

    public void resume(@NotNull Packet resumePacket) {
        this.resume(resumePacket, false);
    }

    public void resume(@NotNull Packet resumePacket, boolean forceSync) {
        this.resume(resumePacket, forceSync, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resume(@NotNull Packet resumePacket, boolean forceSync, CompletionCallback callback) {
        this.lock.lock();
        try {
            if (callback != null) {
                this.setCompletionCallback(callback);
            }
            if (Fiber.isTraceEnabled()) {
                LOGGER.log(Level.FINE, "{0} resuming. Will have suspendedCount={1}", new Object[]{this.getName(), this.suspendedCount - 1});
            }
            this.packet = resumePacket;
            if (--this.suspendedCount == 0) {
                if (!this.isInsideSuspendCallbacks) {
                    List<Listener> listeners = this.getCurrentListeners();
                    for (Listener listener : listeners) {
                        try {
                            listener.fiberResumed(this);
                        }
                        catch (Throwable e) {
                            if (!Fiber.isTraceEnabled()) continue;
                            LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[]{listener, e.getMessage()});
                        }
                    }
                    if (this.synchronous) {
                        this.condition.signalAll();
                    } else if (forceSync || this.startedSync) {
                        this.run();
                    } else {
                        this.dumpFiberContext("resuming (async)");
                        this.owner.addRunnable(this);
                    }
                }
            } else if (Fiber.isTraceEnabled()) {
                LOGGER.log(Level.FINE, "{0} taking no action on resume because suspendedCount != 0: {1}", new Object[]{this.getName(), this.suspendedCount});
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    public void resumeAndReturn(@NotNull Packet resumePacket, boolean forceSync) {
        if (Fiber.isTraceEnabled()) {
            LOGGER.log(Level.FINE, "{0} resumed with Return Packet", this.getName());
        }
        this.next = null;
        this.resume(resumePacket, forceSync);
    }

    public void resume(@NotNull Throwable throwable) {
        this.resume(throwable, this.packet, false);
    }

    public void resume(@NotNull Throwable throwable, @NotNull Packet packet) {
        this.resume(throwable, packet, false);
    }

    public void resume(@NotNull Throwable error, boolean forceSync) {
        this.resume(error, this.packet, forceSync);
    }

    public void resume(@NotNull Throwable error, @NotNull Packet packet, boolean forceSync) {
        if (Fiber.isTraceEnabled()) {
            LOGGER.log(Level.FINE, "{0} resumed with Return Throwable", this.getName());
        }
        this.next = null;
        this.throwable = error;
        this.resume(packet, forceSync);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cancel(boolean mayInterrupt) {
        this.isCanceled = true;
        if (mayInterrupt) {
            Fiber fiber = this;
            synchronized (fiber) {
                if (this.currentThread != null) {
                    this.currentThread.interrupt();
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean suspend(Holder<Boolean> isRequireUnlock, Runnable onExitRunnable) {
        if (Fiber.isTraceEnabled()) {
            LOGGER.log(Level.FINE, "{0} suspending. Will have suspendedCount={1}", new Object[]{this.getName(), this.suspendedCount + 1});
            if (this.suspendedCount > 0) {
                LOGGER.log(Level.FINE, "WARNING - {0} suspended more than resumed. Will require more than one resume to actually resume this fiber.", this.getName());
            }
        }
        List<Listener> listeners = this.getCurrentListeners();
        if (++this.suspendedCount == 1) {
            this.isInsideSuspendCallbacks = true;
            try {
                for (Listener listener : listeners) {
                    try {
                        listener.fiberSuspended(this);
                    }
                    catch (Throwable e) {
                        if (!Fiber.isTraceEnabled()) continue;
                        LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[]{listener, e.getMessage()});
                    }
                }
            }
            finally {
                this.isInsideSuspendCallbacks = false;
            }
        }
        if (this.suspendedCount <= 0) {
            for (Listener listener : listeners) {
                try {
                    listener.fiberResumed(this);
                }
                catch (Throwable e) {
                    if (!Fiber.isTraceEnabled()) continue;
                    LOGGER.log(Level.FINE, "Listener {0} threw exception: {1}", new Object[]{listener, e.getMessage()});
                }
            }
        } else if (onExitRunnable != null) {
            if (!this.synchronous) {
                Fiber fiber = this;
                synchronized (fiber) {
                    this.currentThread = null;
                }
                this.lock.unlock();
                assert (!this.lock.isHeldByCurrentThread());
                isRequireUnlock.value = Boolean.FALSE;
                try {
                    onExitRunnable.run();
                }
                catch (Throwable t) {
                    throw new OnExitRunnableException(t);
                }
                return true;
            }
            if (Fiber.isTraceEnabled()) {
                LOGGER.fine("onExitRunnable used with synchronous Fiber execution -- not exiting current thread");
            }
            onExitRunnable.run();
        }
        return false;
    }

    public synchronized void addInterceptor(@NotNull FiberContextSwitchInterceptor interceptor) {
        if (this.interceptors == null) {
            this.interceptors = new ArrayList<FiberContextSwitchInterceptor>();
        } else {
            ArrayList<FiberContextSwitchInterceptor> l = new ArrayList<FiberContextSwitchInterceptor>();
            l.addAll(this.interceptors);
            this.interceptors = l;
        }
        this.interceptors.add(interceptor);
    }

    public synchronized boolean removeInterceptor(@NotNull FiberContextSwitchInterceptor interceptor) {
        if (this.interceptors != null) {
            boolean result = this.interceptors.remove(interceptor);
            if (this.interceptors.isEmpty()) {
                this.interceptors = null;
            } else {
                ArrayList<FiberContextSwitchInterceptor> l = new ArrayList<FiberContextSwitchInterceptor>();
                l.addAll(this.interceptors);
                this.interceptors = l;
            }
            return result;
        }
        return false;
    }

    @Nullable
    public ClassLoader getContextClassLoader() {
        return this.contextClassLoader;
    }

    public ClassLoader setContextClassLoader(@Nullable ClassLoader contextClassLoader) {
        ClassLoader r = this.contextClassLoader;
        this.contextClassLoader = contextClassLoader;
        return r;
    }

    @Override
    @Deprecated
    public void run() {
        Container old = ContainerResolver.getDefault().enterContainer(this.owner.getContainer());
        try {
            assert (!this.synchronous);
            if (!this.doRun()) {
                if (this.startedSync && this.suspendedCount == 0 && (this.next != null || this.contsSize > 0)) {
                    this.startedSync = false;
                    this.dumpFiberContext("restarting (async) after startSync");
                    this.owner.addRunnable(this);
                } else {
                    this.completionCheck();
                }
            }
        }
        finally {
            ContainerResolver.getDefault().exitContainer(old);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @NotNull
    public Packet runSync(@NotNull Tube tubeline, @NotNull Packet request) {
        this.lock.lock();
        try {
            Tube[] oldCont = this.conts;
            int oldContSize = this.contsSize;
            boolean oldSynchronous = this.synchronous;
            Tube oldNext = this.next;
            if (oldContSize > 0) {
                this.conts = new Tube[16];
                this.contsSize = 0;
            }
            try {
                this.synchronous = true;
                this.packet = request;
                this.next = tubeline;
                this.doRun();
                if (this.throwable != null) {
                    if (this.isDeliverThrowableInPacket) {
                        this.packet.addSatellite(new ThrowableContainerPropertySet(this.throwable));
                    } else {
                        if (this.throwable instanceof RuntimeException) {
                            throw (RuntimeException)this.throwable;
                        }
                        if (this.throwable instanceof Error) {
                            throw (Error)this.throwable;
                        }
                        throw new AssertionError((Object)this.throwable);
                    }
                }
                Packet packet = this.packet;
                this.conts = oldCont;
                this.contsSize = oldContSize;
                this.synchronous = oldSynchronous;
                this.next = oldNext;
                if (this.interrupted) {
                    Thread.currentThread().interrupt();
                    this.interrupted = false;
                }
                if (!this.started && !this.startedSync) {
                    this.completionCheck();
                }
                return packet;
            }
            catch (Throwable throwable) {
                this.conts = oldCont;
                this.contsSize = oldContSize;
                this.synchronous = oldSynchronous;
                this.next = oldNext;
                if (this.interrupted) {
                    Thread.currentThread().interrupt();
                    this.interrupted = false;
                }
                if (!this.started && !this.startedSync) {
                    this.completionCheck();
                }
                throw throwable;
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    private void completionCheck() {
        this.lock.lock();
        try {
            if (!this.isCanceled && this.contsSize == 0 && this.suspendedCount == 0) {
                if (Fiber.isTraceEnabled()) {
                    LOGGER.log(Level.FINE, "{0} completed", this.getName());
                }
                this.clearListeners();
                this.condition.signalAll();
                if (this.completionCallback != null) {
                    if (this.throwable != null) {
                        if (this.isDeliverThrowableInPacket) {
                            this.packet.addSatellite(new ThrowableContainerPropertySet(this.throwable));
                            this.completionCallback.onCompletion(this.packet);
                        } else {
                            this.completionCallback.onCompletion(this.throwable);
                        }
                    } else {
                        this.completionCallback.onCompletion(this.packet);
                    }
                }
            }
        }
        finally {
            this.lock.unlock();
        }
    }

    private boolean doRun() {
        this.dumpFiberContext("running");
        if (serializeExecution) {
            serializedExecutionLock.lock();
            try {
                boolean bl = this._doRun(this.next);
                return bl;
            }
            finally {
                serializedExecutionLock.unlock();
            }
        }
        return this._doRun(this.next);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private boolean _doRun(Tube next) {
        Holder isRequireUnlock = new Holder((Object)Boolean.TRUE);
        this.lock.lock();
        try {
            ClassLoader old;
            List<FiberContextSwitchInterceptor> ints;
            Fiber fiber = this;
            synchronized (fiber) {
                ints = this.interceptors;
                this.currentThread = Thread.currentThread();
                if (Fiber.isTraceEnabled()) {
                    LOGGER.log(Level.FINE, "Thread entering _doRun(): {0}", this.currentThread);
                }
                old = this.currentThread.getContextClassLoader();
                this.currentThread.setContextClassLoader(this.contextClassLoader);
            }
            try {
                boolean needsToReenter;
                do {
                    if (ints == null) {
                        this.next = next;
                        if (this.__doRun((Holder<Boolean>)isRequireUnlock, null)) {
                            boolean bl = true;
                            return bl;
                        }
                    } else if ((next = new InterceptorHandler((Holder<Boolean>)isRequireUnlock, ints).invoke(next)) == PLACEHOLDER) {
                        boolean bl = true;
                        return bl;
                    }
                    Fiber fiber2 = this;
                    synchronized (fiber2) {
                        boolean bl = needsToReenter = ints != this.interceptors;
                        if (needsToReenter) {
                            ints = this.interceptors;
                        }
                    }
                } while (needsToReenter);
            }
            catch (OnExitRunnableException o) {
                Throwable t = o.target;
                if (!(t instanceof WebServiceException)) throw new WebServiceException(t);
                throw (WebServiceException)t;
            }
            finally {
                Thread thread = Thread.currentThread();
                thread.setContextClassLoader(old);
                if (Fiber.isTraceEnabled()) {
                    LOGGER.log(Level.FINE, "Thread leaving _doRun(): {0}", thread);
                }
            }
            boolean bl = false;
            return bl;
        }
        finally {
            if (((Boolean)isRequireUnlock.value).booleanValue()) {
                Runnable thread = this;
                synchronized (thread) {
                    this.currentThread = null;
                }
                this.lock.unlock();
            }
        }
    }

    /*
     * Exception decompiling
     */
    private boolean __doRun(Holder<Boolean> isRequireUnlock, List<FiberContextSwitchInterceptor> originalInterceptors) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[TRYBLOCK]], but top level block is 17[WHILELOOP]
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

    private void pushCont(Tube tube) {
        this.conts[this.contsSize++] = tube;
        int len = this.conts.length;
        if (this.contsSize == len) {
            Tube[] newBuf = new Tube[len * 2];
            System.arraycopy(this.conts, 0, newBuf, 0, len);
            this.conts = newBuf;
        }
    }

    private Tube popCont() {
        return this.conts[--this.contsSize];
    }

    private Tube peekCont() {
        int index = this.contsSize - 1;
        if (index >= 0 && index < this.conts.length) {
            return this.conts[index];
        }
        return null;
    }

    public void resetCont(Tube[] conts, int contsSize) {
        this.conts = conts;
        this.contsSize = contsSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isReady(List<FiberContextSwitchInterceptor> originalInterceptors) {
        if (this.synchronous) {
            while (this.suspendedCount == 1) {
                try {
                    if (Fiber.isTraceEnabled()) {
                        LOGGER.log(Level.FINE, "{0} is blocking thread {1}", new Object[]{this.getName(), Thread.currentThread().getName()});
                    }
                    this.condition.await();
                }
                catch (InterruptedException e) {
                    this.interrupted = true;
                }
            }
            Fiber fiber = this;
            synchronized (fiber) {
                return this.interceptors == originalInterceptors;
            }
        }
        if (this.suspendedCount > 0) {
            return false;
        }
        Fiber fiber = this;
        synchronized (fiber) {
            return this.interceptors == originalInterceptors;
        }
    }

    private String getName() {
        return "engine-" + this.owner.id + "fiber-" + this.id;
    }

    public String toString() {
        return this.getName();
    }

    @Nullable
    public Packet getPacket() {
        return this.packet;
    }

    public CompletionCallback getCompletionCallback() {
        return this.completionCallback;
    }

    public void setCompletionCallback(CompletionCallback completionCallback) {
        this.completionCallback = completionCallback;
    }

    public static boolean isSynchronous() {
        return Fiber.current().synchronous;
    }

    public boolean isStartedSync() {
        return this.startedSync;
    }

    @NotNull
    public static Fiber current() {
        Fiber fiber = CURRENT_FIBER.get();
        if (fiber == null) {
            throw new IllegalStateException("Can be only used from fibers");
        }
        return fiber;
    }

    public static Fiber getCurrentIfSet() {
        return CURRENT_FIBER.get();
    }

    private static boolean isTraceEnabled() {
        return LOGGER.isLoggable(Level.FINE);
    }

    @Override
    public <S> S getSPI(Class<S> spiType) {
        for (Component c : this.components) {
            S spi = c.getSPI(spiType);
            if (spi == null) continue;
            return spi;
        }
        return null;
    }

    @Override
    public Set<Component> getComponents() {
        return this.components;
    }

    private static class PlaceholderTube
    extends AbstractTubeImpl {
        private PlaceholderTube() {
        }

        @Override
        public NextAction processRequest(Packet request) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NextAction processResponse(Packet response) {
            throw new UnsupportedOperationException();
        }

        @Override
        public NextAction processException(Throwable t) {
            return this.doThrow(t);
        }

        @Override
        public void preDestroy() {
        }

        @Override
        public PlaceholderTube copy(TubeCloner cloner) {
            throw new UnsupportedOperationException();
        }
    }

    private class InterceptorHandler
    implements FiberContextSwitchInterceptor.Work<Tube, Tube> {
        private final Holder<Boolean> isUnlockRequired;
        private final List<FiberContextSwitchInterceptor> ints;
        private int idx;

        public InterceptorHandler(Holder<Boolean> isUnlockRequired, List<FiberContextSwitchInterceptor> ints) {
            this.isUnlockRequired = isUnlockRequired;
            this.ints = ints;
        }

        Tube invoke(Tube next) {
            this.idx = 0;
            return this.execute(next);
        }

        @Override
        public Tube execute(Tube next) {
            if (this.idx == this.ints.size()) {
                Fiber.this.next = next;
                if (Fiber.this.__doRun((Holder<Boolean>)this.isUnlockRequired, this.ints)) {
                    return PLACEHOLDER;
                }
            } else {
                FiberContextSwitchInterceptor interceptor = this.ints.get(this.idx++);
                return interceptor.execute(Fiber.this, next, this);
            }
            return Fiber.this.next;
        }
    }

    private static final class OnExitRunnableException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;
        Throwable target;

        public OnExitRunnableException(Throwable target) {
            super((Throwable)null);
            this.target = target;
        }
    }

    public static interface CompletionCallback {
        public void onCompletion(@NotNull Packet var1);

        public void onCompletion(@NotNull Throwable var1);
    }

    public static interface Listener {
        public void fiberSuspended(Fiber var1);

        public void fiberResumed(Fiber var1);
    }
}

