/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.tipis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.ChannelException;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.RemoteProcessException;
import org.apache.catalina.tribes.tipis.AbstractReplicatedMap;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class ReplicatedMap<K, V>
extends AbstractReplicatedMap<K, V> {
    private static final long serialVersionUID = 1L;
    private volatile transient Log log;

    public ReplicatedMap(AbstractReplicatedMap.MapOwner owner, Channel channel, long timeout, String mapContextName, int initialCapacity, float loadFactor, ClassLoader[] cls) {
        super(owner, channel, timeout, mapContextName, initialCapacity, loadFactor, 2, cls, true);
    }

    public ReplicatedMap(AbstractReplicatedMap.MapOwner owner, Channel channel, long timeout, String mapContextName, int initialCapacity, ClassLoader[] cls) {
        super(owner, channel, timeout, mapContextName, initialCapacity, 0.75f, 2, cls, true);
    }

    public ReplicatedMap(AbstractReplicatedMap.MapOwner owner, Channel channel, long timeout, String mapContextName, ClassLoader[] cls) {
        super(owner, channel, timeout, mapContextName, 16, 0.75f, 2, cls, true);
    }

    public ReplicatedMap(AbstractReplicatedMap.MapOwner owner, Channel channel, long timeout, String mapContextName, ClassLoader[] cls, boolean terminate) {
        super(owner, channel, timeout, mapContextName, 16, 0.75f, 2, cls, terminate);
    }

    @Override
    protected int getStateMessageType() {
        return 10;
    }

    @Override
    protected int getReplicateMessageType() {
        return 9;
    }

    @Override
    protected Member[] publishEntryInfo(Object key, Object value) throws ChannelException {
        Object[] backup;
        block7: {
            if (!(key instanceof Serializable) || !(value instanceof Serializable)) {
                return new Member[0];
            }
            backup = this.getMapMembers();
            if (backup == null || backup.length == 0) {
                return null;
            }
            try {
                AbstractReplicatedMap.MapMessage msg = new AbstractReplicatedMap.MapMessage(this.getMapContextName(), 9, false, (Serializable)key, (Serializable)value, null, this.channel.getLocalMember(false), (Member[])backup);
                this.getChannel().send((Member[])backup, msg, this.getChannelSendOptions());
            }
            catch (ChannelException e) {
                ChannelException.FaultyMember[] faultyMembers = e.getFaultyMembers();
                if (faultyMembers.length == 0) {
                    throw e;
                }
                ArrayList<Member> faulty = new ArrayList<Member>();
                for (ChannelException.FaultyMember faultyMember : faultyMembers) {
                    if (faultyMember.getCause() instanceof RemoteProcessException) continue;
                    faulty.add(faultyMember.getMember());
                }
                Object[] realFaultyMembers = faulty.toArray(new Member[0]);
                if (realFaultyMembers.length == 0) break block7;
                if ((backup = this.excludeFromSet((Member[])realFaultyMembers, (Member[])backup)).length == 0) {
                    throw e;
                }
                if (!this.getLog().isWarnEnabled()) break block7;
                this.getLog().warn((Object)sm.getString("replicatedMap.unableReplicate.completely", key, Arrays.toString(backup), Arrays.toString(realFaultyMembers)), (Throwable)e);
            }
        }
        return backup;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void memberDisappeared(Member member) {
        boolean removed = false;
        Log log = this.getLog();
        HashMap hashMap = this.mapMembers;
        synchronized (hashMap) {
            boolean bl = removed = this.mapMembers.remove(member) != null;
            if (!removed) {
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Member[" + member + "] disappeared, but was not present in the map."));
                }
                return;
            }
        }
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("replicatedMap.member.disappeared", member));
        }
        long start = System.currentTimeMillis();
        for (Map.Entry e : this.innerMap.entrySet()) {
            AbstractReplicatedMap.MapMessage msg;
            Member[] backup;
            AbstractReplicatedMap.MapEntry entry = (AbstractReplicatedMap.MapEntry)this.innerMap.get(e.getKey());
            if (entry == null) continue;
            if (entry.isPrimary()) {
                try {
                    backup = this.getMapMembers();
                    if (backup.length > 0) {
                        msg = new AbstractReplicatedMap.MapMessage(this.getMapContextName(), 12, false, (Serializable)entry.getKey(), null, null, this.channel.getLocalMember(false), backup);
                        this.getChannel().send(backup, msg, this.getChannelSendOptions());
                    }
                    entry.setBackupNodes(backup);
                    entry.setPrimary(this.channel.getLocalMember(false));
                }
                catch (ChannelException x) {
                    log.error((Object)sm.getString("replicatedMap.unable.relocate", entry.getKey()), (Throwable)x);
                }
            } else if (member.equals(entry.getPrimary())) {
                entry.setPrimary(null);
            }
            if (entry.getPrimary() != null || !entry.isCopy() || entry.getBackupNodes() == null || entry.getBackupNodes().length <= 0 || !entry.getBackupNodes()[0].equals(this.channel.getLocalMember(false))) continue;
            try {
                entry.setPrimary(this.channel.getLocalMember(false));
                entry.setBackup(false);
                entry.setProxy(false);
                entry.setCopy(false);
                backup = this.getMapMembers();
                if (backup.length > 0) {
                    msg = new AbstractReplicatedMap.MapMessage(this.getMapContextName(), 12, false, (Serializable)entry.getKey(), null, null, this.channel.getLocalMember(false), backup);
                    this.getChannel().send(backup, msg, this.getChannelSendOptions());
                }
                entry.setBackupNodes(backup);
                if (this.mapOwner == null) continue;
                this.mapOwner.objectMadePrimary(entry.getKey(), entry.getValue());
            }
            catch (ChannelException x) {
                log.error((Object)sm.getString("replicatedMap.unable.relocate", entry.getKey()), (Throwable)x);
            }
        }
        long complete = System.currentTimeMillis() - start;
        if (log.isInfoEnabled()) {
            log.info((Object)sm.getString("replicatedMap.relocate.complete", Long.toString(complete)));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void mapMemberAdded(Member member) {
        if (member.equals(this.getChannel().getLocalMember(false))) {
            return;
        }
        boolean memberAdded = false;
        Object object = this.mapMembers;
        synchronized (object) {
            if (!this.mapMembers.containsKey(member)) {
                this.mapMembers.put(member, System.currentTimeMillis());
                memberAdded = true;
            }
        }
        if (memberAdded) {
            object = this.stateMutex;
            synchronized (object) {
                Member[] backup = this.getMapMembers();
                for (Map.Entry e : this.innerMap.entrySet()) {
                    AbstractReplicatedMap.MapEntry entry = (AbstractReplicatedMap.MapEntry)this.innerMap.get(e.getKey());
                    if (entry == null || !entry.isPrimary() || this.inSet(member, entry.getBackupNodes())) continue;
                    entry.setBackupNodes(backup);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Log getLog() {
        if (this.log == null) {
            ReplicatedMap replicatedMap = this;
            synchronized (replicatedMap) {
                if (this.log == null) {
                    this.log = LogFactory.getLog(ReplicatedMap.class);
                }
            }
        }
        return this.log;
    }
}

