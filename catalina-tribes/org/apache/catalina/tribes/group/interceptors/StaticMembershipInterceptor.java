/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.group.interceptors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelInterceptor;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.AbsoluteOrder;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.interceptors.StaticMembershipInterceptorMBean;
import org.apache.catalina.tribes.group.interceptors.TcpFailureDetector;
import org.apache.catalina.tribes.group.interceptors.TcpPingInterceptor;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class StaticMembershipInterceptor
extends ChannelInterceptorBase
implements StaticMembershipInterceptorMBean {
    private static final Log log = LogFactory.getLog(StaticMembershipInterceptor.class);
    protected static final StringManager sm = StringManager.getManager(StaticMembershipInterceptor.class);
    protected static final byte[] MEMBER_START = new byte[]{76, 111, 99, 97, 108, 32, 83, 116, 97, 116, 105, 99, 77, 101, 109, 98, 101, 114, 32, 78, 111, 116, 105, 102, 105, 99, 97, 116, 105, 111, 110, 32, 68, 97, 116, 97};
    protected static final byte[] MEMBER_STOP = new byte[]{76, 111, 99, 97, 108, 32, 83, 116, 97, 116, 105, 99, 77, 101, 109, 98, 101, 114, 32, 83, 104, 117, 116, 100, 111, 119, 110, 32, 68, 97, 116, 97};
    protected final ArrayList<Member> members = new ArrayList();
    protected Member localMember = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addStaticMember(Member member) {
        ArrayList<Member> arrayList = this.members;
        synchronized (arrayList) {
            if (!this.members.contains(member)) {
                this.members.add(member);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeStaticMember(Member member) {
        ArrayList<Member> arrayList = this.members;
        synchronized (arrayList) {
            if (this.members.contains(member)) {
                this.members.remove(member);
            }
        }
    }

    public void setLocalMember(Member member) {
        this.localMember = member;
        this.localMember.setLocal(true);
    }

    @Override
    public void messageReceived(ChannelMessage msg) {
        if (msg.getMessage().getLength() == MEMBER_START.length && Arrays.equals(MEMBER_START, msg.getMessage().getBytes())) {
            Member member = this.getMember(msg.getAddress());
            if (member != null) {
                super.memberAdded(member);
            }
        } else if (msg.getMessage().getLength() == MEMBER_STOP.length && Arrays.equals(MEMBER_STOP, msg.getMessage().getBytes())) {
            Member member = this.getMember(msg.getAddress());
            if (member != null) {
                try {
                    member.setCommand(Member.SHUTDOWN_PAYLOAD);
                    super.memberDisappeared(member);
                }
                finally {
                    member.setCommand(new byte[0]);
                }
            }
        } else {
            super.messageReceived(msg);
        }
    }

    @Override
    public boolean hasMembers() {
        return super.hasMembers() || this.members.size() > 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Member[] getMembers() {
        if (this.members.size() == 0) {
            return super.getMembers();
        }
        ArrayList<Member> arrayList = this.members;
        synchronized (arrayList) {
            int i;
            Member[] others = super.getMembers();
            Member[] result = new Member[this.members.size() + others.length];
            for (i = 0; i < others.length; ++i) {
                result[i] = others[i];
            }
            for (i = 0; i < this.members.size(); ++i) {
                result[i + others.length] = this.members.get(i);
            }
            AbsoluteOrder.absoluteOrder(result);
            return result;
        }
    }

    @Override
    public Member getMember(Member mbr) {
        if (this.members.contains(mbr)) {
            return this.members.get(this.members.indexOf(mbr));
        }
        return super.getMember(mbr);
    }

    @Override
    public Member getLocalMember(boolean incAlive) {
        if (this.localMember != null) {
            return this.localMember;
        }
        return super.getLocalMember(incAlive);
    }

    @Override
    public void start(int svc) throws ChannelException {
        if ((1 & svc) == 1) {
            super.start(1);
        }
        if ((2 & svc) == 2) {
            super.start(2);
        }
        StaticMembershipInterceptor base = this;
        ScheduledExecutorService executor = this.getChannel().getUtilityExecutor();
        for (Member member : this.members) {
            Runnable r = () -> {
                base.memberAdded(member);
                if (this.getfirstInterceptor().getMember(member) != null) {
                    this.sendLocalMember(new Member[]{member});
                }
            };
            executor.execute(r);
        }
        super.start(svc & 0xFFFFFFFE & 0xFFFFFFFD);
        TcpFailureDetector failureDetector = null;
        TcpPingInterceptor pingInterceptor = null;
        for (ChannelInterceptor prev = this.getPrevious(); prev != null; prev = prev.getPrevious()) {
            if (prev instanceof TcpFailureDetector) {
                failureDetector = (TcpFailureDetector)prev;
            }
            if (!(prev instanceof TcpPingInterceptor)) continue;
            pingInterceptor = (TcpPingInterceptor)prev;
        }
        if (failureDetector == null) {
            log.warn((Object)sm.getString("staticMembershipInterceptor.no.failureDetector"));
        }
        if (pingInterceptor == null) {
            log.warn((Object)sm.getString("staticMembershipInterceptor.no.pingInterceptor"));
        }
    }

    @Override
    public void stop(int svc) throws ChannelException {
        Member[] members = this.getfirstInterceptor().getMembers();
        this.sendShutdown(members);
        super.stop(svc);
    }

    protected void sendLocalMember(Member[] members) {
        try {
            this.sendMemberMessage(members, MEMBER_START);
        }
        catch (ChannelException cx) {
            log.warn((Object)sm.getString("staticMembershipInterceptor.sendLocalMember.failed"), (Throwable)cx);
        }
    }

    protected void sendShutdown(Member[] members) {
        try {
            this.sendMemberMessage(members, MEMBER_STOP);
        }
        catch (ChannelException cx) {
            log.warn((Object)sm.getString("staticMembershipInterceptor.sendShutdown.failed"), (Throwable)cx);
        }
    }

    protected ChannelInterceptor getfirstInterceptor() {
        StaticMembershipInterceptor result = null;
        ChannelInterceptor now = this;
        do {
            result = now;
        } while ((now = now.getPrevious()).getPrevious() != null);
        return result;
    }

    protected void sendMemberMessage(Member[] members, byte[] message) throws ChannelException {
        if (members == null || members.length == 0) {
            return;
        }
        ChannelData data = new ChannelData(true);
        data.setAddress(this.getLocalMember(false));
        data.setTimestamp(System.currentTimeMillis());
        data.setOptions(this.getOptionFlag());
        data.setMessage(new XByteBuffer(message, false));
        super.sendMessage(members, data, null);
    }
}

