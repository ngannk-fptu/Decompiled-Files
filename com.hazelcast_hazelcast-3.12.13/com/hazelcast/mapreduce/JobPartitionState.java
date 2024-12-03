/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.mapreduce;

import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.BinaryInterface;

@Deprecated
@BinaryInterface
public interface JobPartitionState {
    public Address getOwner();

    public State getState();

    public static enum State {
        WAITING,
        MAPPING,
        REDUCING,
        PROCESSED,
        CANCELLED;


        public static State byOrdinal(int ordinal) {
            for (State state : State.values()) {
                if (state.ordinal() != ordinal) continue;
                return state;
            }
            return null;
        }
    }
}

