/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.XHTMLContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class EmptyParser
extends AbstractParser {
    public static final EmptyParser INSTANCE = new EmptyParser();
    private static final long serialVersionUID = -4218649699095732123L;

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return Collections.emptySet();
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws SAXException {
        XHTMLContentHandler xhtml = new XHTMLContentHandler(handler, metadata);
        xhtml.startDocument();
        xhtml.endDocument();
    }
}

