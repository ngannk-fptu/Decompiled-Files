/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.Date;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.IdentifierType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.JdbcDateTypeDescriptor;
import org.hibernate.type.descriptor.sql.DateTypeDescriptor;

public class DateType
extends AbstractSingleColumnStandardBasicType<Date>
implements IdentifierType<Date>,
LiteralType<Date> {
    public static final DateType INSTANCE = new DateType();

    public DateType() {
        super(DateTypeDescriptor.INSTANCE, JdbcDateTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "date";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), java.sql.Date.class.getName()};
    }

    @Override
    public String objectToSQLString(Date value, Dialect dialect) throws Exception {
        java.sql.Date jdbcDate = java.sql.Date.class.isInstance(value) ? (java.sql.Date)value : new java.sql.Date(value.getTime());
        return StringType.INSTANCE.objectToSQLString(jdbcDate.toString(), dialect);
    }

    @Override
    public Date stringToObject(String xml) {
        return (Date)this.fromString(xml);
    }
}

