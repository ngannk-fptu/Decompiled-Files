/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.group.interceptors;

import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.ChannelMessage;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.UniqueId;
import org.apache.catalina.tribes.group.ChannelInterceptorBase;
import org.apache.catalina.tribes.group.InterceptorPayload;
import org.apache.catalina.tribes.util.Arrays;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.catalina.tribes.util.UUIDGenerator;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class TwoPhaseCommitInterceptor
extends ChannelInterceptorBase {
    private static final byte[] START_DATA = new byte[]{113, 1, -58, 2, -34, -60, 75, -78, -101, -12, 32, -29, 32, 111, -40, 4};
    private static final byte[] END_DATA = new byte[]{54, -13, 90, 110, 47, -31, 75, -24, -81, -29, 36, 52, -58, 77, -110, 56};
    private static final Log log = LogFactory.getLog(TwoPhaseCommitInterceptor.class);
    protected static final StringManager sm = StringManager.getManager(TwoPhaseCommitInterceptor.class);
    protected final HashMap<UniqueId, MapEntry> messages = new HashMap();
    protected long expire = 60000L;
    protected boolean deepclone = true;

    @Override
    public void sendMessage(Member[] destination, ChannelMessage msg, InterceptorPayload payload) throws ChannelException {
        if (this.okToProcess(msg.getOptions())) {
            super.sendMessage(destination, msg, null);
            ChannelMessage confirmation = null;
            confirmation = this.deepclone ? (ChannelMessage)msg.deepclone() : (ChannelMessage)msg.clone();
            confirmation.getMessage().reset();
            UUIDGenerator.randomUUID(false, confirmation.getUniqueId(), 0);
            confirmation.getMessage().append(START_DATA, 0, START_DATA.length);
            confirmation.getMessage().append(msg.getUniqueId(), 0, msg.getUniqueId().length);
            confirmation.getMessage().append(END_DATA, 0, END_DATA.length);
            super.sendMessage(destination, confirmation, payload);
        } else {
            super.sendMessage(destination, msg, payload);
        }
    }

    @Override
    public void messageReceived(ChannelMessage msg) {
        if (this.okToProcess(msg.getOptions())) {
            if (msg.getMessage().getLength() == START_DATA.length + msg.getUniqueId().length + END_DATA.length && Arrays.contains(msg.getMessage().getBytesDirect(), 0, START_DATA, 0, START_DATA.length) && Arrays.contains(msg.getMessage().getBytesDirect(), START_DATA.length + msg.getUniqueId().length, END_DATA, 0, END_DATA.length)) {
                UniqueId id = new UniqueId(msg.getMessage().getBytesDirect(), START_DATA.length, msg.getUniqueId().length);
                MapEntry original = this.messages.get(id);
                if (original != null) {
                    super.messageReceived(original.msg);
                    this.messages.remove(id);
                } else {
                    log.warn((Object)sm.getString("twoPhaseCommitInterceptor.originalMessage.missing", Arrays.toString(id.getBytes())));
                }
            } else {
                UniqueId id = new UniqueId(msg.getUniqueId());
                MapEntry entry = new MapEntry((ChannelMessage)msg.deepclone(), id, System.currentTimeMillis());
                this.messages.put(id, entry);
            }
        } else {
            super.messageReceived(msg);
        }
    }

    public boolean getDeepclone() {
        return this.deepclone;
    }

    public long getExpire() {
        return this.expire;
    }

    public void setDeepclone(boolean deepclone) {
        this.deepclone = deepclone;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void heartbeat() {
        try {
            Map.Entry[] entries;
            long now = System.currentTimeMillis();
            for (Map.Entry uniqueIdMapEntryEntry : entries = this.messages.entrySet().toArray(new Map.Entry[0])) {
                MapEntry entry = (MapEntry)uniqueIdMapEntryEntry.getValue();
                if (!entry.expired(now, this.expire)) continue;
                log.info((Object)sm.getString("twoPhaseCommitInterceptor.expiredMessage", entry.id));
                this.messages.remove(entry.id);
            }
        }
        catch (Exception x) {
            log.warn((Object)sm.getString("twoPhaseCommitInterceptor.heartbeat.failed"), (Throwable)x);
        }
        finally {
            super.heartbeat();
        }
    }

    public static class MapEntry {
        public final ChannelMessage msg;
        public final UniqueId id;
        public final long timestamp;

        public MapEntry(ChannelMessage msg, UniqueId id, long timestamp) {
            this.msg = msg;
            this.id = id;
            this.timestamp = timestamp;
        }

        public boolean expired(long now, long expiration) {
            return now - this.timestamp > expiration;
        }
    }
}

