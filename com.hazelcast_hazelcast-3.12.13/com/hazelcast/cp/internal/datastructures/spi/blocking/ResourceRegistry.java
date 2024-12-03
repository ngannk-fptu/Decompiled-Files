/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.spi.blocking;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.spi.blocking.BlockingResource;
import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKey;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.cp.internal.util.UUIDSerializationUtil;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.spi.LiveOperations;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.util.Clock;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public abstract class ResourceRegistry<W extends WaitKey, R extends BlockingResource<W>>
implements DataSerializable {
    private static final long OPERATION_TIMEOUT_EXTENSION_MS = TimeUnit.SECONDS.toMillis(5L);
    private static final long NO_WAIT_KEY_DEADLINE = Long.MAX_VALUE;
    protected CPGroupId groupId;
    protected final Map<String, R> resources = new ConcurrentHashMap<String, R>();
    protected final Set<String> destroyedNames = new HashSet<String>();
    protected final ConcurrentMap<Tuple2<String, UUID>, Tuple2<Long, Long>> waitTimeouts = new ConcurrentHashMap<Tuple2<String, UUID>, Tuple2<Long, Long>>();
    private final Map<Tuple2<Address, Long>, Long> liveOperationMap = new ConcurrentHashMap<Tuple2<Address, Long>, Long>();

    protected ResourceRegistry() {
    }

    protected ResourceRegistry(CPGroupId groupId) {
        this.groupId = groupId;
    }

    protected abstract R createNewResource(CPGroupId var1, String var2);

    protected abstract ResourceRegistry<W, R> cloneForSnapshot();

    public final R getResourceOrNull(String name) {
        this.checkNotDestroyed(name);
        return (R)((BlockingResource)this.resources.get(name));
    }

    protected final R getOrInitResource(String name) {
        this.checkNotDestroyed(name);
        BlockingResource<Object> resource = (BlockingResource)this.resources.get(name);
        if (resource == null) {
            resource = this.createNewResource(this.groupId, name);
            this.resources.put(name, resource);
        }
        return (R)resource;
    }

    private void checkNotDestroyed(String name) {
        Preconditions.checkNotNull(name);
        if (this.destroyedNames.contains(name)) {
            throw new DistributedObjectDestroyedException("Resource[" + name + "] is already destroyed!");
        }
    }

    protected final void addWaitKey(String name, W key, long timeoutMs) {
        long deadline;
        if (timeoutMs > 0L) {
            long now = Clock.currentTimeMillis();
            deadline = Long.MAX_VALUE - now >= timeoutMs ? now + timeoutMs : Long.MAX_VALUE;
            this.waitTimeouts.putIfAbsent(Tuple2.of(name, ((WaitKey)key).invocationUid), Tuple2.of(timeoutMs, deadline));
        } else {
            deadline = Long.MAX_VALUE;
        }
        if (timeoutMs != 0L) {
            this.addLiveOperation(key, deadline);
        }
    }

    protected final void removeWaitKey(String name, W key) {
        this.waitTimeouts.remove(Tuple2.of(name, ((WaitKey)key).invocationUid()));
        this.removeLiveOperation(key);
    }

    final void expireWaitKey(String name, UUID invocationUid, List<W> expired) {
        this.waitTimeouts.remove(Tuple2.of(name, invocationUid));
        R resource = this.getResourceOrNull(name);
        if (resource != null) {
            ((BlockingResource)resource).expireWaitKeys(invocationUid, expired);
        }
    }

    final Collection<Tuple2<String, UUID>> getWaitKeysToExpire(long now) {
        ArrayList<Tuple2<String, UUID>> expired = new ArrayList<Tuple2<String, UUID>>();
        for (Map.Entry e : this.waitTimeouts.entrySet()) {
            long deadline = (Long)((Tuple2)e.getValue()).element2;
            if (deadline > now) continue;
            expired.add((Tuple2<String, UUID>)e.getKey());
        }
        return expired;
    }

    final Map<Tuple2<String, UUID>, Long> overwriteWaitTimeouts(Map<Tuple2<String, UUID>, Tuple2<Long, Long>> existingWaitTimeouts) {
        for (Map.Entry<Tuple2<String, UUID>, Tuple2<Long, Long>> e : existingWaitTimeouts.entrySet()) {
            this.waitTimeouts.put(e.getKey(), e.getValue());
        }
        HashMap<Tuple2<String, UUID>, Long> newKeys = new HashMap<Tuple2<String, UUID>, Long>();
        for (Map.Entry e : this.waitTimeouts.entrySet()) {
            Tuple2 key = (Tuple2)e.getKey();
            if (existingWaitTimeouts.containsKey(key)) continue;
            Long timeout = (Long)((Tuple2)e.getValue()).element1;
            newKeys.put(key, timeout);
        }
        return newKeys;
    }

    final void closeSession(long sessionId, List<Long> expiredWaitKeys, Map<Long, Object> result) {
        for (BlockingResource resource : this.resources.values()) {
            resource.closeSession(sessionId, expiredWaitKeys, result);
        }
    }

    final Collection<Long> getAttachedSessions() {
        HashSet<Long> sessions = new HashSet<Long>();
        for (BlockingResource res : this.resources.values()) {
            res.collectAttachedSessions(sessions);
        }
        return sessions;
    }

    final Collection<W> destroyResource(String name) {
        this.destroyedNames.add(name);
        BlockingResource resource = (BlockingResource)this.resources.remove(name);
        if (resource == null) {
            return null;
        }
        Collection keys = resource.getAllWaitKeys();
        for (WaitKey key : keys) {
            this.removeWaitKey(name, key);
        }
        return keys;
    }

    public final CPGroupId getGroupId() {
        return this.groupId;
    }

    public final Map<Tuple2<String, UUID>, Tuple2<Long, Long>> getWaitTimeouts() {
        return Collections.unmodifiableMap(this.waitTimeouts);
    }

    public final Collection<Long> destroy() {
        this.destroyedNames.addAll(this.resources.keySet());
        ArrayList<Long> indices = new ArrayList<Long>();
        for (BlockingResource raftLock : this.resources.values()) {
            for (WaitKey key : raftLock.getAllWaitKeys()) {
                indices.add(key.commitIndex());
            }
        }
        this.resources.clear();
        this.waitTimeouts.clear();
        return indices;
    }

    public void populate(LiveOperations liveOperations, long now) {
        Iterator<Map.Entry<Tuple2<Address, Long>, Long>> it = this.liveOperationMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Tuple2<Address, Long>, Long> e = it.next();
            long deadline = e.getValue();
            if (deadline >= now) {
                Tuple2<Address, Long> t = e.getKey();
                liveOperations.add((Address)t.element1, (Long)t.element2);
                continue;
            }
            it.remove();
        }
    }

    private void addLiveOperation(W key, long deadline) {
        if (Long.MAX_VALUE - deadline >= OPERATION_TIMEOUT_EXTENSION_MS) {
            deadline += OPERATION_TIMEOUT_EXTENSION_MS;
        }
        this.liveOperationMap.put(Tuple2.of(((WaitKey)key).callerAddress(), ((WaitKey)key).callId()), deadline);
    }

    final void removeLiveOperation(W key) {
        this.liveOperationMap.remove(Tuple2.of(((WaitKey)key).callerAddress(), ((WaitKey)key).callId()));
    }

    public final Collection<Tuple2<Address, Long>> getLiveOperations() {
        return this.liveOperationMap.keySet();
    }

    final void onSnapshotRestore() {
        for (BlockingResource resource : this.resources.values()) {
            for (WaitKey key : resource.getAllWaitKeys()) {
                Tuple2 t = (Tuple2)this.waitTimeouts.get(Tuple2.of(resource.getName(), key.invocationUid));
                long deadline = t != null ? (Long)t.element1 : Long.MAX_VALUE;
                this.addLiveOperation(key, deadline);
            }
        }
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.groupId);
        out.writeInt(this.resources.size());
        for (Map.Entry<String, R> entry : this.resources.entrySet()) {
            out.writeUTF(entry.getKey());
            out.writeObject(entry.getValue());
        }
        out.writeInt(this.destroyedNames.size());
        for (String string : this.destroyedNames) {
            out.writeUTF(string);
        }
        out.writeInt(this.waitTimeouts.size());
        for (Map.Entry entry : this.waitTimeouts.entrySet()) {
            Tuple2 t = (Tuple2)entry.getKey();
            out.writeUTF((String)t.element1);
            UUIDSerializationUtil.writeUUID(out, (UUID)t.element2);
            out.writeLong((Long)((Tuple2)entry.getValue()).element1);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        String name;
        int i;
        this.groupId = (CPGroupId)in.readObject();
        int count = in.readInt();
        for (i = 0; i < count; ++i) {
            name = in.readUTF();
            BlockingResource res = (BlockingResource)in.readObject();
            this.resources.put(name, res);
        }
        count = in.readInt();
        for (i = 0; i < count; ++i) {
            name = in.readUTF();
            this.destroyedNames.add(name);
        }
        long now = Clock.currentTimeMillis();
        count = in.readInt();
        for (int i2 = 0; i2 < count; ++i2) {
            String name2 = in.readUTF();
            UUID invocationUid = UUIDSerializationUtil.readUUID(in);
            long timeout = in.readLong();
            this.waitTimeouts.put(Tuple2.of(name2, invocationUid), Tuple2.of(timeout, now + timeout));
        }
    }

    public String toString() {
        return "ResourceRegistry{groupId=" + this.groupId + ", resources=" + this.resources + ", destroyedNames=" + this.destroyedNames + ", waitTimeouts=" + this.waitTimeouts + '}';
    }
}

