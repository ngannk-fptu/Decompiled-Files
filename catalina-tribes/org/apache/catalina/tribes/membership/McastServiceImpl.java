/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.membership;

import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.MessageListener;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.membership.McastService;
import org.apache.catalina.tribes.membership.MemberImpl;
import org.apache.catalina.tribes.membership.Membership;
import org.apache.catalina.tribes.membership.MembershipProviderBase;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class McastServiceImpl
extends MembershipProviderBase {
    private static final Log log = LogFactory.getLog(McastService.class);
    protected static final int MAX_PACKET_SIZE = 65535;
    protected static final StringManager sm = StringManager.getManager("org.apache.catalina.tribes.membership");
    protected volatile boolean doRunSender = false;
    protected volatile boolean doRunReceiver = false;
    protected volatile int startLevel = 0;
    protected MulticastSocket socket;
    protected final MemberImpl member;
    protected final InetAddress address;
    protected final int port;
    protected final long timeToExpiration;
    protected final long sendFrequency;
    protected DatagramPacket sendPacket;
    protected DatagramPacket receivePacket;
    protected final MembershipListener service;
    protected final MessageListener msgservice;
    protected ReceiverThread receiver;
    protected SenderThread sender;
    protected final int mcastTTL;
    protected int mcastSoTimeout = -1;
    protected final InetAddress mcastBindAddress;
    protected int recoveryCounter = 10;
    protected long recoverySleepTime = 5000L;
    protected boolean recoveryEnabled = true;
    protected final boolean localLoopbackDisabled;
    private Channel channel;
    protected final Object expiredMutex = new Object();
    private final Object sendLock = new Object();

    public McastServiceImpl(MemberImpl member, long sendFrequency, long expireTime, int port, InetAddress bind, InetAddress mcastAddress, int ttl, int soTimeout, MembershipListener service, MessageListener msgservice, boolean localLoopbackDisabled) throws IOException {
        this.member = member;
        this.address = mcastAddress;
        this.port = port;
        this.mcastSoTimeout = soTimeout;
        this.mcastTTL = ttl;
        this.mcastBindAddress = bind;
        this.timeToExpiration = expireTime;
        this.service = service;
        this.msgservice = msgservice;
        this.sendFrequency = sendFrequency;
        this.localLoopbackDisabled = localLoopbackDisabled;
        this.init();
    }

    public void init() throws IOException {
        this.setupSocket();
        this.sendPacket = new DatagramPacket(new byte[65535], 65535);
        this.sendPacket.setAddress(this.address);
        this.sendPacket.setPort(this.port);
        this.receivePacket = new DatagramPacket(new byte[65535], 65535);
        this.receivePacket.setAddress(this.address);
        this.receivePacket.setPort(this.port);
        this.member.setCommand(new byte[0]);
        if (this.membership == null) {
            this.membership = new Membership(this.member);
        }
    }

    protected void setupSocket() throws IOException {
        if (this.mcastBindAddress != null) {
            try {
                log.info((Object)sm.getString("mcastServiceImpl.bind", this.address, Integer.toString(this.port)));
                this.socket = new MulticastSocket(new InetSocketAddress(this.address, this.port));
            }
            catch (BindException e) {
                log.info((Object)sm.getString("mcastServiceImpl.bind.failed"));
                this.socket = new MulticastSocket(this.port);
            }
        } else {
            this.socket = new MulticastSocket(this.port);
        }
        this.socket.setLoopbackMode(this.localLoopbackDisabled);
        if (this.mcastBindAddress != null) {
            if (log.isInfoEnabled()) {
                log.info((Object)sm.getString("mcastServiceImpl.setInterface", this.mcastBindAddress));
            }
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(this.mcastBindAddress);
            this.socket.setNetworkInterface(networkInterface);
        }
        if (this.mcastSoTimeout <= 0) {
            this.mcastSoTimeout = (int)this.sendFrequency;
        }
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("mcastServiceImpl.setSoTimeout", Integer.toString(this.mcastSoTimeout)));
        }
        this.socket.setSoTimeout(this.mcastSoTimeout);
        if (this.mcastTTL >= 0) {
            if (log.isInfoEnabled()) {
                log.info((Object)sm.getString("mcastServiceImpl.setTTL", Integer.toString(this.mcastTTL)));
            }
            this.socket.setTimeToLive(this.mcastTTL);
        }
    }

    @Override
    public synchronized void start(int level) throws IOException {
        boolean valid = false;
        if ((level & 4) == 4) {
            if (this.receiver != null) {
                throw new IllegalStateException(sm.getString("mcastServiceImpl.receive.running"));
            }
            try {
                if (this.sender == null) {
                    this.socket.joinGroup(new InetSocketAddress(this.address, 0), null);
                }
            }
            catch (IOException iox) {
                log.error((Object)sm.getString("mcastServiceImpl.unable.join"));
                throw iox;
            }
            this.doRunReceiver = true;
            this.receiver = new ReceiverThread();
            this.receiver.setDaemon(true);
            this.receiver.start();
            valid = true;
        }
        if ((level & 8) == 8) {
            if (this.sender != null) {
                throw new IllegalStateException(sm.getString("mcastServiceImpl.send.running"));
            }
            if (this.receiver == null) {
                this.socket.joinGroup(new InetSocketAddress(this.address, 0), null);
            }
            this.send(false);
            this.doRunSender = true;
            this.sender = new SenderThread(this.sendFrequency);
            this.sender.setDaemon(true);
            this.sender.start();
            valid = true;
        }
        if (!valid) {
            throw new IllegalArgumentException(sm.getString("mcastServiceImpl.invalid.startLevel"));
        }
        this.waitForMembers(level);
        this.startLevel |= level;
    }

    private void waitForMembers(int level) {
        long memberwait = this.sendFrequency * 2L;
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("mcastServiceImpl.waitForMembers.start", Long.toString(memberwait), Integer.toString(level)));
        }
        try {
            Thread.sleep(memberwait);
        }
        catch (InterruptedException interruptedException) {
            // empty catch block
        }
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("mcastServiceImpl.waitForMembers.done", Integer.toString(level)));
        }
    }

    @Override
    public synchronized boolean stop(int level) throws IOException {
        boolean valid = false;
        if ((level & 4) == 4) {
            valid = true;
            this.doRunReceiver = false;
            if (this.receiver != null) {
                this.receiver.interrupt();
            }
            this.receiver = null;
        }
        if ((level & 8) == 8) {
            valid = true;
            this.doRunSender = false;
            if (this.sender != null) {
                this.sender.interrupt();
            }
            this.sender = null;
        }
        if (!valid) {
            throw new IllegalArgumentException(sm.getString("mcastServiceImpl.invalid.stopLevel"));
        }
        this.startLevel &= ~level;
        if (this.startLevel == 0) {
            this.member.setCommand(Member.SHUTDOWN_PAYLOAD);
            this.send(false);
            try {
                this.socket.leaveGroup(new InetSocketAddress(this.address, 0), null);
            }
            catch (Exception exception) {
                // empty catch block
            }
            try {
                this.socket.close();
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.member.setServiceStartTime(-1L);
        }
        return this.startLevel == 0;
    }

    public void receive() throws IOException {
        boolean checkexpired = true;
        try {
            this.socket.receive(this.receivePacket);
            if (this.receivePacket.getLength() > 65535) {
                log.error((Object)sm.getString("mcastServiceImpl.packet.tooLong", Integer.toString(this.receivePacket.getLength())));
            } else {
                byte[] data = new byte[this.receivePacket.getLength()];
                System.arraycopy(this.receivePacket.getData(), this.receivePacket.getOffset(), data, 0, data.length);
                if (XByteBuffer.firstIndexOf(data, 0, MemberImpl.TRIBES_MBR_BEGIN) == 0) {
                    this.memberDataReceived(data);
                } else {
                    this.memberBroadcastsReceived(data);
                }
            }
        }
        catch (SocketTimeoutException socketTimeoutException) {
            // empty catch block
        }
        if (checkexpired) {
            this.checkExpired();
        }
    }

    private void memberDataReceived(byte[] data) {
        Member m = MemberImpl.getMember(data);
        if (log.isTraceEnabled()) {
            log.trace((Object)("Mcast receive ping from member " + m));
        }
        Runnable t = null;
        Thread currentThread = Thread.currentThread();
        if (Arrays.equals(m.getCommand(), Member.SHUTDOWN_PAYLOAD)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Member has shutdown:" + m));
            }
            this.membership.removeMember(m);
            t = () -> {
                String name = currentThread.getName();
                try {
                    currentThread.setName("Membership-MemberDisappeared");
                    this.service.memberDisappeared(m);
                }
                finally {
                    currentThread.setName(name);
                }
            };
        } else if (this.membership.memberAlive(m)) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Mcast add member " + m));
            }
            t = () -> {
                String name = currentThread.getName();
                try {
                    currentThread.setName("Membership-MemberAdded");
                    this.service.memberAdded(m);
                }
                finally {
                    currentThread.setName(name);
                }
            };
        }
        if (t != null) {
            this.executor.execute(t);
        }
    }

    private void memberBroadcastsReceived(byte[] b) {
        XByteBuffer buffer;
        if (log.isTraceEnabled()) {
            log.trace((Object)"Mcast received broadcasts.");
        }
        if ((buffer = new XByteBuffer(b, true)).countPackages(true) > 0) {
            int count = buffer.countPackages();
            ChannelData[] data = new ChannelData[count];
            for (int i = 0; i < count; ++i) {
                try {
                    data[i] = buffer.extractPackage(true);
                    continue;
                }
                catch (IllegalStateException ise) {
                    log.debug((Object)"Unable to decode message.", (Throwable)ise);
                }
            }
            Runnable t = () -> {
                Thread currentThread = Thread.currentThread();
                String name = currentThread.getName();
                try {
                    currentThread.setName("Membership-MemberAdded");
                    for (ChannelData datum : data) {
                        try {
                            if (datum == null || this.member.equals(datum.getAddress())) continue;
                            this.msgservice.messageReceived(datum);
                        }
                        catch (Throwable t1) {
                            if (t1 instanceof ThreadDeath) {
                                throw (ThreadDeath)t1;
                            }
                            if (t1 instanceof VirtualMachineError) {
                                throw (VirtualMachineError)t1;
                            }
                            log.error((Object)sm.getString("mcastServiceImpl.unableReceive.broadcastMessage"), t1);
                        }
                    }
                }
                finally {
                    currentThread.setName(name);
                }
            };
            this.executor.execute(t);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void checkExpired() {
        Object object = this.expiredMutex;
        synchronized (object) {
            Member[] expired;
            for (Member member : expired = this.membership.expire(this.timeToExpiration)) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Mcast expire  member " + member));
                }
                try {
                    Runnable t = () -> {
                        Thread currentThread = Thread.currentThread();
                        String name = currentThread.getName();
                        try {
                            currentThread.setName("Membership-MemberExpired");
                            this.service.memberDisappeared(member);
                        }
                        finally {
                            currentThread.setName(name);
                        }
                    };
                    this.executor.execute(t);
                }
                catch (Exception x) {
                    log.error((Object)sm.getString("mcastServiceImpl.memberDisappeared.failed"), (Throwable)x);
                }
            }
        }
    }

    public void send(boolean checkexpired) throws IOException {
        this.send(checkexpired, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void send(boolean checkexpired, DatagramPacket packet) throws IOException {
        boolean bl = checkexpired = checkexpired && packet == null;
        if (packet == null) {
            this.member.inc();
            if (log.isTraceEnabled()) {
                log.trace((Object)("Mcast send ping from member " + this.member));
            }
            byte[] data = this.member.getData();
            packet = new DatagramPacket(data, data.length);
        } else if (log.isTraceEnabled()) {
            log.trace((Object)("Sending message broadcast " + packet.getLength() + " bytes from " + this.member));
        }
        packet.setAddress(this.address);
        packet.setPort(this.port);
        Object object = this.sendLock;
        synchronized (object) {
            this.socket.send(packet);
        }
        if (checkexpired) {
            this.checkExpired();
        }
    }

    public long getServiceStartTime() {
        return this.member != null ? this.member.getServiceStartTime() : -1L;
    }

    public int getRecoveryCounter() {
        return this.recoveryCounter;
    }

    public boolean isRecoveryEnabled() {
        return this.recoveryEnabled;
    }

    public long getRecoverySleepTime() {
        return this.recoverySleepTime;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setRecoveryCounter(int recoveryCounter) {
        this.recoveryCounter = recoveryCounter;
    }

    public void setRecoveryEnabled(boolean recoveryEnabled) {
        this.recoveryEnabled = recoveryEnabled;
    }

    public void setRecoverySleepTime(long recoverySleepTime) {
        this.recoverySleepTime = recoverySleepTime;
    }

    public class ReceiverThread
    extends Thread {
        int errorCounter = 0;

        public ReceiverThread() {
            String channelName = "";
            if (McastServiceImpl.this.channel.getName() != null) {
                channelName = "[" + McastServiceImpl.this.channel.getName() + "]";
            }
            this.setName("Tribes-MembershipReceiver" + channelName);
        }

        @Override
        public void run() {
            while (McastServiceImpl.this.doRunReceiver) {
                try {
                    McastServiceImpl.this.receive();
                    this.errorCounter = 0;
                }
                catch (ArrayIndexOutOfBoundsException ax) {
                    if (!log.isDebugEnabled()) continue;
                    log.debug((Object)"Invalid member mcast package.", (Throwable)ax);
                }
                catch (Exception x) {
                    if (this.errorCounter == 0 && McastServiceImpl.this.doRunReceiver) {
                        log.warn((Object)sm.getString("mcastServiceImpl.error.receiving"), (Throwable)x);
                    } else if (log.isDebugEnabled()) {
                        log.debug((Object)("Error receiving mcast package" + (McastServiceImpl.this.doRunReceiver ? ". Sleeping 500ms" : ".")), (Throwable)x);
                    }
                    if (!McastServiceImpl.this.doRunReceiver) continue;
                    try {
                        ReceiverThread.sleep(500L);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    if (++this.errorCounter < McastServiceImpl.this.recoveryCounter) continue;
                    this.errorCounter = 0;
                    RecoveryThread.recover(McastServiceImpl.this);
                }
            }
        }
    }

    public class SenderThread
    extends Thread {
        final long time;
        int errorCounter = 0;

        public SenderThread(long time) {
            this.time = time;
            String channelName = "";
            if (McastServiceImpl.this.channel.getName() != null) {
                channelName = "[" + McastServiceImpl.this.channel.getName() + "]";
            }
            this.setName("Tribes-MembershipSender" + channelName);
        }

        @Override
        public void run() {
            while (McastServiceImpl.this.doRunSender) {
                block7: {
                    try {
                        McastServiceImpl.this.send(true);
                        this.errorCounter = 0;
                    }
                    catch (Exception x) {
                        if (this.errorCounter == 0) {
                            log.warn((Object)sm.getString("mcastServiceImpl.send.failed"), (Throwable)x);
                        } else {
                            log.debug((Object)"Unable to send mcast message.", (Throwable)x);
                        }
                        if (++this.errorCounter < McastServiceImpl.this.recoveryCounter) break block7;
                        this.errorCounter = 0;
                        RecoveryThread.recover(McastServiceImpl.this);
                    }
                }
                try {
                    SenderThread.sleep(this.time);
                }
                catch (Exception exception) {}
            }
        }
    }

    protected static class RecoveryThread
    extends Thread {
        private static final AtomicBoolean running = new AtomicBoolean(false);
        final McastServiceImpl parent;

        public static synchronized void recover(McastServiceImpl parent) {
            if (!parent.isRecoveryEnabled()) {
                return;
            }
            if (!running.compareAndSet(false, true)) {
                return;
            }
            RecoveryThread t = new RecoveryThread(parent);
            String channelName = "";
            if (parent.channel.getName() != null) {
                channelName = "[" + parent.channel.getName() + "]";
            }
            t.setName("Tribes-MembershipRecovery" + channelName);
            t.setDaemon(true);
            t.start();
        }

        public RecoveryThread(McastServiceImpl parent) {
            this.parent = parent;
        }

        public boolean stopService() {
            try {
                this.parent.stop(12);
                return true;
            }
            catch (Exception x) {
                log.warn((Object)sm.getString("mcastServiceImpl.recovery.stopFailed"), (Throwable)x);
                return false;
            }
        }

        public boolean startService() {
            try {
                this.parent.init();
                this.parent.start(12);
                return true;
            }
            catch (Exception x) {
                log.warn((Object)sm.getString("mcastServiceImpl.recovery.startFailed"), (Throwable)x);
                return false;
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public void run() {
            boolean success = false;
            int attempt = 0;
            try {
                while (!success) {
                    if (log.isInfoEnabled()) {
                        log.info((Object)sm.getString("mcastServiceImpl.recovery"));
                    }
                    if (this.stopService() & this.startService()) {
                        success = true;
                        if (log.isInfoEnabled()) {
                            log.info((Object)sm.getString("mcastServiceImpl.recovery.successful"));
                        }
                    }
                    try {
                        if (success) continue;
                        if (log.isInfoEnabled()) {
                            log.info((Object)sm.getString("mcastServiceImpl.recovery.failed", Integer.toString(++attempt), Long.toString(this.parent.recoverySleepTime)));
                        }
                        RecoveryThread.sleep(this.parent.recoverySleepTime);
                    }
                    catch (InterruptedException interruptedException) {}
                }
                return;
            }
            finally {
                running.set(false);
            }
        }
    }
}

