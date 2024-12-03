/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.hibernate.HibernateException;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.type.AdaptedImmutableType;
import org.hibernate.type.BasicType;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.BigIntegerType;
import org.hibernate.type.BinaryType;
import org.hibernate.type.BlobType;
import org.hibernate.type.BooleanType;
import org.hibernate.type.ByteType;
import org.hibernate.type.CalendarDateType;
import org.hibernate.type.CalendarTimeType;
import org.hibernate.type.CalendarType;
import org.hibernate.type.CharArrayType;
import org.hibernate.type.CharacterArrayType;
import org.hibernate.type.CharacterNCharType;
import org.hibernate.type.CharacterType;
import org.hibernate.type.ClassType;
import org.hibernate.type.ClobType;
import org.hibernate.type.CompositeCustomType;
import org.hibernate.type.CurrencyType;
import org.hibernate.type.CustomType;
import org.hibernate.type.DateType;
import org.hibernate.type.DbTimestampType;
import org.hibernate.type.DoubleType;
import org.hibernate.type.DurationType;
import org.hibernate.type.FloatType;
import org.hibernate.type.ImageType;
import org.hibernate.type.InstantType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LocalDateTimeType;
import org.hibernate.type.LocalDateType;
import org.hibernate.type.LocalTimeType;
import org.hibernate.type.LocaleType;
import org.hibernate.type.LongType;
import org.hibernate.type.MaterializedBlobType;
import org.hibernate.type.MaterializedClobType;
import org.hibernate.type.MaterializedNClobType;
import org.hibernate.type.NClobType;
import org.hibernate.type.NTextType;
import org.hibernate.type.NumericBooleanType;
import org.hibernate.type.ObjectType;
import org.hibernate.type.OffsetDateTimeType;
import org.hibernate.type.OffsetTimeType;
import org.hibernate.type.RowVersionType;
import org.hibernate.type.SerializableType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringNVarcharType;
import org.hibernate.type.StringType;
import org.hibernate.type.TextType;
import org.hibernate.type.TimeType;
import org.hibernate.type.TimeZoneType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.TrueFalseType;
import org.hibernate.type.Type;
import org.hibernate.type.UUIDBinaryType;
import org.hibernate.type.UUIDCharType;
import org.hibernate.type.UrlType;
import org.hibernate.type.WrapperBinaryType;
import org.hibernate.type.YesNoType;
import org.hibernate.type.ZonedDateTimeType;
import org.hibernate.type.spi.TypeConfiguration;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;

