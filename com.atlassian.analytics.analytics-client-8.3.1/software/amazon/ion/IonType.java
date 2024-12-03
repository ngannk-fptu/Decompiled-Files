/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum IonType {
    NULL,
    BOOL,
    INT,
    FLOAT,
    DECIMAL,
    TIMESTAMP,
    SYMBOL,
    STRING,
    CLOB,
    BLOB,
    LIST,
    SEXP,
    STRUCT,
    DATAGRAM;


    public static boolean isContainer(IonType t) {
        return t != null && t.ordinal() >= LIST.ordinal();
    }

    public static boolean isText(IonType t) {
        return t == STRING || t == SYMBOL;
    }

    public static boolean isLob(IonType t) {
        return t == BLOB || t == CLOB;
    }
}

