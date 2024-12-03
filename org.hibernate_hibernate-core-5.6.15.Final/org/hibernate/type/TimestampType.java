/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;
import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.VersionType;
import org.hibernate.type.descriptor.java.JdbcTimestampTypeDescriptor;
import org.hibernate.type.descriptor.sql.TimestampTypeDescriptor;

public class TimestampType
extends AbstractSingleColumnStandardBasicType<Date>
implements VersionType<Date>,
LiteralType<Date> {
    public static final TimestampType INSTANCE = new TimestampType();

    public TimestampType() {
        super(TimestampTypeDescriptor.INSTANCE, JdbcTimestampTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "timestamp";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[]{this.getName(), Timestamp.class.getName(), Date.class.getName()};
    }

    @Override
    public Date next(Date current, SharedSessionContractImplementor session) {
        return this.seed(session);
    }

    @Override
    public Date seed(SharedSessionContractImplementor session) {
        return new Timestamp(System.currentTimeMillis());
    }

    @Override
    public Comparator<Date> getComparator() {
        return this.getJavaTypeDescriptor().getComparator();
    }

    @Override
    public String objectToSQLString(Date value, Dialect dialect) throws Exception {
        Timestamp ts = Timestamp.class.isInstance(value) ? (Timestamp)value : new Timestamp(value.getTime());
        return StringType.INSTANCE.objectToSQLString(ts.toString(), dialect);
    }

    @Override
    public Date fromStringValue(String xml) throws HibernateException {
        return (Date)this.fromString(xml);
    }
}

