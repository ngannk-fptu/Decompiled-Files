/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.CPMember;
import java.util.Collection;

public interface CPGroup {
    public static final String METADATA_CP_GROUP_NAME = "METADATA";
    public static final String DEFAULT_GROUP_NAME = "default";

    public CPGroupId id();

    public CPGroupStatus status();

    public Collection<CPMember> members();

    public static enum CPGroupStatus {
        ACTIVE,
        DESTROYING,
        DESTROYED;

    }
}

