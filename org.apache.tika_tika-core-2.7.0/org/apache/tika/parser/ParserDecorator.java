/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.multiple.AbstractMultipleParser;
import org.apache.tika.parser.multiple.FallbackParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ParserDecorator
extends AbstractParser {
    private static final long serialVersionUID = -3861669115439125268L;
    private final Parser parser;

    public ParserDecorator(Parser parser) {
        this.parser = parser;
    }

    public static final Parser withTypes(Parser parser, final Set<MediaType> types) {
        return new ParserDecorator(parser){
            private static final long serialVersionUID = -7345051519565330731L;

            @Override
            public Set<MediaType> getSupportedTypes(ParseContext context) {
                return types;
            }

            @Override
            public String getDecorationName() {
                return "With Types";
            }
        };
    }

    public static final Parser withoutTypes(Parser parser, final Set<MediaType> excludeTypes) {
        return new ParserDecorator(parser){
            private static final long serialVersionUID = 7979614774021768609L;

            @Override
            public Set<MediaType> getSupportedTypes(ParseContext context) {
                HashSet<MediaType> parserTypes = new HashSet<MediaType>(super.getSupportedTypes(context));
                parserTypes.removeAll(excludeTypes);
                return parserTypes;
            }

            @Override
            public String getDecorationName() {
                return "Without Types";
            }
        };
    }

    public static final Parser withFallbacks(Collection<? extends Parser> parsers, Set<MediaType> types) {
        MediaTypeRegistry registry = MediaTypeRegistry.getDefaultRegistry();
        FallbackParser p = new FallbackParser(registry, AbstractMultipleParser.MetadataPolicy.KEEP_ALL, parsers);
        if (types == null || types.isEmpty()) {
            return p;
        }
        return ParserDecorator.withTypes(p, types);
    }

    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return this.parser.getSupportedTypes(context);
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        this.parser.parse(stream, handler, metadata, context);
    }

    public String getDecorationName() {
        return null;
    }

    public Parser getWrappedParser() {
        return this.parser;
    }
}

