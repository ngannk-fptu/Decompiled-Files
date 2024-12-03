/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.anakia;

public class Escape {
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static final String getText(String st) {
        int i;
        StringBuffer buff = new StringBuffer();
        char[] block = st.toCharArray();
        String stEntity = null;
        int last = 0;
        for (i = 0; i < block.length; ++i) {
            switch (block[i]) {
                case '<': {
                    stEntity = "&lt;";
                    break;
                }
                case '>': {
                    stEntity = "&gt;";
                    break;
                }
                case '&': {
                    stEntity = "&amp;";
                    break;
                }
                case '\"': {
                    stEntity = "&quot;";
                    break;
                }
                case '\n': {
                    stEntity = LINE_SEPARATOR;
                    break;
                }
            }
            if (stEntity == null) continue;
            buff.append(block, last, i - last);
            buff.append(stEntity);
            stEntity = null;
            last = i + 1;
        }
        if (last < block.length) {
            buff.append(block, last, i - last);
        }
        return buff.toString();
    }
}

