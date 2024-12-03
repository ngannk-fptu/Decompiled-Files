/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.group.interceptors;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelInterceptor;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.interceptors.StaticMembershipInterceptor;
import org.apache.catalina.tribes.group.interceptors.TcpFailureDetector;
import org.apache.catalina.tribes.group.interceptors.TcpPingInterceptorMBean;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class TcpPingInterceptor
extends ChannelInterceptorBase
implements TcpPingInterceptorMBean {
    private static final Log log = LogFactory.getLog(TcpPingInterceptor.class);
    protected static final StringManager sm = StringManager.getManager(TcpPingInterceptor.class);
    protected static final byte[] TCP_PING_DATA = new byte[]{79, -89, 115, 72, 121, -33, 67, -55, -97, 111, -119, -128, -95, 91, 7, 20, 125, -39, 82, 91, -21, -33, 67, -102, -73, 126, -66, -113, -127, 103, 30, -74, 55, 21, -66, -121, 69, 33, 76, -88, -65, 10, 77, 19, 83, 56, 21, 50, 85, -10, -108, -73, 58, -33, 33, 120, -111, 4, 125, -41, 114, -124, -64, -43};
    protected long interval = 1000L;
    protected boolean useThread = false;
    protected boolean staticOnly = false;
    protected volatile boolean running = true;
    protected PingThread thread = null;
    protected static final AtomicInteger cnt = new AtomicInteger(0);
    WeakReference<TcpFailureDetector> failureDetector = null;
    WeakReference<StaticMembershipInterceptor> staticMembers = null;

    @Override
    public synchronized void start(int svc) throws ChannelException {
        super.start(svc);
        this.running = true;
        if (this.thread == null && this.useThread) {
            this.thread = new PingThread();
            this.thread.setDaemon(true);
            String channelName = "";
            if (this.getChannel().getName() != null) {
                channelName = "[" + this.getChannel().getName() + "]";
            }
            this.thread.setName("TcpPingInterceptor.PingThread" + channelName + "-" + cnt.addAndGet(1));
            this.thread.start();
        }
        for (ChannelInterceptor next = this.getNext(); next != null; next = next.getNext()) {
            if (next instanceof TcpFailureDetector) {
                this.failureDetector = new WeakReference<TcpFailureDetector>((TcpFailureDetector)next);
            }
            if (!(next instanceof StaticMembershipInterceptor)) continue;
            this.staticMembers = new WeakReference<StaticMembershipInterceptor>((StaticMembershipInterceptor)next);
        }
    }

    @Override
    public synchronized void stop(int svc) throws ChannelException {
        this.running = false;
        if (this.thread != null) {
            this.thread.interrupt();
            this.thread = null;
        }
        super.stop(svc);
    }

    @Override
    public void heartbeat() {
        super.heartbeat();
        if (!this.getUseThread()) {
            this.sendPing();
        }
    }

    @Override
    public long getInterval() {
        return this.interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void setUseThread(boolean useThread) {
        this.useThread = useThread;
    }

    public void setStaticOnly(boolean staticOnly) {
        this.staticOnly = staticOnly;
    }

    @Override
    public boolean getUseThread() {
        return this.useThread;
    }

    public boolean getStaticOnly() {
        return this.staticOnly;
    }

    protected void sendPing() {
        TcpFailureDetector tcpFailureDetector;
        TcpFailureDetector tcpFailureDetector2 = tcpFailureDetector = this.failureDetector != null ? (TcpFailureDetector)this.failureDetector.get() : null;
        if (tcpFailureDetector != null) {
            tcpFailureDetector.checkMembers(true);
        } else {
            StaticMembershipInterceptor smi;
            StaticMembershipInterceptor staticMembershipInterceptor = smi = this.staticOnly && this.staticMembers != null ? (StaticMembershipInterceptor)this.staticMembers.get() : null;
            if (smi != null) {
                this.sendPingMessage(smi.getMembers());
            } else {
                this.sendPingMessage(this.getMembers());
            }
        }
    }

    protected void sendPingMessage(Member[] members) {
        if (members == null || members.length == 0) {
            return;
        }
        ChannelData data = new ChannelData(true);
        data.setAddress(this.getLocalMember(false));
        data.setTimestamp(System.currentTimeMillis());
        data.setOptions(this.getOptionFlag());
        data.setMessage(new XByteBuffer(TCP_PING_DATA, false));
        try {
            super.sendMessage(members, data, null);
        }
        catch (ChannelException x) {
            log.warn((Object)sm.getString("tcpPingInterceptor.ping.failed"), (Throwable)x);
        }
    }

    @Override
    public void messageReceived(ChannelMessage msg) {
        boolean process = true;
        if (this.okToProcess(msg.getOptions())) {
            boolean bl = process = msg.getMessage().getLength() != TCP_PING_DATA.length || !Arrays.equals(TCP_PING_DATA, msg.getMessage().getBytes());
        }
        if (process) {
            super.messageReceived(msg);
        } else if (log.isDebugEnabled()) {
            log.debug((Object)("Received a TCP ping packet:" + msg));
        }
    }

    protected class PingThread
    extends Thread {
        protected PingThread() {
        }

        @Override
        public void run() {
            while (TcpPingInterceptor.this.running) {
                try {
                    PingThread.sleep(TcpPingInterceptor.this.interval);
                    TcpPingInterceptor.this.sendPing();
                }
                catch (InterruptedException interruptedException) {
                }
                catch (Exception x) {
                    log.warn((Object)sm.getString("tcpPingInterceptor.pingFailed.pingThread"), (Throwable)x);
                }
            }
        }
    }
}

