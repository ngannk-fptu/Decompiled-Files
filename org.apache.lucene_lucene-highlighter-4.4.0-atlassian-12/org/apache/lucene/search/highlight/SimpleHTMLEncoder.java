/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search.highlight;

import org.apache.lucene.search.highlight.Encoder;

public class SimpleHTMLEncoder
implements Encoder {
    @Override
    public String encodeText(String originalText) {
        return SimpleHTMLEncoder.htmlEncode(originalText);
    }

    public static final String htmlEncode(String plainText) {
        if (plainText == null || plainText.length() == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder(plainText.length());
        block6: for (int index = 0; index < plainText.length(); ++index) {
            char ch = plainText.charAt(index);
            switch (ch) {
                case '\"': {
                    result.append("&quot;");
                    continue block6;
                }
                case '&': {
                    result.append("&amp;");
                    continue block6;
                }
                case '<': {
                    result.append("&lt;");
                    continue block6;
                }
                case '>': {
                    result.append("&gt;");
                    continue block6;
                }
                default: {
                    if (ch < '\u0080') {
                        result.append(ch);
                        continue block6;
                    }
                    result.append("&#").append((int)ch).append(";");
                }
            }
        }
        return result.toString();
    }
}

