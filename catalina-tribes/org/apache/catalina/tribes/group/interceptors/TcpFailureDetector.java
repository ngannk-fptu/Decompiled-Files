/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.group.interceptors;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.RemoteProcessException;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.group.interceptors.TcpFailureDetectorMBean;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.membership.Membership;
import org.apache.catalina.tribes.membership.StaticMember;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class TcpFailureDetector
extends ChannelInterceptorBase
implements TcpFailureDetectorMBean {
    private static final Log log = LogFactory.getLog(TcpFailureDetector.class);
    protected static final StringManager sm = StringManager.getManager(TcpFailureDetector.class);
    protected static final byte[] TCP_FAIL_DETECT = new byte[]{79, -89, 115, 72, 121, -126, 67, -55, -97, 111, -119, -128, -95, 91, 7, 20, 125, -39, 82, 91, -21, -15, 67, -102, -73, 126, -66, -113, -127, 103, 30, -74, 55, 21, -66, -121, 69, 126, 76, -88, -65, 10, 77, 19, 83, 56, 21, 50, 85, -10, -108, -73, 58, -6, 64, 120, -111, 4, 125, -41, 114, -124, -64, -43};
    protected long connectTimeout = 1000L;
    protected boolean performSendTest = true;
    protected boolean performReadTest = false;
    protected long readTestTimeout = 5000L;
    protected Membership membership = null;
    protected final HashMap<Member, Long> removeSuspects = new HashMap();
    protected final HashMap<Member, Long> addSuspects = new HashMap();
    protected int removeSuspectsTimeout = 300;

    @Override
    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        try {
            super.sendMessage(destination, msg, payload);
        }
        catch (ChannelException cx) {
            ChannelException.FaultyMember[] mbrs;
            for (ChannelException.FaultyMember mbr : mbrs = cx.getFaultyMembers()) {
                if (mbr.getCause() == null || mbr.getCause() instanceof RemoteProcessException) continue;
                this.memberDisappeared(mbr.getMember());
            }
            throw cx;
        }
    }

    @Override
    public void messageReceived(ChannelMessage msg) {
        boolean process = true;
        if (this.okToProcess(msg.getOptions())) {
            boolean bl = process = msg.getMessage().getLength() != TCP_FAIL_DETECT.length || !Arrays.equals(TCP_FAIL_DETECT, msg.getMessage().getBytes());
        }
        if (process) {
            super.messageReceived(msg);
        } else if (log.isDebugEnabled()) {
            log.debug((Object)("Received a failure detector packet:" + msg));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void memberAdded(Member member) {
        if (this.membership == null) {
            this.setupMembership();
        }
        boolean notify = false;
        Membership membership = this.membership;
        synchronized (membership) {
            if (this.removeSuspects.containsKey(member)) {
                this.removeSuspects.remove(member);
            } else if (this.membership.getMember(member) == null) {
                if (this.memberAlive(member)) {
                    this.membership.memberAlive(member);
                    this.addSuspects.remove(member);
                    notify = true;
                } else if (member instanceof StaticMember) {
                    this.addSuspects.put(member, System.currentTimeMillis());
                }
            }
        }
        if (notify) {
            super.memberAdded(member);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void memberDisappeared(Member member) {
        boolean shutdown;
        if (this.membership == null) {
            this.setupMembership();
        }
        if (shutdown = Arrays.equals(member.getCommand(), Member.SHUTDOWN_PAYLOAD)) {
            Membership membership = this.membership;
            synchronized (membership) {
                if (!this.membership.contains(member)) {
                    return;
                }
                this.membership.removeMember(member);
                this.removeSuspects.remove(member);
                if (member instanceof StaticMember) {
                    this.addSuspects.put(member, System.currentTimeMillis());
                }
            }
            super.memberDisappeared(member);
        } else {
            boolean notify = false;
            if (log.isInfoEnabled()) {
                log.info((Object)sm.getString("tcpFailureDetector.memberDisappeared.verify", member));
            }
            Membership membership = this.membership;
            synchronized (membership) {
                if (!this.membership.contains(member)) {
                    if (log.isInfoEnabled()) {
                        log.info((Object)sm.getString("tcpFailureDetector.already.disappeared", member));
                    }
                    return;
                }
                if (!this.memberAlive(member)) {
                    this.membership.removeMember(member);
                    this.removeSuspects.remove(member);
                    if (member instanceof StaticMember) {
                        this.addSuspects.put(member, System.currentTimeMillis());
                    }
                    notify = true;
                } else {
                    this.removeSuspects.put(member, System.currentTimeMillis());
                }
            }
            if (notify) {
                if (log.isInfoEnabled()) {
                    log.info((Object)sm.getString("tcpFailureDetector.member.disappeared", member));
                }
                super.memberDisappeared(member);
            } else if (log.isInfoEnabled()) {
                log.info((Object)sm.getString("tcpFailureDetector.still.alive", member));
            }
        }
    }

    @Override
    public boolean hasMembers() {
        if (this.membership == null) {
            this.setupMembership();
        }
        return this.membership.hasMembers();
    }

    @Override
    public Member[] getMembers() {
        if (this.membership == null) {
            this.setupMembership();
        }
        return this.membership.getMembers();
    }

    @Override
    public Member getMember(Member mbr) {
        if (this.membership == null) {
            this.setupMembership();
        }
        return this.membership.getMember(mbr);
    }

    @Override
    public Member getLocalMember(boolean incAlive) {
        return super.getLocalMember(incAlive);
    }

    @Override
    public void heartbeat() {
        super.heartbeat();
        this.checkMembers(false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void checkMembers(boolean checkAll) {
        try {
            if (this.membership == null) {
                this.setupMembership();
            }
            Membership membership = this.membership;
            synchronized (membership) {
                if (!checkAll) {
                    this.performBasicCheck();
                } else {
                    this.performForcedCheck();
                }
            }
        }
        catch (Exception x) {
            log.warn((Object)sm.getString("tcpFailureDetector.heartbeat.failed"), (Throwable)x);
        }
    }

    protected void performForcedCheck() {
        Member[] members = super.getMembers();
        for (int i = 0; members != null && i < members.length; ++i) {
            if (this.memberAlive(members[i])) {
                if (this.membership.memberAlive(members[i])) {
                    super.memberAdded(members[i]);
                }
                this.addSuspects.remove(members[i]);
                continue;
            }
            if (this.membership.getMember(members[i]) == null) continue;
            this.membership.removeMember(members[i]);
            this.removeSuspects.remove(members[i]);
            if (members[i] instanceof StaticMember) {
                this.addSuspects.put(members[i], System.currentTimeMillis());
            }
            super.memberDisappeared(members[i]);
        }
    }

    protected void performBasicCheck() {
        Member[] keys;
        Member[] members = super.getMembers();
        for (int i = 0; members != null && i < members.length; ++i) {
            if (this.addSuspects.containsKey(members[i]) && this.membership.getMember(members[i]) == null || !this.membership.memberAlive(members[i])) continue;
            if (this.memberAlive(members[i])) {
                log.warn((Object)sm.getString("tcpFailureDetector.performBasicCheck.memberAdded", members[i]));
                super.memberAdded(members[i]);
                continue;
            }
            this.membership.removeMember(members[i]);
        }
        for (Member m : keys = this.removeSuspects.keySet().toArray(new Member[0])) {
            long timeNow;
            int timeIdle;
            if (this.membership.getMember(m) != null && !this.memberAlive(m)) {
                this.membership.removeMember(m);
                if (m instanceof StaticMember) {
                    this.addSuspects.put(m, System.currentTimeMillis());
                }
                super.memberDisappeared(m);
                this.removeSuspects.remove(m);
                if (!log.isInfoEnabled()) continue;
                log.info((Object)sm.getString("tcpFailureDetector.suspectMember.dead", m));
                continue;
            }
            if (this.removeSuspectsTimeout <= 0 || (timeIdle = (int)(((timeNow = System.currentTimeMillis()) - this.removeSuspects.get(m)) / 1000L)) <= this.removeSuspectsTimeout) continue;
            this.removeSuspects.remove(m);
        }
        for (Member m : keys = this.addSuspects.keySet().toArray(new Member[0])) {
            if (this.membership.getMember(m) != null || !this.memberAlive(m)) continue;
            this.membership.memberAlive(m);
            super.memberAdded(m);
            this.addSuspects.remove(m);
            if (!log.isInfoEnabled()) continue;
            log.info((Object)sm.getString("tcpFailureDetector.suspectMember.alive", m));
        }
    }

    protected synchronized void setupMembership() {
        if (this.membership == null) {
            this.membership = new Membership(super.getLocalMember(true));
        }
    }

    protected boolean memberAlive(Member mbr) {
        return this.memberAlive(mbr, TCP_FAIL_DETECT, this.performSendTest, this.performReadTest, this.readTestTimeout, this.connectTimeout, this.getOptionFlag());
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected boolean memberAlive(Member mbr, byte[] msgData, boolean sendTest, boolean readTest, long readTimeout, long conTimeout, int optionFlag) {
        if (Arrays.equals(mbr.getCommand(), Member.SHUTDOWN_PAYLOAD)) {
            return false;
        }
        try (Socket socket2222 = new Socket();){
            InetAddress ia = InetAddress.getByAddress(mbr.getHost());
            InetSocketAddress addr = new InetSocketAddress(ia, mbr.getPort());
            socket2222.setSoTimeout((int)readTimeout);
            socket2222.connect(addr, (int)conTimeout);
            if (sendTest) {
                ChannelData data = new ChannelData(true);
                data.setAddress(this.getLocalMember(false));
                data.setMessage(new XByteBuffer(msgData, false));
                data.setTimestamp(System.currentTimeMillis());
                int options = optionFlag | 1;
                options = readTest ? (options |= 2) : (options &= 0xFFFFFFFD);
                data.setOptions(options);
                byte[] message = XByteBuffer.createDataPackage(data);
                socket2222.getOutputStream().write(message);
                if (readTest) {
                    int length = socket2222.getInputStream().read(message);
                    boolean bl = length > 0;
                    return bl;
                }
            }
            boolean bl = true;
            return bl;
        }
        catch (ConnectException | NoRouteToHostException | SocketTimeoutException socket2222) {
            return false;
        }
        catch (Exception x) {
            log.error((Object)sm.getString("tcpFailureDetector.failureDetection.failed", mbr), (Throwable)x);
        }
        return false;
    }

    @Override
    public long getReadTestTimeout() {
        return this.readTestTimeout;
    }

    @Override
    public boolean getPerformSendTest() {
        return this.performSendTest;
    }

    @Override
    public boolean getPerformReadTest() {
        return this.performReadTest;
    }

    @Override
    public long getConnectTimeout() {
        return this.connectTimeout;
    }

    @Override
    public int getRemoveSuspectsTimeout() {
        return this.removeSuspectsTimeout;
    }

    @Override
    public void setPerformReadTest(boolean performReadTest) {
        this.performReadTest = performReadTest;
    }

    @Override
    public void setPerformSendTest(boolean performSendTest) {
        this.performSendTest = performSendTest;
    }

    @Override
    public void setReadTestTimeout(long readTestTimeout) {
        this.readTestTimeout = readTestTimeout;
    }

    @Override
    public void setConnectTimeout(long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    @Override
    public void setRemoveSuspectsTimeout(int removeSuspectsTimeout) {
        this.removeSuspectsTimeout = removeSuspectsTimeout;
    }
}

