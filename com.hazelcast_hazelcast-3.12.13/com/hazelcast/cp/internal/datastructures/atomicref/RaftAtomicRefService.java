/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.atomicref;

import com.hazelcast.core.IAtomicReference;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftNodeLifecycleAwareService;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.datastructures.atomicref.RaftAtomicRef;
import com.hazelcast.cp.internal.datastructures.atomicref.RaftAtomicRefSnapshot;
import com.hazelcast.cp.internal.datastructures.atomicref.proxy.RaftAtomicRefProxy;
import com.hazelcast.cp.internal.datastructures.spi.RaftManagedService;
import com.hazelcast.cp.internal.datastructures.spi.RaftRemoteService;
import com.hazelcast.cp.internal.raft.SnapshotAwareService;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.nio.serialization.Data;
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

public class RaftAtomicRefService
implements RaftManagedService,
RaftRemoteService,
RaftNodeLifecycleAwareService,
SnapshotAwareService<RaftAtomicRefSnapshot> {
    public static final String SERVICE_NAME = "hz:raft:atomicRefService";
    private final Map<Tuple2<CPGroupId, String>, RaftAtomicRef> atomicRefs = new ConcurrentHashMap<Tuple2<CPGroupId, String>, RaftAtomicRef>();
    private final Set<Tuple2<CPGroupId, String>> destroyedRefs = Collections.newSetFromMap(new ConcurrentHashMap());
    private final NodeEngine nodeEngine;
    private volatile RaftService raftService;

    public RaftAtomicRefService(NodeEngine nodeEngine) {
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
    }

    @Override
    public void onCPSubsystemRestart() {
        this.atomicRefs.clear();
        this.destroyedRefs.clear();
    }

    @Override
    public RaftAtomicRefSnapshot takeSnapshot(CPGroupId groupId, long commitIndex) {
        Preconditions.checkNotNull(groupId);
        HashMap<String, Data> refs = new HashMap<String, Data>();
        for (RaftAtomicRef ref : this.atomicRefs.values()) {
            if (!ref.groupId().equals(groupId)) continue;
            refs.put(ref.name(), ref.get());
        }
        HashSet<String> destroyed = new HashSet<String>();
        for (Tuple2<CPGroupId, String> tuple : this.destroyedRefs) {
            if (!groupId.equals(tuple.element1)) continue;
            destroyed.add((String)tuple.element2);
        }
        return new RaftAtomicRefSnapshot(refs, destroyed);
    }

    @Override
    public void restoreSnapshot(CPGroupId groupId, long commitIndex, RaftAtomicRefSnapshot snapshot) {
        Preconditions.checkNotNull(groupId);
        for (Map.Entry<String, Data> e : snapshot.getRefs()) {
            String name = e.getKey();
            Data val = e.getValue();
            this.atomicRefs.put(Tuple2.of(groupId, name), new RaftAtomicRef(groupId, name, val));
        }
        for (String name : snapshot.getDestroyed()) {
            this.destroyedRefs.add(Tuple2.of(groupId, name));
        }
    }

    @Override
    public void onRaftGroupDestroyed(CPGroupId groupId) {
        Iterator<Tuple2<CPGroupId, String>> iter = this.atomicRefs.keySet().iterator();
        while (iter.hasNext()) {
            Tuple2<CPGroupId, String> next = iter.next();
            if (!groupId.equals(next.element1)) continue;
            this.destroyedRefs.add(next);
            iter.remove();
        }
    }

    @Override
    public void onRaftNodeSteppedDown(CPGroupId groupId) {
    }

    @Override
    public boolean destroyRaftObject(CPGroupId groupId, String name) {
        Tuple2<CPGroupId, String> key = Tuple2.of(groupId, name);
        this.destroyedRefs.add(key);
        return this.atomicRefs.remove(key) != null;
    }

    public RaftAtomicRef getAtomicRef(CPGroupId groupId, String name) {
        Preconditions.checkNotNull(groupId);
        Preconditions.checkNotNull(name);
        Tuple2<CPGroupId, String> key = Tuple2.of(groupId, name);
        if (this.destroyedRefs.contains(key)) {
            throw new DistributedObjectDestroyedException("AtomicReference[" + name + "] is already destroyed!");
        }
        RaftAtomicRef atomicRef = this.atomicRefs.get(key);
        if (atomicRef == null) {
            atomicRef = new RaftAtomicRef(groupId, name);
            this.atomicRefs.put(key, atomicRef);
        }
        return atomicRef;
    }

    public IAtomicReference createProxy(String proxyName) {
        try {
            proxyName = RaftService.withoutDefaultGroupName(proxyName);
            RaftGroupId groupId = this.raftService.createRaftGroupForProxy(proxyName);
            return new RaftAtomicRefProxy(this.nodeEngine, groupId, proxyName, RaftService.getObjectNameForProxy(proxyName));
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }
}

