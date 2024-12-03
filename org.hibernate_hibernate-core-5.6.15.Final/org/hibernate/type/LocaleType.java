/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.Locale;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.LocaleTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class LocaleType
extends AbstractSingleColumnStandardBasicType<Locale>
implements LiteralType<Locale> {
    public static final LocaleType INSTANCE = new LocaleType();

    public LocaleType() {
        super(VarcharTypeDescriptor.INSTANCE, LocaleTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "locale";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public String objectToSQLString(Locale value, Dialect dialect) throws Exception {
        return StringType.INSTANCE.objectToSQLString(this.toString(value), dialect);
    }
}

