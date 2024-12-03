/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.group.interceptors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.group.interceptors.FragmentationInterceptorMBean;
import org.apache.catalina.tribes.io.XByteBuffer;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class FragmentationInterceptor
extends ChannelInterceptorBase
implements FragmentationInterceptorMBean {
    private static final Log log = LogFactory.getLog(FragmentationInterceptor.class);
    protected static final StringManager sm = StringManager.getManager(FragmentationInterceptor.class);
    protected final HashMap<FragKey, FragCollection> fragpieces = new HashMap();
    private int maxSize = 102400;
    private long expire = 60000L;
    protected final boolean deepclone = true;

    @Override
    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        boolean frag;
        int size = msg.getMessage().getLength();
        boolean bl = frag = size > this.maxSize && this.okToProcess(msg.getOptions());
        if (frag) {
            this.frag(destination, msg, payload);
        } else {
            msg.getMessage().append(frag);
            super.sendMessage(destination, msg, payload);
        }
    }

    @Override
    public void messageReceived(ChannelMessage msg) {
        boolean isFrag = XByteBuffer.toBoolean(msg.getMessage().getBytesDirect(), msg.getMessage().getLength() - 1);
        msg.getMessage().trim(1);
        if (isFrag) {
            this.defrag(msg);
        } else {
            super.messageReceived(msg);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public FragCollection getFragCollection(FragKey key, ChannelMessage msg) {
        FragCollection coll = this.fragpieces.get(key);
        if (coll == null) {
            HashMap<FragKey, FragCollection> hashMap = this.fragpieces;
            synchronized (hashMap) {
                coll = this.fragpieces.get(key);
                if (coll == null) {
                    coll = new FragCollection(msg);
                    this.fragpieces.put(key, coll);
                }
            }
        }
        return coll;
    }

    public void removeFragCollection(FragKey key) {
        this.fragpieces.remove(key);
    }

    public void defrag(ChannelMessage msg) {
        FragKey key = new FragKey(msg.getUniqueId());
        FragCollection coll = this.getFragCollection(key, msg);
        coll.addMessage((ChannelMessage)msg.deepclone());
        if (coll.complete()) {
            this.removeFragCollection(key);
            ChannelMessage complete = coll.assemble();
            super.messageReceived(complete);
        }
    }

    public void frag(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        int size = msg.getMessage().getLength();
        int count = size / this.maxSize + (size % this.maxSize == 0 ? 0 : 1);
        ChannelMessage[] messages = new ChannelMessage[count];
        int remaining = size;
        for (int i = 0; i < count; ++i) {
            ChannelMessage tmp = (ChannelMessage)msg.clone();
            int offset = i * this.maxSize;
            int length = Math.min(remaining, this.maxSize);
            tmp.getMessage().clear();
            tmp.getMessage().append(msg.getMessage().getBytesDirect(), offset, length);
            tmp.getMessage().append(i);
            tmp.getMessage().append(count);
            tmp.getMessage().append(true);
            messages[i] = tmp;
            remaining -= length;
        }
        for (ChannelMessage message : messages) {
            super.sendMessage(destination, message, payload);
        }
    }

    @Override
    public void heartbeat() {
        block3: {
            try {
                Object[] keys;
                Set<FragKey> set = this.fragpieces.keySet();
                for (Object o : keys = set.toArray()) {
                    FragKey key = (FragKey)o;
                    if (key == null || !key.expired(this.getExpire())) continue;
                    this.removeFragCollection(key);
                }
            }
            catch (Exception x) {
                if (!log.isErrorEnabled()) break block3;
                log.error((Object)sm.getString("fragmentationInterceptor.heartbeat.failed"), (Throwable)x);
            }
        }
        super.heartbeat();
    }

    @Override
    public int getMaxSize() {
        return this.maxSize;
    }

    @Override
    public long getExpire() {
        return this.expire;
    }

    @Override
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public void setExpire(long expire) {
        this.expire = expire;
    }

    public static class FragCollection {
        private final long received = System.currentTimeMillis();
        private final ChannelMessage msg;
        private final XByteBuffer[] frags;

        public FragCollection(ChannelMessage msg) {
            int count = XByteBuffer.toInt(msg.getMessage().getBytesDirect(), msg.getMessage().getLength() - 4);
            this.frags = new XByteBuffer[count];
            this.msg = msg;
        }

        public void addMessage(ChannelMessage msg) {
            msg.getMessage().trim(4);
            int nr = XByteBuffer.toInt(msg.getMessage().getBytesDirect(), msg.getMessage().getLength() - 4);
            msg.getMessage().trim(4);
            this.frags[nr] = msg.getMessage();
        }

        public boolean complete() {
            boolean result = true;
            for (int i = 0; i < this.frags.length && result; ++i) {
                result = this.frags[i] != null;
            }
            return result;
        }

        public ChannelMessage assemble() {
            if (!this.complete()) {
                throw new IllegalStateException(sm.getString("fragmentationInterceptor.fragments.missing"));
            }
            int buffersize = 0;
            for (XByteBuffer frag : this.frags) {
                buffersize += frag.getLength();
            }
            XByteBuffer buf = new XByteBuffer(buffersize, false);
            this.msg.setMessage(buf);
            for (XByteBuffer frag : this.frags) {
                this.msg.getMessage().append(frag.getBytesDirect(), 0, frag.getLength());
            }
            return this.msg;
        }

        public boolean expired(long expire) {
            return System.currentTimeMillis() - this.received > expire;
        }
    }

    public static class FragKey {
        private final byte[] uniqueId;
        private final long received = System.currentTimeMillis();

        public FragKey(byte[] id) {
            this.uniqueId = id;
        }

        public int hashCode() {
            return XByteBuffer.toInt(this.uniqueId, 0);
        }

        public boolean equals(Object o) {
            if (o instanceof FragKey) {
                return Arrays.equals(this.uniqueId, ((FragKey)o).uniqueId);
            }
            return false;
        }

        public boolean expired(long expire) {
            return System.currentTimeMillis() - this.received > expire;
        }
    }
}

