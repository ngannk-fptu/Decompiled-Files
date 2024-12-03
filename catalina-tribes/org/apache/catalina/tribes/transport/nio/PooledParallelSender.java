/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.transport.nio;

import java.io.IOException;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.transport.DataSender;
import org.apache.catalina.tribes.transport.PooledSender;
import org.apache.catalina.tribes.transport.nio.ParallelNioSender;
import org.apache.catalina.tribes.transport.nio.PooledParallelSenderMBean;
import org.apache.catalina.tribes.util.StringManager;

public class PooledParallelSender
extends PooledSender
implements PooledParallelSenderMBean {
    protected static final StringManager sm = StringManager.getManager(PooledParallelSender.class);

    @Override
    public void sendMessage(Member[] destination, ChannelMessage message) throws ChannelException {
        if (!this.isConnected()) {
            throw new ChannelException(sm.getString("pooledParallelSender.sender.disconnected"));
        }
        ParallelNioSender sender = (ParallelNioSender)this.getSender();
        if (sender == null) {
            ChannelException cx = new ChannelException(sm.getString("pooledParallelSender.unable.retrieveSender.timeout", Long.toString(this.getMaxWait())));
            for (Member member : destination) {
                cx.addFaultyMember(member, new NullPointerException(sm.getString("pooledParallelSender.unable.retrieveSender")));
            }
            throw cx;
        }
        try {
            if (!sender.isConnected()) {
                sender.connect();
            }
            sender.sendMessage(destination, message);
            sender.keepalive();
        }
        catch (ChannelException x) {
            sender.disconnect();
            throw x;
        }
        finally {
            this.returnSender(sender);
        }
    }

    @Override
    public DataSender getNewDataSender() {
        try {
            ParallelNioSender sender = new ParallelNioSender();
            PooledParallelSender.transferProperties(this, sender);
            return sender;
        }
        catch (IOException x) {
            throw new RuntimeException(sm.getString("pooledParallelSender.unable.open"), x);
        }
    }
}

