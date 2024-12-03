/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.BooleanTypeDescriptor;
import org.hibernate.type.descriptor.sql.CharTypeDescriptor;

public class TrueFalseType
extends AbstractSingleColumnStandardBasicType<Boolean>
implements PrimitiveType<Boolean>,
DiscriminatorType<Boolean> {
    public static final TrueFalseType INSTANCE = new TrueFalseType();

    public TrueFalseType() {
        super(CharTypeDescriptor.INSTANCE, new BooleanTypeDescriptor('T', 'F'));
    }

    @Override
    public String getName() {
        return "true_false";
    }

    @Override
    public Class getPrimitiveClass() {
        return Boolean.TYPE;
    }

    @Override
    public Boolean stringToObject(String xml) throws Exception {
        return (Boolean)this.fromString(xml);
    }

    @Override
    public Serializable getDefaultValue() {
        return Boolean.FALSE;
    }

    @Override
    public String objectToSQLString(Boolean value, Dialect dialect) throws Exception {
        return StringType.INSTANCE.objectToSQLString(value != false ? "T" : "F", dialect);
    }
}

