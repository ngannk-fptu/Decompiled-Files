/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import org.apache.tomcat.util.http.parser.HttpParser;

public class EntityTag {
    public static Boolean compareEntityTag(StringReader input, boolean compareWeak, String resourceETag) throws IOException {
        String comparisonETag = compareWeak && resourceETag.startsWith("W/") ? resourceETag.substring(2) : resourceETag;
        Boolean result = Boolean.FALSE;
        while (true) {
            boolean strong = false;
            HttpParser.skipLws(input);
            switch (HttpParser.skipConstant(input, "W/")) {
                case EOF: {
                    return null;
                }
                case NOT_FOUND: {
                    strong = true;
                    break;
                }
                case FOUND: {
                    strong = false;
                }
            }
            String value = HttpParser.readQuotedString(input, true);
            if (value == null) {
                return null;
            }
            if ((strong || compareWeak) && comparisonETag.equals(value)) {
                result = Boolean.TRUE;
            }
            HttpParser.skipLws(input);
            switch (HttpParser.skipConstant(input, ",")) {
                case EOF: {
                    return result;
                }
                case NOT_FOUND: {
                    return null;
                }
            }
        }
    }
}

