/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.descriptor.java.LocalTimeJavaDescriptor;
import org.hibernate.type.descriptor.sql.TimeTypeDescriptor;

public class LocalTimeType
extends AbstractSingleColumnStandardBasicType<LocalTime>
implements LiteralType<LocalTime> {
    public static final LocalTimeType INSTANCE = new LocalTimeType();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ENGLISH);

    public LocalTimeType() {
        super(TimeTypeDescriptor.INSTANCE, LocalTimeJavaDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return LocalTime.class.getSimpleName();
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public String objectToSQLString(LocalTime value, Dialect dialect) throws Exception {
        return "{t '" + FORMATTER.format(value) + "'}";
    }
}

