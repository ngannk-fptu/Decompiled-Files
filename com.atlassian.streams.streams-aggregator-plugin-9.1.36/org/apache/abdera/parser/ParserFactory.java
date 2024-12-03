/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.parser;

import org.apache.abdera.parser.Parser;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface ParserFactory {
    public <T extends Parser> T getParser();

    public <T extends Parser> T getParser(String var1);
}

