/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type.descriptor.java;

import java.io.Serializable;
import java.util.UUID;
import org.hibernate.internal.util.BytesHelper;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.spi.JdbcRecommendedSqlTypeMappingContext;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

public class UUIDTypeDescriptor
extends AbstractTypeDescriptor<UUID> {
    public static final UUIDTypeDescriptor INSTANCE = new UUIDTypeDescriptor();

    public UUIDTypeDescriptor() {
        super(UUID.class);
    }

    @Override
    public String toString(UUID value) {
        return ToStringTransformer.INSTANCE.transform(value);
    }

    @Override
    public UUID fromString(String string) {
        return ToStringTransformer.INSTANCE.parse(string);
    }

    @Override
    public SqlTypeDescriptor getJdbcRecommendedSqlType(JdbcRecommendedSqlTypeMappingContext context) {
        return context.getTypeConfiguration().getSqlTypeDescriptorRegistry().getDescriptor(12);
    }

    @Override
    public <X> X unwrap(UUID value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (UUID.class.isAssignableFrom(type)) {
            return (X)PassThroughTransformer.INSTANCE.transform(value);
        }
        if (String.class.isAssignableFrom(type)) {
            return (X)ToStringTransformer.INSTANCE.transform(value);
        }
        if (byte[].class.isAssignableFrom(type)) {
            return (X)ToBytesTransformer.INSTANCE.transform(value);
        }
        throw this.unknownUnwrap(type);
    }

    @Override
    public <X> UUID wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (UUID.class.isInstance(value)) {
            return PassThroughTransformer.INSTANCE.parse(value);
        }
        if (String.class.isInstance(value)) {
            return ToStringTransformer.INSTANCE.parse(value);
        }
        if (byte[].class.isInstance(value)) {
            return ToBytesTransformer.INSTANCE.parse(value);
        }
        throw this.unknownWrap(value.getClass());
    }

    public static class ToBytesTransformer
    implements ValueTransformer {
        public static final ToBytesTransformer INSTANCE = new ToBytesTransformer();

        public byte[] transform(UUID uuid) {
            byte[] bytes = new byte[16];
            BytesHelper.fromLong(uuid.getMostSignificantBits(), bytes, 0);
            BytesHelper.fromLong(uuid.getLeastSignificantBits(), bytes, 8);
            return bytes;
        }

        @Override
        public UUID parse(Object value) {
            byte[] bytea = (byte[])value;
            return new UUID(BytesHelper.asLong(bytea, 0), BytesHelper.asLong(bytea, 8));
        }
    }

    public static class ToStringTransformer
    implements ValueTransformer {
        public static final ToStringTransformer INSTANCE = new ToStringTransformer();

        public String transform(UUID uuid) {
            return uuid.toString();
        }

        @Override
        public UUID parse(Object value) {
            return UUID.fromString((String)value);
        }
    }

    public static class PassThroughTransformer
    implements ValueTransformer {
        public static final PassThroughTransformer INSTANCE = new PassThroughTransformer();

        @Override
        public UUID transform(UUID uuid) {
            return uuid;
        }

        @Override
        public UUID parse(Object value) {
            return (UUID)value;
        }
    }

    public static interface ValueTransformer {
        public Serializable transform(UUID var1);

        public UUID parse(Object var1);
    }
}

