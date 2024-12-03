/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.http.parser.SkipResult;

public class AcceptEncoding {
    private final String encoding;
    private final double quality;

    protected AcceptEncoding(String encoding, double quality) {
        this.encoding = encoding;
        this.quality = quality;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public double getQuality() {
        return this.quality;
    }

    public static List<AcceptEncoding> parse(StringReader input) throws IOException {
        ArrayList<AcceptEncoding> result = new ArrayList<AcceptEncoding>();
        while (true) {
            String encoding;
            if ((encoding = HttpParser.readToken(input)) == null) {
                HttpParser.skipUntil(input, 0, ',');
                continue;
            }
            if (encoding.length() == 0) break;
            double quality = 1.0;
            SkipResult lookForSemiColon = HttpParser.skipConstant(input, ";");
            if (lookForSemiColon == SkipResult.FOUND) {
                quality = HttpParser.readWeight(input, ',');
            }
            if (!(quality > 0.0)) continue;
            result.add(new AcceptEncoding(encoding, quality));
        }
        return result;
    }
}

