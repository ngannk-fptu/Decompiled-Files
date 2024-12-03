/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomiclong;

import com.hazelcast.core.IAtomicLong;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftNodeLifecycleAwareService;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLong;
import com.hazelcast.cp.internal.datastructures.atomiclong.RaftAtomicLongSnapshot;
import com.hazelcast.cp.internal.datastructures.atomiclong.proxy.RaftAtomicLongProxy;
import com.hazelcast.cp.internal.datastructures.spi.RaftManagedService;
import com.hazelcast.cp.internal.datastructures.spi.RaftRemoteService;
import com.hazelcast.cp.internal.raft.SnapshotAwareService;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RaftAtomicLongService
implements RaftManagedService,
RaftRemoteService,
RaftNodeLifecycleAwareService,
SnapshotAwareService<RaftAtomicLongSnapshot> {
    public static final String SERVICE_NAME = "hz:raft:atomicLongService";
    private final Map<Tuple2<CPGroupId, String>, RaftAtomicLong> atomicLongs = new ConcurrentHashMap<Tuple2<CPGroupId, String>, RaftAtomicLong>();
    private final Set<Tuple2<CPGroupId, String>> destroyedLongs = Collections.newSetFromMap(new ConcurrentHashMap());
    private final NodeEngine nodeEngine;
    private volatile RaftService raftService;

    public RaftAtomicLongService(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
    }

    @Override
    public void init(NodeEngine nodeEngine, Properties properties) {
        this.raftService = (RaftService)nodeEngine.getService("hz:core:raft");
    }

    @Override
    public void reset() {
    }

    @Override
    public void shutdown(boolean terminate) {
        this.atomicLongs.clear();
    }

    @Override
    public void onCPSubsystemRestart() {
        this.atomicLongs.clear();
        this.destroyedLongs.clear();
    }

    @Override
    public RaftAtomicLongSnapshot takeSnapshot(CPGroupId groupId, long commitIndex) {
        Preconditions.checkNotNull(groupId);
        HashMap<String, Long> longs = new HashMap<String, Long>();
        for (RaftAtomicLong atomicLong : this.atomicLongs.values()) {
            if (!atomicLong.groupId().equals(groupId)) continue;
            longs.put(atomicLong.name(), atomicLong.value());
        }
        HashSet<String> destroyed = new HashSet<String>();
        for (Tuple2<CPGroupId, String> tuple : this.destroyedLongs) {
            if (!groupId.equals(tuple.element1)) continue;
            destroyed.add((String)tuple.element2);
        }
        return new RaftAtomicLongSnapshot(longs, destroyed);
    }

    @Override
    public void restoreSnapshot(CPGroupId groupId, long commitIndex, RaftAtomicLongSnapshot snapshot) {
        Preconditions.checkNotNull(groupId);
        for (Map.Entry<String, Long> e : snapshot.getLongs()) {
            String name = e.getKey();
            long val = e.getValue();
            this.atomicLongs.put(Tuple2.of(groupId, name), new RaftAtomicLong(groupId, name, val));
        }
        for (String name : snapshot.getDestroyed()) {
            this.destroyedLongs.add(Tuple2.of(groupId, name));
        }
    }

    @Override
    public boolean destroyRaftObject(CPGroupId groupId, String name) {
        Tuple2<CPGroupId, String> key = Tuple2.of(groupId, name);
        this.destroyedLongs.add(key);
        return this.atomicLongs.remove(key) != null;
    }

    @Override
    public void onRaftGroupDestroyed(CPGroupId groupId) {
        Iterator<Tuple2<CPGroupId, String>> iter = this.atomicLongs.keySet().iterator();
        while (iter.hasNext()) {
            Tuple2<CPGroupId, String> next = iter.next();
            if (!groupId.equals(next.element1)) continue;
            this.destroyedLongs.add(next);
            iter.remove();
        }
    }

    @Override
    public void onRaftNodeSteppedDown(CPGroupId groupId) {
    }

    public RaftAtomicLong getAtomicLong(CPGroupId groupId, String name) {
        Preconditions.checkNotNull(groupId);
        Preconditions.checkNotNull(name);
        Tuple2<CPGroupId, String> key = Tuple2.of(groupId, name);
        if (this.destroyedLongs.contains(key)) {
            throw new DistributedObjectDestroyedException("AtomicLong[" + name + "] is already destroyed!");
        }
        RaftAtomicLong atomicLong = this.atomicLongs.get(key);
        if (atomicLong == null) {
            atomicLong = new RaftAtomicLong(groupId, name);
            this.atomicLongs.put(key, atomicLong);
        }
        return atomicLong;
    }

    public IAtomicLong createProxy(String proxyName) {
        try {
            proxyName = RaftService.withoutDefaultGroupName(proxyName);
            RaftGroupId groupId = this.raftService.createRaftGroupForProxy(proxyName);
            return new RaftAtomicLongProxy(this.nodeEngine, groupId, proxyName, RaftService.getObjectNameForProxy(proxyName));
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }
}

