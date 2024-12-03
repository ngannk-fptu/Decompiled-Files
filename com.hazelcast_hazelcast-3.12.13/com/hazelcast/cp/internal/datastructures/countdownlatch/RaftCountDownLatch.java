/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.datastructures.countdownlatch;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.datastructures.countdownlatch.AwaitInvocationKey;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatchDataSerializerHook;
import com.hazelcast.cp.internal.datastructures.spi.blocking.BlockingResource;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.cp.internal.util.UUIDSerializationUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RaftCountDownLatch
extends BlockingResource<AwaitInvocationKey>
implements IdentifiedDataSerializable {
    private int round;
    private int countDownFrom;
    private final Set<UUID> countDownUids = new HashSet<UUID>();

    RaftCountDownLatch() {
    }

    RaftCountDownLatch(CPGroupId groupId, String name) {
        super(groupId, name);
    }

    Tuple2<Integer, Collection<AwaitInvocationKey>> countDown(UUID invocationUuid, int expectedRound) {
        if (expectedRound > this.round) {
            throw new IllegalArgumentException("expected round: " + expectedRound + ", actual round: " + this.round);
        }
        if (expectedRound < this.round) {
            List c = Collections.emptyList();
            return Tuple2.of(0, c);
        }
        this.countDownUids.add(invocationUuid);
        int remaining = this.getRemainingCount();
        if (remaining > 0) {
            List c = Collections.emptyList();
            return Tuple2.of(remaining, c);
        }
        Collection w = this.getAllWaitKeys();
        this.clearWaitKeys();
        return Tuple2.of(0, w);
    }

    boolean trySetCount(int count) {
        if (this.getRemainingCount() > 0) {
            return false;
        }
        Preconditions.checkTrue(count > 0, "cannot set non-positive count: " + count);
        this.countDownFrom = count;
        ++this.round;
        this.countDownUids.clear();
        return true;
    }

    boolean await(AwaitInvocationKey key, boolean wait) {
        boolean success;
        boolean bl = success = this.getRemainingCount() == 0;
        if (!success && wait) {
            this.addWaitKey(key.invocationUid(), key);
        }
        return success;
    }

    int getRound() {
        return this.round;
    }

    int getRemainingCount() {
        return Math.max(0, this.countDownFrom - this.countDownUids.size());
    }

    RaftCountDownLatch cloneForSnapshot() {
        RaftCountDownLatch clone = new RaftCountDownLatch();
        this.cloneForSnapshot(clone);
        clone.round = this.round;
        clone.countDownFrom = this.countDownFrom;
        clone.countDownUids.addAll(this.countDownUids);
        return clone;
    }

    @Override
    protected void onSessionClose(long sessionId, Map<Long, Object> responses) {
    }

    @Override
    protected Collection<Long> getActivelyAttachedSessions() {
        return Collections.emptyList();
    }

    @Override
    public int getFactoryId() {
        return RaftCountDownLatchDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);
        out.writeInt(this.round);
        out.writeInt(this.countDownFrom);
        out.writeInt(this.countDownUids.size());
        for (UUID uid : this.countDownUids) {
            UUIDSerializationUtil.writeUUID(out, uid);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);
        this.round = in.readInt();
        this.countDownFrom = in.readInt();
        int count = in.readInt();
        for (int i = 0; i < count; ++i) {
            this.countDownUids.add(UUIDSerializationUtil.readUUID(in));
        }
    }

    public String toString() {
        return "RaftCountDownLatch{" + this.internalToString() + ", round=" + this.round + ", countDownFrom=" + this.countDownFrom + ", countDownUids=" + this.countDownUids + '}';
    }
}

