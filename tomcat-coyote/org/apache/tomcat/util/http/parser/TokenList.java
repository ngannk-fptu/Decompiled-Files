/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.http.parser.SkipResult;

public class TokenList {
    private TokenList() {
    }

    public static boolean parseTokenList(Enumeration<String> inputs, Collection<String> collection) throws IOException {
        boolean result = true;
        while (inputs.hasMoreElements()) {
            String nextHeaderValue = inputs.nextElement();
            if (nextHeaderValue == null || TokenList.parseTokenList(new StringReader(nextHeaderValue), collection)) continue;
            result = false;
        }
        return result;
    }

    public static boolean parseTokenList(Reader input, Collection<String> collection) throws IOException {
        boolean invalid = false;
        boolean valid = false;
        while (true) {
            String element;
            if ((element = HttpParser.readToken(input)) == null) {
                if (HttpParser.skipConstant(input, ",") == SkipResult.FOUND) continue;
                invalid = true;
                HttpParser.skipUntil(input, 0, ',');
                continue;
            }
            if (element.length() == 0) break;
            SkipResult skipResult = HttpParser.skipConstant(input, ",");
            if (skipResult == SkipResult.EOF) {
                valid = true;
                collection.add(element.toLowerCase(Locale.ENGLISH));
                break;
            }
            if (skipResult == SkipResult.FOUND) {
                valid = true;
                collection.add(element.toLowerCase(Locale.ENGLISH));
                continue;
            }
            invalid = true;
            HttpParser.skipUntil(input, 0, ',');
        }
        return valid && !invalid;
    }
}

