/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.time.OffsetTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.descriptor.java.OffsetTimeJavaDescriptor;
import org.hibernate.type.descriptor.sql.TimeTypeDescriptor;

public class OffsetTimeType
extends AbstractSingleColumnStandardBasicType<OffsetTime>
implements LiteralType<OffsetTime> {
    public static final OffsetTimeType INSTANCE = new OffsetTimeType();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.S xxxxx", Locale.ENGLISH);

    public OffsetTimeType() {
        super(TimeTypeDescriptor.INSTANCE, OffsetTimeJavaDescriptor.INSTANCE);
    }

    @Override
    public String objectToSQLString(OffsetTime value, Dialect dialect) throws Exception {
        return "{t '" + FORMATTER.format(value) + "'}";
    }

    @Override
    public String getName() {
        return OffsetTime.class.getSimpleName();
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }
}

