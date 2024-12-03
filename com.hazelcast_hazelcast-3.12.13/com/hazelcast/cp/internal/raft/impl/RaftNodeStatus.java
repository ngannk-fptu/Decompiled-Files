/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl;

public enum RaftNodeStatus {
    ACTIVE,
    UPDATING_GROUP_MEMBER_LIST,
    STEPPED_DOWN,
    TERMINATING,
    TERMINATED;

}

