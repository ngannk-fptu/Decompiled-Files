/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.transport.bio;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.transport.AbstractSender;
import org.apache.catalina.tribes.transport.MultiPointSender;
import org.apache.catalina.tribes.transport.bio.BioSender;

@Deprecated
public class MultipointBioSender
extends AbstractSender
implements MultiPointSender {
    protected final HashMap<Member, BioSender> bioSenders = new HashMap();

    @Override
    public synchronized void sendMessage(Member[] destination, ChannelMessage msg) throws ChannelException {
        byte[] data = XByteBuffer.createDataPackage((ChannelData)msg);
        BioSender[] senders = this.setupForSend(destination);
        ChannelException cx = null;
        for (int i = 0; i < senders.length; ++i) {
            try {
                senders[i].sendMessage(data, (msg.getOptions() & 2) == 2);
                continue;
            }
            catch (Exception x) {
                if (cx == null) {
                    cx = new ChannelException(x);
                }
                cx.addFaultyMember(destination[i], x);
            }
        }
        if (cx != null) {
            throw cx;
        }
    }

    protected BioSender[] setupForSend(Member[] destination) throws ChannelException {
        ChannelException cx = null;
        BioSender[] result = new BioSender[destination.length];
        for (int i = 0; i < destination.length; ++i) {
            try {
                BioSender sender = this.bioSenders.get(destination[i]);
                if (sender == null) {
                    sender = new BioSender();
                    AbstractSender.transferProperties(this, sender);
                    sender.setDestination(destination[i]);
                    this.bioSenders.put(destination[i], sender);
                }
                result[i] = sender;
                if (!result[i].isConnected()) {
                    result[i].connect();
                }
                result[i].keepalive();
                continue;
            }
            catch (Exception x) {
                if (cx == null) {
                    cx = new ChannelException(x);
                }
                cx.addFaultyMember(destination[i], x);
            }
        }
        if (cx != null) {
            throw cx;
        }
        return result;
    }

    @Override
    public void connect() throws IOException {
        this.setConnected(true);
    }

    private synchronized void close() throws ChannelException {
        ChannelException x = null;
        Object[] members = this.bioSenders.keySet().toArray();
        for (int i = 0; i < members.length; ++i) {
            Member mbr = (Member)members[i];
            try {
                BioSender sender = this.bioSenders.get(mbr);
                sender.disconnect();
            }
            catch (Exception e) {
                if (x == null) {
                    x = new ChannelException(e);
                }
                x.addFaultyMember(mbr, e);
            }
            this.bioSenders.remove(mbr);
        }
        if (x != null) {
            throw x;
        }
    }

    @Override
    public void add(Member member) {
    }

    @Override
    public void remove(Member member) {
        BioSender sender = this.bioSenders.remove(member);
        if (sender != null) {
            sender.disconnect();
        }
    }

    @Override
    public synchronized void disconnect() {
        try {
            this.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
        this.setConnected(false);
    }

    protected void finalize() throws Throwable {
        try {
            this.disconnect();
        }
        catch (Exception exception) {
            // empty catch block
        }
        super.finalize();
    }

    @Override
    public boolean keepalive() {
        boolean result = false;
        Map.Entry[] entries = this.bioSenders.entrySet().toArray(new Map.Entry[0]);
        for (int i = 0; i < entries.length; ++i) {
            BioSender sender = (BioSender)entries[i].getValue();
            if (!sender.keepalive()) continue;
            this.bioSenders.remove(entries[i].getKey());
        }
        return result;
    }
}

