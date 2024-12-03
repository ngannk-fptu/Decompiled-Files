/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.SchemaDVFactory;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.xs.XSObjectList;

public class BaseDVFactory
extends SchemaDVFactory {
    static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
    static SymbolHash fBaseTypes = new SymbolHash(53);

    @Override
    public XSSimpleType getBuiltInType(String string) {
        return (XSSimpleType)fBaseTypes.get(string);
    }

    @Override
    public SymbolHash getBuiltInTypes() {
        return fBaseTypes.makeClone();
    }

    @Override
    public XSSimpleType createTypeRestriction(String string, String string2, short s, XSSimpleType xSSimpleType, XSObjectList xSObjectList) {
        return new XSSimpleTypeDecl((XSSimpleTypeDecl)xSSimpleType, string, string2, s, false, xSObjectList);
    }

    @Override
    public XSSimpleType createTypeList(String string, String string2, short s, XSSimpleType xSSimpleType, XSObjectList xSObjectList) {
        return new XSSimpleTypeDecl(string, string2, s, (XSSimpleTypeDecl)xSSimpleType, false, xSObjectList);
    }

    @Override
    public XSSimpleType createTypeUnion(String string, String string2, short s, XSSimpleType[] xSSimpleTypeArray, XSObjectList xSObjectList) {
        int n = xSSimpleTypeArray.length;
        XSSimpleTypeDecl[] xSSimpleTypeDeclArray = new XSSimpleTypeDecl[n];
        System.arraycopy(xSSimpleTypeArray, 0, xSSimpleTypeDeclArray, 0, n);
        return new XSSimpleTypeDecl(string, string2, s, xSSimpleTypeDeclArray, xSObjectList);
    }

    static void createBuiltInTypes(SymbolHash symbolHash) {
        XSFacets xSFacets = new XSFacets();
        XSSimpleTypeDecl xSSimpleTypeDecl = XSSimpleTypeDecl.fAnySimpleType;
        symbolHash.put("anySimpleType", xSSimpleTypeDecl);
        XSSimpleTypeDecl xSSimpleTypeDecl2 = new XSSimpleTypeDecl(xSSimpleTypeDecl, "string", 1, 0, false, false, false, true, 2);
        symbolHash.put("string", xSSimpleTypeDecl2);
        symbolHash.put("boolean", new XSSimpleTypeDecl(xSSimpleTypeDecl, "boolean", 2, 0, false, true, false, true, 3));
        XSSimpleTypeDecl xSSimpleTypeDecl3 = new XSSimpleTypeDecl(xSSimpleTypeDecl, "decimal", 3, 2, false, false, true, true, 4);
        symbolHash.put("decimal", xSSimpleTypeDecl3);
        symbolHash.put("anyURI", new XSSimpleTypeDecl(xSSimpleTypeDecl, "anyURI", 17, 0, false, false, false, true, 18));
        symbolHash.put("base64Binary", new XSSimpleTypeDecl(xSSimpleTypeDecl, "base64Binary", 16, 0, false, false, false, true, 17));
        symbolHash.put("dateTime", new XSSimpleTypeDecl(xSSimpleTypeDecl, "dateTime", 7, 1, false, false, false, true, 8));
        symbolHash.put("time", new XSSimpleTypeDecl(xSSimpleTypeDecl, "time", 8, 1, false, false, false, true, 9));
        symbolHash.put("date", new XSSimpleTypeDecl(xSSimpleTypeDecl, "date", 9, 1, false, false, false, true, 10));
        symbolHash.put("gYearMonth", new XSSimpleTypeDecl(xSSimpleTypeDecl, "gYearMonth", 10, 1, false, false, false, true, 11));
        symbolHash.put("gYear", new XSSimpleTypeDecl(xSSimpleTypeDecl, "gYear", 11, 1, false, false, false, true, 12));
        symbolHash.put("gMonthDay", new XSSimpleTypeDecl(xSSimpleTypeDecl, "gMonthDay", 12, 1, false, false, false, true, 13));
        symbolHash.put("gDay", new XSSimpleTypeDecl(xSSimpleTypeDecl, "gDay", 13, 1, false, false, false, true, 14));
        symbolHash.put("gMonth", new XSSimpleTypeDecl(xSSimpleTypeDecl, "gMonth", 14, 1, false, false, false, true, 15));
        XSSimpleTypeDecl xSSimpleTypeDecl4 = new XSSimpleTypeDecl(xSSimpleTypeDecl3, "integer", 24, 2, false, false, true, true, 30);
        symbolHash.put("integer", xSSimpleTypeDecl4);
        xSFacets.maxInclusive = "0";
        XSSimpleTypeDecl xSSimpleTypeDecl5 = new XSSimpleTypeDecl(xSSimpleTypeDecl4, "nonPositiveInteger", URI_SCHEMAFORSCHEMA, 0, false, null, 31);
        xSSimpleTypeDecl5.applyFacets1(xSFacets, (short)32, (short)0);
        symbolHash.put("nonPositiveInteger", xSSimpleTypeDecl5);
        xSFacets.maxInclusive = "-1";
        XSSimpleTypeDecl xSSimpleTypeDecl6 = new XSSimpleTypeDecl(xSSimpleTypeDecl5, "negativeInteger", URI_SCHEMAFORSCHEMA, 0, false, null, 32);
        xSSimpleTypeDecl6.applyFacets1(xSFacets, (short)32, (short)0);
        symbolHash.put("negativeInteger", xSSimpleTypeDecl6);
        xSFacets.maxInclusive = "9223372036854775807";
        xSFacets.minInclusive = "-9223372036854775808";
        XSSimpleTypeDecl xSSimpleTypeDecl7 = new XSSimpleTypeDecl(xSSimpleTypeDecl4, "long", URI_SCHEMAFORSCHEMA, 0, false, null, 33);
        xSSimpleTypeDecl7.applyFacets1(xSFacets, (short)288, (short)0);
        symbolHash.put("long", xSSimpleTypeDecl7);
        xSFacets.maxInclusive = "2147483647";
        xSFacets.minInclusive = "-2147483648";
        XSSimpleTypeDecl xSSimpleTypeDecl8 = new XSSimpleTypeDecl(xSSimpleTypeDecl7, "int", URI_SCHEMAFORSCHEMA, 0, false, null, 34);
        xSSimpleTypeDecl8.applyFacets1(xSFacets, (short)288, (short)0);
        symbolHash.put("int", xSSimpleTypeDecl8);
        xSFacets.maxInclusive = "32767";
        xSFacets.minInclusive = "-32768";
        XSSimpleTypeDecl xSSimpleTypeDecl9 = new XSSimpleTypeDecl(xSSimpleTypeDecl8, "short", URI_SCHEMAFORSCHEMA, 0, false, null, 35);
        xSSimpleTypeDecl9.applyFacets1(xSFacets, (short)288, (short)0);
        symbolHash.put("short", xSSimpleTypeDecl9);
        xSFacets.maxInclusive = "127";
        xSFacets.minInclusive = "-128";
        XSSimpleTypeDecl xSSimpleTypeDecl10 = new XSSimpleTypeDecl(xSSimpleTypeDecl9, "byte", URI_SCHEMAFORSCHEMA, 0, false, null, 36);
        xSSimpleTypeDecl10.applyFacets1(xSFacets, (short)288, (short)0);
        symbolHash.put("byte", xSSimpleTypeDecl10);
        xSFacets.minInclusive = "0";
        XSSimpleTypeDecl xSSimpleTypeDecl11 = new XSSimpleTypeDecl(xSSimpleTypeDecl4, "nonNegativeInteger", URI_SCHEMAFORSCHEMA, 0, false, null, 37);
        xSSimpleTypeDecl11.applyFacets1(xSFacets, (short)256, (short)0);
        symbolHash.put("nonNegativeInteger", xSSimpleTypeDecl11);
        xSFacets.maxInclusive = "18446744073709551615";
        XSSimpleTypeDecl xSSimpleTypeDecl12 = new XSSimpleTypeDecl(xSSimpleTypeDecl11, "unsignedLong", URI_SCHEMAFORSCHEMA, 0, false, null, 38);
        xSSimpleTypeDecl12.applyFacets1(xSFacets, (short)32, (short)0);
        symbolHash.put("unsignedLong", xSSimpleTypeDecl12);
        xSFacets.maxInclusive = "4294967295";
        XSSimpleTypeDecl xSSimpleTypeDecl13 = new XSSimpleTypeDecl(xSSimpleTypeDecl12, "unsignedInt", URI_SCHEMAFORSCHEMA, 0, false, null, 39);
        xSSimpleTypeDecl13.applyFacets1(xSFacets, (short)32, (short)0);
        symbolHash.put("unsignedInt", xSSimpleTypeDecl13);
        xSFacets.maxInclusive = "65535";
        XSSimpleTypeDecl xSSimpleTypeDecl14 = new XSSimpleTypeDecl(xSSimpleTypeDecl13, "unsignedShort", URI_SCHEMAFORSCHEMA, 0, false, null, 40);
        xSSimpleTypeDecl14.applyFacets1(xSFacets, (short)32, (short)0);
        symbolHash.put("unsignedShort", xSSimpleTypeDecl14);
        xSFacets.maxInclusive = "255";
        XSSimpleTypeDecl xSSimpleTypeDecl15 = new XSSimpleTypeDecl(xSSimpleTypeDecl14, "unsignedByte", URI_SCHEMAFORSCHEMA, 0, false, null, 41);
        xSSimpleTypeDecl15.applyFacets1(xSFacets, (short)32, (short)0);
        symbolHash.put("unsignedByte", xSSimpleTypeDecl15);
        xSFacets.minInclusive = "1";
        XSSimpleTypeDecl xSSimpleTypeDecl16 = new XSSimpleTypeDecl(xSSimpleTypeDecl11, "positiveInteger", URI_SCHEMAFORSCHEMA, 0, false, null, 42);
        xSSimpleTypeDecl16.applyFacets1(xSFacets, (short)256, (short)0);
        symbolHash.put("positiveInteger", xSSimpleTypeDecl16);
    }

    static {
        BaseDVFactory.createBuiltInTypes(fBaseTypes);
    }
}

