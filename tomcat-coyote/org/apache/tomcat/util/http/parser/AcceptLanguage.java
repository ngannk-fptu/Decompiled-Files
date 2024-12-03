/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.http.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.apache.tomcat.util.http.parser.SkipResult;

public class AcceptLanguage {
    private final Locale locale;
    private final double quality;

    protected AcceptLanguage(Locale locale, double quality) {
        this.locale = locale;
        this.quality = quality;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public double getQuality() {
        return this.quality;
    }

    public static List<AcceptLanguage> parse(StringReader input) throws IOException {
        ArrayList<AcceptLanguage> result = new ArrayList<AcceptLanguage>();
        while (true) {
            String languageTag;
            if ((languageTag = HttpParser.readToken(input)) == null) {
                HttpParser.skipUntil(input, 0, ',');
                continue;
            }
            if (languageTag.length() == 0) break;
            double quality = 1.0;
            SkipResult lookForSemiColon = HttpParser.skipConstant(input, ";");
            if (lookForSemiColon == SkipResult.FOUND) {
                quality = HttpParser.readWeight(input, ',');
            }
            if (!(quality > 0.0)) continue;
            result.add(new AcceptLanguage(Locale.forLanguageTag(languageTag), quality));
        }
        return result;
    }
}

