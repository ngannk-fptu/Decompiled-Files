/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import java.io.IOException;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.Heartbeat;
import org.apache.catalina.tribes.MessageListener;

public interface ChannelReceiver
extends Heartbeat {
    public static final int MAX_UDP_SIZE = 65535;

    public void start() throws IOException;

    public void stop();

    public String getHost();

    public int getPort();

    public int getSecurePort();

    public int getUdpPort();

    public void setMessageListener(MessageListener var1);

    public MessageListener getMessageListener();

    public Channel getChannel();

    public void setChannel(Channel var1);
}

