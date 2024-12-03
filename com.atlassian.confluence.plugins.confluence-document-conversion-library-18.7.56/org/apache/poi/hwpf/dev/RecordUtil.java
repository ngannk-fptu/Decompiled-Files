/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.dev;

import org.apache.poi.util.Internal;
import org.apache.poi.util.StringUtil;

@Internal
public class RecordUtil {
    public static String getBitFieldFunction(String name, String bitMask, String parentType, String withType) {
        String type = RecordUtil.getBitFieldType(name, bitMask, parentType);
        String retVal = "";
        if (withType.equals("true")) {
            retVal = type + " ";
        }
        retVal = type.equals("boolean") ? retVal + "is" + RecordUtil.getFieldName1stCap(name, 0) : retVal + "get" + RecordUtil.getFieldName1stCap(name, 0);
        return retVal;
    }

    public static String getBitFieldGet(String name, String bitMask, String parentType, String parentField) {
        String type = RecordUtil.getBitFieldType(name, bitMask, parentType);
        String retVal = null;
        retVal = type.equals("boolean") ? name + ".isSet(" + parentField + ");" : "( " + type + " )" + name + ".getValue(" + parentField + ");";
        return retVal;
    }

    public static String getBitFieldSet(String name, String bitMask, String parentType, String parentField) {
        String type = RecordUtil.getBitFieldType(name, bitMask, parentType);
        String retVal = null;
        retVal = type.equals("boolean") ? (parentType.equals("int") ? RecordUtil.getFieldName(name, 0) + ".setBoolean(" + parentField + ", value)" : "(" + parentType + ")" + RecordUtil.getFieldName(name, 0) + ".setBoolean(" + parentField + ", value)") : (parentType.equals("int") ? RecordUtil.getFieldName(name, 0) + ".setValue(" + parentField + ", value)" : "(" + parentType + ")" + RecordUtil.getFieldName(name, 0) + ".setValue(" + parentField + ", value)");
        return retVal;
    }

    public static String getBitFieldType(String name, String bitMask, String parentType) {
        int parentSize = 0;
        int numBits = 0;
        int mask = (int)Long.parseLong(bitMask.substring(2), 16);
        switch (parentType) {
            case "byte": {
                parentSize = 8;
                break;
            }
            case "short": {
                parentSize = 16;
                break;
            }
            case "int": {
                parentSize = 32;
            }
        }
        for (int x = 0; x < parentSize; ++x) {
            numBits = (byte)(numBits + (byte)(mask >> x & 1));
        }
        if (numBits == 1) {
            return "boolean";
        }
        if (numBits < 8) {
            return "byte";
        }
        if (numBits < 16) {
            return "short";
        }
        return "int";
    }

    public static String getConstName(String parentName, String constName, int padTo) {
        StringBuilder fieldName = new StringBuilder();
        RecordUtil.toConstIdentifier(parentName, fieldName);
        fieldName.append('_');
        RecordUtil.toConstIdentifier(constName, fieldName);
        RecordUtil.pad(fieldName, padTo);
        return fieldName.toString();
    }

    public static String getFieldName(int position, String name, int padTo) {
        StringBuilder fieldName = new StringBuilder().append("field_").append(position).append('_');
        RecordUtil.toIdentifier(name, fieldName);
        RecordUtil.pad(fieldName, padTo);
        return fieldName.toString();
    }

    public static String getFieldName(String name, int padTo) {
        StringBuilder fieldName = new StringBuilder();
        RecordUtil.toIdentifier(name, fieldName);
        RecordUtil.pad(fieldName, padTo);
        return fieldName.toString();
    }

    public static String getFieldName1stCap(String name, int padTo) {
        StringBuilder fieldName = new StringBuilder();
        RecordUtil.toIdentifier(name, fieldName);
        fieldName.setCharAt(0, RecordUtil.toUpperCase(fieldName.charAt(0)));
        RecordUtil.pad(fieldName, padTo);
        return fieldName.toString();
    }

    public static String getType1stCap(String size, String type, int padTo) {
        StringBuilder result = new StringBuilder();
        result.append(type);
        result = RecordUtil.pad(result, padTo);
        result.setCharAt(0, RecordUtil.toUpperCase(result.charAt(0)));
        return result.toString();
    }

    protected static StringBuilder pad(StringBuilder fieldName, int padTo) {
        for (int i = fieldName.length(); i < padTo; ++i) {
            fieldName.append(' ');
        }
        return fieldName;
    }

    private static void toConstIdentifier(String name, StringBuilder fieldName) {
        for (int i = 0; i < name.length(); ++i) {
            if (name.charAt(i) == ' ') {
                fieldName.append('_');
                continue;
            }
            fieldName.append(RecordUtil.toUpperCase(name.charAt(i)));
        }
    }

    private static void toIdentifier(String name, StringBuilder fieldName) {
        for (int i = 0; i < name.length(); ++i) {
            if (name.charAt(i) == ' ') {
                fieldName.append(RecordUtil.toUpperCase(name.charAt(++i)));
                continue;
            }
            fieldName.append(name.charAt(i));
        }
    }

    private static char toUpperCase(char c) {
        return StringUtil.toUpperCase(c).charAt(0);
    }
}

