/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.transport.bio;

import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.transport.AbstractSender;
import org.apache.catalina.tribes.transport.DataSender;
import org.apache.catalina.tribes.transport.MultiPointSender;
import org.apache.catalina.tribes.transport.PooledSender;
import org.apache.catalina.tribes.transport.bio.MultipointBioSender;
import org.apache.catalina.tribes.util.StringManager;

@Deprecated
public class PooledMultiSender
extends PooledSender {
    protected static final StringManager sm = StringManager.getManager(PooledMultiSender.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void sendMessage(Member[] destination, ChannelMessage msg) throws ChannelException {
        MultiPointSender sender = null;
        try {
            sender = (MultiPointSender)this.getSender();
            if (sender == null) {
                ChannelException cx = new ChannelException(sm.getString("pooledMultiSender.unable.retrieve.sender", Long.toString(this.getMaxWait())));
                for (int i = 0; i < destination.length; ++i) {
                    cx.addFaultyMember(destination[i], new NullPointerException(sm.getString("pooledMultiSender.retrieve.fail")));
                }
                throw cx;
            }
            sender.sendMessage(destination, msg);
            sender.keepalive();
        }
        finally {
            if (sender != null) {
                this.returnSender(sender);
            }
        }
    }

    @Override
    public DataSender getNewDataSender() {
        MultipointBioSender sender = new MultipointBioSender();
        AbstractSender.transferProperties(this, sender);
        return sender;
    }
}

