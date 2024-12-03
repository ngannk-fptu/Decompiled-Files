/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.membership;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Heartbeat;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.Response;
import org.apache.catalina.tribes.group.RpcCallback;
import org.apache.catalina.tribes.group.RpcChannel;
import org.apache.catalina.tribes.membership.Membership;
import org.apache.catalina.tribes.membership.MembershipProviderBase;
import org.apache.catalina.tribes.membership.StaticMember;
import org.apache.catalina.tribes.util.Arrays;
import org.apache.catalina.tribes.util.ExceptionUtils;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class StaticMembershipProvider
extends MembershipProviderBase
implements RpcCallback,
ChannelListener,
Heartbeat {
    protected static final StringManager sm = StringManager.getManager(StaticMembershipProvider.class);
    private static final Log log = LogFactory.getLog(StaticMembershipProvider.class);
    protected Channel channel;
    protected RpcChannel rpcChannel;
    private String membershipName = null;
    private byte[] membershipId = null;
    protected ArrayList<StaticMember> staticMembers;
    protected int sendOptions = 8;
    protected long expirationTime = 5000L;
    protected int connectTimeout = 500;
    protected long rpcTimeout = 3000L;
    protected int startLevel = 0;
    protected boolean useThread = false;
    protected long pingInterval = 1000L;
    protected volatile boolean running = true;
    protected PingThread thread = null;

    @Override
    public void init(Properties properties) throws Exception {
        String expirationTimeStr = properties.getProperty("expirationTime");
        this.expirationTime = Long.parseLong(expirationTimeStr);
        String connectTimeoutStr = properties.getProperty("connectTimeout");
        this.connectTimeout = Integer.parseInt(connectTimeoutStr);
        String rpcTimeouStr = properties.getProperty("rpcTimeout");
        this.rpcTimeout = Long.parseLong(rpcTimeouStr);
        this.membershipName = properties.getProperty("membershipName");
        this.membershipId = this.membershipName.getBytes(StandardCharsets.ISO_8859_1);
        this.membership = new Membership(this.service.getLocalMember(true));
        this.rpcChannel = new RpcChannel(this.membershipId, this.channel, this);
        this.channel.addChannelListener(this);
        String useThreadStr = properties.getProperty("useThread");
        this.useThread = Boolean.parseBoolean(useThreadStr);
        String pingIntervalStr = properties.getProperty("pingInterval");
        this.pingInterval = Long.parseLong(pingIntervalStr);
    }

    @Override
    public void start(int level) throws Exception {
        if (4 == (level & 4)) {
            // empty if block
        }
        if (8 == (level & 8)) {
            // empty if block
        }
        this.startLevel |= level;
        if (this.startLevel == 12) {
            this.startMembership(this.getAliveMembers(this.staticMembers.toArray(new Member[0])));
            this.running = true;
            if (this.thread == null && this.useThread) {
                this.thread = new PingThread();
                this.thread.setDaemon(true);
                this.thread.setName("StaticMembership.PingThread[" + this.channel.getName() + "]");
                this.thread.start();
            }
        }
    }

    @Override
    public boolean stop(int level) throws Exception {
        if (4 == (level & 4)) {
            // empty if block
        }
        if (8 == (level & 8)) {
            // empty if block
        }
        this.startLevel &= ~level;
        if (this.startLevel == 0) {
            this.running = false;
            if (this.thread != null) {
                this.thread.interrupt();
                this.thread = null;
            }
            if (this.rpcChannel != null) {
                this.rpcChannel.breakdown();
            }
            if (this.channel != null) {
                try {
                    this.stopMembership(this.getMembers());
                }
                catch (Throwable t) {
                    ExceptionUtils.handleThrowable(t);
                }
                this.channel.removeChannelListener(this);
                this.channel = null;
            }
            this.rpcChannel = null;
            this.membership.reset();
        }
        return this.startLevel == 0;
    }

    protected void startMembership(Member[] members) throws ChannelException {
        if (members.length == 0) {
            return;
        }
        MemberMessage msg = new MemberMessage(this.membershipId, 1, this.service.getLocalMember(true));
        Response[] resp = this.rpcChannel.send(members, msg, 3, this.sendOptions, this.rpcTimeout);
        if (resp.length > 0) {
            for (Response response : resp) {
                this.messageReceived(response.getMessage(), response.getSource());
            }
        } else {
            log.warn((Object)sm.getString("staticMembershipProvider.startMembership.noReplies"));
        }
    }

    protected Member setupMember(Member mbr) {
        return mbr;
    }

    protected void memberAdded(Member member) {
        Member mbr = this.setupMember(member);
        if (this.membership.memberAlive(mbr)) {
            Runnable r = () -> {
                Thread currentThread = Thread.currentThread();
                String name = currentThread.getName();
                try {
                    currentThread.setName("StaticMembership-memberAdded");
                    this.membershipListener.memberAdded(mbr);
                }
                finally {
                    currentThread.setName(name);
                }
            };
            this.executor.execute(r);
        }
    }

    protected void memberDisappeared(Member member) {
        this.membership.removeMember(member);
        Runnable r = () -> {
            Thread currentThread = Thread.currentThread();
            String name = currentThread.getName();
            try {
                currentThread.setName("StaticMembership-memberDisappeared");
                this.membershipListener.memberDisappeared(member);
            }
            finally {
                currentThread.setName(name);
            }
        };
        this.executor.execute(r);
    }

    protected void memberAlive(Member member) {
        if (!this.membership.contains(member)) {
            this.memberAdded(member);
        }
        this.membership.memberAlive(member);
    }

    protected void stopMembership(Member[] members) {
        if (members.length == 0) {
            return;
        }
        Member localmember = this.service.getLocalMember(false);
        localmember.setCommand(Member.SHUTDOWN_PAYLOAD);
        MemberMessage msg = new MemberMessage(this.membershipId, 2, localmember);
        try {
            this.channel.send(members, msg, this.sendOptions);
        }
        catch (ChannelException e) {
            log.error((Object)sm.getString("staticMembershipProvider.stopMembership.sendFailed"), (Throwable)e);
        }
    }

    @Override
    public void messageReceived(Serializable msg, Member sender) {
        MemberMessage memMsg = (MemberMessage)msg;
        Member member = memMsg.getMember();
        if (memMsg.getMsgtype() == 1) {
            this.memberAdded(member);
        } else if (memMsg.getMsgtype() == 2) {
            this.memberDisappeared(member);
        } else if (memMsg.getMsgtype() == 3) {
            this.memberAlive(member);
        }
    }

    @Override
    public boolean accept(Serializable msg, Member sender) {
        boolean result = false;
        if (msg instanceof MemberMessage) {
            result = Arrays.equals(this.membershipId, ((MemberMessage)msg).getMembershipId());
        }
        return result;
    }

    @Override
    public Serializable replyRequest(Serializable msg, Member sender) {
        if (!(msg instanceof MemberMessage)) {
            return null;
        }
        MemberMessage memMsg = (MemberMessage)msg;
        if (memMsg.getMsgtype() == 1) {
            this.messageReceived(memMsg, sender);
            memMsg.setMember(this.service.getLocalMember(true));
            return memMsg;
        }
        if (memMsg.getMsgtype() == 3) {
            this.messageReceived(memMsg, sender);
            memMsg.setMember(this.service.getLocalMember(true));
            return memMsg;
        }
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("staticMembershipProvider.replyRequest.ignored", memMsg.getTypeDesc()));
        }
        return null;
    }

    @Override
    public void leftOver(Serializable msg, Member sender) {
        if (!(msg instanceof MemberMessage)) {
            return;
        }
        MemberMessage memMsg = (MemberMessage)msg;
        if (memMsg.getMsgtype() == 1) {
            this.messageReceived(memMsg, sender);
        } else if (memMsg.getMsgtype() == 3) {
            this.messageReceived(memMsg, sender);
        } else if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("staticMembershipProvider.leftOver.ignored", memMsg.getTypeDesc()));
        }
    }

    @Override
    public void heartbeat() {
        try {
            if (!this.useThread) {
                this.ping();
            }
        }
        catch (ChannelException e) {
            log.warn((Object)sm.getString("staticMembershipProvider.heartbeat.failed"), (Throwable)e);
        }
    }

    protected void ping() throws ChannelException {
        Member[] members = this.getAliveMembers(this.staticMembers.toArray(new Member[0]));
        if (members.length > 0) {
            try {
                Response[] resp;
                MemberMessage msg = new MemberMessage(this.membershipId, 3, this.service.getLocalMember(true));
                for (Response response : resp = this.rpcChannel.send(members, msg, 3, this.sendOptions, this.rpcTimeout)) {
                    this.messageReceived(response.getMessage(), response.getSource());
                }
            }
            catch (ChannelException ce) {
                ChannelException.FaultyMember[] faultyMembers;
                for (ChannelException.FaultyMember faultyMember : faultyMembers = ce.getFaultyMembers()) {
                    this.memberDisappeared(faultyMember.getMember());
                }
                throw ce;
            }
        }
        this.checkExpired();
    }

    protected void checkExpired() {
        Member[] expired;
        for (Member member : expired = this.membership.expire(this.expirationTime)) {
            this.membershipListener.memberDisappeared(member);
        }
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setStaticMembers(ArrayList<StaticMember> staticMembers) {
        this.staticMembers = staticMembers;
    }

    private Member[] getAliveMembers(Member[] members) {
        ArrayList<Member> aliveMembers = new ArrayList<Member>();
        for (Member member : members) {
            try (Socket socket = new Socket();){
                InetAddress ia = InetAddress.getByAddress(member.getHost());
                InetSocketAddress addr = new InetSocketAddress(ia, member.getPort());
                socket.connect(addr, this.connectTimeout);
                aliveMembers.add(member);
            }
            catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
            }
        }
        return aliveMembers.toArray(new Member[0]);
    }

    protected class PingThread
    extends Thread {
        protected PingThread() {
        }

        @Override
        public void run() {
            while (StaticMembershipProvider.this.running) {
                try {
                    PingThread.sleep(StaticMembershipProvider.this.pingInterval);
                    StaticMembershipProvider.this.ping();
                }
                catch (InterruptedException interruptedException) {
                }
                catch (Exception x) {
                    log.warn((Object)sm.getString("staticMembershipProvider.pingThread.failed"), (Throwable)x);
                }
            }
        }
    }

    public static class MemberMessage
    implements Serializable {
        private static final long serialVersionUID = 1L;
        public static final int MSG_START = 1;
        public static final int MSG_STOP = 2;
        public static final int MSG_PING = 3;
        private final int msgtype;
        private final byte[] membershipId;
        private Member member;

        public MemberMessage(byte[] membershipId, int msgtype, Member member) {
            this.membershipId = membershipId;
            this.msgtype = msgtype;
            this.member = member;
        }

        public int getMsgtype() {
            return this.msgtype;
        }

        public byte[] getMembershipId() {
            return this.membershipId;
        }

        public Member getMember() {
            return this.member;
        }

        public void setMember(Member local) {
            this.member = local;
        }

        public String toString() {
            StringBuilder buf = new StringBuilder("MemberMessage[");
            buf.append("name=");
            buf.append(new String(this.membershipId));
            buf.append("; type=");
            buf.append(this.getTypeDesc());
            buf.append("; member=");
            buf.append(this.member);
            buf.append(']');
            return buf.toString();
        }

        protected String getTypeDesc() {
            switch (this.msgtype) {
                case 1: {
                    return "MSG_START";
                }
                case 2: {
                    return "MSG_STOP";
                }
                case 3: {
                    return "MSG_PING";
                }
            }
            return "UNKNOWN";
        }
    }
}

