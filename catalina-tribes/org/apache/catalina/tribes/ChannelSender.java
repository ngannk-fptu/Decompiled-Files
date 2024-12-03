/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import java.io.IOException;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Heartbeat;
import org.apache.catalina.tribes.Member;

public interface ChannelSender
extends Heartbeat {
    public void add(Member var1);

    public void remove(Member var1);

    public void start() throws IOException;

    public void stop();

    @Override
    public void heartbeat();

    public void sendMessage(ChannelMessage var1, Member[] var2) throws ChannelException;

    public Channel getChannel();

    public void setChannel(Channel var1);
}

