/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.exception.ZeroByteFileException;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.ParsingEmbeddedDocumentExtractor;
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.SecureContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class AutoDetectParser
extends CompositeParser {
    private static final long serialVersionUID = 6110455808615143122L;
    private Detector detector;

    public AutoDetectParser() {
        this(TikaConfig.getDefaultConfig());
    }

    public AutoDetectParser(Detector detector) {
        this(TikaConfig.getDefaultConfig());
        this.setDetector(detector);
    }

    public AutoDetectParser(Parser ... parsers) {
        this(new DefaultDetector(), parsers);
    }

    public AutoDetectParser(Detector detector, Parser ... parsers) {
        super(MediaTypeRegistry.getDefaultRegistry(), parsers);
        this.setDetector(detector);
    }

    public AutoDetectParser(TikaConfig config) {
        super(config.getMediaTypeRegistry(), config.getParser());
        this.setDetector(config.getDetector());
    }

    public Detector getDetector() {
        return this.detector;
    }

    public void setDetector(Detector detector) {
        this.detector = detector;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        TemporaryResources tmp = new TemporaryResources();
        try {
            SecureContentHandler sch;
            TikaInputStream tis = TikaInputStream.get(stream, tmp);
            MediaType type = this.detector.detect(tis, metadata);
            metadata.set("Content-Type", type.toString());
            if (tis.getOpenContainer() == null) {
                tis.mark(1);
                if (tis.read() == -1) {
                    throw new ZeroByteFileException("InputStream must have > 0 bytes");
                }
                tis.reset();
            }
            SecureContentHandler secureContentHandler = sch = handler != null ? new SecureContentHandler(handler, tis) : null;
            if (context.get(EmbeddedDocumentExtractor.class) == null) {
                Parser p = context.get(Parser.class);
                if (p == null) {
                    context.set(Parser.class, this);
                }
                context.set(EmbeddedDocumentExtractor.class, new ParsingEmbeddedDocumentExtractor(context));
            }
            try {
                super.parse(tis, sch, metadata, context);
            }
            catch (SAXException e) {
                sch.throwIfCauseOf(e);
                throw e;
            }
        }
        finally {
            tmp.dispose();
        }
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata) throws IOException, SAXException, TikaException {
        ParseContext context = new ParseContext();
        context.set(Parser.class, this);
        this.parse(stream, handler, metadata, context);
    }
}

