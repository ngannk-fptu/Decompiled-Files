/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl.getters;

import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.HazelcastSerializationException;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.extractor.ValueExtractor;
import com.hazelcast.query.impl.DefaultArgumentParser;
import com.hazelcast.query.impl.getters.EvictableGetterCache;
import com.hazelcast.query.impl.getters.ExtractorGetter;
import com.hazelcast.query.impl.getters.ExtractorHelper;
import com.hazelcast.query.impl.getters.Getter;
import com.hazelcast.query.impl.getters.JsonDataGetter;
import com.hazelcast.query.impl.getters.JsonGetter;
import com.hazelcast.query.impl.getters.PortableGetter;
import com.hazelcast.query.impl.getters.ReflectionHelper;
import com.hazelcast.util.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class Extractors {
    private static final int MAX_CLASSES_IN_CACHE = 1000;
    private static final int MAX_GETTERS_PER_CLASS_IN_CACHE = 100;
    private static final float EVICTION_PERCENTAGE = 0.2f;
    private volatile PortableGetter genericPortableGetter;
    private volatile JsonDataGetter jsonDataGetter;
    private final Map<String, ValueExtractor> extractors;
    private final InternalSerializationService ss;
    private final EvictableGetterCache getterCache;
    private final DefaultArgumentParser argumentsParser;

    private Extractors(List<MapAttributeConfig> mapAttributeConfigs, ClassLoader classLoader, InternalSerializationService ss) {
        this.extractors = mapAttributeConfigs == null ? Collections.emptyMap() : ExtractorHelper.instantiateExtractors(mapAttributeConfigs, classLoader);
        this.getterCache = new EvictableGetterCache(1000, 100, 0.2f, false);
        this.argumentsParser = new DefaultArgumentParser();
        this.ss = ss;
    }

    public Object extract(Object target, String attributeName, Object metadata) {
        Object targetObject = this.getTargetObject(target);
        if (targetObject != null) {
            Getter getter = this.getGetter(targetObject, attributeName);
            try {
                return getter.getValue(targetObject, attributeName, metadata);
            }
            catch (Exception ex) {
                throw new QueryException(ex);
            }
        }
        return null;
    }

    private Object getTargetObject(Object target) {
        Object targetData;
        if (target instanceof Portable && (targetData = this.ss.toData(target)).isPortable()) {
            return targetData;
        }
        if (target instanceof Data) {
            targetData = (Data)target;
            if (targetData.isPortable() || targetData.isJson()) {
                return targetData;
            }
            return this.ss.toObject(target);
        }
        return target;
    }

    Getter getGetter(Object targetObject, String attributeName) {
        Getter getter = this.getterCache.getGetter(targetObject.getClass(), attributeName);
        if (getter == null && (getter = this.instantiateGetter(targetObject, attributeName)).isCacheable()) {
            this.getterCache.putGetter(targetObject.getClass(), attributeName, getter);
        }
        return getter;
    }

    private Getter instantiateGetter(Object targetObject, String attributeName) {
        String attributeNameWithoutArguments = ExtractorHelper.extractAttributeNameNameWithoutArguments(attributeName);
        ValueExtractor valueExtractor = this.extractors.get(attributeNameWithoutArguments);
        if (valueExtractor != null) {
            Object arguments = this.argumentsParser.parse(ExtractorHelper.extractArgumentsFromAttributeName(attributeName));
            return new ExtractorGetter(this.ss, valueExtractor, arguments);
        }
        if (targetObject instanceof Data) {
            if (((Data)targetObject).isPortable()) {
                if (this.genericPortableGetter == null) {
                    this.genericPortableGetter = new PortableGetter(this.ss);
                }
                return this.genericPortableGetter;
            }
            if (((Data)targetObject).isJson()) {
                if (this.jsonDataGetter == null) {
                    this.jsonDataGetter = new JsonDataGetter(this.ss);
                }
                return this.jsonDataGetter;
            }
            throw new HazelcastSerializationException("No Data getter found for type " + ((Data)targetObject).getType());
        }
        if (targetObject instanceof HazelcastJsonValue) {
            return JsonGetter.INSTANCE;
        }
        return ReflectionHelper.createGetter(targetObject, attributeName);
    }

    public static Builder newBuilder(InternalSerializationService ss) {
        return new Builder(ss);
    }

    public static final class Builder {
        private ClassLoader classLoader;
        private List<MapAttributeConfig> mapAttributeConfigs;
        private final InternalSerializationService ss;

        public Builder(InternalSerializationService ss) {
            this.ss = Preconditions.checkNotNull(ss);
        }

        public Builder setMapAttributeConfigs(List<MapAttributeConfig> mapAttributeConfigs) {
            this.mapAttributeConfigs = mapAttributeConfigs;
            return this;
        }

        public Builder setClassLoader(ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        public Extractors build() {
            return new Extractors(this.mapAttributeConfigs, this.classLoader, this.ss);
        }
    }
}

