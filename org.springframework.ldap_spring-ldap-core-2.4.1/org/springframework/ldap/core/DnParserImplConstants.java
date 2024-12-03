/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

public interface DnParserImplConstants {
    public static final int EOF = 0;
    public static final int ALPHA = 1;
    public static final int DIGIT = 2;
    public static final int LEADCHAR = 3;
    public static final int STRINGCHAR = 4;
    public static final int TRAILCHAR = 5;
    public static final int SPECIAL = 6;
    public static final int HEXCHAR = 7;
    public static final int HEXPAIR = 8;
    public static final int BACKSLASHCHAR = 9;
    public static final int PAIR = 10;
    public static final int ESCAPEDSTART = 11;
    public static final int QUOTECHAR = 12;
    public static final int HASHCHAR = 13;
    public static final int ATTRIBUTE_TYPE_STRING = 14;
    public static final int LDAP_OID = 15;
    public static final int SPACE = 16;
    public static final int ATTRVALUE = 17;
    public static final int SPACED_EQUALS = 18;
    public static final int DEFAULT = 0;
    public static final int ATTRVALUE_S = 1;
    public static final int SPACED_EQUALS_S = 2;
    public static final String[] tokenImage = new String[]{"<EOF>", "<ALPHA>", "<DIGIT>", "<LEADCHAR>", "<STRINGCHAR>", "<TRAILCHAR>", "<SPECIAL>", "<HEXCHAR>", "<HEXPAIR>", "\"\\\\\"", "<PAIR>", "<ESCAPEDSTART>", "\"\\\"\"", "\"#\"", "<ATTRIBUTE_TYPE_STRING>", "<LDAP_OID>", "\" \"", "<ATTRVALUE>", "<SPACED_EQUALS>", "\",\"", "\";\"", "\"+\""};
}

