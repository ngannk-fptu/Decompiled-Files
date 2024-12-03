/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

public final class PropertyType {
    public static final int STRING = 1;
    public static final int BINARY = 2;
    public static final int LONG = 3;
    public static final int DOUBLE = 4;
    public static final int DATE = 5;
    public static final int BOOLEAN = 6;
    public static final int NAME = 7;
    public static final int PATH = 8;
    public static final int REFERENCE = 9;
    public static final int WEAKREFERENCE = 10;
    public static final int URI = 11;
    public static final int DECIMAL = 12;
    public static final int UNDEFINED = 0;
    public static final String TYPENAME_STRING = "String";
    public static final String TYPENAME_BINARY = "Binary";
    public static final String TYPENAME_LONG = "Long";
    public static final String TYPENAME_DOUBLE = "Double";
    public static final String TYPENAME_DECIMAL = "Decimal";
    public static final String TYPENAME_DATE = "Date";
    public static final String TYPENAME_BOOLEAN = "Boolean";
    public static final String TYPENAME_NAME = "Name";
    public static final String TYPENAME_PATH = "Path";
    public static final String TYPENAME_REFERENCE = "Reference";
    public static final String TYPENAME_WEAKREFERENCE = "WeakReference";
    public static final String TYPENAME_URI = "URI";
    public static final String TYPENAME_UNDEFINED = "undefined";

    public static String nameFromValue(int type) {
        switch (type) {
            case 1: {
                return TYPENAME_STRING;
            }
            case 2: {
                return TYPENAME_BINARY;
            }
            case 6: {
                return TYPENAME_BOOLEAN;
            }
            case 3: {
                return TYPENAME_LONG;
            }
            case 4: {
                return TYPENAME_DOUBLE;
            }
            case 12: {
                return TYPENAME_DECIMAL;
            }
            case 5: {
                return TYPENAME_DATE;
            }
            case 7: {
                return TYPENAME_NAME;
            }
            case 8: {
                return TYPENAME_PATH;
            }
            case 9: {
                return TYPENAME_REFERENCE;
            }
            case 10: {
                return TYPENAME_WEAKREFERENCE;
            }
            case 11: {
                return TYPENAME_URI;
            }
            case 0: {
                return TYPENAME_UNDEFINED;
            }
        }
        throw new IllegalArgumentException("unknown type: " + type);
    }

    public static int valueFromName(String name) {
        if (name.equals(TYPENAME_STRING)) {
            return 1;
        }
        if (name.equals(TYPENAME_BINARY)) {
            return 2;
        }
        if (name.equals(TYPENAME_BOOLEAN)) {
            return 6;
        }
        if (name.equals(TYPENAME_LONG)) {
            return 3;
        }
        if (name.equals(TYPENAME_DOUBLE)) {
            return 4;
        }
        if (name.equals(TYPENAME_DECIMAL)) {
            return 12;
        }
        if (name.equals(TYPENAME_DATE)) {
            return 5;
        }
        if (name.equals(TYPENAME_NAME)) {
            return 7;
        }
        if (name.equals(TYPENAME_PATH)) {
            return 8;
        }
        if (name.equals(TYPENAME_REFERENCE)) {
            return 9;
        }
        if (name.equals(TYPENAME_WEAKREFERENCE)) {
            return 10;
        }
        if (name.equals(TYPENAME_URI)) {
            return 11;
        }
        if (name.equals(TYPENAME_UNDEFINED)) {
            return 0;
        }
        throw new IllegalArgumentException("unknown type: " + name);
    }

    private PropertyType() {
    }
}

