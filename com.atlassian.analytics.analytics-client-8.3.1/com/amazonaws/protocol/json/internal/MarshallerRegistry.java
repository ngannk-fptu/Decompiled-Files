/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.protocol.json.internal;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.protocol.MarshallLocation;
import com.amazonaws.protocol.MarshallingType;
import com.amazonaws.protocol.StructuredPojo;
import com.amazonaws.protocol.json.internal.JsonMarshaller;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SdkInternalApi
public class MarshallerRegistry {
    private final Map<MarshallLocation, Map<MarshallingType, JsonMarshaller<?>>> marshallers;
    private final Set<MarshallingType<?>> marshallingTypes;
    private final Map<Class<?>, MarshallingType<?>> marshallingTypeCache;

    private MarshallerRegistry(Builder builder) {
        this.marshallers = builder.marshallers;
        this.marshallingTypes = builder.marshallingTypes;
        this.marshallingTypeCache = new HashMap(this.marshallingTypes.size());
    }

    public <T> JsonMarshaller<T> getMarshaller(MarshallLocation marshallLocation, T val) {
        return this.getMarshaller(marshallLocation, this.toMarshallingType(val));
    }

    public <T> JsonMarshaller<T> getMarshaller(MarshallLocation marshallLocation, MarshallingType<T> marshallingType, T val) {
        return this.getMarshaller(marshallLocation, val == null ? MarshallingType.NULL : marshallingType);
    }

    private <T> JsonMarshaller<T> getMarshaller(MarshallLocation marshallLocation, MarshallingType<?> marshallingType) {
        return this.marshallers.get((Object)marshallLocation).get(marshallingType);
    }

    public <T> MarshallingType<T> toMarshallingType(T val) {
        if (val == null) {
            return MarshallingType.NULL;
        }
        if (val instanceof StructuredPojo) {
            return MarshallingType.STRUCTURED;
        }
        if (!this.marshallingTypeCache.containsKey(val.getClass())) {
            return this.populateMarshallingTypeCache(val.getClass());
        }
        return this.marshallingTypeCache.get(val.getClass());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private MarshallingType<?> populateMarshallingTypeCache(Class<?> clzz) {
        Map<Class<?>, MarshallingType<?>> map = this.marshallingTypeCache;
        synchronized (map) {
            if (!this.marshallingTypeCache.containsKey(clzz)) {
                for (MarshallingType<?> marshallingType : this.marshallingTypes) {
                    if (!marshallingType.isDefaultMarshallerForType(clzz)) continue;
                    this.marshallingTypeCache.put(clzz, marshallingType);
                    return marshallingType;
                }
                throw new SdkClientException("MarshallingType not found for class " + clzz);
            }
        }
        return this.marshallingTypeCache.get(clzz);
    }

    public MarshallerRegistry merge(Builder marshallerRegistryOverrides) {
        if (marshallerRegistryOverrides == null) {
            return this;
        }
        Builder merged = MarshallerRegistry.builder();
        merged.copyMarshallersFromRegistry(this.marshallers);
        merged.copyMarshallersFromRegistry(marshallerRegistryOverrides.marshallers);
        return merged.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<MarshallLocation, Map<MarshallingType, JsonMarshaller<?>>> marshallers = new HashMap();
        private final Set<MarshallingType<?>> marshallingTypes = new HashSet();

        private Builder() {
        }

        public <T> Builder payloadMarshaller(MarshallingType<T> marshallingType, JsonMarshaller<T> marshaller) {
            return this.addMarshaller(MarshallLocation.PAYLOAD, marshallingType, marshaller);
        }

        public <T> Builder headerMarshaller(MarshallingType<T> marshallingType, JsonMarshaller<T> marshaller) {
            return this.addMarshaller(MarshallLocation.HEADER, marshallingType, marshaller);
        }

        public <T> Builder queryParamMarshaller(MarshallingType<T> marshallingType, JsonMarshaller<T> marshaller) {
            return this.addMarshaller(MarshallLocation.QUERY_PARAM, marshallingType, marshaller);
        }

        public <T> Builder pathParamMarshaller(MarshallingType<T> marshallingType, JsonMarshaller<T> marshaller) {
            return this.addMarshaller(MarshallLocation.PATH, marshallingType, marshaller);
        }

        public <T> Builder greedyPathParamMarshaller(MarshallingType<T> marshallingType, JsonMarshaller<T> marshaller) {
            return this.addMarshaller(MarshallLocation.GREEDY_PATH, marshallingType, marshaller);
        }

        public <T> Builder addMarshaller(MarshallLocation marshallLocation, MarshallingType<T> marshallingType, JsonMarshaller<T> marshaller) {
            this.marshallingTypes.add(marshallingType);
            if (!this.marshallers.containsKey((Object)marshallLocation)) {
                this.marshallers.put(marshallLocation, new HashMap());
            }
            this.marshallers.get((Object)marshallLocation).put(marshallingType, marshaller);
            return this;
        }

        public MarshallerRegistry build() {
            return new MarshallerRegistry(this);
        }

        private void copyMarshallersFromRegistry(Map<MarshallLocation, Map<MarshallingType, JsonMarshaller<?>>> sourceMarshallers) {
            for (Map.Entry<MarshallLocation, Map<MarshallingType, JsonMarshaller<?>>> byLocationEntry : sourceMarshallers.entrySet()) {
                for (Map.Entry<MarshallingType, JsonMarshaller<?>> byTypeEntry : byLocationEntry.getValue().entrySet()) {
                    this.addMarshaller(byLocationEntry.getKey(), byTypeEntry.getKey(), byTypeEntry.getValue());
                }
            }
        }
    }
}

