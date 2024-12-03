/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.embedded.api;

public enum OperationType {
    CREATE_USER,
    CREATE_GROUP,
    CREATE_ROLE,
    UPDATE_USER,
    UPDATE_GROUP,
    UPDATE_ROLE,
    UPDATE_USER_ATTRIBUTE,
    UPDATE_GROUP_ATTRIBUTE,
    UPDATE_ROLE_ATTRIBUTE,
    DELETE_USER,
    DELETE_GROUP,
    DELETE_ROLE;


    public String getName() {
        return this.name();
    }

    public OperationType fromName(String name) {
        return OperationType.valueOf(name);
    }
}

