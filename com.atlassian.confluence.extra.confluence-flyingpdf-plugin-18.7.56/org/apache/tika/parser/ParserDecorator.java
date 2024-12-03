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
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.EmptyParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class ParserDecorator
extends AbstractParser {
    private static final long serialVersionUID = -3861669115439125268L;
    private final Parser parser;

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

    public static final Parser withFallbacks(final Collection<? extends Parser> parsers, final Set<MediaType> types) {
        Parser parser = EmptyParser.INSTANCE;
        if (!parsers.isEmpty()) {
            parser = parsers.iterator().next();
        }
        return new ParserDecorator(parser){
            private static final long serialVersionUID = 1625187131782069683L;

            @Override
            public Set<MediaType> getSupportedTypes(ParseContext context) {
                return types;
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             * Enabled force condition propagation
             * Lifted jumps to return sites
             */
            @Override
            public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
                TemporaryResources tmp = TikaInputStream.isTikaInputStream(stream) ? null : new TemporaryResources();
                try {
                    TikaInputStream tstream = TikaInputStream.get(stream, tmp);
                    tstream.getFile();
                    for (Parser p : parsers) {
                        tstream.mark(-1);
                        try {
                            p.parse(tstream, handler, metadata, context);
                            return;
                        }
                        catch (Exception exception) {
                            try {
                                tstream.reset();
                            }
                            catch (Throwable throwable) {
                                throw throwable;
                                return;
                            }
                        }
                    }
                }
                finally {
                    if (tmp != null) {
                        tmp.dispose();
                    }
                }
            }

            @Override
            public String getDecorationName() {
                return "With Fallback";
            }
        };
    }

    public ParserDecorator(Parser parser) {
        this.parser = parser;
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

