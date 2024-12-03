/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.EmptyParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class DelegatingParser
extends AbstractParser {
    protected Parser getDelegateParser(ParseContext context) {
        return context.get(Parser.class, EmptyParser.INSTANCE);
    }

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return this.getDelegateParser(context).getSupportedTypes(context);
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws SAXException, IOException, TikaException {
        this.getDelegateParser(context).parse(stream, handler, metadata, context);
    }
}

