/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.descriptor.java.StringTypeDescriptor;
import org.hibernate.type.descriptor.sql.NVarcharTypeDescriptor;

public class StringNVarcharType
extends AbstractSingleColumnStandardBasicType<String>
implements DiscriminatorType<String> {
    public static final StringNVarcharType INSTANCE = new StringNVarcharType();

    public StringNVarcharType() {
        super(NVarcharTypeDescriptor.INSTANCE, StringTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "nstring";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return false;
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

