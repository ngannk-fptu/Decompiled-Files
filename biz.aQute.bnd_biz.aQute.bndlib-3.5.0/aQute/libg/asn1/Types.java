/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.asn1;

public interface Types {
    public static final int UNIVERSAL = 0;
    public static final int APPLICATION = 0x40000000;
    public static final int CONTEXT = Integer.MIN_VALUE;
    public static final int PRIVATE = -1073741824;
    public static final int CLASSMASK = -1073741824;
    public static final int CONSTRUCTED = 0x20000000;
    public static final int TAGMASK = 0x1FFFFFFF;
    public static final String[] CLASSES = new String[]{"U", "A", "C", "P"};
    public static final int EOC = 0;
    public static final int BOOLEAN = 1;
    public static final int INTEGER = 2;
    public static final int BIT_STRING = 3;
    public static final int OCTET_STRING = 4;
    public static final int NULL = 5;
    public static final int OBJECT_IDENTIFIER = 6;
    public static final int OBJECT_DESCRIPTOR = 7;
    public static final int EXTERNAL = 8;
    public static final int REAL = 9;
    public static final int ENUMERATED = 10;
    public static final int EMBEDDED_PDV = 11;
    public static final int UTF8_STRING = 12;
    public static final int RELATIVE_OID = 13;
    public static final int SEQUENCE = 16;
    public static final int SET = 17;
    public static final int NUMERIC_STRING = 18;
    public static final int PRINTABLE_STRING = 19;
    public static final int T61_STRING = 20;
    public static final int VIDEOTEX_STRING = 21;
    public static final int IA5STRING = 22;
    public static final int UTCTIME = 23;
    public static final int GENERALIZED_TIME = 24;
    public static final int GRAPHIC_STRING = 25;
    public static final int VISIBLE_STRING = 26;
    public static final int GENERAL_STRING = 27;
    public static final int UNIVERSAL_STRING = 28;
    public static final int CHARACTER_STRING = 29;
    public static final int BMP_STRING = 30;
    public static final String[] TAGS = new String[]{"EOC               ", "BOOLEAN           ", "INTEGER           ", "BIT_STRING        ", "OCTET_STRING      ", "NULL              ", "OBJECT_IDENTIFIER ", "OBJECT_DESCRIPTOR ", "EXTERNAL          ", "REAL              ", "ENUMERATED        ", "EMBEDDED_PDV      ", "UTF8_STRING       ", "RELATIVE_OID      ", "?(14)             ", "?(15)             ", "SEQUENCE          ", "SET               ", "NUMERIC_STRING    ", "PRINTABLE_STRING  ", "T61_STRING        ", "VIDEOTEX_STRING   ", "IA5STRING         ", "UTCTIME           ", "GENERALIZED_TIME  ", "GRAPHIC_STRING    ", "VISIBLE_STRING    ", "GENERAL_STRING    ", "UNIVERSAL_STRING  ", "CHARACTER_STRING  ", "BMP_STRING        "};
}

