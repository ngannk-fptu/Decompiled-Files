/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.search.parser;

public interface EhcacheSearchParserConstants {
    public static final int EOF = 0;
    public static final int KW_STAR = 1;
    public static final int KW_ALL = 2;
    public static final int KW_COMMA = 3;
    public static final int KW_OPEN_PAREN = 4;
    public static final int KW_CLOSED_PAREN = 5;
    public static final int KW_LSQUARE = 6;
    public static final int KW_RSQUARE = 7;
    public static final int KW_EQ = 8;
    public static final int KW_NE = 9;
    public static final int KW_NULL = 10;
    public static final int KW_NOT_NULL = 11;
    public static final int KW_GE = 12;
    public static final int KW_GT = 13;
    public static final int KW_LE = 14;
    public static final int KW_LT = 15;
    public static final int KW_KEY = 16;
    public static final int KW_VALUE = 17;
    public static final int KW_WHERE = 18;
    public static final int KW_NOT = 19;
    public static final int KW_ISBETWEEN = 20;
    public static final int KW_BETWEEN = 21;
    public static final int KW_ILIKE = 22;
    public static final int KW_LIKE = 23;
    public static final int KW_IN = 24;
    public static final int KW_AND = 25;
    public static final int KW_OR = 26;
    public static final int KW_SUM = 27;
    public static final int KW_MAX = 28;
    public static final int KW_MIN = 29;
    public static final int KW_AVG = 30;
    public static final int KW_COUNT = 31;
    public static final int KW_SELECT = 32;
    public static final int KW_FROM = 33;
    public static final int KW_LIMIT = 34;
    public static final int KW_BOOL_CAST = 35;
    public static final int KW_BYTE_CAST = 36;
    public static final int KW_INT_CAST = 37;
    public static final int KW_SHORT_CAST = 38;
    public static final int KW_LONG_CAST = 39;
    public static final int KW_FLOAT_CAST = 40;
    public static final int KW_DOUBLE_CAST = 41;
    public static final int KW_DATE_CAST = 42;
    public static final int KW_STRING_CAST = 43;
    public static final int KW_SQLDATE_CAST = 44;
    public static final int KW_CHAR_CAST = 45;
    public static final int KW_SHORT_DESC = 46;
    public static final int KW_SHORT_ASC = 47;
    public static final int KW_DESC = 48;
    public static final int KW_ASC = 49;
    public static final int KW_ORDER_BY = 50;
    public static final int KW_USE_CACHE = 51;
    public static final int KW_USE_CACHE_MANAGER = 52;
    public static final int KW_GROUP_BY = 53;
    public static final int FIXEDINT = 54;
    public static final int QUOTEDSTR = 55;
    public static final int STRING = 56;
    public static final int ENUMFQCLASSNAME = 57;
    public static final int FQCLASSNAME = 58;
    public static final int DEFAULT = 0;
    public static final String[] tokenImage = new String[]{"<EOF>", "\"*\"", "\"all\"", "\",\"", "\"(\"", "\")\"", "\"[\"", "\"]\"", "\"=\"", "\"!=\"", "\"IS NULL\"", "\"IS NOT NULL\"", "\">=\"", "\">\"", "\"<=\"", "\"<\"", "\"key\"", "\"value\"", "\"where\"", "\"not\"", "\"isbetween\"", "\"between\"", "\"ilike\"", "\"like\"", "\"in\"", "\"and\"", "\"or\"", "\"sum\"", "\"max\"", "\"min\"", "<KW_AVG>", "\"count\"", "\"select\"", "\"from\"", "\"limit\"", "\"(bool)\"", "\"(byte)\"", "\"(int)\"", "\"(short)\"", "\"(long)\"", "\"(float)\"", "\"(double)\"", "\"(date)\"", "\"(string)\"", "\"(sqldate)\"", "\"(char)\"", "\"desc\"", "\"asc\"", "\"descending\"", "\"ascending\"", "\"order by\"", "\"use cache\"", "\"use cache manager\"", "\"group by\"", "<FIXEDINT>", "<QUOTEDSTR>", "<STRING>", "<ENUMFQCLASSNAME>", "<FQCLASSNAME>", "\" \"", "\"\\t\"", "\"\\n\""};
}

