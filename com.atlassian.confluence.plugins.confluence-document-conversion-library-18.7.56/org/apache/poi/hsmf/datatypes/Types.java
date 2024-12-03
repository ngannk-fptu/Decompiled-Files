/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hsmf.datatypes;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.poi.util.StringUtil;

public final class Types {
    private static final Map<Integer, MAPIType> builtInTypes = new HashMap<Integer, MAPIType>();
    private static final Map<Integer, MAPIType> customTypes = new HashMap<Integer, MAPIType>();
    public static final MAPIType UNSPECIFIED = new MAPIType(0, "Unspecified", -1);
    public static final MAPIType UNKNOWN = new MAPIType(-1, "Unknown", -1);
    public static final MAPIType NULL = new MAPIType(1, "Null", 0);
    public static final MAPIType SHORT = new MAPIType(2, "Short", 2);
    public static final MAPIType LONG = new MAPIType(3, "Long", 4);
    public static final MAPIType FLOAT = new MAPIType(4, "Float", 4);
    public static final MAPIType DOUBLE = new MAPIType(5, "Double", 8);
    public static final MAPIType CURRENCY = new MAPIType(6, "Currency", 8);
    public static final MAPIType APP_TIME = new MAPIType(7, "Application Time", 8);
    public static final MAPIType ERROR = new MAPIType(10, "Error", 4);
    public static final MAPIType BOOLEAN = new MAPIType(11, "Boolean", 2);
    public static final MAPIType DIRECTORY = new MAPIType(13, "Directory", -1);
    public static final MAPIType LONG_LONG = new MAPIType(20, "Long Long", 8);
    public static final MAPIType TIME = new MAPIType(64, "Time", 8);
    public static final MAPIType CLS_ID = new MAPIType(72, "CLS ID GUID", 16);
    public static final MAPIType BINARY = new MAPIType(258, "Binary", -1);
    public static final MAPIType ASCII_STRING = new MAPIType(30, "ASCII String", -1);
    public static final MAPIType UNICODE_STRING = new MAPIType(31, "Unicode String", -1);
    public static final int MULTIVALUED_FLAG = 4096;

    public static MAPIType getById(int typeId) {
        return builtInTypes.get(typeId);
    }

    public static String asFileEnding(int type) {
        StringBuilder str = new StringBuilder(Integer.toHexString(type).toUpperCase(Locale.ROOT));
        int need0count = 4 - str.length();
        if (need0count > 0) {
            str.insert(0, StringUtil.repeat('0', need0count));
        }
        return str.toString();
    }

    public static String asName(int typeId) {
        MAPIType type = builtInTypes.get(typeId);
        if (type != null) {
            return type.name;
        }
        return Types.asCustomName(typeId);
    }

    private static String asCustomName(int typeId) {
        return "0x" + Integer.toHexString(typeId);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static MAPIType createCustom(int typeId) {
        if (Types.getById(typeId) != null) {
            return Types.getById(typeId);
        }
        MAPIType type = customTypes.get(typeId);
        if (type == null) {
            Map<Integer, MAPIType> map = customTypes;
            synchronized (map) {
                type = customTypes.get(typeId);
                if (type == null) {
                    type = new MAPIType(typeId, -1);
                }
            }
        }
        return type;
    }

    public static final class MAPIType {
        private final int id;
        private final String name;
        private final int length;

        private MAPIType(int id, String name, int length) {
            this.id = id;
            this.name = name;
            this.length = length;
            builtInTypes.put(id, this);
        }

        private MAPIType(int id, int length) {
            this.id = id;
            this.name = Types.asCustomName(id);
            this.length = length;
            customTypes.put(id, this);
        }

        public int getLength() {
            return this.length;
        }

        public boolean isFixedLength() {
            return this.length != -1 && this.length <= 8 || this.id == Types.CLS_ID.id;
        }

        public int getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.id + " / 0x" + this.asFileEnding() + " - " + this.name + " @ " + this.length;
        }

        public String asFileEnding() {
            return Types.asFileEnding(this.id);
        }
    }
}

