/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.BaseSchemaDVFactory;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.util.SymbolHash;

public class SchemaDVFactoryImpl
extends BaseSchemaDVFactory {
    static final SymbolHash fBuiltInTypes = new SymbolHash();

    static void createBuiltInTypes() {
        SchemaDVFactoryImpl.createBuiltInTypes(fBuiltInTypes, XSSimpleTypeDecl.fAnySimpleType);
    }

    @Override
    public XSSimpleType getBuiltInType(String string) {
        return (XSSimpleType)fBuiltInTypes.get(string);
    }

    @Override
    public SymbolHash getBuiltInTypes() {
        return fBuiltInTypes.makeClone();
    }

    static {
        SchemaDVFactoryImpl.createBuiltInTypes();
    }
}

