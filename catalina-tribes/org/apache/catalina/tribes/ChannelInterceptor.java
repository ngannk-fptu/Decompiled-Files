/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Heartbeat;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.MembershipListener;
import org.apache.catalina.tribes.group.InterceptorPayload;

public interface ChannelInterceptor
extends MembershipListener,
Heartbeat {
    public int getOptionFlag();

    public void setOptionFlag(int var1);

    public void setNext(ChannelInterceptor var1);

    public ChannelInterceptor getNext();

    public void setPrevious(ChannelInterceptor var1);

    public ChannelInterceptor getPrevious();

    public void sendMessage(Member[] var1, ChannelMessage var2, InterceptorPayload var3) throws ChannelException;

    public void messageReceived(ChannelMessage var1);

    @Override
    public void heartbeat();

    public boolean hasMembers();

    public Member[] getMembers();

    public Member getLocalMember(boolean var1);

    public Member getMember(Member var1);

    public void start(int var1) throws ChannelException;

    public void stop(int var1) throws ChannelException;

    public void fireInterceptorEvent(InterceptorEvent var1);

    public Channel getChannel();

    public void setChannel(Channel var1);

    public static interface InterceptorEvent {
        public int getEventType();

        public String getEventTypeDesc();

        public ChannelInterceptor getInterceptor();
    }
}

