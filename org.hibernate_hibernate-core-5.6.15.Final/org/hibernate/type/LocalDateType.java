/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.descriptor.java.LocalDateJavaDescriptor;
import org.hibernate.type.descriptor.sql.DateTypeDescriptor;

public class LocalDateType
extends AbstractSingleColumnStandardBasicType<LocalDate>
implements LiteralType<LocalDate> {
    public static final LocalDateType INSTANCE = new LocalDateType();
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

    public LocalDateType() {
        super(DateTypeDescriptor.INSTANCE, LocalDateJavaDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return LocalDate.class.getSimpleName();
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public String objectToSQLString(LocalDate value, Dialect dialect) throws Exception {
        return "{d '" + FORMATTER.format(value) + "'}";
    }
}

