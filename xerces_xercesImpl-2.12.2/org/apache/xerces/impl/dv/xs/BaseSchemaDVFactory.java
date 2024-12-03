/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.SchemaDVFactory;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.XSDeclarationPool;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.xs.XSObjectList;

public abstract class BaseSchemaDVFactory
extends SchemaDVFactory {
    static final String URI_SCHEMAFORSCHEMA = "http://www.w3.org/2001/XMLSchema";
    protected XSDeclarationPool fDeclPool = null;

    protected static void createBuiltInTypes(SymbolHash symbolHash, XSSimpleTypeDecl xSSimpleTypeDecl) {
        XSFacets xSFacets = new XSFacets();
        symbolHash.put("anySimpleType", XSSimpleTypeDecl.fAnySimpleType);
        XSSimpleTypeDecl xSSimpleTypeDecl2 = new XSSimpleTypeDecl(xSSimpleTypeDecl, "string", 1, 0, false, false, false, true, 2);
        symbolHash.put("string", xSSimpleTypeDecl2);
        symbolHash.put("boolean", new XSSimpleTypeDecl(xSSimpleTypeDecl, "boolean", 2, 0, false, true, false, true, 3));
        XSSimpleTypeDecl xSSimpleTypeDecl3 = new XSSimpleTypeDecl(xSSimpleTypeDecl, "decimal", 3, 2, false, false, true, true, 4);
        symbolHash.put("decimal", xSSimpleTypeDecl3);
        symbolHash.put("anyURI", new XSSimpleTypeDecl(xSSimpleTypeDecl, "anyURI", 17, 0, false, false, false, true, 18));
        symbolHash.put("base64Binary", new XSSimpleTypeDecl(xSSimpleTypeDecl, "base64Binary", 16, 0, false, false, false, true, 17));
        XSSimpleTypeDecl xSSimpleTypeDecl4 = new XSSimpleTypeDecl(xSSimpleTypeDecl, "duration", 6, 1, false, false, false, true, 7);
        symbolHash.put("duration", xSSimpleTypeDecl4);
        symbolHash.put("dateTime", new XSSimpleTypeDecl(xSSimpleTypeDecl, "dateTime", 7, 1, false, false, false, true, 8));
        symbolHash.put("time", new XSSimpleTypeDecl(xSSimpleTypeDecl, "time", 8, 1, false, false, false, true, 9));
        symbolHash.put("date", new XSSimpleTypeDecl(xSSimpleTypeDecl, "date", 9, 1, false, false, false, true, 10));
        symbolHash.put("gYearMonth", new XSSimpleTypeDecl(xSSimpleTypeDecl, "gYearMonth", 10, 1, false, false, false, true, 11));
        symbolHash.put("gYear", new XSSimpleTypeDecl(xSSimpleTypeDecl, "gYear", 11, 1, false, false, false, true, 12));
        symbolHash.put("gMonthDay", new XSSimpleTypeDecl(xSSimpleTypeDecl, "gMonthDay", 12, 1, false, false, false, true, 13));
        symbolHash.put("gDay", new XSSimpleTypeDecl(xSSimpleTypeDecl, "gDay", 13, 1, false, false, false, true, 14));
        symbolHash.put("gMonth", new XSSimpleTypeDecl(xSSimpleTypeDecl, "gMonth", 14, 1, false, false, false, true, 15));
        XSSimpleTypeDecl xSSimpleTypeDecl5 = new XSSimpleTypeDecl(xSSimpleTypeDecl3, "integer", 24, 2, false, false, true, true, 30);
        symbolHash.put("integer", xSSimpleTypeDecl5);
        xSFacets.maxInclusive = "0";
        XSSimpleTypeDecl xSSimpleTypeDecl6 = new XSSimpleTypeDecl(xSSimpleTypeDecl5, "nonPositiveInteger", URI_SCHEMAFORSCHEMA, 0, false, null, 31);
        xSSimpleTypeDecl6.applyFacets1(xSFacets, (short)32, (short)0);
        symbolHash.put("nonPositiveInteger", xSSimpleTypeDecl6);
        xSFacets.maxInclusive = "-1";
        XSSimpleTypeDecl xSSimpleTypeDecl7 = new XSSimpleTypeDecl(xSSimpleTypeDecl6, "negativeInteger", URI_SCHEMAFORSCHEMA, 0, false, null, 32);
        xSSimpleTypeDecl7.applyFacets1(xSFacets, (short)32, (short)0);
        symbolHash.put("negativeInteger", xSSimpleTypeDecl7);
        xSFacets.maxInclusive = "9223372036854775807";
        xSFacets.minInclusive = "-9223372036854775808";
        XSSimpleTypeDecl xSSimpleTypeDecl8 = new XSSimpleTypeDecl(xSSimpleTypeDecl5, "long", URI_SCHEMAFORSCHEMA, 0, false, null, 33);
        xSSimpleTypeDecl8.applyFacets1(xSFacets, (short)288, (short)0);
        symbolHash.put("long", xSSimpleTypeDecl8);
        xSFacets.maxInclusive = "2147483647";
        xSFacets.minInclusive = "-2147483648";
        XSSimpleTypeDecl xSSimpleTypeDecl9 = new XSSimpleTypeDecl(xSSimpleTypeDecl8, "int", URI_SCHEMAFORSCHEMA, 0, false, null, 34);
        xSSimpleTypeDecl9.applyFacets1(xSFacets, (short)288, (short)0);
        symbolHash.put("int", xSSimpleTypeDecl9);
        xSFacets.maxInclusive = "32767";
        xSFacets.minInclusive = "-32768";
        XSSimpleTypeDecl xSSimpleTypeDecl10 = new XSSimpleTypeDecl(xSSimpleTypeDecl9, "short", URI_SCHEMAFORSCHEMA, 0, false, null, 35);
        xSSimpleTypeDecl10.applyFacets1(xSFacets, (short)288, (short)0);
        symbolHash.put("short", xSSimpleTypeDecl10);
        xSFacets.maxInclusive = "127";
        xSFacets.minInclusive = "-128";
        XSSimpleTypeDecl xSSimpleTypeDecl11 = new XSSimpleTypeDecl(xSSimpleTypeDecl10, "byte", URI_SCHEMAFORSCHEMA, 0, false, null, 36);
        xSSimpleTypeDecl11.applyFacets1(xSFacets, (short)288, (short)0);
        symbolHash.put("byte", xSSimpleTypeDecl11);
        xSFacets.minInclusive = "0";
        XSSimpleTypeDecl xSSimpleTypeDecl12 = new XSSimpleTypeDecl(xSSimpleTypeDecl5, "nonNegativeInteger", URI_SCHEMAFORSCHEMA, 0, false, null, 37);
        xSSimpleTypeDecl12.applyFacets1(xSFacets, (short)256, (short)0);
        symbolHash.put("nonNegativeInteger", xSSimpleTypeDecl12);
        xSFacets.maxInclusive = "18446744073709551615";
        XSSimpleTypeDecl xSSimpleTypeDecl13 = new XSSimpleTypeDecl(xSSimpleTypeDecl12, "unsignedLong", URI_SCHEMAFORSCHEMA, 0, false, null, 38);
        xSSimpleTypeDecl13.applyFacets1(xSFacets, (short)32, (short)0);
        symbolHash.put("unsignedLong", xSSimpleTypeDecl13);
        xSFacets.maxInclusive = "4294967295";
        XSSimpleTypeDecl xSSimpleTypeDecl14 = new XSSimpleTypeDecl(xSSimpleTypeDecl13, "unsignedInt", URI_SCHEMAFORSCHEMA, 0, false, null, 39);
        xSSimpleTypeDecl14.applyFacets1(xSFacets, (short)32, (short)0);
        symbolHash.put("unsignedInt", xSSimpleTypeDecl14);
        xSFacets.maxInclusive = "65535";
        XSSimpleTypeDecl xSSimpleTypeDecl15 = new XSSimpleTypeDecl(xSSimpleTypeDecl14, "unsignedShort", URI_SCHEMAFORSCHEMA, 0, false, null, 40);
        xSSimpleTypeDecl15.applyFacets1(xSFacets, (short)32, (short)0);
        symbolHash.put("unsignedShort", xSSimpleTypeDecl15);
        xSFacets.maxInclusive = "255";
        XSSimpleTypeDecl xSSimpleTypeDecl16 = new XSSimpleTypeDecl(xSSimpleTypeDecl15, "unsignedByte", URI_SCHEMAFORSCHEMA, 0, false, null, 41);
        xSSimpleTypeDecl16.applyFacets1(xSFacets, (short)32, (short)0);
        symbolHash.put("unsignedByte", xSSimpleTypeDecl16);
        xSFacets.minInclusive = "1";
        XSSimpleTypeDecl xSSimpleTypeDecl17 = new XSSimpleTypeDecl(xSSimpleTypeDecl12, "positiveInteger", URI_SCHEMAFORSCHEMA, 0, false, null, 42);
        xSSimpleTypeDecl17.applyFacets1(xSFacets, (short)256, (short)0);
        symbolHash.put("positiveInteger", xSSimpleTypeDecl17);
        symbolHash.put("float", new XSSimpleTypeDecl(xSSimpleTypeDecl, "float", 4, 1, true, true, true, true, 5));
        symbolHash.put("double", new XSSimpleTypeDecl(xSSimpleTypeDecl, "double", 5, 1, true, true, true, true, 6));
        symbolHash.put("hexBinary", new XSSimpleTypeDecl(xSSimpleTypeDecl, "hexBinary", 15, 0, false, false, false, true, 16));
        symbolHash.put("NOTATION", new XSSimpleTypeDecl(xSSimpleTypeDecl, "NOTATION", 20, 0, false, false, false, true, 20));
        xSFacets.whiteSpace = 1;
        XSSimpleTypeDecl xSSimpleTypeDecl18 = new XSSimpleTypeDecl(xSSimpleTypeDecl2, "normalizedString", URI_SCHEMAFORSCHEMA, 0, false, null, 21);
        xSSimpleTypeDecl18.applyFacets1(xSFacets, (short)16, (short)0);
        symbolHash.put("normalizedString", xSSimpleTypeDecl18);
        xSFacets.whiteSpace = (short)2;
        XSSimpleTypeDecl xSSimpleTypeDecl19 = new XSSimpleTypeDecl(xSSimpleTypeDecl18, "token", URI_SCHEMAFORSCHEMA, 0, false, null, 22);
        xSSimpleTypeDecl19.applyFacets1(xSFacets, (short)16, (short)0);
        symbolHash.put("token", xSSimpleTypeDecl19);
        xSFacets.whiteSpace = (short)2;
        xSFacets.pattern = "([a-zA-Z]{1,8})(-[a-zA-Z0-9]{1,8})*";
        XSSimpleTypeDecl xSSimpleTypeDecl20 = new XSSimpleTypeDecl(xSSimpleTypeDecl19, "language", URI_SCHEMAFORSCHEMA, 0, false, null, 23);
        xSSimpleTypeDecl20.applyFacets1(xSFacets, (short)24, (short)0);
        symbolHash.put("language", xSSimpleTypeDecl20);
        xSFacets.whiteSpace = (short)2;
        XSSimpleTypeDecl xSSimpleTypeDecl21 = new XSSimpleTypeDecl(xSSimpleTypeDecl19, "Name", URI_SCHEMAFORSCHEMA, 0, false, null, 25);
        xSSimpleTypeDecl21.applyFacets1(xSFacets, (short)16, (short)0, (short)2);
        symbolHash.put("Name", xSSimpleTypeDecl21);
        xSFacets.whiteSpace = (short)2;
        XSSimpleTypeDecl xSSimpleTypeDecl22 = new XSSimpleTypeDecl(xSSimpleTypeDecl21, "NCName", URI_SCHEMAFORSCHEMA, 0, false, null, 26);
        xSSimpleTypeDecl22.applyFacets1(xSFacets, (short)16, (short)0, (short)3);
        symbolHash.put("NCName", xSSimpleTypeDecl22);
        symbolHash.put("QName", new XSSimpleTypeDecl(xSSimpleTypeDecl, "QName", 18, 0, false, false, false, true, 19));
        symbolHash.put("ID", new XSSimpleTypeDecl(xSSimpleTypeDecl22, "ID", 21, 0, false, false, false, true, 27));
        XSSimpleTypeDecl xSSimpleTypeDecl23 = new XSSimpleTypeDecl(xSSimpleTypeDecl22, "IDREF", 22, 0, false, false, false, true, 28);
        symbolHash.put("IDREF", xSSimpleTypeDecl23);
        xSFacets.minLength = 1;
        XSSimpleTypeDecl xSSimpleTypeDecl24 = new XSSimpleTypeDecl(null, URI_SCHEMAFORSCHEMA, 0, xSSimpleTypeDecl23, true, null);
        XSSimpleTypeDecl xSSimpleTypeDecl25 = new XSSimpleTypeDecl(xSSimpleTypeDecl24, "IDREFS", URI_SCHEMAFORSCHEMA, 0, false, null);
        xSSimpleTypeDecl25.applyFacets1(xSFacets, (short)2, (short)0);
        symbolHash.put("IDREFS", xSSimpleTypeDecl25);
        XSSimpleTypeDecl xSSimpleTypeDecl26 = new XSSimpleTypeDecl(xSSimpleTypeDecl22, "ENTITY", 23, 0, false, false, false, true, 29);
        symbolHash.put("ENTITY", xSSimpleTypeDecl26);
        xSFacets.minLength = 1;
        xSSimpleTypeDecl24 = new XSSimpleTypeDecl(null, URI_SCHEMAFORSCHEMA, 0, xSSimpleTypeDecl26, true, null);
        XSSimpleTypeDecl xSSimpleTypeDecl27 = new XSSimpleTypeDecl(xSSimpleTypeDecl24, "ENTITIES", URI_SCHEMAFORSCHEMA, 0, false, null);
        xSSimpleTypeDecl27.applyFacets1(xSFacets, (short)2, (short)0);
        symbolHash.put("ENTITIES", xSSimpleTypeDecl27);
        xSFacets.whiteSpace = (short)2;
        XSSimpleTypeDecl xSSimpleTypeDecl28 = new XSSimpleTypeDecl(xSSimpleTypeDecl19, "NMTOKEN", URI_SCHEMAFORSCHEMA, 0, false, null, 24);
        xSSimpleTypeDecl28.applyFacets1(xSFacets, (short)16, (short)0, (short)1);
        symbolHash.put("NMTOKEN", xSSimpleTypeDecl28);
        xSFacets.minLength = 1;
        xSSimpleTypeDecl24 = new XSSimpleTypeDecl(null, URI_SCHEMAFORSCHEMA, 0, xSSimpleTypeDecl28, true, null);
        XSSimpleTypeDecl xSSimpleTypeDecl29 = new XSSimpleTypeDecl(xSSimpleTypeDecl24, "NMTOKENS", URI_SCHEMAFORSCHEMA, 0, false, null);
        xSSimpleTypeDecl29.applyFacets1(xSFacets, (short)2, (short)0);
        symbolHash.put("NMTOKENS", xSSimpleTypeDecl29);
    }

    @Override
    public XSSimpleType createTypeRestriction(String string, String string2, short s, XSSimpleType xSSimpleType, XSObjectList xSObjectList) {
        if (this.fDeclPool != null) {
            XSSimpleTypeDecl xSSimpleTypeDecl = this.fDeclPool.getSimpleTypeDecl();
            return xSSimpleTypeDecl.setRestrictionValues((XSSimpleTypeDecl)xSSimpleType, string, string2, s, xSObjectList);
        }
        return new XSSimpleTypeDecl((XSSimpleTypeDecl)xSSimpleType, string, string2, s, false, xSObjectList);
    }

    @Override
    public XSSimpleType createTypeList(String string, String string2, short s, XSSimpleType xSSimpleType, XSObjectList xSObjectList) {
        if (this.fDeclPool != null) {
            XSSimpleTypeDecl xSSimpleTypeDecl = this.fDeclPool.getSimpleTypeDecl();
            return xSSimpleTypeDecl.setListValues(string, string2, s, (XSSimpleTypeDecl)xSSimpleType, xSObjectList);
        }
        return new XSSimpleTypeDecl(string, string2, s, (XSSimpleTypeDecl)xSSimpleType, false, xSObjectList);
    }

    @Override
    public XSSimpleType createTypeUnion(String string, String string2, short s, XSSimpleType[] xSSimpleTypeArray, XSObjectList xSObjectList) {
        int n = xSSimpleTypeArray.length;
        XSSimpleTypeDecl[] xSSimpleTypeDeclArray = new XSSimpleTypeDecl[n];
        System.arraycopy(xSSimpleTypeArray, 0, xSSimpleTypeDeclArray, 0, n);
        if (this.fDeclPool != null) {
            XSSimpleTypeDecl xSSimpleTypeDecl = this.fDeclPool.getSimpleTypeDecl();
            return xSSimpleTypeDecl.setUnionValues(string, string2, s, xSSimpleTypeDeclArray, xSObjectList);
        }
        return new XSSimpleTypeDecl(string, string2, s, xSSimpleTypeDeclArray, xSObjectList);
    }

    public void setDeclPool(XSDeclarationPool xSDeclarationPool) {
        this.fDeclPool = xSDeclarationPool;
    }

    public XSSimpleTypeDecl newXSSimpleTypeDecl() {
        return new XSSimpleTypeDecl();
    }
}

