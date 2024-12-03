/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.client;

import java.security.Permission;

public interface SecureRequest {
    public Permission getRequiredPermission();

    public String getDistributedObjectType();

    public String getDistributedObjectName();

    public String getMethodName();

    public Object[] getParameters();
}

