/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.BaseDVFactory;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.util.SymbolHash;

public class FullDVFactory
extends BaseDVFactory {
    static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
    static SymbolHash fFullTypes = new SymbolHash(89);

    @Override
    public XSSimpleType getBuiltInType(String string) {
        return (XSSimpleType)fFullTypes.get(string);
    }

    @Override
    public SymbolHash getBuiltInTypes() {
        return fFullTypes.makeClone();
    }

    static void createBuiltInTypes(SymbolHash symbolHash) {
        BaseDVFactory.createBuiltInTypes(symbolHash);
        XSFacets xSFacets = new XSFacets();
        XSSimpleTypeDecl xSSimpleTypeDecl = XSSimpleTypeDecl.fAnySimpleType;
        XSSimpleTypeDecl xSSimpleTypeDecl2 = (XSSimpleTypeDecl)symbolHash.get("string");
        symbolHash.put("float", new XSSimpleTypeDecl(xSSimpleTypeDecl, "float", 4, 1, true, true, true, true, 5));
        symbolHash.put("double", new XSSimpleTypeDecl(xSSimpleTypeDecl, "double", 5, 1, true, true, true, true, 6));
        symbolHash.put("duration", new XSSimpleTypeDecl(xSSimpleTypeDecl, "duration", 6, 1, false, false, false, true, 7));
        symbolHash.put("hexBinary", new XSSimpleTypeDecl(xSSimpleTypeDecl, "hexBinary", 15, 0, false, false, false, true, 16));
        symbolHash.put("QName", new XSSimpleTypeDecl(xSSimpleTypeDecl, "QName", 18, 0, false, false, false, true, 19));
        symbolHash.put("NOTATION", new XSSimpleTypeDecl(xSSimpleTypeDecl, "NOTATION", 20, 0, false, false, false, true, 20));
        xSFacets.whiteSpace = 1;
        XSSimpleTypeDecl xSSimpleTypeDecl3 = new XSSimpleTypeDecl(xSSimpleTypeDecl2, "normalizedString", URI_SCHEMAFORSCHEMA, 0, false, null, 21);
        xSSimpleTypeDecl3.applyFacets1(xSFacets, (short)16, (short)0);
        symbolHash.put("normalizedString", xSSimpleTypeDecl3);
        xSFacets.whiteSpace = (short)2;
        XSSimpleTypeDecl xSSimpleTypeDecl4 = new XSSimpleTypeDecl(xSSimpleTypeDecl3, "token", URI_SCHEMAFORSCHEMA, 0, false, null, 22);
        xSSimpleTypeDecl4.applyFacets1(xSFacets, (short)16, (short)0);
        symbolHash.put("token", xSSimpleTypeDecl4);
        xSFacets.whiteSpace = (short)2;
        xSFacets.pattern = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
        XSSimpleTypeDecl xSSimpleTypeDecl5 = new XSSimpleTypeDecl(xSSimpleTypeDecl4, "language", URI_SCHEMAFORSCHEMA, 0, false, null, 23);
        xSSimpleTypeDecl5.applyFacets1(xSFacets, (short)24, (short)0);
        symbolHash.put("language", xSSimpleTypeDecl5);
        xSFacets.whiteSpace = (short)2;
        XSSimpleTypeDecl xSSimpleTypeDecl6 = new XSSimpleTypeDecl(xSSimpleTypeDecl4, "Name", URI_SCHEMAFORSCHEMA, 0, false, null, 25);
        xSSimpleTypeDecl6.applyFacets1(xSFacets, (short)16, (short)0, (short)2);
        symbolHash.put("Name", xSSimpleTypeDecl6);
        xSFacets.whiteSpace = (short)2;
        XSSimpleTypeDecl xSSimpleTypeDecl7 = new XSSimpleTypeDecl(xSSimpleTypeDecl6, "NCName", URI_SCHEMAFORSCHEMA, 0, false, null, 26);
        xSSimpleTypeDecl7.applyFacets1(xSFacets, (short)16, (short)0, (short)3);
        symbolHash.put("NCName", xSSimpleTypeDecl7);
        symbolHash.put("ID", new XSSimpleTypeDecl(xSSimpleTypeDecl7, "ID", 21, 0, false, false, false, true, 27));
        XSSimpleTypeDecl xSSimpleTypeDecl8 = new XSSimpleTypeDecl(xSSimpleTypeDecl7, "IDREF", 22, 0, false, false, false, true, 28);
        symbolHash.put("IDREF", xSSimpleTypeDecl8);
        xSFacets.minLength = 1;
        XSSimpleTypeDecl xSSimpleTypeDecl9 = new XSSimpleTypeDecl(null, URI_SCHEMAFORSCHEMA, 0, xSSimpleTypeDecl8, true, null);
        XSSimpleTypeDecl xSSimpleTypeDecl10 = new XSSimpleTypeDecl(xSSimpleTypeDecl9, "IDREFS", URI_SCHEMAFORSCHEMA, 0, false, null);
        xSSimpleTypeDecl10.applyFacets1(xSFacets, (short)2, (short)0);
        symbolHash.put("IDREFS", xSSimpleTypeDecl10);
        XSSimpleTypeDecl xSSimpleTypeDecl11 = new XSSimpleTypeDecl(xSSimpleTypeDecl7, "ENTITY", 23, 0, false, false, false, true, 29);
        symbolHash.put("ENTITY", xSSimpleTypeDecl11);
        xSFacets.minLength = 1;
        xSSimpleTypeDecl9 = new XSSimpleTypeDecl(null, URI_SCHEMAFORSCHEMA, 0, xSSimpleTypeDecl11, true, null);
        XSSimpleTypeDecl xSSimpleTypeDecl12 = new XSSimpleTypeDecl(xSSimpleTypeDecl9, "ENTITIES", URI_SCHEMAFORSCHEMA, 0, false, null);
        xSSimpleTypeDecl12.applyFacets1(xSFacets, (short)2, (short)0);
        symbolHash.put("ENTITIES", xSSimpleTypeDecl12);
        xSFacets.whiteSpace = (short)2;
        XSSimpleTypeDecl xSSimpleTypeDecl13 = new XSSimpleTypeDecl(xSSimpleTypeDecl4, "NMTOKEN", URI_SCHEMAFORSCHEMA, 0, false, null, 24);
        xSSimpleTypeDecl13.applyFacets1(xSFacets, (short)16, (short)0, (short)1);
        symbolHash.put("NMTOKEN", xSSimpleTypeDecl13);
        xSFacets.minLength = 1;
        xSSimpleTypeDecl9 = new XSSimpleTypeDecl(null, URI_SCHEMAFORSCHEMA, 0, xSSimpleTypeDecl13, true, null);
        XSSimpleTypeDecl xSSimpleTypeDecl14 = new XSSimpleTypeDecl(xSSimpleTypeDecl9, "NMTOKENS", URI_SCHEMAFORSCHEMA, 0, false, null);
        xSSimpleTypeDecl14.applyFacets1(xSFacets, (short)2, (short)0);
        symbolHash.put("NMTOKENS", xSSimpleTypeDecl14);
    }

    static {
        FullDVFactory.createBuiltInTypes(fFullTypes);
    }
}

