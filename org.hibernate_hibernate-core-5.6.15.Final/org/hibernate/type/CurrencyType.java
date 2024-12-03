/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.util.Currency;
import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.descriptor.java.CurrencyTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

public class CurrencyType
extends AbstractSingleColumnStandardBasicType<Currency>
implements LiteralType<Currency> {
    public static final CurrencyType INSTANCE = new CurrencyType();

    public CurrencyType() {
        super(VarcharTypeDescriptor.INSTANCE, CurrencyTypeDescriptor.INSTANCE);
    }

    @Override
    public String getName() {
        return "currency";
    }

    @Override
    protected boolean registerUnderJavaType() {
        return true;
    }

    @Override
    public String objectToSQLString(Currency value, Dialect dialect) throws Exception {
        return "'" + this.toString(value) + "'";
    }
}

