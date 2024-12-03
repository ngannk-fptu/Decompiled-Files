/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Set;
import org.apache.tomcat.util.http.parser.TokenList;

@Deprecated
public class Vary {
    private Vary() {
    }

    public static void parseVary(StringReader input, Set<String> result) throws IOException {
        TokenList.parseTokenList(input, result);
    }
}

