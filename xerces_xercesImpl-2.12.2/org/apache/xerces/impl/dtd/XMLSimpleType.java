/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

import org.apache.xerces.impl.dv.DatatypeValidator;

public class XMLSimpleType {
    public static final short TYPE_CDATA = 0;
    public static final short TYPE_ENTITY = 1;
    public static final short TYPE_ENUMERATION = 2;
    public static final short TYPE_ID = 3;
    public static final short TYPE_IDREF = 4;
    public static final short TYPE_NMTOKEN = 5;
    public static final short TYPE_NOTATION = 6;
    public static final short TYPE_NAMED = 7;
    public static final short DEFAULT_TYPE_DEFAULT = 3;
    public static final short DEFAULT_TYPE_FIXED = 1;
    public static final short DEFAULT_TYPE_IMPLIED = 0;
    public static final short DEFAULT_TYPE_REQUIRED = 2;
    public short type;
    public String name;
    public String[] enumeration;
    public boolean list;
    public short defaultType;
    public String defaultValue;
    public String nonNormalizedDefaultValue;
    public DatatypeValidator datatypeValidator;

    public void setValues(short s, String string, String[] stringArray, boolean bl, short s2, String string2, String string3, DatatypeValidator datatypeValidator) {
        this.type = s;
        this.name = string;
        if (stringArray != null && stringArray.length > 0) {
            this.enumeration = new String[stringArray.length];
            System.arraycopy(stringArray, 0, this.enumeration, 0, this.enumeration.length);
        } else {
            this.enumeration = null;
        }
        this.list = bl;
        this.defaultType = s2;
        this.defaultValue = string2;
        this.nonNormalizedDefaultValue = string3;
        this.datatypeValidator = datatypeValidator;
    }

    public void setValues(XMLSimpleType xMLSimpleType) {
        this.type = xMLSimpleType.type;
        this.name = xMLSimpleType.name;
        if (xMLSimpleType.enumeration != null && xMLSimpleType.enumeration.length > 0) {
            this.enumeration = new String[xMLSimpleType.enumeration.length];
            System.arraycopy(xMLSimpleType.enumeration, 0, this.enumeration, 0, this.enumeration.length);
        } else {
            this.enumeration = null;
        }
        this.list = xMLSimpleType.list;
        this.defaultType = xMLSimpleType.defaultType;
        this.defaultValue = xMLSimpleType.defaultValue;
        this.nonNormalizedDefaultValue = xMLSimpleType.nonNormalizedDefaultValue;
        this.datatypeValidator = xMLSimpleType.datatypeValidator;
    }

    public void clear() {
        this.type = (short)-1;
        this.name = null;
        this.enumeration = null;
        this.list = false;
        this.defaultType = (short)-1;
        this.defaultValue = null;
        this.nonNormalizedDefaultValue = null;
        this.datatypeValidator = null;
    }
}

