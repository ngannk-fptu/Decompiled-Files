/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser;

import java.io.StringReader;
import org.xhtmlrenderer.css.parser.CSSErrorHandler;
import org.xhtmlrenderer.css.parser.CSSParser;

public class ParserTest {
    public static void main(String[] args) throws Exception {
        long end;
        CSSParser p;
        long start;
        int i;
        String test = "div { background-image: url('something') }\n";
        StringBuffer longTest = new StringBuffer();
        for (int i2 = 0; i2 < 10000; ++i2) {
            longTest.append(test);
        }
        CSSErrorHandler errorHandler = new CSSErrorHandler(){

            @Override
            public void error(String uri, String message) {
                System.out.println(message);
            }
        };
        long total = 0L;
        for (i = 0; i < 40; ++i) {
            start = System.currentTimeMillis();
            p = new CSSParser(errorHandler);
            p.parseStylesheet(null, 0, new StringReader(longTest.toString()));
            end = System.currentTimeMillis();
            total += end - start;
        }
        System.out.println("Average " + total / 10L + " ms");
        total = 0L;
        for (i = 0; i < 10; ++i) {
            start = System.currentTimeMillis();
            p = new CSSParser(errorHandler);
            p.parseStylesheet(null, 0, new StringReader(longTest.toString()));
            end = System.currentTimeMillis();
            total += end - start;
        }
        System.out.println("Average " + total / 10L + " ms");
        CSSParser p2 = new CSSParser(errorHandler);
        total = 0L;
        for (int i3 = 0; i3 < 10; ++i3) {
            long start2 = System.currentTimeMillis();
            for (int j = 0; j < 10000; ++j) {
                p2.parseStylesheet(null, 0, new StringReader(test));
            }
            end = System.currentTimeMillis();
            total += end - start2;
        }
        System.out.println("Average " + total / 10L + " ms");
    }
}

