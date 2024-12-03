/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.sql.Time;
import java.util.Date;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.JdbcTimeTypeDescriptor;
import org.hibernate.type.descriptor.sql.TimeTypeDescriptor;

public class TimeType
extends AbstractSingleColumnStandardBasicType<Date>
implements LiteralType<Date> {
    public static final TimeType INSTANCE = new TimeType();

    public TimeType() {
        super(TimeTypeDescriptor.INSTANCE, JdbcTimeTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "time";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), Time.class.getName()};
    }

    @Override
    public String objectToSQLString(Date value, Dialect dialect) throws Exception {
        Time jdbcTime = Time.class.isInstance(value) ? (Time)value : new Time(value.getTime());
        return StringType.INSTANCE.objectToSQLString(jdbcTime.toString(), dialect);
    }
}

