/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import org.apache.catalina.tribes.ChannelMessage;

public interface MessageListener {
    public void messageReceived(ChannelMessage var1);

    public boolean accept(ChannelMessage var1);
}

