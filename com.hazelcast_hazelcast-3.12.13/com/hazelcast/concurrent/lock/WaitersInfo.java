/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.concurrent.lock;

import com.hazelcast.concurrent.lock.LockDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.SetUtil;
import java.io.IOException;
import java.util.Set;

final class WaitersInfo
implements IdentifiedDataSerializable {
    private static final int INITIAL_WAITER_SIZE = 2;
    private String conditionId;
    private Set<ConditionWaiter> waiters = SetUtil.createHashSet(2);

    public WaitersInfo() {
    }

    public WaitersInfo(String conditionId) {
        this.conditionId = conditionId;
    }

    public void addWaiter(String caller, long threadId) {
        ConditionWaiter waiter = new ConditionWaiter(caller, threadId);
        this.waiters.add(waiter);
    }

    public void removeWaiter(String caller, long threadId) {
        ConditionWaiter waiter = new ConditionWaiter(caller, threadId);
        this.waiters.remove(waiter);
    }

    public String getConditionId() {
        return this.conditionId;
    }

    public boolean hasWaiter() {
        return !this.waiters.isEmpty();
    }

    public Set<ConditionWaiter> getWaiters() {
        return this.waiters;
    }

    @Override
    public int getFactoryId() {
        return LockDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.conditionId);
        int len = this.waiters.size();
        out.writeInt(len);
        if (len > 0) {
            for (ConditionWaiter w : this.waiters) {
                out.writeUTF(w.caller);
                out.writeLong(w.threadId);
            }
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.conditionId = in.readUTF();
        int len = in.readInt();
        if (len > 0) {
            for (int i = 0; i < len; ++i) {
                ConditionWaiter waiter = new ConditionWaiter(in.readUTF(), in.readLong());
                this.waiters.add(waiter);
            }
        }
    }

    public static class ConditionWaiter {
        private final String caller;
        private final long threadId;

        ConditionWaiter(String caller, long threadId) {
            this.caller = caller;
            this.threadId = threadId;
        }

        public long getThreadId() {
            return this.threadId;
        }

        public String getCaller() {
            return this.caller;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ConditionWaiter that = (ConditionWaiter)o;
            if (this.threadId != that.threadId) {
                return false;
            }
            return !(this.caller != null ? !this.caller.equals(that.caller) : that.caller != null);
        }

        public int hashCode() {
            int result = this.caller != null ? this.caller.hashCode() : 0;
            result = 31 * result + (int)(this.threadId ^ this.threadId >>> 32);
            return result;
        }
    }
}

