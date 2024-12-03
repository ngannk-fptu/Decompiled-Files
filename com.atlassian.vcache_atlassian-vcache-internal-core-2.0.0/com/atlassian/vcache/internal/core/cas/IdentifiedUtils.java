/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.marshalling.api.MarshallingException
 *  com.atlassian.marshalling.api.MarshallingPair
 *  com.atlassian.vcache.CasIdentifier
 *  com.atlassian.vcache.ExternalCacheException
 *  com.atlassian.vcache.ExternalCacheException$Reason
 *  com.atlassian.vcache.IdentifiedValue
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.vcache.internal.core.cas;

import com.atlassian.marshalling.api.MarshallingException;
import com.atlassian.marshalling.api.MarshallingPair;
import com.atlassian.vcache.CasIdentifier;
import com.atlassian.vcache.ExternalCacheException;
import com.atlassian.vcache.IdentifiedValue;
import com.atlassian.vcache.internal.core.DefaultIdentifiedValue;
import com.atlassian.vcache.internal.core.cas.IdentifiedData;
import com.atlassian.vcache.internal.core.cas.IdentifiedDataBytes;
import com.atlassian.vcache.internal.core.cas.IdentifiedDataSerializable;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdentifiedUtils {
    private static final Logger log = LoggerFactory.getLogger(IdentifiedUtils.class);

    public static <V> IdentifiedData marshall(V data, Optional<MarshallingPair<V>> valueMarshalling) throws ExternalCacheException {
        Objects.requireNonNull(data);
        try {
            return valueMarshalling.isPresent() ? new IdentifiedDataBytes(valueMarshalling.get().getMarshaller().marshallToBytes(data)) : new IdentifiedDataSerializable((Serializable)data);
        }
        catch (MarshallingException e) {
            throw new ExternalCacheException(ExternalCacheException.Reason.MARSHALLER_FAILURE, (Throwable)e);
        }
    }

    public static <V> Optional<V> unmarshall(@Nullable IdentifiedData idata, Optional<MarshallingPair<V>> valueMarshalling) throws ExternalCacheException {
        if (idata == null) {
            return Optional.empty();
        }
        try {
            return valueMarshalling.isPresent() ? Optional.of(valueMarshalling.get().getUnmarshaller().unmarshallFrom(((IdentifiedDataBytes)idata).getBytes())) : Optional.of(((IdentifiedDataSerializable)idata).getObject());
        }
        catch (MarshallingException ex) {
            throw new ExternalCacheException(ExternalCacheException.Reason.MARSHALLER_FAILURE, (Throwable)ex);
        }
    }

    public static <V> Optional<IdentifiedValue<V>> unmarshallIdentified(@Nullable IdentifiedData idata, Optional<MarshallingPair<V>> valueMarshalling) {
        if (idata == null) {
            return Optional.empty();
        }
        try {
            Serializable value = valueMarshalling.isPresent() ? valueMarshalling.get().getUnmarshaller().unmarshallFrom(((IdentifiedDataBytes)idata).getBytes()) : ((IdentifiedDataSerializable)idata).getObject();
            return Optional.of(new DefaultIdentifiedValue<Serializable>(idata, value));
        }
        catch (MarshallingException ex) {
            throw new ExternalCacheException(ExternalCacheException.Reason.MARSHALLER_FAILURE, (Throwable)ex);
        }
    }

    public static IdentifiedData safeCast(CasIdentifier casId) {
        if (casId instanceof IdentifiedData) {
            return (IdentifiedData)casId;
        }
        log.warn("Passed an unknown CasIdentifier instance of class {}.", (Object)casId.getClass().getName());
        throw new ExternalCacheException(ExternalCacheException.Reason.UNCLASSIFIED_FAILURE);
    }
}

