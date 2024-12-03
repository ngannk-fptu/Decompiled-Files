/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.TimeZone;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.TimeZoneTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class TimeZoneType
extends AbstractSingleColumnStandardBasicType<TimeZone>
implements LiteralType<TimeZone> {
    public static final TimeZoneType INSTANCE = new TimeZoneType();

    public TimeZoneType() {
        super(VarcharTypeDescriptor.INSTANCE, TimeZoneTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "timezone";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public String objectToSQLString(TimeZone value, Dialect dialect) throws Exception {
        return StringType.INSTANCE.objectToSQLString(value.getID(), dialect);
    }
}

