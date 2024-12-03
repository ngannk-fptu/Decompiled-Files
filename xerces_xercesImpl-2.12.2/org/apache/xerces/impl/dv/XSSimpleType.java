/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv;

import org.apache.xerces.impl.dv.DatatypeException;
import org.apache.xerces.impl.dv.InvalidDatatypeFacetException;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.XSFacets;
import org.apache.xerces.xs.XSSimpleTypeDefinition;

public interface XSSimpleType
extends XSSimpleTypeDefinition {
    public static final short WS_PRESERVE = 0;
    public static final short WS_REPLACE = 1;
    public static final short WS_COLLAPSE = 2;
    public static final short PRIMITIVE_STRING = 1;
    public static final short PRIMITIVE_BOOLEAN = 2;
    public static final short PRIMITIVE_DECIMAL = 3;
    public static final short PRIMITIVE_FLOAT = 4;
    public static final short PRIMITIVE_DOUBLE = 5;
    public static final short PRIMITIVE_DURATION = 6;
    public static final short PRIMITIVE_DATETIME = 7;
    public static final short PRIMITIVE_TIME = 8;
    public static final short PRIMITIVE_DATE = 9;
    public static final short PRIMITIVE_GYEARMONTH = 10;
    public static final short PRIMITIVE_GYEAR = 11;
    public static final short PRIMITIVE_GMONTHDAY = 12;
    public static final short PRIMITIVE_GDAY = 13;
    public static final short PRIMITIVE_GMONTH = 14;
    public static final short PRIMITIVE_HEXBINARY = 15;
    public static final short PRIMITIVE_BASE64BINARY = 16;
    public static final short PRIMITIVE_ANYURI = 17;
    public static final short PRIMITIVE_QNAME = 18;
    public static final short PRIMITIVE_PRECISIONDECIMAL = 19;
    public static final short PRIMITIVE_NOTATION = 20;

    public short getPrimitiveKind();

    public Object validate(String var1, ValidationContext var2, ValidatedInfo var3) throws InvalidDatatypeValueException;

    public Object validate(Object var1, ValidationContext var2, ValidatedInfo var3) throws InvalidDatatypeValueException;

    public void validate(ValidationContext var1, ValidatedInfo var2) throws InvalidDatatypeValueException;

    public void applyFacets(XSFacets var1, short var2, short var3, ValidationContext var4) throws InvalidDatatypeFacetException;

    public boolean isEqual(Object var1, Object var2);

    public boolean isIDType();

    public short getWhitespace() throws DatatypeException;
}

