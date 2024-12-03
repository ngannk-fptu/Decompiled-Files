/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.log;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public final class LogUtils {
    public static String createParamsList(Object[] objectArray) {
        StringBuffer stringBuffer = new StringBuffer(511);
        LogUtils.appendParamsList(stringBuffer, objectArray);
        return stringBuffer.toString();
    }

    public static void appendParamsList(StringBuffer stringBuffer, Object[] objectArray) {
        stringBuffer.append("[params: ");
        if (objectArray != null) {
            int n = objectArray.length;
            for (int i = 0; i < n; ++i) {
                if (i != 0) {
                    stringBuffer.append(", ");
                }
                stringBuffer.append(objectArray[i]);
            }
        }
        stringBuffer.append(']');
    }

    public static String createMessage(String string, String string2, String string3) {
        StringBuffer stringBuffer = new StringBuffer(511);
        stringBuffer.append("[class: ");
        stringBuffer.append(string);
        stringBuffer.append("; method: ");
        stringBuffer.append(string2);
        if (!string2.endsWith(")")) {
            stringBuffer.append("()");
        }
        stringBuffer.append("] ");
        stringBuffer.append(string3);
        return stringBuffer.toString();
    }

    public static String createMessage(String string, String string2) {
        StringBuffer stringBuffer = new StringBuffer(511);
        stringBuffer.append("[method: ");
        stringBuffer.append(string);
        if (!string.endsWith(")")) {
            stringBuffer.append("()");
        }
        stringBuffer.append("] ");
        stringBuffer.append(string2);
        return stringBuffer.toString();
    }

    public static String formatMessage(String string, String string2, Object[] objectArray) {
        String string3;
        ResourceBundle resourceBundle;
        if (string2 == null) {
            if (objectArray == null) {
                return "";
            }
            return LogUtils.createParamsList(objectArray);
        }
        if (string != null && (resourceBundle = ResourceBundle.getBundle(string)) != null && (string3 = resourceBundle.getString(string2)) != null) {
            string2 = string3;
        }
        return objectArray == null ? string2 : MessageFormat.format(string2, objectArray);
    }

    private LogUtils() {
    }
}

