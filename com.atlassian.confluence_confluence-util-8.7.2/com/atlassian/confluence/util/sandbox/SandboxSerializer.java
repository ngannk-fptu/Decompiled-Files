/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.util.sandbox;

import com.atlassian.annotations.ExperimentalApi;

@ExperimentalApi
public interface SandboxSerializer<T> {
    public byte[] serialize(T var1);

    public T deserialize(byte[] var1);
}

