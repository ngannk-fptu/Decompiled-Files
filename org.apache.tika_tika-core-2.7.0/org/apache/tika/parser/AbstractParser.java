/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class AbstractParser
implements Parser {
    private static final long serialVersionUID = 7186985395903074255L;

    public void parse(InputStream stream, ContentHandler handler, Metadata metadata) throws IOException, SAXException, TikaException {
        this.parse(stream, handler, metadata, new ParseContext());
    }
}

