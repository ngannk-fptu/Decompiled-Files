/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class StringType
extends AbstractSingleColumnStandardBasicType<String>
implements DiscriminatorType<String> {
    public static final StringType INSTANCE = new StringType();

    public StringType() {
        super(VarcharTypeDescriptor.INSTANCE, StringTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "string";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public String objectToSQLString(String value, Dialect dialect) throws Exception {
        return '\'' + value + '\'';
    }

    @Override
    public String stringToObject(String xml) throws Exception {
        return xml;
    }

    @Override
    public String toString(String value) {
        return value;
    }
}

