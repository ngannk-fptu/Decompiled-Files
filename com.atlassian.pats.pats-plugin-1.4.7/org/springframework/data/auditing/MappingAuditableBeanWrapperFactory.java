/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 */
package org.springframework.data.auditing;

import java.lang.annotation.Annotation;
import java.time.temporal.TemporalAccessor;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.auditing.AuditableBeanWrapper;
import org.springframework.data.auditing.DefaultAuditableBeanWrapperFactory;
import org.springframework.data.domain.Auditable;
import org.springframework.data.mapping.AccessOptions;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mapping.PersistentPropertyPathAccessor;
import org.springframework.data.mapping.PersistentPropertyPaths;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.data.util.Lazy;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

public class MappingAuditableBeanWrapperFactory
extends DefaultAuditableBeanWrapperFactory {
    private final PersistentEntities entities;
    private final Map<Class<?>, MappingAuditingMetadata> metadataCache;

    public MappingAuditableBeanWrapperFactory(PersistentEntities entities) {
        Assert.notNull((Object)entities, (String)"PersistentEntities must not be null!");
        this.entities = entities;
        this.metadataCache = new ConcurrentReferenceHashMap();
    }

    @Override
    public <T> Optional<AuditableBeanWrapper<T>> getBeanWrapperFor(T source) {
        return Optional.of(source).flatMap(it -> {
            if (it instanceof Auditable) {
                return super.getBeanWrapperFor(source);
            }
            return this.entities.mapOnContext(it.getClass(), (context, entity) -> {
                MappingAuditingMetadata metadata = this.metadataCache.computeIfAbsent(it.getClass(), key -> new MappingAuditingMetadata((MappingContext<?, ? extends PersistentProperty<?>>)context, it.getClass()));
                return Optional.ofNullable(metadata.isAuditable() ? new MappingMetadataAuditableBeanWrapper<Object>(this.getConversionService(), entity.getPropertyPathAccessor(it), metadata) : null);
            }).orElseGet(() -> super.getBeanWrapperFor(source));
        });
    }

    static class MappingMetadataAuditableBeanWrapper<T>
    extends DefaultAuditableBeanWrapperFactory.DateConvertingAuditableBeanWrapper<T> {
        private static final AccessOptions.SetOptions OPTIONS = AccessOptions.defaultSetOptions().skipNulls().withCollectionAndMapPropagation(AccessOptions.SetOptions.Propagation.SKIP);
        private final PersistentPropertyPathAccessor<T> accessor;
        private final MappingAuditingMetadata metadata;

        public MappingMetadataAuditableBeanWrapper(ConversionService conversionService, PersistentPropertyPathAccessor<T> accessor, MappingAuditingMetadata metadata) {
            super(conversionService);
            Assert.notNull(accessor, (String)"PersistentPropertyAccessor must not be null!");
            Assert.notNull((Object)metadata, (String)"Auditing metadata must not be null!");
            this.accessor = accessor;
            this.metadata = metadata;
        }

        @Override
        public Object setCreatedBy(Object value) {
            return this.setProperty(this.metadata.createdByPaths, value);
        }

        @Override
        public TemporalAccessor setCreatedDate(TemporalAccessor value) {
            return this.setDateProperty(this.metadata.createdDatePaths, value);
        }

        @Override
        public Object setLastModifiedBy(Object value) {
            return this.setProperty(this.metadata.lastModifiedByPaths, value);
        }

        @Override
        public Optional<TemporalAccessor> getLastModifiedDate() {
            Optional<Object> firstValue = this.metadata.lastModifiedDatePaths.getFirst().map(this.accessor::getProperty);
            return this.getAsTemporalAccessor(firstValue, TemporalAccessor.class);
        }

        @Override
        public TemporalAccessor setLastModifiedDate(TemporalAccessor value) {
            return this.setDateProperty(this.metadata.lastModifiedDatePaths, value);
        }

        @Override
        public T getBean() {
            return this.accessor.getBean();
        }

        private <S> S setProperty(PersistentPropertyPaths<?, ? extends PersistentProperty<?>> paths, S value) {
            paths.forEach(it -> this.accessor.setProperty((PersistentPropertyPath<PersistentProperty<?>>)it, value, OPTIONS));
            return value;
        }

        private TemporalAccessor setDateProperty(PersistentPropertyPaths<?, ? extends PersistentProperty<?>> property, TemporalAccessor value) {
            property.forEach(it -> {
                Class<?> type = it.getRequiredLeafProperty().getType();
                this.accessor.setProperty((PersistentPropertyPath<PersistentProperty<?>>)it, this.getDateValueToSet(value, type, this.accessor.getBean()), OPTIONS);
            });
            return value;
        }
    }

    static class MappingAuditingMetadata {
        private static final Predicate<? super PersistentProperty<?>> HAS_COLLECTION_PROPERTY = it -> it.isCollectionLike() || it.isMap();
        private final PersistentPropertyPaths<?, ? extends PersistentProperty<?>> createdByPaths;
        private final PersistentPropertyPaths<?, ? extends PersistentProperty<?>> createdDatePaths;
        private final PersistentPropertyPaths<?, ? extends PersistentProperty<?>> lastModifiedByPaths;
        private final PersistentPropertyPaths<?, ? extends PersistentProperty<?>> lastModifiedDatePaths;
        private final Lazy<Boolean> isAuditable;

        public <P> MappingAuditingMetadata(MappingContext<?, ? extends PersistentProperty<?>> context, Class<?> type) {
            Assert.notNull(type, (String)"Type must not be null!");
            this.createdByPaths = this.findPropertyPaths(type, CreatedBy.class, context);
            this.createdDatePaths = this.findPropertyPaths(type, CreatedDate.class, context);
            this.lastModifiedByPaths = this.findPropertyPaths(type, LastModifiedBy.class, context);
            this.lastModifiedDatePaths = this.findPropertyPaths(type, LastModifiedDate.class, context);
            this.isAuditable = Lazy.of(() -> Stream.of(this.createdByPaths, this.createdDatePaths, this.lastModifiedByPaths, this.lastModifiedDatePaths).anyMatch(it -> !it.isEmpty()));
        }

        public boolean isAuditable() {
            return this.isAuditable.get();
        }

        private PersistentPropertyPaths<?, ? extends PersistentProperty<?>> findPropertyPaths(Class<?> type, Class<? extends Annotation> annotation, MappingContext<?, ? extends PersistentProperty<?>> context) {
            return context.findPersistentPropertyPaths(type, MappingAuditingMetadata.withAnnotation(annotation)).dropPathIfSegmentMatches(HAS_COLLECTION_PROPERTY);
        }

        private static Predicate<PersistentProperty<?>> withAnnotation(Class<? extends Annotation> type) {
            return t -> t.findAnnotation(type) != null;
        }
    }
}

