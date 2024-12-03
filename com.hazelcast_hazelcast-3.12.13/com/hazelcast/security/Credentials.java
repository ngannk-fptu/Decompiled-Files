/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security;

import com.hazelcast.nio.serialization.BinaryInterface;
import java.io.Serializable;

@BinaryInterface
public interface Credentials
extends Serializable {
    public String getEndpoint();

    public void setEndpoint(String var1);

    public String getPrincipal();
}

