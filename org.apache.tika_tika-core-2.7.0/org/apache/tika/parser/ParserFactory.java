/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.IOException;
import java.util.Map;
import org.apache.tika.exception.TikaException;
import org.apache.tika.parser.Parser;
import org.xml.sax.SAXException;

public abstract class ParserFactory {
    final Map<String, String> args;

    public ParserFactory(Map<String, String> args) {
        this.args = args;
    }

    public abstract Parser build() throws IOException, SAXException, TikaException;
}

