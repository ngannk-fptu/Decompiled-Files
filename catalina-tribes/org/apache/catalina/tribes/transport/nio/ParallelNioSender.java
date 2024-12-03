/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.transport.nio;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.UniqueId;
import org.apache.catalina.tribes.io.ChannelData;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.transport.AbstractSender;
import org.apache.catalina.tribes.transport.MultiPointSender;
import org.apache.catalina.tribes.transport.SenderState;
import org.apache.catalina.tribes.transport.nio.NioSender;
import org.apache.catalina.tribes.util.Logs;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class ParallelNioSender
extends AbstractSender
implements MultiPointSender {
    private static final Log log = LogFactory.getLog(ParallelNioSender.class);
    protected static final StringManager sm = StringManager.getManager(ParallelNioSender.class);
    protected final long selectTimeout = 5000L;
    protected final Selector selector;
    protected final HashMap<Member, NioSender> nioSenders = new HashMap();

    public ParallelNioSender() throws IOException {
        this.selector = Selector.open();
        this.setConnected(true);
    }

    @Override
    public synchronized void sendMessage(Member[] destination, ChannelMessage msg) throws ChannelException {
        long start = System.currentTimeMillis();
        this.setUdpBased((msg.getOptions() & 0x20) == 32);
        byte[] data = XByteBuffer.createDataPackage((ChannelData)msg);
        NioSender[] senders = this.setupForSend(destination);
        this.connect(senders);
        this.setData(senders, data);
        int remaining = senders.length;
        ChannelException cx = null;
        try {
            boolean waitForAck;
            long delta = System.currentTimeMillis() - start;
            boolean bl = waitForAck = (2 & msg.getOptions()) == 2;
            while (remaining > 0 && delta < this.getTimeout()) {
                try {
                    SendResult result = this.doLoop(5000L, this.getMaxRetryAttempts(), waitForAck, msg);
                    remaining -= result.getCompleted();
                    if (result.getFailed() != null) {
                        remaining -= result.getFailed().getFaultyMembers().length;
                        if (cx == null) {
                            cx = result.getFailed();
                        } else {
                            cx.addFaultyMember(result.getFailed().getFaultyMembers());
                        }
                    }
                }
                catch (Exception x) {
                    if (log.isTraceEnabled()) {
                        log.trace((Object)"Error sending message", (Throwable)x);
                    }
                    if (cx == null) {
                        cx = x instanceof ChannelException ? (ChannelException)x : new ChannelException(sm.getString("parallelNioSender.send.failed"), x);
                    }
                    for (NioSender sender : senders) {
                        if (sender.isComplete()) continue;
                        cx.addFaultyMember(sender.getDestination(), x);
                    }
                    throw cx;
                }
                delta = System.currentTimeMillis() - start;
            }
            if (remaining > 0) {
                ChannelException cxtimeout = new ChannelException(sm.getString("parallelNioSender.operation.timedout", Long.toString(this.getTimeout())));
                if (cx == null) {
                    cx = new ChannelException(sm.getString("parallelNioSender.operation.timedout", Long.toString(this.getTimeout())));
                }
                for (NioSender sender : senders) {
                    if (sender.isComplete()) continue;
                    cx.addFaultyMember(sender.getDestination(), cxtimeout);
                }
                throw cx;
            }
            if (cx != null) {
                throw cx;
            }
        }
        catch (Exception x) {
            try {
                this.disconnect();
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (x instanceof ChannelException) {
                throw (ChannelException)x;
            }
            throw new ChannelException(x);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SendResult doLoop(long selectTimeOut, int maxAttempts, boolean waitForAck, ChannelMessage msg) throws ChannelException {
        int selectedKeys;
        SendResult result = new SendResult();
        try {
            selectedKeys = this.selector.select(selectTimeOut);
        }
        catch (IOException ioe) {
            throw new ChannelException(sm.getString("parallelNioSender.send.failed"), ioe);
        }
        if (selectedKeys == 0) {
            return result;
        }
        Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
        while (it.hasNext()) {
            SelectionKey sk = it.next();
            it.remove();
            int readyOps = sk.readyOps();
            sk.interestOps(sk.interestOps() & ~readyOps);
            NioSender sender = (NioSender)sk.attachment();
            try {
                if (!sender.process(sk, waitForAck)) continue;
                sender.setComplete(true);
                result.complete(sender);
                if (Logs.MESSAGES.isTraceEnabled()) {
                    Logs.MESSAGES.trace((Object)("ParallelNioSender - Sent msg:" + new UniqueId(msg.getUniqueId()) + " at " + new Timestamp(System.currentTimeMillis()) + " to " + sender.getDestination().getName()));
                }
                SenderState.getSenderState(sender.getDestination()).setReady();
            }
            catch (Exception x) {
                if (log.isTraceEnabled()) {
                    log.trace((Object)("Error while processing send to " + sender.getDestination().getName()), (Throwable)x);
                }
                SenderState state = SenderState.getSenderState(sender.getDestination());
                int attempt = sender.getAttempt() + 1;
                boolean retry = attempt <= maxAttempts && maxAttempts > 0;
                SenderState senderState = state;
                synchronized (senderState) {
                    if (state.isSuspect()) {
                        state.setFailing();
                    }
                    if (state.isReady()) {
                        state.setSuspect();
                        if (retry) {
                            log.warn((Object)sm.getString("parallelNioSender.send.fail.retrying", sender.getDestination().getName()));
                        } else {
                            log.warn((Object)sm.getString("parallelNioSender.send.fail", sender.getDestination().getName()), (Throwable)x);
                        }
                    }
                }
                if (!this.isConnected()) {
                    log.warn((Object)sm.getString("parallelNioSender.sender.disconnected.notRetry", sender.getDestination().getName()));
                    ChannelException cx = new ChannelException(sm.getString("parallelNioSender.sender.disconnected.sendFailed"), x);
                    cx.addFaultyMember(sender.getDestination(), x);
                    result.failed(cx);
                    break;
                }
                byte[] data = sender.getMessage();
                if (retry) {
                    try {
                        sender.disconnect();
                        sender.connect();
                        sender.setAttempt(attempt);
                        sender.setMessage(data);
                    }
                    catch (Exception ignore) {
                        state.setFailing();
                    }
                    continue;
                }
                ChannelException cx = new ChannelException(sm.getString("parallelNioSender.sendFailed.attempt", Integer.toString(sender.getAttempt()), Integer.toString(maxAttempts)), x);
                cx.addFaultyMember(sender.getDestination(), x);
                result.failed(cx);
            }
        }
        return result;
    }

    private void connect(NioSender[] senders) throws ChannelException {
        ChannelException x = null;
        for (NioSender sender : senders) {
            try {
                sender.connect();
            }
            catch (IOException io) {
                if (x == null) {
                    x = new ChannelException(io);
                }
                x.addFaultyMember(sender.getDestination(), io);
            }
        }
        if (x != null) {
            throw x;
        }
    }

    private void setData(NioSender[] senders, byte[] data) throws ChannelException {
        ChannelException x = null;
        for (NioSender sender : senders) {
            try {
                sender.setMessage(data);
            }
            catch (IOException io) {
                if (x == null) {
                    x = new ChannelException(io);
                }
                x.addFaultyMember(sender.getDestination(), io);
            }
        }
        if (x != null) {
            throw x;
        }
    }

    private NioSender[] setupForSend(Member[] destination) throws ChannelException {
        ChannelException cx = null;
        NioSender[] result = new NioSender[destination.length];
        for (int i = 0; i < destination.length; ++i) {
            NioSender sender = this.nioSenders.get(destination[i]);
            try {
                if (sender == null) {
                    sender = new NioSender();
                    ParallelNioSender.transferProperties(this, sender);
                    this.nioSenders.put(destination[i], sender);
                }
                sender.reset();
                sender.setDestination(destination[i]);
                sender.setSelector(this.selector);
                sender.setUdpBased(this.isUdpBased());
                result[i] = sender;
                continue;
            }
            catch (UnknownHostException x) {
                if (cx == null) {
                    cx = new ChannelException(sm.getString("parallelNioSender.unable.setup.NioSender"), x);
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
    public void connect() {
        this.setConnected(true);
    }

    private synchronized void close() throws ChannelException {
        Object[] members;
        ChannelException x = null;
        for (Object member : members = this.nioSenders.keySet().toArray()) {
            Member mbr = (Member)member;
            try {
                NioSender sender = this.nioSenders.get(mbr);
                sender.disconnect();
            }
            catch (Exception e) {
                if (x == null) {
                    x = new ChannelException(e);
                }
                x.addFaultyMember(mbr, e);
            }
            this.nioSenders.remove(mbr);
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
        NioSender sender = this.nioSenders.remove(member);
        if (sender != null) {
            sender.disconnect();
        }
    }

    @Override
    public synchronized void disconnect() {
        this.setConnected(false);
        try {
            this.close();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    protected void finalize() throws Throwable {
        block4: {
            try {
                this.disconnect();
            }
            catch (Exception exception) {
                // empty catch block
            }
            try {
                this.selector.close();
            }
            catch (Exception e) {
                if (!log.isDebugEnabled()) break block4;
                log.debug((Object)"Failed to close selector", (Throwable)e);
            }
        }
        super.finalize();
    }

    @Override
    public synchronized boolean keepalive() {
        boolean result = false;
        Iterator<Map.Entry<Member, NioSender>> i = this.nioSenders.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<Member, NioSender> entry = i.next();
            NioSender sender = entry.getValue();
            if (sender.keepalive()) {
                i.remove();
                result = true;
                continue;
            }
            try {
                sender.read();
            }
            catch (IOException x) {
                sender.disconnect();
                sender.reset();
                i.remove();
                result = true;
            }
            catch (Exception x) {
                log.warn((Object)sm.getString("parallelNioSender.error.keepalive", sender), (Throwable)x);
            }
        }
        if (result) {
            try {
                this.selector.selectNow();
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return result;
    }

    private static class SendResult {
        private List<NioSender> completeSenders = new ArrayList<NioSender>();
        private ChannelException exception = null;

        private SendResult() {
        }

        private void complete(NioSender sender) {
            if (!this.completeSenders.contains(sender)) {
                this.completeSenders.add(sender);
            }
        }

        private int getCompleted() {
            return this.completeSenders.size();
        }

        private void failed(ChannelException cx) {
            if (this.exception == null) {
                this.exception = cx;
            }
            this.exception.addFaultyMember(cx.getFaultyMembers());
        }

        private ChannelException getFailed() {
            return this.exception;
        }
    }
}

