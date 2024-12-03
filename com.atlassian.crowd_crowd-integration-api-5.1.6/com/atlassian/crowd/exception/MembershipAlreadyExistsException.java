/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception;

import com.atlassian.crowd.exception.ObjectAlreadyExistsException;

public class MembershipAlreadyExistsException
extends ObjectAlreadyExistsException {
    private final String childEntity;
    private final String parentEntity;

    public MembershipAlreadyExistsException(long directoryId, String childEntity, String parentEntity) {
        super("Membership already exists in directory [" + directoryId + "] from child entity [" + childEntity + "] to parent entity [" + parentEntity + "]");
        this.childEntity = childEntity;
        this.parentEntity = parentEntity;
    }

    public MembershipAlreadyExistsException(String childEntity, String parentEntity) {
        super("Membership already exists from child entity [" + childEntity + "] to parent entity [" + parentEntity + "]");
        this.childEntity = childEntity;
        this.parentEntity = parentEntity;
    }

    public String getChildEntity() {
        return this.childEntity;
    }

    public String getParentEntity() {
        return this.parentEntity;
    }
}

