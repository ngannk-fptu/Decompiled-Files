/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.group;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.ErrorHandler;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.UniqueId;
import org.apache.catalina.tribes.group.ExtendedRpcCallback;
import org.apache.catalina.tribes.group.Response;
import org.apache.catalina.tribes.group.RpcCallback;
import org.apache.catalina.tribes.group.RpcMessage;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.catalina.tribes.util.UUIDGenerator;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class RpcChannel
implements ChannelListener {
    private static final Log log = LogFactory.getLog(RpcChannel.class);
    protected static final StringManager sm = StringManager.getManager(RpcChannel.class);
    public static final int FIRST_REPLY = 1;
    public static final int MAJORITY_REPLY = 2;
    public static final int ALL_REPLY = 3;
    public static final int NO_REPLY = 4;
    private Channel channel;
    private RpcCallback callback;
    private byte[] rpcId;
    private int replyMessageOptions = 0;
    private final ConcurrentMap<RpcCollectorKey, RpcCollector> responseMap = new ConcurrentHashMap<RpcCollectorKey, RpcCollector>();

    public RpcChannel(byte[] rpcId, Channel channel, RpcCallback callback) {
        this.channel = channel;
        this.callback = callback;
        this.rpcId = rpcId;
        channel.addChannelListener(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Response[] send(Member[] destination, Serializable message, int rpcOptions, int channelOptions, long timeout) throws ChannelException {
        if (destination == null || destination.length == 0) {
            return new Response[0];
        }
        int sendOptions = channelOptions & 0xFFFFFFFB;
        RpcCollectorKey key = new RpcCollectorKey(UUIDGenerator.randomUUID(false));
        RpcCollector collector = new RpcCollector(key, rpcOptions, destination.length);
        try {
            RpcCollector rpcCollector = collector;
            synchronized (rpcCollector) {
                if (rpcOptions != 4) {
                    this.responseMap.put(key, collector);
                }
                RpcMessage rmsg = new RpcMessage(this.rpcId, key.id, message);
                this.channel.send(destination, rmsg, sendOptions);
                if (rpcOptions != 4) {
                    collector.wait(timeout);
                }
            }
        }
        catch (InterruptedException ix) {
            Thread.currentThread().interrupt();
        }
        finally {
            this.responseMap.remove(key);
        }
        return collector.getResponses();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void messageReceived(Serializable msg, Member sender) {
        RpcMessage rmsg = (RpcMessage)msg;
        RpcCollectorKey key = new RpcCollectorKey(rmsg.uuid);
        if (rmsg.reply) {
            RpcCollector collector = (RpcCollector)this.responseMap.get(key);
            if (collector == null) {
                if (!(rmsg instanceof RpcMessage.NoRpcChannelReply)) {
                    this.callback.leftOver(rmsg.message, sender);
                }
            } else {
                RpcCollector rpcCollector = collector;
                synchronized (rpcCollector) {
                    if (this.responseMap.containsKey(key)) {
                        if (rmsg instanceof RpcMessage.NoRpcChannelReply) {
                            --collector.destcnt;
                        } else {
                            collector.addResponse(rmsg.message, sender);
                        }
                        if (collector.isComplete()) {
                            collector.notifyAll();
                        }
                    } else if (!(rmsg instanceof RpcMessage.NoRpcChannelReply)) {
                        this.callback.leftOver(rmsg.message, sender);
                    }
                }
            }
        } else {
            boolean finished = false;
            final ExtendedRpcCallback excallback = this.callback instanceof ExtendedRpcCallback ? (ExtendedRpcCallback)this.callback : null;
            boolean asyncReply = (this.replyMessageOptions & 8) == 8;
            Serializable reply = this.callback.replyRequest(rmsg.message, sender);
            ErrorHandler handler = null;
            final Serializable request = msg;
            final Serializable response = reply;
            final Member fsender = sender;
            if (excallback != null && asyncReply) {
                handler = new ErrorHandler(){

                    @Override
                    public void handleError(ChannelException x, UniqueId id) {
                        excallback.replyFailed(request, response, fsender, x);
                    }

                    @Override
                    public void handleCompletion(UniqueId id) {
                        excallback.replySucceeded(request, response, fsender);
                    }
                };
            }
            rmsg.reply = true;
            rmsg.message = reply;
            try {
                if (handler != null) {
                    this.channel.send(new Member[]{sender}, rmsg, this.replyMessageOptions & 0xFFFFFFFB, handler);
                } else {
                    this.channel.send(new Member[]{sender}, rmsg, this.replyMessageOptions & 0xFFFFFFFB);
                }
                finished = true;
            }
            catch (Exception x) {
                if (excallback != null && !asyncReply) {
                    excallback.replyFailed(rmsg.message, reply, sender, x);
                }
                log.error((Object)sm.getString("rpcChannel.replyFailed"), (Throwable)x);
            }
            if (finished && excallback != null && !asyncReply) {
                excallback.replySucceeded(rmsg.message, reply, sender);
            }
        }
    }

    public void breakdown() {
        this.channel.removeChannelListener(this);
    }

    @Override
    public boolean accept(Serializable msg, Member sender) {
        if (msg instanceof RpcMessage) {
            RpcMessage rmsg = (RpcMessage)msg;
            return Arrays.equals(rmsg.rpcId, this.rpcId);
        }
        return false;
    }

    public Channel getChannel() {
        return this.channel;
    }

    public RpcCallback getCallback() {
        return this.callback;
    }

    public byte[] getRpcId() {
        return this.rpcId;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public void setCallback(RpcCallback callback) {
        this.callback = callback;
    }

    public void setRpcId(byte[] rpcId) {
        this.rpcId = rpcId;
    }

    public int getReplyMessageOptions() {
        return this.replyMessageOptions;
    }

    public void setReplyMessageOptions(int replyMessageOptions) {
        this.replyMessageOptions = replyMessageOptions;
    }

    public static class RpcCollectorKey {
        final byte[] id;

        public RpcCollectorKey(byte[] id) {
            this.id = id;
        }

        public int hashCode() {
            return this.id[0] + this.id[1] + this.id[2] + this.id[3];
        }

        public boolean equals(Object o) {
            if (o instanceof RpcCollectorKey) {
                RpcCollectorKey r = (RpcCollectorKey)o;
                return Arrays.equals(this.id, r.id);
            }
            return false;
        }
    }

    public static class RpcCollector {
        public final ArrayList<Response> responses = new ArrayList();
        public final RpcCollectorKey key;
        public final int options;
        public int destcnt;

        public RpcCollector(RpcCollectorKey key, int options, int destcnt) {
            this.key = key;
            this.options = options;
            this.destcnt = destcnt;
        }

        public void addResponse(Serializable message, Member sender) {
            Response resp = new Response(sender, message);
            this.responses.add(resp);
        }

        public boolean isComplete() {
            if (this.destcnt <= 0) {
                return true;
            }
            switch (this.options) {
                case 3: {
                    return this.destcnt == this.responses.size();
                }
                case 2: {
                    float perc = (float)this.responses.size() / (float)this.destcnt;
                    return perc >= 0.5f;
                }
                case 1: {
                    return this.responses.size() > 0;
                }
            }
            return false;
        }

        public int hashCode() {
            return this.key.hashCode();
        }

        public boolean equals(Object o) {
            if (o instanceof RpcCollector) {
                RpcCollector r = (RpcCollector)o;
                return r.key.equals(this.key);
            }
            return false;
        }

        public Response[] getResponses() {
            return this.responses.toArray(new Response[0]);
        }
    }
}

