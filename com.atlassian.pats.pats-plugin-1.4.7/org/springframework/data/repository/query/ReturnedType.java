/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.repository.query;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import org.springframework.data.mapping.PreferredConstructor;
import org.springframework.data.mapping.model.PreferredConstructorDiscoverer;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.ProjectionInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;

public abstract class ReturnedType {
    private static final Map<CacheKey, ReturnedType> cache = new ConcurrentReferenceHashMap(32);
    private final Class<?> domainType;

    private ReturnedType(Class<?> domainType) {
        this.domainType = domainType;
    }

    static ReturnedType of(Class<?> returnedType, Class<?> domainType, ProjectionFactory factory) {
        Assert.notNull(returnedType, (String)"Returned type must not be null!");
        Assert.notNull(domainType, (String)"Domain type must not be null!");
        Assert.notNull((Object)factory, (String)"ProjectionFactory must not be null!");
        return cache.computeIfAbsent(CacheKey.of(returnedType, domainType, factory.hashCode()), key -> returnedType.isInterface() ? new ReturnedInterface(factory.getProjectionInformation(returnedType), domainType) : new ReturnedClass(returnedType, domainType));
    }

    public final Class<?> getDomainType() {
        return this.domainType;
    }

    public final boolean isInstance(@Nullable Object source) {
        return this.getReturnedType().isInstance(source);
    }

    public abstract boolean isProjecting();

    public abstract Class<?> getReturnedType();

    public abstract boolean needsCustomConstruction();

    @Nullable
    public abstract Class<?> getTypeToRead();

    public abstract List<String> getInputProperties();

    private static final class CacheKey {
        private final Class<?> returnedType;
        private final Class<?> domainType;
        private final int projectionFactoryHashCode;

        private CacheKey(Class<?> returnedType, Class<?> domainType, int projectionFactoryHashCode) {
            this.returnedType = returnedType;
            this.domainType = domainType;
            this.projectionFactoryHashCode = projectionFactoryHashCode;
        }

        public static CacheKey of(Class<?> returnedType, Class<?> domainType, int projectionFactoryHashCode) {
            return new CacheKey(returnedType, domainType, projectionFactoryHashCode);
        }

        public Class<?> getReturnedType() {
            return this.returnedType;
        }

        public Class<?> getDomainType() {
            return this.domainType;
        }

        public int getProjectionFactoryHashCode() {
            return this.projectionFactoryHashCode;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CacheKey)) {
                return false;
            }
            CacheKey cacheKey = (CacheKey)o;
            if (this.projectionFactoryHashCode != cacheKey.projectionFactoryHashCode) {
                return false;
            }
            if (!ObjectUtils.nullSafeEquals(this.returnedType, cacheKey.returnedType)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.domainType, cacheKey.domainType);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode(this.returnedType);
            result = 31 * result + ObjectUtils.nullSafeHashCode(this.domainType);
            result = 31 * result + this.projectionFactoryHashCode;
            return result;
        }

        public String toString() {
            return "ReturnedType.CacheKey(returnedType=" + this.getReturnedType() + ", domainType=" + this.getDomainType() + ", projectionFactoryHashCode=" + this.getProjectionFactoryHashCode() + ")";
        }
    }

    private static final class ReturnedClass
    extends ReturnedType {
        private static final Set<Class<?>> VOID_TYPES = new HashSet<Class>(Arrays.asList(Void.class, Void.TYPE));
        private final Class<?> type;
        private final List<String> inputProperties;

        public ReturnedClass(Class<?> returnedType, Class<?> domainType) {
            super(domainType);
            Assert.notNull(returnedType, (String)"Returned type must not be null!");
            Assert.notNull(domainType, (String)"Domain type must not be null!");
            Assert.isTrue((!returnedType.isInterface() ? 1 : 0) != 0, (String)"Returned type must not be an interface!");
            this.type = returnedType;
            this.inputProperties = this.detectConstructorParameterNames(returnedType);
        }

        @Override
        public Class<?> getReturnedType() {
            return this.type;
        }

        @Override
        @Nonnull
        public Class<?> getTypeToRead() {
            return this.type;
        }

        @Override
        public boolean isProjecting() {
            return this.isDto();
        }

        @Override
        public boolean needsCustomConstruction() {
            return this.isDto() && !this.inputProperties.isEmpty();
        }

        @Override
        public List<String> getInputProperties() {
            return this.inputProperties;
        }

        private List<String> detectConstructorParameterNames(Class<?> type) {
            if (!this.isDto()) {
                return Collections.emptyList();
            }
            PreferredConstructor constructor = PreferredConstructorDiscoverer.discover(type);
            if (constructor == null) {
                return Collections.emptyList();
            }
            ArrayList<String> properties = new ArrayList<String>(constructor.getConstructor().getParameterCount());
            for (PreferredConstructor.Parameter parameter : constructor.getParameters()) {
                properties.add(parameter.getName());
            }
            return properties;
        }

        private boolean isDto() {
            return !Object.class.equals(this.type) && !this.type.isEnum() && !this.isDomainSubtype() && !this.isPrimitiveOrWrapper() && !Number.class.isAssignableFrom(this.type) && !VOID_TYPES.contains(this.type) && !this.type.getPackage().getName().startsWith("java.");
        }

        private boolean isDomainSubtype() {
            return this.getDomainType().equals(this.type) && this.getDomainType().isAssignableFrom(this.type);
        }

        private boolean isPrimitiveOrWrapper() {
            return ClassUtils.isPrimitiveOrWrapper(this.type);
        }
    }

    private static final class ReturnedInterface
    extends ReturnedType {
        private final ProjectionInformation information;
        private final Class<?> domainType;

        public ReturnedInterface(ProjectionInformation information, Class<?> domainType) {
            super(domainType);
            Assert.notNull((Object)information, (String)"Projection information must not be null!");
            this.information = information;
            this.domainType = domainType;
        }

        @Override
        public Class<?> getReturnedType() {
            return this.information.getType();
        }

        @Override
        public boolean needsCustomConstruction() {
            return this.isProjecting() && this.information.isClosed();
        }

        @Override
        public boolean isProjecting() {
            return !this.information.getType().isAssignableFrom(this.domainType);
        }

        @Override
        @Nullable
        public Class<?> getTypeToRead() {
            return this.isProjecting() && this.information.isClosed() ? null : this.domainType;
        }

        @Override
        public List<String> getInputProperties() {
            ArrayList<String> properties = new ArrayList<String>();
            for (PropertyDescriptor descriptor : this.information.getInputProperties()) {
                if (properties.contains(descriptor.getName())) continue;
                properties.add(descriptor.getName());
            }
            return properties;
        }
    }
}

