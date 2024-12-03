/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.membership;

import java.util.Properties;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.MembershipService;
import org.apache.catalina.tribes.membership.Membership;

public abstract class MembershipServiceBase
implements MembershipService,
MembershipListener {
    protected Properties properties = new Properties();
    protected volatile MembershipListener listener;
    protected Channel channel;

    @Override
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public boolean hasMembers() {
        if (this.getMembershipProvider() == null) {
            return false;
        }
        return this.getMembershipProvider().hasMembers();
    }

    @Override
    public Member getMember(Member mbr) {
        if (this.getMembershipProvider() == null) {
            return null;
        }
        return this.getMembershipProvider().getMember(mbr);
    }

    @Override
    public Member[] getMembers() {
        if (this.getMembershipProvider() == null) {
            return Membership.EMPTY_MEMBERS;
        }
        return this.getMembershipProvider().getMembers();
    }

    @Override
    public String[] getMembersByName() {
        String[] membernames;
        Member[] currentMembers = this.getMembers();
        if (currentMembers != null) {
            membernames = new String[currentMembers.length];
            for (int i = 0; i < currentMembers.length; ++i) {
                membernames[i] = currentMembers[i].toString();
            }
        } else {
            membernames = new String[]{};
        }
        return membernames;
    }

    @Override
    public Member findMemberByName(String name) {
        Member[] currentMembers;
        for (Member currentMember : currentMembers = this.getMembers()) {
            if (!name.equals(currentMember.toString())) continue;
            return currentMember;
        }
        return null;
    }

    @Override
    public void setMembershipListener(MembershipListener listener) {
        this.listener = listener;
    }

    @Override
    public void removeMembershipListener() {
        this.listener = null;
    }

    @Override
    public void memberAdded(Member member) {
        MembershipListener listener = this.listener;
        if (listener != null) {
            listener.memberAdded(member);
        }
    }

    @Override
    public void memberDisappeared(Member member) {
        MembershipListener listener = this.listener;
        if (listener != null) {
            listener.memberDisappeared(member);
        }
    }

    @Override
    public void broadcast(ChannelMessage message) throws ChannelException {
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void start() throws Exception {
        this.start(4);
        this.start(8);
    }
}

