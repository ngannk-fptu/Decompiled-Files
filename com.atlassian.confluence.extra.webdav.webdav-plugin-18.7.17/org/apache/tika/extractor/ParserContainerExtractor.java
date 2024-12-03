/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.extractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.extractor.ContainerExtractor;
import org.apache.tika.extractor.EmbeddedResourceHandler;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.StatefulParser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ParserContainerExtractor
implements ContainerExtractor {
    private static final long serialVersionUID = 2261131045580861514L;
    private final Parser parser;
    private final Detector detector;

    public ParserContainerExtractor() {
        this(TikaConfig.getDefaultConfig());
    }

    public ParserContainerExtractor(TikaConfig config) {
        this(new AutoDetectParser(config), new DefaultDetector(config.getMimeRepository()));
    }

    public ParserContainerExtractor(Parser parser, Detector detector) {
        this.parser = parser;
        this.detector = detector;
    }

    @Override
    public boolean isSupported(TikaInputStream input) throws IOException {
        MediaType type = this.detector.detect((InputStream)((Object)input), new Metadata());
        return this.parser.getSupportedTypes(new ParseContext()).contains(type);
    }

    @Override
    public void extract(TikaInputStream stream, ContainerExtractor recurseExtractor, EmbeddedResourceHandler handler) throws IOException, TikaException {
        ParseContext context = new ParseContext();
        context.set(Parser.class, new RecursiveParser(this.parser, recurseExtractor, handler));
        try {
            this.parser.parse((InputStream)((Object)stream), new DefaultHandler(), new Metadata(), context);
        }
        catch (SAXException e) {
            throw new TikaException("Unexpected SAX exception", e);
        }
    }

    private class RecursiveParser
    extends StatefulParser {
        private final ContainerExtractor extractor;
        private final EmbeddedResourceHandler handler;

        private RecursiveParser(Parser statelessParser, ContainerExtractor extractor, EmbeddedResourceHandler handler) {
            super(statelessParser);
            this.extractor = extractor;
            this.handler = handler;
        }

        @Override
        public Set<MediaType> getSupportedTypes(ParseContext context) {
            return ParserContainerExtractor.this.parser.getSupportedTypes(context);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void parse(InputStream stream, ContentHandler ignored, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
            block16: {
                TemporaryResources tmp = new TemporaryResources();
                try {
                    TikaInputStream tis = TikaInputStream.get(stream, tmp);
                    String filename = metadata.get("resourceName");
                    MediaType type = ParserContainerExtractor.this.detector.detect((InputStream)((Object)tis), metadata);
                    if (this.extractor == null) {
                        this.handler.handle(filename, type, (InputStream)((Object)tis));
                        break block16;
                    }
                    File file = tis.getFile();
                    try (TikaInputStream input = TikaInputStream.get(file);){
                        this.handler.handle(filename, type, (InputStream)((Object)input));
                    }
                    this.extractor.extract(tis, this.extractor, this.handler);
                }
                finally {
                    tmp.dispose();
                }
            }
        }
    }
}

