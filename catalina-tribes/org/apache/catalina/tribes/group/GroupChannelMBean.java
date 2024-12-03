/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.group;

import java.io.Serializable;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.ErrorHandler;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.UniqueId;

public interface GroupChannelMBean {
    public boolean getOptionCheck();

    public boolean getHeartbeat();

    public long getHeartbeatSleeptime();

    public void start(int var1) throws ChannelException;

    public void stop(int var1) throws ChannelException;

    public UniqueId send(Member[] var1, Serializable var2, int var3) throws ChannelException;

    public UniqueId send(Member[] var1, Serializable var2, int var3, ErrorHandler var4) throws ChannelException;

    public void addMembershipListener(MembershipListener var1);

    public void addChannelListener(ChannelListener var1);

    public void removeMembershipListener(MembershipListener var1);

    public void removeChannelListener(ChannelListener var1);

    public boolean hasMembers();

    public Member[] getMembers();

    public Member getLocalMember(boolean var1);
}

