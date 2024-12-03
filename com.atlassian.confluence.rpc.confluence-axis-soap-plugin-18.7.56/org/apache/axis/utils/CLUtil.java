/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import org.apache.axis.utils.CLOptionDescriptor;
import org.apache.axis.utils.JavaUtils;

public final class CLUtil {
    private static final int MAX_DESCRIPTION_COLUMN_LENGTH = 60;

    public static final StringBuffer describeOptions(CLOptionDescriptor[] options) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < options.length; ++i) {
            char ch = (char)options[i].getId();
            String name = options[i].getName();
            String description = options[i].getDescription();
            int flags = options[i].getFlags();
            boolean argumentRequired = (flags & 2) == 2;
            boolean twoArgumentsRequired = (flags & 0x10) == 16;
            boolean needComma = false;
            if (twoArgumentsRequired) {
                argumentRequired = true;
            }
            sb.append('\t');
            if (Character.isLetter(ch)) {
                sb.append("-");
                sb.append(ch);
                needComma = true;
            }
            if (null != name) {
                if (needComma) {
                    sb.append(", ");
                }
                sb.append("--");
                sb.append(name);
                if (argumentRequired) {
                    sb.append(" <argument>");
                }
                if (twoArgumentsRequired) {
                    sb.append("=<value>");
                }
                sb.append(JavaUtils.LS);
            }
            if (null == description) continue;
            while (description.length() > 60) {
                String descriptionPart = description.substring(0, 60);
                description = description.substring(60);
                sb.append("\t\t");
                sb.append(descriptionPart);
                sb.append(JavaUtils.LS);
            }
            sb.append("\t\t");
            sb.append(description);
            sb.append(JavaUtils.LS);
        }
        return sb;
    }

    private CLUtil() {
    }
}

