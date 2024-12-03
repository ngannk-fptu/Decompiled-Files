/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util.hashslot.impl;

import com.hazelcast.internal.util.hashslot.SlotAssignmentResult;

public class SlotAssignmentResultImpl
implements SlotAssignmentResult {
    private long address;
    private boolean isNew;

    @Override
    public long address() {
        return this.address;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void setAddress(long address) {
        this.address = address;
    }
}

