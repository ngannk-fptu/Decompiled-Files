/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import java.util.Properties;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.MembershipProvider;

public interface MembershipService {
    public static final int MBR_RX = 4;
    public static final int MBR_TX = 8;

    public void setProperties(Properties var1);

    public Properties getProperties();

    public void start() throws Exception;

    public void start(int var1) throws Exception;

    public void stop(int var1);

    public boolean hasMembers();

    public Member getMember(Member var1);

    public Member[] getMembers();

    public Member getLocalMember(boolean var1);

    public String[] getMembersByName();

    public Member findMemberByName(String var1);

    public void setLocalMemberProperties(String var1, int var2, int var3, int var4);

    public void setMembershipListener(MembershipListener var1);

    public void removeMembershipListener();

    public void setPayload(byte[] var1);

    public void setDomain(byte[] var1);

    public void broadcast(ChannelMessage var1) throws ChannelException;

    public Channel getChannel();

    public void setChannel(Channel var1);

    public MembershipProvider getMembershipProvider();
}

