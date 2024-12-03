/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 */
package org.hibernate.type.descriptor.java;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.AttributeConverter;
import org.hibernate.HibernateException;
import org.hibernate.annotations.Immutable;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.BigDecimalTypeDescriptor;
import org.hibernate.type.descriptor.java.BigIntegerTypeDescriptor;
import org.hibernate.type.descriptor.java.BlobTypeDescriptor;
import org.hibernate.type.descriptor.java.BooleanTypeDescriptor;
import org.hibernate.type.descriptor.java.ByteArrayTypeDescriptor;
import org.hibernate.type.descriptor.java.ByteTypeDescriptor;
import org.hibernate.type.descriptor.java.CalendarTypeDescriptor;
import org.hibernate.type.descriptor.java.CharacterArrayTypeDescriptor;
import org.hibernate.type.descriptor.java.CharacterTypeDescriptor;
import org.hibernate.type.descriptor.java.ClassTypeDescriptor;
import org.hibernate.type.descriptor.java.ClobTypeDescriptor;
import org.hibernate.type.descriptor.java.CurrencyTypeDescriptor;
import org.hibernate.type.descriptor.java.DateTypeDescriptor;
import org.hibernate.type.descriptor.java.DoubleTypeDescriptor;
import org.hibernate.type.descriptor.java.DurationJavaDescriptor;
import org.hibernate.type.descriptor.java.FloatTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.java.InstantJavaDescriptor;
import org.hibernate.type.descriptor.java.IntegerTypeDescriptor;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.java.JdbcDateTypeDescriptor;
import org.hibernate.type.descriptor.java.JdbcTimeTypeDescriptor;
import org.hibernate.type.descriptor.java.JdbcTimestampTypeDescriptor;
import org.hibernate.type.descriptor.java.LocalDateJavaDescriptor;
import org.hibernate.type.descriptor.java.LocalDateTimeJavaDescriptor;
import org.hibernate.type.descriptor.java.LocaleTypeDescriptor;
import org.hibernate.type.descriptor.java.LongTypeDescriptor;
import org.hibernate.type.descriptor.java.MutabilityPlan;
import org.hibernate.type.descriptor.java.MutableMutabilityPlan;
import org.hibernate.type.descriptor.java.NClobTypeDescriptor;
import org.hibernate.type.descriptor.java.OffsetDateTimeJavaDescriptor;
import org.hibernate.type.descriptor.java.OffsetTimeJavaDescriptor;
import org.hibernate.type.descriptor.java.PrimitiveByteArrayTypeDescriptor;
import org.hibernate.type.descriptor.java.PrimitiveCharacterArrayTypeDescriptor;
import org.hibernate.type.descriptor.java.SerializableTypeDescriptor;
import org.hibernate.type.descriptor.java.ShortTypeDescriptor;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.java.TimeZoneTypeDescriptor;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
import org.hibernate.type.descriptor.java.UrlTypeDescriptor;
import org.hibernate.type.descriptor.java.ZonedDateTimeJavaDescriptor;
import org.hibernate.type.descriptor.java.spi.RegistryHelper;

