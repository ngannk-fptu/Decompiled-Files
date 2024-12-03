/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.tipis;

import java.io.Serializable;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.UniqueId;
import org.apache.catalina.tribes.tipis.AbstractReplicatedMap;
import org.apache.catalina.tribes.util.Arrays;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class LazyReplicatedMap<K, V>
extends AbstractReplicatedMap<K, V> {
    private static final long serialVersionUID = 1L;
    private volatile transient Log log;

    public LazyReplicatedMap(AbstractReplicatedMap.MapOwner owner, Channel channel, long timeout, String mapContextName, int initialCapacity, float loadFactor, ClassLoader[] cls) {
        super(owner, channel, timeout, mapContextName, initialCapacity, loadFactor, 2, cls, true);
    }

    public LazyReplicatedMap(AbstractReplicatedMap.MapOwner owner, Channel channel, long timeout, String mapContextName, int initialCapacity, ClassLoader[] cls) {
        super(owner, channel, timeout, mapContextName, initialCapacity, 0.75f, 2, cls, true);
    }

    public LazyReplicatedMap(AbstractReplicatedMap.MapOwner owner, Channel channel, long timeout, String mapContextName, ClassLoader[] cls) {
        super(owner, channel, timeout, mapContextName, 16, 0.75f, 2, cls, true);
    }

    public LazyReplicatedMap(AbstractReplicatedMap.MapOwner owner, Channel channel, long timeout, String mapContextName, ClassLoader[] cls, boolean terminate) {
        super(owner, channel, timeout, mapContextName, 16, 0.75f, 2, cls, terminate);
    }

    @Override
    protected int getStateMessageType() {
        return 5;
    }

    @Override
    protected int getReplicateMessageType() {
        return 1;
    }

    @Override
    protected Member[] publishEntryInfo(Object key, Object value) throws ChannelException {
        int firstIdx;
        Log log = this.getLog();
        if (!(key instanceof Serializable) || !(value instanceof Serializable)) {
            return new Member[0];
        }
        Member[] members = this.getMapMembers();
        int nextIdx = firstIdx = this.getNextBackupIndex();
        Member[] backup = new Member[]{};
        if (members.length == 0 || firstIdx == -1) {
            return backup;
        }
        boolean success = false;
        do {
            Member next = members[nextIdx];
            if (++nextIdx >= members.length) {
                nextIdx = 0;
            }
            if (next == null) continue;
            AbstractReplicatedMap.MapMessage msg = null;
            try {
                Member[] tmpBackup = this.wrap(next);
                msg = new AbstractReplicatedMap.MapMessage(this.getMapContextName(), 1, false, (Serializable)key, (Serializable)value, null, this.channel.getLocalMember(false), tmpBackup);
                if (log.isTraceEnabled()) {
                    log.trace((Object)("Publishing backup data:" + msg + " to: " + next.getName()));
                }
                UniqueId id = this.getChannel().send(tmpBackup, msg, this.getChannelSendOptions());
                if (log.isTraceEnabled()) {
                    log.trace((Object)("Data published:" + msg + " msg Id:" + id));
                }
                success = true;
                backup = tmpBackup;
            }
            catch (ChannelException x) {
                log.error((Object)sm.getString("lazyReplicatedMap.unableReplicate.backup", key, next, x.getMessage()), (Throwable)x);
                continue;
            }
            try {
                Member[] proxies = this.excludeFromSet(backup, this.getMapMembers());
                if (!success || proxies.length <= 0) continue;
                msg = new AbstractReplicatedMap.MapMessage(this.getMapContextName(), 3, false, (Serializable)key, null, null, this.channel.getLocalMember(false), backup);
                if (log.isTraceEnabled()) {
                    log.trace((Object)("Publishing proxy data:" + msg + " to: " + Arrays.toNameString(proxies)));
                }
                this.getChannel().send(proxies, msg, this.getChannelSendOptions());
            }
            catch (ChannelException x) {
                log.error((Object)sm.getString("lazyReplicatedMap.unableReplicate.proxy", key, next, x.getMessage()), (Throwable)x);
            }
        } while (!success && firstIdx != nextIdx);
        return backup;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Log getLog() {
        if (this.log == null) {
            LazyReplicatedMap lazyReplicatedMap = this;
            synchronized (lazyReplicatedMap) {
                if (this.log == null) {
                    this.log = LogFactory.getLog(LazyReplicatedMap.class);
                }
            }
        }
        return this.log;
    }
}

