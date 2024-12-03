/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.http.parser.SkipResult;

public class Authorization {
    private static final Map<String, FieldType> fieldTypes = new HashMap<String, FieldType>();

    private Authorization() {
    }

    public static Map<String, String> parseAuthorizationDigest(StringReader input) throws IllegalArgumentException, IOException {
        HashMap<String, String> result = new HashMap<String, String>();
        if (HttpParser.skipConstant(input, "Digest") != SkipResult.FOUND) {
            return null;
        }
        String field = HttpParser.readToken(input);
        if (field == null) {
            return null;
        }
        while (!field.equals("")) {
            if (HttpParser.skipConstant(input, "=") != SkipResult.FOUND) {
                return null;
            }
            String value = null;
            FieldType type = fieldTypes.get(field.toLowerCase(Locale.ENGLISH));
            if (type == null) {
                type = FieldType.TOKEN_OR_QUOTED_STRING;
            }
            switch (type) {
                case QUOTED_STRING: {
                    value = HttpParser.readQuotedString(input, false);
                    break;
                }
                case TOKEN_OR_QUOTED_STRING: {
                    value = HttpParser.readTokenOrQuotedString(input, false);
                    break;
                }
                case LHEX: {
                    value = HttpParser.readLhex(input);
                    break;
                }
                case QUOTED_TOKEN: {
                    value = HttpParser.readQuotedToken(input);
                }
            }
            if (value == null) {
                return null;
            }
            result.put(field, value);
            if (HttpParser.skipConstant(input, ",") == SkipResult.NOT_FOUND) {
                return null;
            }
            field = HttpParser.readToken(input);
            if (field != null) continue;
            return null;
        }
        return result;
    }

    static {
        fieldTypes.put("username", FieldType.QUOTED_STRING);
        fieldTypes.put("realm", FieldType.QUOTED_STRING);
        fieldTypes.put("nonce", FieldType.QUOTED_STRING);
        fieldTypes.put("digest-uri", FieldType.QUOTED_STRING);
        fieldTypes.put("response", FieldType.LHEX);
        fieldTypes.put("algorithm", FieldType.QUOTED_TOKEN);
        fieldTypes.put("cnonce", FieldType.QUOTED_STRING);
        fieldTypes.put("opaque", FieldType.QUOTED_STRING);
        fieldTypes.put("qop", FieldType.QUOTED_TOKEN);
        fieldTypes.put("nc", FieldType.LHEX);
    }

    private static enum FieldType {
        QUOTED_STRING,
        TOKEN_OR_QUOTED_STRING,
        LHEX,
        QUOTED_TOKEN;

    }
}

