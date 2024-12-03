/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 */
package com.atlassian.marshalling.api;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.marshalling.api.MarshallingException;

@FunctionalInterface
@PublicSpi
public interface Unmarshaller<T> {
    public T unmarshallFrom(byte[] var1) throws MarshallingException;
}

