/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes.transport;

import java.io.IOException;
import javax.management.ObjectName;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.ChannelSender;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.jmx.JmxRegistry;
import org.apache.catalina.tribes.transport.MultiPointSender;
import org.apache.catalina.tribes.transport.nio.PooledParallelSender;

public class ReplicationTransmitter
implements ChannelSender {
    private Channel channel;
    private ObjectName oname = null;
    private MultiPointSender transport = new PooledParallelSender();

    public MultiPointSender getTransport() {
        return this.transport;
    }

    public void setTransport(MultiPointSender transport) {
        this.transport = transport;
    }

    @Override
    public void sendMessage(ChannelMessage message, Member[] destination) throws ChannelException {
        MultiPointSender sender = this.getTransport();
        sender.sendMessage(destination, message);
    }

    @Override
    public synchronized void start() throws IOException {
        this.getTransport().connect();
        JmxRegistry jmxRegistry = JmxRegistry.getRegistry(this.channel);
        if (jmxRegistry != null) {
            this.oname = jmxRegistry.registerJmx(",component=Sender", this.transport);
        }
    }

    @Override
    public synchronized void stop() {
        this.getTransport().disconnect();
        if (this.oname != null) {
            JmxRegistry.getRegistry(this.channel).unregisterJmx(this.oname);
            this.oname = null;
        }
        this.channel = null;
    }

    @Override
    public void heartbeat() {
        if (this.getTransport() != null) {
            this.getTransport().keepalive();
        }
    }

    @Override
    public synchronized void add(Member member) {
        this.getTransport().add(member);
    }

    @Override
    public synchronized void remove(Member member) {
        this.getTransport().remove(member);
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }

    @Override
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
}