@Deprecated
public class JavaTypeDescriptorRegistry
implements Serializable {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(JavaTypeDescriptorRegistry.class);
    @Deprecated
    public static final JavaTypeDescriptorRegistry INSTANCE = new JavaTypeDescriptorRegistry();
    private ConcurrentHashMap<Class, JavaTypeDescriptor> descriptorsByClass = new ConcurrentHashMap();

    public JavaTypeDescriptorRegistry() {
        this.addDescriptorInternal(ByteTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(BooleanTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(CharacterTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(ShortTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(IntegerTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(LongTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(FloatTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(DoubleTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(BigDecimalTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(BigIntegerTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(StringTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(BlobTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(ClobTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(NClobTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(ByteArrayTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(CharacterArrayTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(PrimitiveByteArrayTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(PrimitiveCharacterArrayTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(DurationJavaDescriptor.INSTANCE);
        this.addDescriptorInternal(InstantJavaDescriptor.INSTANCE);
        this.addDescriptorInternal(LocalDateJavaDescriptor.INSTANCE);
        this.addDescriptorInternal(LocalDateTimeJavaDescriptor.INSTANCE);
        this.addDescriptorInternal(OffsetDateTimeJavaDescriptor.INSTANCE);
        this.addDescriptorInternal(OffsetTimeJavaDescriptor.INSTANCE);
        this.addDescriptorInternal(ZonedDateTimeJavaDescriptor.INSTANCE);
        this.addDescriptorInternal(CalendarTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(DateTypeDescriptor.INSTANCE);
        this.descriptorsByClass.put(Date.class, JdbcDateTypeDescriptor.INSTANCE);
        this.descriptorsByClass.put(Time.class, JdbcTimeTypeDescriptor.INSTANCE);
        this.descriptorsByClass.put(Timestamp.class, JdbcTimestampTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(TimeZoneTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(ClassTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(CurrencyTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(LocaleTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(UrlTypeDescriptor.INSTANCE);
        this.addDescriptorInternal(UUIDTypeDescriptor.INSTANCE);
    }

    private JavaTypeDescriptor addDescriptorInternal(JavaTypeDescriptor descriptor) {
        return this.descriptorsByClass.put(descriptor.getJavaType(), descriptor);
    }

    @Deprecated
    public void addDescriptor(JavaTypeDescriptor descriptor) {
        JavaTypeDescriptor old = this.addDescriptorInternal(descriptor);
        if (old != null) {
            log.debugf("JavaTypeDescriptorRegistry entry replaced : %s -> %s (was %s)", descriptor.getJavaType(), descriptor, old);
        }
    }

    @Deprecated
    public <J> JavaTypeDescriptor<J> getDescriptor(Class<J> cls) {
        return RegistryHelper.INSTANCE.resolveDescriptor(this.descriptorsByClass, cls, () -> {
            if (Serializable.class.isAssignableFrom(cls)) {
                return new SerializableTypeDescriptor(cls);
            }
            if (!AttributeConverter.class.isAssignableFrom(cls)) {
                log.debugf("Could not find matching JavaTypeDescriptor for requested Java class [%s]; using fallback.  This means Hibernate does not know how to perform certain basic operations in relation to this Java type.", cls.getName());
                this.checkEqualsAndHashCode(cls);
            }
            return new FallbackJavaTypeDescriptor(cls);
        });
    }

    private void checkEqualsAndHashCode(Class javaType) {
        if (!ReflectHelper.overridesEquals(javaType) || !ReflectHelper.overridesHashCode(javaType)) {
            log.unknownJavaTypeNoEqualsHashCode(javaType);
        }
    }

    public static class FallbackJavaTypeDescriptor<T>
    extends AbstractTypeDescriptor<T> {
        protected FallbackJavaTypeDescriptor(Class<T> type) {
            super(type, FallbackJavaTypeDescriptor.createMutabilityPlan(type));
        }

        private static <T> MutabilityPlan<T> createMutabilityPlan(final Class<T> type) {
            if (type.isAnnotationPresent(Immutable.class)) {
                return ImmutableMutabilityPlan.INSTANCE;
            }
            return new MutableMutabilityPlan<T>(){

                @Override
                protected T deepCopyNotNull(T value) {
                    throw new HibernateException("Not known how to deep copy value of type: [" + type.getName() + "]");
                }
            };
        }

        @Override
        public String toString(T value) {
            return value == null ? "<null>" : value.toString();
        }

        @Override
        public T fromString(String string) {
            throw new HibernateException("Not known how to convert String to given type [" + this.getJavaType().getName() + "]");
        }

        @Override
        public <X> X unwrap(T value, Class<X> type, WrapperOptions options) {
            return (X)value;
        }

        @Override
        public <X> T wrap(X value, WrapperOptions options) {
            return (T)value;
        }
    }
}

