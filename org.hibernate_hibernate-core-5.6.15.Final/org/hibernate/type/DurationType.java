/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.time.Duration;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.descriptor.java.DurationJavaDescriptor;
import org.hibernate.type.descriptor.sql.BigIntTypeDescriptor;

public class DurationType
extends AbstractSingleColumnStandardBasicType<Duration>
implements LiteralType<Duration> {
    public static final DurationType INSTANCE = new DurationType();

    public DurationType() {
        super(BigIntTypeDescriptor.INSTANCE, DurationJavaDescriptor.INSTANCE);
    }

    @Override
    public String objectToSQLString(Duration value, Dialect dialect) throws Exception {
        return String.valueOf(value.toNanos());
    }

    @Override
    public String getName() {
        return Duration.class.getSimpleName();
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }
}

