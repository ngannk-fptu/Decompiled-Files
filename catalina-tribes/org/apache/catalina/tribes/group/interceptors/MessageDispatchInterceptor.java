/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.group.interceptors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.ErrorHandler;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.UniqueId;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.group.interceptors.MessageDispatchInterceptorMBean;
import org.apache.catalina.tribes.util.ExecutorFactory;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.catalina.tribes.util.TcclThreadFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class MessageDispatchInterceptor
extends ChannelInterceptorBase
implements MessageDispatchInterceptorMBean {
    private static final Log log = LogFactory.getLog(MessageDispatchInterceptor.class);
    protected static final StringManager sm = StringManager.getManager(MessageDispatchInterceptor.class);
    protected long maxQueueSize = 0x4000000L;
    protected volatile boolean run = false;
    protected boolean useDeepClone = true;
    protected boolean alwaysSend = true;
    protected final AtomicLong currentSize = new AtomicLong(0L);
    protected ExecutorService executor = null;
    protected int maxThreads = 10;
    protected int maxSpareThreads = 2;
    protected long keepAliveTime = 5000L;

    public MessageDispatchInterceptor() {
        this.setOptionFlag(8);
    }

    @Override
    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        boolean async;
        boolean bl = async = (msg.getOptions() & 8) == 8;
        if (async && this.run) {
            if (this.getCurrentSize() + (long)msg.getMessage().getLength() > this.maxQueueSize) {
                if (this.alwaysSend) {
                    super.sendMessage(destination, msg, payload);
                    return;
                }
                throw new ChannelException(sm.getString("messageDispatchInterceptor.queue.full", Long.toString(this.maxQueueSize), Long.toString(this.getCurrentSize())));
            }
            if (this.useDeepClone) {
                msg = (ChannelMessage)msg.deepclone();
            }
            if (!this.addToQueue(msg, destination, payload)) {
                throw new ChannelException(sm.getString("messageDispatchInterceptor.unableAdd.queue"));
            }
            this.addAndGetCurrentSize(msg.getMessage().getLength());
        } else {
            super.sendMessage(destination, msg, payload);
        }
    }

    public boolean addToQueue(ChannelMessage msg, Member[] destination, InterceptorPayload payload) {
        this.executor.execute(() -> this.sendAsyncData(msg, destination, payload));
        return true;
    }

    public void startQueue() {
        if (this.run) {
            return;
        }
        String channelName = "";
        if (this.getChannel().getName() != null) {
            channelName = "[" + this.getChannel().getName() + "]";
        }
        this.executor = ExecutorFactory.newThreadPool(this.maxSpareThreads, this.maxThreads, this.keepAliveTime, TimeUnit.MILLISECONDS, new TcclThreadFactory("MessageDispatchInterceptor.MessageDispatchThread" + channelName));
        this.run = true;
    }

    public void stopQueue() {
        this.run = false;
        this.executor.shutdownNow();
        this.setAndGetCurrentSize(0L);
    }

    @Override
    public void setOptionFlag(int flag) {
        if (flag != 8) {
            log.warn((Object)sm.getString("messageDispatchInterceptor.warning.optionflag"));
        }
        super.setOptionFlag(flag);
    }

    public void setMaxQueueSize(long maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public void setUseDeepClone(boolean useDeepClone) {
        this.useDeepClone = useDeepClone;
    }

    @Override
    public long getMaxQueueSize() {
        return this.maxQueueSize;
    }

    public boolean getUseDeepClone() {
        return this.useDeepClone;
    }

    @Override
    public long getCurrentSize() {
        return this.currentSize.get();
    }

    public long addAndGetCurrentSize(long inc) {
        return this.currentSize.addAndGet(inc);
    }

    public long setAndGetCurrentSize(long value) {
        this.currentSize.set(value);
        return value;
    }

    @Override
    public long getKeepAliveTime() {
        return this.keepAliveTime;
    }

    @Override
    public int getMaxSpareThreads() {
        return this.maxSpareThreads;
    }

    @Override
    public int getMaxThreads() {
        return this.maxThreads;
    }

    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public void setMaxSpareThreads(int maxSpareThreads) {
        this.maxSpareThreads = maxSpareThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }

    @Override
    public boolean isAlwaysSend() {
        return this.alwaysSend;
    }

    @Override
    public void setAlwaysSend(boolean alwaysSend) {
        this.alwaysSend = alwaysSend;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void start(int svc) throws ChannelException {
        if (!this.run) {
            MessageDispatchInterceptor messageDispatchInterceptor = this;
            synchronized (messageDispatchInterceptor) {
                if (!this.run && (svc & 2) == 2) {
                    this.startQueue();
                }
            }
        }
        super.start(svc);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stop(int svc) throws ChannelException {
        if (this.run) {
            MessageDispatchInterceptor messageDispatchInterceptor = this;
            synchronized (messageDispatchInterceptor) {
                if (this.run && (svc & 2) == 2) {
                    this.stopQueue();
                }
            }
        }
        super.stop(svc);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void sendAsyncData(ChannelMessage msg, Member[] destination, InterceptorPayload payload) {
        ErrorHandler handler = null;
        if (payload != null) {
            handler = payload.getErrorHandler();
        }
        try {
            super.sendMessage(destination, msg, null);
            try {
                if (handler != null) {
                    handler.handleCompletion(new UniqueId(msg.getUniqueId()));
                }
            }
            catch (Exception ex) {
                log.error((Object)sm.getString("messageDispatchInterceptor.completeMessage.failed"), (Throwable)ex);
            }
        }
        catch (Exception x) {
            ChannelException cx = null;
            cx = x instanceof ChannelException ? (ChannelException)x : new ChannelException(x);
            if (log.isDebugEnabled()) {
                log.debug((Object)sm.getString("messageDispatchInterceptor.AsyncMessage.failed"), (Throwable)x);
            }
            try {
                if (handler != null) {
                    handler.handleError(cx, new UniqueId(msg.getUniqueId()));
                }
            }
            catch (Exception ex) {
                log.error((Object)sm.getString("messageDispatchInterceptor.errorMessage.failed"), (Throwable)ex);
            }
        }
        finally {
            this.addAndGetCurrentSize(-msg.getMessage().getLength());
        }
    }

    @Override
    public int getPoolSize() {
        if (this.executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor)this.executor).getPoolSize();
        }
        return -1;
    }

    @Override
    public int getActiveCount() {
        if (this.executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor)this.executor).getActiveCount();
        }
        return -1;
    }

    @Override
    public long getTaskCount() {
        if (this.executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor)this.executor).getTaskCount();
        }
        return -1L;
    }

    @Override
    public long getCompletedTaskCount() {
        if (this.executor instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor)this.executor).getCompletedTaskCount();
        }
        return -1L;
    }
}

