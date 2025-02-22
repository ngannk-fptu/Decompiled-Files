/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.spi.blocking;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKey;
import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKeyContainer;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BlockingResource<W extends WaitKey>
implements DataSerializable {
    private CPGroupId groupId;
    private String name;
    private final Map<Object, WaitKeyContainer<W>> waitKeys = new LinkedHashMap<Object, WaitKeyContainer<W>>();

    protected BlockingResource() {
    }

    protected BlockingResource(CPGroupId groupId, String name) {
        this.groupId = groupId;
        this.name = name;
    }

    public final CPGroupId getGroupId() {
        return this.groupId;
    }

    public final String getName() {
        return this.name;
    }

    public final Map<Object, WaitKeyContainer<W>> getInternalWaitKeysMap() {
        return this.waitKeys;
    }

    protected abstract void onSessionClose(long var1, Map<Long, Object> var3);

    protected abstract Collection<Long> getActivelyAttachedSessions();

    protected final void addWaitKey(Object waitKeyId, W waitKey) {
        WaitKeyContainer<W> container = this.waitKeys.get(waitKeyId);
        if (container != null) {
            container.addRetry(waitKey);
        } else {
            this.waitKeys.put(waitKeyId, new WaitKeyContainer<W>(waitKey));
        }
    }

    protected final WaitKeyContainer<W> getWaitKeyContainer(Object waitKeyId) {
        return this.waitKeys.get(waitKeyId);
    }

    protected final void removeWaitKey(Object waitKeyId) {
        this.waitKeys.remove(waitKeyId);
    }

    protected final Collection<W> getAllWaitKeys() {
        ArrayList<W> all = new ArrayList<W>(this.waitKeys.size());
        for (WaitKeyContainer<W> container : this.waitKeys.values()) {
            all.addAll(container.keyAndRetries());
        }
        return all;
    }

    final void expireWaitKeys(UUID invocationUid, List<W> expired) {
        Iterator<WaitKeyContainer<W>> iter = this.waitKeys.values().iterator();
        while (iter.hasNext()) {
            WaitKeyContainer<W> container = iter.next();
            if (!container.invocationUid().equals(invocationUid)) continue;
            expired.addAll(container.keyAndRetries());
            iter.remove();
            this.onWaitKeyExpire(container.key());
            return;
        }
    }

    protected void onWaitKeyExpire(W waitKey) {
    }

    protected final Iterator<WaitKeyContainer<W>> waitKeyContainersIterator() {
        return this.waitKeys.values().iterator();
    }

    protected final void clearWaitKeys() {
        this.waitKeys.clear();
    }

    final void closeSession(long sessionId, List<Long> expiredWaitKeys, Map<Long, Object> result) {
        Iterator<WaitKeyContainer<W>> iter = this.waitKeys.values().iterator();
        while (iter.hasNext()) {
            WaitKeyContainer<W> container = iter.next();
            if (container.sessionId() != sessionId) continue;
            for (WaitKey retry : container.keyAndRetries()) {
                expiredWaitKeys.add(retry.commitIndex());
            }
            iter.remove();
        }
        this.onSessionClose(sessionId, result);
    }

    final void collectAttachedSessions(Collection<Long> sessions) {
        sessions.addAll(this.getActivelyAttachedSessions());
        for (WaitKeyContainer<W> key : this.waitKeys.values()) {
            sessions.add(key.sessionId());
        }
    }

    protected final void cloneForSnapshot(BlockingResource<W> clone) {
        clone.groupId = this.groupId;
        clone.name = this.name;
        clone.waitKeys.putAll(this.waitKeys);
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.groupId);
        out.writeUTF(this.name);
        out.writeInt(this.waitKeys.size());
        for (Map.Entry<Object, WaitKeyContainer<W>> e : this.waitKeys.entrySet()) {
            out.writeObject(e.getKey());
            out.writeObject(e.getValue());
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.groupId = (CPGroupId)in.readObject();
        this.name = in.readUTF();
        int count = in.readInt();
        for (int i = 0; i < count; ++i) {
            Object key = in.readObject();
            WaitKeyContainer container = (WaitKeyContainer)in.readObject();
            this.waitKeys.put(key, container);
        }
    }

    protected final String internalToString() {
        return "groupId=" + this.groupId + ", name='" + this.name + '\'' + ", waitKeys=" + this.waitKeys;
    }
}

