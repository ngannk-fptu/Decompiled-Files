/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.permission;

import com.google.common.collect.ImmutableMap;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;

public enum UserPermission {
    ADMIN(1),
    SYS_ADMIN(2);

    private static final ImmutableMap<Integer, UserPermission> ID_TO_PERMISSION_MAP;
    private final int id;

    private UserPermission(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static Set<UserPermission> allPermissions() {
        return EnumSet.allOf(UserPermission.class);
    }

    @Nullable
    public static UserPermission fromId(int id) {
        return (UserPermission)((Object)ID_TO_PERMISSION_MAP.get((Object)id));
    }

    static {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        for (UserPermission permission : UserPermission.values()) {
            builder.put((Object)permission.getId(), (Object)permission);
        }
        ID_TO_PERMISSION_MAP = builder.build();
    }
}

