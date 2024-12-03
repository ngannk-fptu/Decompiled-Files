/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.format.support.DefaultFormattingConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.auditing;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.auditing.AnnotationAuditingMetadata;
import org.springframework.data.auditing.AuditableBeanWrapper;
import org.springframework.data.auditing.AuditableBeanWrapperFactory;
import org.springframework.data.convert.JodaTimeConverters;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.convert.ThreeTenBackPortConverters;
import org.springframework.data.domain.Auditable;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

class DefaultAuditableBeanWrapperFactory
implements AuditableBeanWrapperFactory {
    private final ConversionService conversionService;

    public DefaultAuditableBeanWrapperFactory() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        JodaTimeConverters.getConvertersToRegister().forEach(arg_0 -> ((DefaultFormattingConversionService)conversionService).addConverter(arg_0));
        Jsr310Converters.getConvertersToRegister().forEach(arg_0 -> ((DefaultFormattingConversionService)conversionService).addConverter(arg_0));
        ThreeTenBackPortConverters.getConvertersToRegister().forEach(arg_0 -> ((DefaultFormattingConversionService)conversionService).addConverter(arg_0));
        this.conversionService = conversionService;
    }

    ConversionService getConversionService() {
        return this.conversionService;
    }

    @Override
    public <T> Optional<AuditableBeanWrapper<T>> getBeanWrapperFor(T source) {
        Assert.notNull(source, (String)"Source must not be null!");
        return Optional.of(source).map(it -> {
            if (it instanceof Auditable) {
                return new AuditableInterfaceBeanWrapper(this.conversionService, (Auditable)it);
            }
            AnnotationAuditingMetadata metadata = AnnotationAuditingMetadata.getMetadata(it.getClass());
            if (metadata.isAuditable()) {
                return new ReflectionAuditingBeanWrapper<Object>(this.conversionService, it);
            }
            return null;
        });
    }

    private static IllegalArgumentException rejectUnsupportedType(Object source) {
        return new IllegalArgumentException(String.format("Invalid date type %s for member %s! Supported types are %s.", source.getClass(), source, AnnotationAuditingMetadata.SUPPORTED_DATE_TYPES));
    }

    static class ReflectionAuditingBeanWrapper<T>
    extends DateConvertingAuditableBeanWrapper<T> {
        private final AnnotationAuditingMetadata metadata;
        private final T target;

        public ReflectionAuditingBeanWrapper(ConversionService conversionService, T target) {
            super(conversionService);
            Assert.notNull(target, (String)"Target object must not be null!");
            this.metadata = AnnotationAuditingMetadata.getMetadata(target.getClass());
            this.target = target;
        }

        @Override
        public Object setCreatedBy(Object value) {
            return this.setField(this.metadata.getCreatedByField(), value);
        }

        @Override
        public TemporalAccessor setCreatedDate(TemporalAccessor value) {
            return this.setDateField(this.metadata.getCreatedDateField(), value);
        }

        @Override
        public Object setLastModifiedBy(Object value) {
            return this.setField(this.metadata.getLastModifiedByField(), value);
        }

        @Override
        public Optional<TemporalAccessor> getLastModifiedDate() {
            return this.getAsTemporalAccessor(this.metadata.getLastModifiedDateField().map(field -> {
                Object value = ReflectionUtils.getField((Field)field, this.target);
                return value instanceof Optional ? ((Optional)value).orElse(null) : value;
            }), TemporalAccessor.class);
        }

        @Override
        public TemporalAccessor setLastModifiedDate(TemporalAccessor value) {
            return this.setDateField(this.metadata.getLastModifiedDateField(), value);
        }

        @Override
        public T getBean() {
            return this.target;
        }

        private <S> S setField(Optional<Field> field, S value) {
            field.ifPresent(it -> org.springframework.data.util.ReflectionUtils.setField(it, this.target, value));
            return value;
        }

        private TemporalAccessor setDateField(Optional<Field> field, TemporalAccessor value) {
            field.ifPresent(it -> org.springframework.data.util.ReflectionUtils.setField(it, this.target, this.getDateValueToSet(value, it.getType(), it)));
            return value;
        }
    }

    static abstract class DateConvertingAuditableBeanWrapper<T>
    implements AuditableBeanWrapper<T> {
        private final ConversionService conversionService;

        DateConvertingAuditableBeanWrapper(ConversionService conversionService) {
            this.conversionService = conversionService;
        }

        @Nullable
        protected Object getDateValueToSet(TemporalAccessor value, Class<?> targetType, Object source) {
            if (TemporalAccessor.class.equals(targetType)) {
                return value;
            }
            if (this.conversionService.canConvert(value.getClass(), targetType)) {
                return this.conversionService.convert((Object)value, targetType);
            }
            if (this.conversionService.canConvert(Date.class, targetType)) {
                if (!this.conversionService.canConvert(value.getClass(), Date.class)) {
                    throw new IllegalArgumentException(String.format("Cannot convert date type for member %s! From %s to java.util.Date to %s.", source, value.getClass(), targetType));
                }
                Date date = (Date)this.conversionService.convert((Object)value, Date.class);
                return this.conversionService.convert((Object)date, targetType);
            }
            throw DefaultAuditableBeanWrapperFactory.rejectUnsupportedType(source);
        }

        protected <S extends TemporalAccessor> Optional<S> getAsTemporalAccessor(Optional<?> source, Class<? extends S> target) {
            return source.map(it -> {
                if (target.isInstance(it)) {
                    return (TemporalAccessor)it;
                }
                Class typeToConvertTo = Stream.of(target, Instant.class).filter(type -> target.isAssignableFrom((Class<?>)type)).filter(type -> this.conversionService.canConvert(it.getClass(), type)).findFirst().orElseThrow(() -> DefaultAuditableBeanWrapperFactory.rejectUnsupportedType(source.map(Object.class::cast).orElseGet(() -> source)));
                return (TemporalAccessor)this.conversionService.convert(it, typeToConvertTo);
            });
        }
    }

    static class AuditableInterfaceBeanWrapper
    extends DateConvertingAuditableBeanWrapper<Auditable<Object, ?, TemporalAccessor>> {
        private final Auditable<Object, ?, TemporalAccessor> auditable;
        private final Class<? extends TemporalAccessor> type;

        public AuditableInterfaceBeanWrapper(ConversionService conversionService, Auditable<Object, ?, TemporalAccessor> auditable) {
            super(conversionService);
            this.auditable = auditable;
            this.type = ResolvableType.forClass(Auditable.class, auditable.getClass()).getGeneric(new int[]{2}).resolve(TemporalAccessor.class);
        }

        @Override
        public Object setCreatedBy(Object value) {
            this.auditable.setCreatedBy(value);
            return value;
        }

        @Override
        public TemporalAccessor setCreatedDate(TemporalAccessor value) {
            this.auditable.setCreatedDate(this.getAsTemporalAccessor(Optional.of(value), this.type).orElseThrow(IllegalStateException::new));
            return value;
        }

        @Override
        public Object setLastModifiedBy(Object value) {
            this.auditable.setLastModifiedBy(value);
            return value;
        }

        @Override
        public Optional<TemporalAccessor> getLastModifiedDate() {
            return this.getAsTemporalAccessor(this.auditable.getLastModifiedDate(), TemporalAccessor.class);
        }

        @Override
        public TemporalAccessor setLastModifiedDate(TemporalAccessor value) {
            this.auditable.setLastModifiedDate(this.getAsTemporalAccessor(Optional.of(value), this.type).orElseThrow(IllegalStateException::new));
            return value;
        }

        @Override
        public Auditable<Object, ?, TemporalAccessor> getBean() {
            return this.auditable;
        }
    }
}

