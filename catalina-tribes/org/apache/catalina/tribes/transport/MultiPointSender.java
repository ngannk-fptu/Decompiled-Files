/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.transport;

import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.transport.DataSender;

public interface MultiPointSender
extends DataSender {
    public void sendMessage(Member[] var1, ChannelMessage var2) throws ChannelException;

    public void setMaxRetryAttempts(int var1);

    public void setDirectBuffer(boolean var1);

    public void add(Member var1);

    public void remove(Member var1);
}

