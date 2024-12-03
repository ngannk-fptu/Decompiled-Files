/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.net.URL;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.UrlTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class UrlType
extends AbstractSingleColumnStandardBasicType<URL>
implements DiscriminatorType<URL> {
    public static final UrlType INSTANCE = new UrlType();

    public UrlType() {
        super(VarcharTypeDescriptor.INSTANCE, UrlTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "url";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public String toString(URL value) {
        return UrlTypeDescriptor.INSTANCE.toString(value);
    }

    @Override
    public String objectToSQLString(URL value, Dialect dialect) throws Exception {
        return StringType.INSTANCE.objectToSQLString(this.toString(value), dialect);
    }

    @Override
    public URL stringToObject(String xml) throws Exception {
        return UrlTypeDescriptor.INSTANCE.fromString(xml);
    }
}

