/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.exception.SdkClientException
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 */
package software.amazon.awssdk.protocols.core;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;

@SdkProtectedApi
public abstract class AbstractMarshallingRegistry {
    private final Map<MarshallLocation, Map<MarshallingType, Object>> registry;
    private final Set<MarshallingType<?>> marshallingTypes;
    private final Map<Class<?>, MarshallingType<?>> marshallingTypeCache;

    protected AbstractMarshallingRegistry(Builder builder) {
        this.registry = builder.registry;
        this.marshallingTypes = builder.marshallingTypes;
        this.marshallingTypeCache = new HashMap(this.marshallingTypes.size());
    }

    protected Object get(MarshallLocation marshallLocation, MarshallingType<?> marshallingType) {
        Map<MarshallingType, Object> byLocation = this.registry.get(marshallLocation);
        if (byLocation == null) {
            throw SdkClientException.create((String)("No marshaller/unmarshaller registered for location " + marshallLocation.name()));
        }
        Object registered = byLocation.get(marshallingType);
        if (registered == null) {
            throw SdkClientException.create((String)String.format("No marshaller/unmarshaller of type %s registered for location %s.", marshallingType, marshallLocation.name()));
        }
        return registered;
    }

    protected <T> MarshallingType<T> toMarshallingType(T val) {
        if (val == null) {
            return MarshallingType.NULL;
        }
        if (val instanceof SdkPojo) {
            return MarshallingType.SDK_POJO;
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
                    if (!marshallingType.getTargetClass().isAssignableFrom(clzz)) continue;
                    this.marshallingTypeCache.put(clzz, marshallingType);
                    return marshallingType;
                }
                throw SdkClientException.builder().message("MarshallingType not found for class " + clzz).build();
            }
        }
        return this.marshallingTypeCache.get(clzz);
    }

    public static abstract class Builder {
        private final Map<MarshallLocation, Map<MarshallingType, Object>> registry = new EnumMap<MarshallLocation, Map<MarshallingType, Object>>(MarshallLocation.class);
        private final Set<MarshallingType<?>> marshallingTypes = new HashSet();

        protected Builder() {
        }

        protected <T> Builder register(MarshallLocation marshallLocation, MarshallingType<T> marshallingType, Object marshaller) {
            this.marshallingTypes.add(marshallingType);
            if (!this.registry.containsKey(marshallLocation)) {
                this.registry.put(marshallLocation, new HashMap());
            }
            this.registry.get(marshallLocation).put(marshallingType, marshaller);
            return this;
        }
    }
}

