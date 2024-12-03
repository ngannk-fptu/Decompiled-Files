/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.java.OffsetDateTimeJavaDescriptor;
import org.hibernate.type.descriptor.sql.TimestampTypeDescriptor;

public class OffsetDateTimeType
extends AbstractSingleColumnStandardBasicType<OffsetDateTime>
implements VersionType<OffsetDateTime>,
LiteralType<OffsetDateTime> {
    public static final OffsetDateTimeType INSTANCE = new OffsetDateTimeType();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S xxxxx", Locale.ENGLISH);

    public OffsetDateTimeType() {
        super(TimestampTypeDescriptor.INSTANCE, OffsetDateTimeJavaDescriptor.INSTANCE);
    }

    @Override
    public String objectToSQLString(OffsetDateTime value, Dialect dialect) throws Exception {
        return "{ts '" + FORMATTER.format(value) + "'}";
    }

    @Override
    public OffsetDateTime seed(SharedSessionContractImplementor session) {
        return OffsetDateTime.now();
    }

    @Override
    public OffsetDateTime next(OffsetDateTime current, SharedSessionContractImplementor session) {
        return OffsetDateTime.now();
    }

    @Override
    public Comparator<OffsetDateTime> getComparator() {
        return OffsetDateTime.timeLineOrder();
    }

    @Override
    public String getName() {
        return OffsetDateTime.class.getSimpleName();
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }
}

