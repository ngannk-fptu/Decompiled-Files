/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv;

import org.apache.xerces.impl.dv.DVFactoryException;
import org.apache.xerces.impl.dv.ObjectFactory;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.xs.XSObjectList;

public abstract class SchemaDVFactory {
    private static final String DEFAULT_FACTORY_CLASS = "org.apache.xerces.impl.dv.xs.SchemaDVFactoryImpl";

    public static final SchemaDVFactory getInstance() throws DVFactoryException {
        return SchemaDVFactory.getInstance(DEFAULT_FACTORY_CLASS);
    }

    public static final SchemaDVFactory getInstance(String string) throws DVFactoryException {
        try {
            return (SchemaDVFactory)ObjectFactory.newInstance(string, ObjectFactory.findClassLoader(), true);
        }
        catch (ClassCastException classCastException) {
            throw new DVFactoryException("Schema factory class " + string + " does not extend from SchemaDVFactory.");
        }
    }

    protected SchemaDVFactory() {
    }

    public abstract XSSimpleType getBuiltInType(String var1);

    public abstract SymbolHash getBuiltInTypes();

    public abstract XSSimpleType createTypeRestriction(String var1, String var2, short var3, XSSimpleType var4, XSObjectList var5);

    public abstract XSSimpleType createTypeList(String var1, String var2, short var3, XSSimpleType var4, XSObjectList var5);

    public abstract XSSimpleType createTypeUnion(String var1, String var2, short var3, XSSimpleType[] var4, XSObjectList var5);
}

