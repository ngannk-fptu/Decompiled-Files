/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.InputStream;
import java.util.Collections;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.xml.sax.ContentHandler;

public class ErrorParser
extends AbstractParser {
    public static final ErrorParser INSTANCE = new ErrorParser();
    private static final long serialVersionUID = 7727423956957641824L;

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return Collections.emptySet();
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws TikaException {
        throw new TikaException("Parse error");
    }
}

