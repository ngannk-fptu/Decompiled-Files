/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.group;

import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.catalina.tribes.ByteMessage;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelInterceptor;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.ChannelReceiver;
import org.apache.catalina.tribes.ChannelSender;
import org.apache.catalina.tribes.ErrorHandler;
import org.apache.catalina.tribes.Heartbeat;
import org.apache.catalina.tribes.JmxChannel;
import org.apache.catalina.tribes.ManagedChannel;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.MembershipService;
import org.apache.catalina.tribes.RemoteProcessException;
import org.apache.catalina.tribes.UniqueId;
import org.apache.catalina.tribes.group.ChannelCoordinator;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.GroupChannelMBean;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.group.RpcChannel;
import org.apache.catalina.tribes.group.RpcMessage;
import org.apache.catalina.tribes.group.interceptors.MessageDispatchInterceptor;
import org.apache.catalina.tribes.io.BufferPool;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.jmx.JmxRegistry;
import org.apache.catalina.tribes.util.Arrays;
import org.apache.catalina.tribes.util.Logs;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class GroupChannel
extends ChannelInterceptorBase
implements ManagedChannel,
JmxChannel,
GroupChannelMBean {
    private static final Log log = LogFactory.getLog(GroupChannel.class);
    protected static final StringManager sm = StringManager.getManager(GroupChannel.class);
    protected boolean heartbeat = true;
    protected long heartbeatSleeptime = 5000L;
    protected ScheduledFuture<?> heartbeatFuture = null;
    protected ScheduledFuture<?> monitorFuture;
    protected final ChannelCoordinator coordinator = new ChannelCoordinator();
    protected ChannelInterceptor interceptors = null;
    protected final List<MembershipListener> membershipListeners = new CopyOnWriteArrayList<MembershipListener>();
    protected final List<ChannelListener> channelListeners = new CopyOnWriteArrayList<ChannelListener>();
    protected boolean optionCheck = false;
    protected String name = null;
    private String jmxDomain = "ClusterChannel";
    private String jmxPrefix = "";
    private boolean jmxEnabled = true;
    protected ScheduledExecutorService utilityExecutor = null;
    private ObjectName oname = null;
    protected boolean ownExecutor = false;

    public GroupChannel() {
        this.addInterceptor(this);
    }

    @Override
    public void addInterceptor(ChannelInterceptor interceptor) {
        if (this.interceptors == null) {
            this.interceptors = interceptor;
            this.interceptors.setNext(this.coordinator);
            this.interceptors.setPrevious(null);
            this.coordinator.setPrevious(this.interceptors);
        } else {
            ChannelInterceptor last = this.interceptors;
            while (last.getNext() != this.coordinator) {
                last = last.getNext();
            }
            last.setNext(interceptor);
            interceptor.setNext(this.coordinator);
            interceptor.setPrevious(last);
            this.coordinator.setPrevious(interceptor);
        }
    }

    @Override
    public void heartbeat() {
        super.heartbeat();
        for (MembershipListener membershipListener : this.membershipListeners) {
            if (!(membershipListener instanceof Heartbeat)) continue;
            ((Heartbeat)((Object)membershipListener)).heartbeat();
        }
        for (ChannelListener channelListener : this.channelListeners) {
            if (!(channelListener instanceof Heartbeat)) continue;
            ((Heartbeat)((Object)channelListener)).heartbeat();
        }
    }

    @Override
    public UniqueId send(Member[] destination, Serializable msg, int options) throws ChannelException {
        return this.send(destination, msg, options, null);
    }

    @Override
    public UniqueId send(Member[] destination, Serializable msg, int options, ErrorHandler handler) throws ChannelException {
        UniqueId uniqueId;
        block11: {
            if (msg == null) {
                throw new ChannelException(sm.getString("groupChannel.nullMessage"));
            }
            XByteBuffer buffer = null;
            try {
                if (destination == null || destination.length == 0) {
                    throw new ChannelException(sm.getString("groupChannel.noDestination"));
                }
                ChannelData data = new ChannelData(true);
                data.setAddress(this.getLocalMember(false));
                data.setTimestamp(System.currentTimeMillis());
                byte[] b = null;
                if (msg instanceof ByteMessage) {
                    b = ((ByteMessage)msg).getMessage();
                    options |= 1;
                } else {
                    b = XByteBuffer.serialize(msg);
                    options &= 0xFFFFFFFE;
                }
                data.setOptions(options);
                buffer = BufferPool.getBufferPool().getBuffer(b.length + 128, false);
                buffer.append(b, 0, b.length);
                data.setMessage(buffer);
                InterceptorPayload payload = null;
                if (handler != null) {
                    payload = new InterceptorPayload();
                    payload.setErrorHandler(handler);
                }
                this.getFirstInterceptor().sendMessage(destination, data, payload);
                if (Logs.MESSAGES.isTraceEnabled()) {
                    Logs.MESSAGES.trace((Object)("GroupChannel - Sent msg:" + new UniqueId(data.getUniqueId()) + " at " + new Timestamp(System.currentTimeMillis()) + " to " + Arrays.toNameString(destination)));
                    Logs.MESSAGES.trace((Object)("GroupChannel - Send Message:" + new UniqueId(data.getUniqueId()) + " is " + msg));
                }
                uniqueId = new UniqueId(data.getUniqueId());
                if (buffer == null) break block11;
            }
            catch (IOException | RuntimeException e) {
                try {
                    throw new ChannelException(e);
                }
                catch (Throwable throwable) {
                    if (buffer != null) {
                        BufferPool.getBufferPool().returnBuffer(buffer);
                    }
                    throw throwable;
                }
            }
            BufferPool.getBufferPool().returnBuffer(buffer);
        }
        return uniqueId;
    }

    @Override
    public void messageReceived(ChannelMessage msg) {
        if (msg == null) {
            return;
        }
        try {
            if (Logs.MESSAGES.isTraceEnabled()) {
                Logs.MESSAGES.trace((Object)("GroupChannel - Received msg:" + new UniqueId(msg.getUniqueId()) + " at " + new Timestamp(System.currentTimeMillis()) + " from " + msg.getAddress().getName()));
            }
            Serializable fwd = null;
            if ((msg.getOptions() & 1) == 1) {
                fwd = new ByteMessage(msg.getMessage().getBytes());
            } else {
                try {
                    fwd = XByteBuffer.deserialize(msg.getMessage().getBytesDirect(), 0, msg.getMessage().getLength());
                }
                catch (Exception sx) {
                    log.error((Object)sm.getString("groupChannel.unable.deserialize", msg), (Throwable)sx);
                    return;
                }
            }
            if (Logs.MESSAGES.isTraceEnabled()) {
                Logs.MESSAGES.trace((Object)("GroupChannel - Receive Message:" + new UniqueId(msg.getUniqueId()) + " is " + fwd));
            }
            Member source = msg.getAddress();
            boolean rx = false;
            boolean delivered = false;
            for (ChannelListener channelListener : this.channelListeners) {
                if (channelListener == null || !channelListener.accept(fwd, source)) continue;
                channelListener.messageReceived(fwd, source);
                delivered = true;
                if (!(channelListener instanceof RpcChannel)) continue;
                rx = true;
            }
            if (!rx && fwd instanceof RpcMessage) {
                this.sendNoRpcChannelReply((RpcMessage)fwd, source);
            }
            if (Logs.MESSAGES.isTraceEnabled()) {
                Logs.MESSAGES.trace((Object)("GroupChannel delivered[" + delivered + "] id:" + new UniqueId(msg.getUniqueId())));
            }
        }
        catch (Exception x) {
            if (log.isWarnEnabled()) {
                log.warn((Object)sm.getString("groupChannel.receiving.error"), (Throwable)x);
            }
            throw new RemoteProcessException(sm.getString("groupChannel.receiving.error"), x);
        }
    }

    protected void sendNoRpcChannelReply(RpcMessage msg, Member destination) {
        try {
            if (msg instanceof RpcMessage.NoRpcChannelReply) {
                return;
            }
            RpcMessage.NoRpcChannelReply reply = new RpcMessage.NoRpcChannelReply(msg.rpcId, msg.uuid);
            this.send(new Member[]{destination}, reply, 8);
        }
        catch (Exception x) {
            log.error((Object)sm.getString("groupChannel.sendFail.noRpcChannelReply"), (Throwable)x);
        }
    }

    @Override
    public void memberAdded(Member member) {
        for (MembershipListener membershipListener : this.membershipListeners) {
            if (membershipListener == null) continue;
            membershipListener.memberAdded(member);
        }
    }

    @Override
    public void memberDisappeared(Member member) {
        for (MembershipListener membershipListener : this.membershipListeners) {
            if (membershipListener == null) continue;
            membershipListener.memberDisappeared(member);
        }
    }

    protected synchronized void setupDefaultStack() throws ChannelException {
        if (this.getFirstInterceptor() != null && this.getFirstInterceptor().getNext() instanceof ChannelCoordinator) {
            this.addInterceptor(new MessageDispatchInterceptor());
        }
        Iterator<ChannelInterceptor> interceptors = this.getInterceptors();
        while (interceptors.hasNext()) {
            ChannelInterceptor channelInterceptor = interceptors.next();
            channelInterceptor.setChannel(this);
        }
        this.coordinator.setChannel(this);
    }

    protected void checkOptionFlags() throws ChannelException {
        StringBuilder conflicts = new StringBuilder();
        for (ChannelInterceptor first = this.interceptors; first != null; first = first.getNext()) {
            int flag = first.getOptionFlag();
            if (flag == 0) continue;
            for (ChannelInterceptor next = first.getNext(); next != null; next = next.getNext()) {
                int nflag = next.getOptionFlag();
                if (nflag == 0 || (flag & nflag) != flag && (flag & nflag) != nflag) continue;
                conflicts.append('[');
                conflicts.append(first.getClass().getName());
                conflicts.append(':');
                conflicts.append(flag);
                conflicts.append(" == ");
                conflicts.append(next.getClass().getName());
                conflicts.append(':');
                conflicts.append(nflag);
                conflicts.append("] ");
            }
        }
        if (conflicts.length() > 0) {
            throw new ChannelException(sm.getString("groupChannel.optionFlag.conflict", conflicts.toString()));
        }
    }

    @Override
    public synchronized void start(int svc) throws ChannelException {
        JmxRegistry jmxRegistry;
        this.setupDefaultStack();
        if (this.optionCheck) {
            this.checkOptionFlags();
        }
        if ((jmxRegistry = JmxRegistry.getRegistry(this)) != null) {
            this.oname = jmxRegistry.registerJmx(",component=Channel", this);
        }
        if (this.utilityExecutor == null) {
            log.warn((Object)sm.getString("groupChannel.warn.noUtilityExecutor"));
            this.utilityExecutor = new ScheduledThreadPoolExecutor(1);
            this.ownExecutor = true;
        }
        super.start(svc);
        this.monitorFuture = this.utilityExecutor.scheduleWithFixedDelay(this::startHeartbeat, 0L, 60L, TimeUnit.SECONDS);
    }

    protected void startHeartbeat() {
        if (this.heartbeat && (this.heartbeatFuture == null || this.heartbeatFuture != null && this.heartbeatFuture.isDone())) {
            if (this.heartbeatFuture != null && this.heartbeatFuture.isDone()) {
                try {
                    this.heartbeatFuture.get();
                }
                catch (InterruptedException | ExecutionException e) {
                    log.error((Object)sm.getString("groupChannel.unable.sendHeartbeat"), (Throwable)e);
                }
            }
            this.heartbeatFuture = this.utilityExecutor.scheduleWithFixedDelay(new HeartbeatRunnable(), this.heartbeatSleeptime, this.heartbeatSleeptime, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public synchronized void stop(int svc) throws ChannelException {
        if (this.monitorFuture != null) {
            this.monitorFuture.cancel(true);
            this.monitorFuture = null;
        }
        if (this.heartbeatFuture != null) {
            this.heartbeatFuture.cancel(true);
            this.heartbeatFuture = null;
        }
        super.stop(svc);
        if (this.ownExecutor) {
            this.utilityExecutor.shutdown();
            this.utilityExecutor = null;
            this.ownExecutor = false;
        }
        if (this.oname != null) {
            JmxRegistry.getRegistry(this).unregisterJmx(this.oname);
            this.oname = null;
        }
    }

    public ChannelInterceptor getFirstInterceptor() {
        if (this.interceptors != null) {
            return this.interceptors;
        }
        return this.coordinator;
    }

    @Override
    public ScheduledExecutorService getUtilityExecutor() {
        return this.utilityExecutor;
    }

    @Override
    public void setUtilityExecutor(ScheduledExecutorService utilityExecutor) {
        this.utilityExecutor = utilityExecutor;
    }

    @Override
    public ChannelReceiver getChannelReceiver() {
        return this.coordinator.getClusterReceiver();
    }

    @Override
    public ChannelSender getChannelSender() {
        return this.coordinator.getClusterSender();
    }

    @Override
    public MembershipService getMembershipService() {
        return this.coordinator.getMembershipService();
    }

    @Override
    public void setChannelReceiver(ChannelReceiver clusterReceiver) {
        this.coordinator.setClusterReceiver(clusterReceiver);
    }

    @Override
    public void setChannelSender(ChannelSender clusterSender) {
        this.coordinator.setClusterSender(clusterSender);
    }

    @Override
    public void setMembershipService(MembershipService membershipService) {
        this.coordinator.setMembershipService(membershipService);
    }

    @Override
    public void addMembershipListener(MembershipListener membershipListener) {
        if (!this.membershipListeners.contains(membershipListener)) {
            this.membershipListeners.add(membershipListener);
        }
    }

    @Override
    public void removeMembershipListener(MembershipListener membershipListener) {
        this.membershipListeners.remove(membershipListener);
    }

    @Override
    public void addChannelListener(ChannelListener channelListener) {
        if (this.channelListeners.contains(channelListener)) {
            throw new IllegalArgumentException(sm.getString("groupChannel.listener.alreadyExist", channelListener, channelListener.getClass().getName()));
        }
        this.channelListeners.add(channelListener);
    }

    @Override
    public void removeChannelListener(ChannelListener channelListener) {
        this.channelListeners.remove(channelListener);
    }

    @Override
    public Iterator<ChannelInterceptor> getInterceptors() {
        return new InterceptorIterator(this.getNext(), this.coordinator);
    }

    public void setOptionCheck(boolean optionCheck) {
        this.optionCheck = optionCheck;
    }

    public void setHeartbeatSleeptime(long heartbeatSleeptime) {
        this.heartbeatSleeptime = heartbeatSleeptime;
    }

    @Override
    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    @Override
    public boolean getOptionCheck() {
        return this.optionCheck;
    }

    @Override
    public boolean getHeartbeat() {
        return this.heartbeat;
    }

    @Override
    public long getHeartbeatSleeptime() {
        return this.heartbeatSleeptime;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isJmxEnabled() {
        return this.jmxEnabled;
    }

    @Override
    public void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }

    @Override
    public String getJmxDomain() {
        return this.jmxDomain;
    }

    @Override
    public void setJmxDomain(String jmxDomain) {
        this.jmxDomain = jmxDomain;
    }

    @Override
    public String getJmxPrefix() {
        return this.jmxPrefix;
    }

    @Override
    public void setJmxPrefix(String jmxPrefix) {
        this.jmxPrefix = jmxPrefix;
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        return null;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
    }

    @Override
    public void preDeregister() throws Exception {
    }

    @Override
    public void postDeregister() {
        JmxRegistry.removeRegistry(this, true);
    }

    public class HeartbeatRunnable
    implements Runnable {
        @Override
        public void run() {
            GroupChannel.this.heartbeat();
        }
    }

    public static class InterceptorIterator
    implements Iterator<ChannelInterceptor> {
        private final ChannelInterceptor end;
        private ChannelInterceptor start;

        public InterceptorIterator(ChannelInterceptor start, ChannelInterceptor end) {
            this.end = end;
            this.start = start;
        }

        @Override
        public boolean hasNext() {
            return this.start != null && this.start != this.end;
        }

        @Override
        public ChannelInterceptor next() {
            ChannelInterceptor result = null;
            if (this.hasNext()) {
                result = this.start;
                this.start = this.start.getNext();
            }
            return result;
        }

        @Override
        public void remove() {
        }
    }
}

