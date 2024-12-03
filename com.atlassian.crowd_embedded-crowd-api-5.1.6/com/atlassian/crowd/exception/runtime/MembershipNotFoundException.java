/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.exception.runtime;

import com.atlassian.crowd.exception.runtime.CrowdRuntimeException;

public class MembershipNotFoundException
extends CrowdRuntimeException {
    private final String childName;
    private final String parentName;

    public MembershipNotFoundException(String childName, String parentName) {
        this(childName, parentName, null);
    }

    public MembershipNotFoundException(String childName, String parentName, Throwable e) {
        super("The child entity <" + childName + "> is not a member of the parent <" + parentName + ">", e);
        this.childName = childName;
        this.parentName = parentName;
    }

    public String getChildName() {
        return this.childName;
    }

    public String getParentName() {
        return this.parentName;
    }
}

