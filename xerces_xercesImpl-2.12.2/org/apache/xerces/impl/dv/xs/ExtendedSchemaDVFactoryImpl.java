/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.BaseSchemaDVFactory;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.util.SymbolHash;

public class ExtendedSchemaDVFactoryImpl
extends BaseSchemaDVFactory {
    static SymbolHash fBuiltInTypes = new SymbolHash();

    static void createBuiltInTypes() {
        ExtendedSchemaDVFactoryImpl.createBuiltInTypes(fBuiltInTypes, XSSimpleTypeDecl.fAnyAtomicType);
        fBuiltInTypes.put("anyAtomicType", XSSimpleTypeDecl.fAnyAtomicType);
        XSSimpleTypeDecl xSSimpleTypeDecl = (XSSimpleTypeDecl)fBuiltInTypes.get("duration");
        fBuiltInTypes.put("yearMonthDuration", new XSSimpleTypeDecl(xSSimpleTypeDecl, "yearMonthDuration", 27, 1, false, false, false, true, 46));
        fBuiltInTypes.put("dayTimeDuration", new XSSimpleTypeDecl(xSSimpleTypeDecl, "dayTimeDuration", 28, 1, false, false, false, true, 47));
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
        ExtendedSchemaDVFactoryImpl.createBuiltInTypes();
    }
}