public class BasicTypeRegistry
implements Serializable {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(BasicTypeRegistry.class);
    private Map<String, BasicType> registry = new ConcurrentHashMap<String, BasicType>(100, 0.75f, 1);
    private boolean locked;
    private TypeConfiguration typeConfiguration;

    public BasicTypeRegistry(TypeConfiguration typeConfiguration) {
        this();
        this.typeConfiguration = typeConfiguration;
    }

    public BasicTypeRegistry() {
        this.register(BooleanType.INSTANCE);
        this.register(NumericBooleanType.INSTANCE);
        this.register(TrueFalseType.INSTANCE);
        this.register(YesNoType.INSTANCE);
        this.register(ByteType.INSTANCE);
        this.register(CharacterType.INSTANCE);
        this.register(ShortType.INSTANCE);
        this.register(IntegerType.INSTANCE);
        this.register(LongType.INSTANCE);
        this.register(FloatType.INSTANCE);
        this.register(DoubleType.INSTANCE);
        this.register(BigDecimalType.INSTANCE);
        this.register(BigIntegerType.INSTANCE);
        this.register(StringType.INSTANCE);
        this.register(StringNVarcharType.INSTANCE);
        this.register(CharacterNCharType.INSTANCE);
        this.register(UrlType.INSTANCE);
        this.register(DurationType.INSTANCE);
        this.register(InstantType.INSTANCE);
        this.register(LocalDateTimeType.INSTANCE);
        this.register(LocalDateType.INSTANCE);
        this.register(LocalTimeType.INSTANCE);
        this.register(OffsetDateTimeType.INSTANCE);
        this.register(OffsetTimeType.INSTANCE);
        this.register(ZonedDateTimeType.INSTANCE);
        this.register(DateType.INSTANCE);
        this.register(TimeType.INSTANCE);
        this.register(TimestampType.INSTANCE);
        this.register(DbTimestampType.INSTANCE);
        this.register(CalendarType.INSTANCE);
        this.register(CalendarDateType.INSTANCE);
        this.register(CalendarTimeType.INSTANCE);
        this.register(LocaleType.INSTANCE);
        this.register(CurrencyType.INSTANCE);
        this.register(TimeZoneType.INSTANCE);
        this.register(ClassType.INSTANCE);
        this.register(UUIDBinaryType.INSTANCE);
        this.register(UUIDCharType.INSTANCE);
        this.register(BinaryType.INSTANCE);
        this.register(WrapperBinaryType.INSTANCE);
        this.register(RowVersionType.INSTANCE);
        this.register(ImageType.INSTANCE);
        this.register(CharArrayType.INSTANCE);
        this.register(CharacterArrayType.INSTANCE);
        this.register(TextType.INSTANCE);
        this.register(NTextType.INSTANCE);
        this.register(BlobType.INSTANCE);
        this.register(MaterializedBlobType.INSTANCE);
        this.register(ClobType.INSTANCE);
        this.register(NClobType.INSTANCE);
        this.register(MaterializedClobType.INSTANCE);
        this.register(MaterializedNClobType.INSTANCE);
        this.register(SerializableType.INSTANCE);
        this.register(ObjectType.INSTANCE);
        this.register(new AdaptedImmutableType<Date>(DateType.INSTANCE));
        this.register(new AdaptedImmutableType<Date>(TimeType.INSTANCE));
        this.register(new AdaptedImmutableType<Date>(TimestampType.INSTANCE));
        this.register(new AdaptedImmutableType<Date>(DbTimestampType.INSTANCE));
        this.register(new AdaptedImmutableType<Calendar>(CalendarType.INSTANCE));
        this.register(new AdaptedImmutableType<Calendar>(CalendarDateType.INSTANCE));
        this.register(new AdaptedImmutableType<byte[]>(BinaryType.INSTANCE));
        this.register(new AdaptedImmutableType<Serializable>(SerializableType.INSTANCE));
    }

    private BasicTypeRegistry(Map<String, BasicType> registeredTypes) {
        this.registry.putAll(registeredTypes);
        this.locked = true;
    }

    public void register(BasicType type) {
        this.register(type, type.getRegistrationKeys());
    }

    public void register(BasicType type, String[] keys) {
        if (this.locked) {
            throw new HibernateException("Can not alter TypeRegistry at this time");
        }
        if (type == null) {
            throw new HibernateException("Type to register cannot be null");
        }
        if (keys == null || keys.length == 0) {
            LOG.typeDefinedNoRegistrationKeys(type);
            return;
        }
        for (String key : keys) {
            if (key == null) continue;
            key = key.intern();
            LOG.debugf("Adding type registration %s -> %s", key, type);
            Type old = this.registry.put(key, type);
            if (old == null || old == type) continue;
            LOG.typeRegistrationOverridesPrevious(key, old);
        }
    }

    public void register(UserType type, String[] keys) {
        this.register(new CustomType(type, keys));
    }

    public void register(CompositeUserType type, String[] keys) {
        this.register(new CompositeCustomType(type, keys));
    }

    public void unregister(String ... keys) {
        for (String key : keys) {
            this.registry.remove(key);
        }
    }

    public BasicType getRegisteredType(String key) {
        return this.registry.get(key);
    }

    public BasicTypeRegistry shallowCopy() {
        return new BasicTypeRegistry(this.registry);
    }
}

