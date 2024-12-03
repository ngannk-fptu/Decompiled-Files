/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.util;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.radeox.regex.Compiler;
import org.radeox.regex.MatchResult;
import org.radeox.regex.Matcher;
import org.radeox.regex.Pattern;
import org.radeox.regex.Substitution;

public class Encoder {
    private static final String DELIMITER = "&\"'<>";
    private static final Map ESCAPED_CHARS = new HashMap();

    public static String escape(String str) {
        StringBuffer result = new StringBuffer();
        StringTokenizer tokenizer = new StringTokenizer(str, DELIMITER, true);
        while (tokenizer.hasMoreTokens()) {
            String currentToken = tokenizer.nextToken();
            if (ESCAPED_CHARS.containsKey(currentToken)) {
                result.append(ESCAPED_CHARS.get(currentToken));
                continue;
            }
            result.append(currentToken);
        }
        return result.toString();
    }

    public static String unescape(String str) {
        StringBuffer result = new StringBuffer();
        Compiler compiler = Compiler.create();
        Pattern entityPattern = compiler.compile("&(#?[0-9a-fA-F]+);");
        Matcher matcher = Matcher.create(str, entityPattern);
        result.append(matcher.substitute(new Substitution(){

            public void handleMatch(StringBuffer buffer, MatchResult result) {
                buffer.append(Encoder.toChar(result.group(1)));
            }
        }));
        return result.toString();
    }

    public static String toEntity(int c) {
        return "&#" + c + ";";
    }

    public static char toChar(String number) {
        return (char)Integer.decode(number.substring(1)).intValue();
    }

    static {
        ESCAPED_CHARS.put("&", Encoder.toEntity(38));
        ESCAPED_CHARS.put("\"", Encoder.toEntity(34));
        ESCAPED_CHARS.put("'", Encoder.toEntity(39));
        ESCAPED_CHARS.put(">", Encoder.toEntity(62));
        ESCAPED_CHARS.put("<", Encoder.toEntity(60));
    }
}

