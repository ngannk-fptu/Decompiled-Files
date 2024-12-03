/*
 * Decompiled with CFR 0.152.
 */
package org.apache.regexp;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

public class REUtil {
    private static final String complexPrefix = "complex:";

    public static RE createRE(String string, int n) throws RESyntaxException {
        if (string.startsWith(complexPrefix)) {
            return new RE(string.substring(complexPrefix.length()), n);
        }
        return new RE(RE.simplePatternToFullRegularExpression(string), n);
    }

    public static RE createRE(String string) throws RESyntaxException {
        return REUtil.createRE(string, 0);
    }
}

