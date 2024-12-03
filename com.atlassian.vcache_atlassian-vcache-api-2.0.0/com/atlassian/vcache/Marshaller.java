/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicSpi
 *  com.atlassian.marshalling.api.Marshaller
 *  com.atlassian.marshalling.api.MarshallingException
 *  com.atlassian.marshalling.api.Unmarshaller
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicSpi;
import com.atlassian.marshalling.api.MarshallingException;
import com.atlassian.marshalling.api.Unmarshaller;
import com.atlassian.vcache.MarshallerException;

@Deprecated
@PublicSpi
public interface Marshaller<T>
extends com.atlassian.marshalling.api.Marshaller<T>,
Unmarshaller<T> {
    public byte[] marshall(T var1) throws MarshallerException;

    public T unmarshall(byte[] var1) throws MarshallerException;

    default public byte[] marshallToBytes(T t) throws MarshallingException {
        try {
            return this.marshall(t);
        }
        catch (MarshallerException e) {
            throw new MarshallingException("Legacy marshall() failed", (Throwable)e);
        }
    }

    default public T unmarshallFrom(byte[] bytes) throws MarshallingException {
        try {
            return this.unmarshall(bytes);
        }
        catch (MarshallerException e) {
            throw new MarshallingException("Legacy unmarshall() failed", (Throwable)e);
        }
    }
}

