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
import org.apache.tika.io.TemporaryResources;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.apache.tika.parser.AutoDetectParserConfig;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.DefaultParser;
import org.apache.tika.parser.DigestingParser;
import org.apache.tika.parser.EmptyParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ParseRecord;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.RecursiveParserWrapper;
import org.apache.tika.sax.SecureContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class AutoDetectParser
extends CompositeParser {
    private static final long serialVersionUID = 6110455808615143122L;
    private Detector detector;
    private AutoDetectParserConfig autoDetectParserConfig;

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
        this.setAutoDetectParserConfig(AutoDetectParserConfig.DEFAULT);
    }

    public AutoDetectParser(TikaConfig config) {
        super(config.getMediaTypeRegistry(), AutoDetectParser.getParser(config));
        this.setFallback(AutoDetectParser.buildFallbackParser(config));
        this.setDetector(config.getDetector());
        this.setAutoDetectParserConfig(config.getAutoDetectParserConfig());
    }

    private static Parser buildFallbackParser(TikaConfig config) {
        Parser fallback = null;
        Parser p = config.getParser();
        fallback = p instanceof DefaultParser ? ((DefaultParser)p).getFallback() : new EmptyParser();
        if (config.getAutoDetectParserConfig().getDigesterFactory() == null) {
            return fallback;
        }
        return new DigestingParser(fallback, config.getAutoDetectParserConfig().getDigesterFactory().build());
    }

    private static Parser getParser(TikaConfig config) {
        if (config.getAutoDetectParserConfig().getDigesterFactory() == null) {
            return config.getParser();
        }
        return new DigestingParser(config.getParser(), config.getAutoDetectParserConfig().getDigesterFactory().build());
    }

    public Detector getDetector() {
        return this.detector;
    }

    public void setDetector(Detector detector) {
        this.detector = detector;
    }

    public void setAutoDetectParserConfig(AutoDetectParserConfig autoDetectParserConfig) {
        this.autoDetectParserConfig = autoDetectParserConfig;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        if (this.autoDetectParserConfig.getMetadataWriteFilterFactory() != null) {
            metadata.setMetadataWriteFilter(this.autoDetectParserConfig.getMetadataWriteFilterFactory().newInstance());
        }
        TemporaryResources tmp = new TemporaryResources();
        try {
            TikaInputStream tis = TikaInputStream.get(stream, tmp, metadata);
            this.maybeSpool(tis, this.autoDetectParserConfig, metadata);
            MediaType type = this.detector.detect((InputStream)((Object)tis), metadata);
            if (metadata.get(TikaCoreProperties.CONTENT_TYPE_PARSER_OVERRIDE) == null || !metadata.get(TikaCoreProperties.CONTENT_TYPE_PARSER_OVERRIDE).equals(type.toString())) {
                metadata.set("Content-Type", type.toString());
            }
            if (tis.getOpenContainer() == null) {
                tis.mark(1);
                if (tis.read() == -1) {
                    throw new ZeroByteFileException("InputStream must have > 0 bytes");
                }
                tis.reset();
            }
            SecureContentHandler sch = (handler = this.decorateHandler(handler, metadata, context, this.autoDetectParserConfig)) != null ? this.createSecureContentHandler(handler, tis, this.autoDetectParserConfig) : null;
            this.initializeEmbeddedDocumentExtractor(metadata, context);
            try {
                super.parse((InputStream)((Object)tis), sch, metadata, context);
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

    private ContentHandler decorateHandler(ContentHandler handler, Metadata metadata, ParseContext context, AutoDetectParserConfig autoDetectParserConfig) {
        if (context.get(RecursiveParserWrapper.RecursivelySecureContentHandler.class) != null) {
            return autoDetectParserConfig.getContentHandlerDecoratorFactory().decorate(handler, metadata, context);
        }
        ParseRecord parseRecord = context.get(ParseRecord.class);
        if (parseRecord == null || parseRecord.getDepth() == 0) {
            return autoDetectParserConfig.getContentHandlerDecoratorFactory().decorate(handler, metadata, context);
        }
        return handler;
    }

    private void maybeSpool(TikaInputStream tis, AutoDetectParserConfig autoDetectParserConfig, Metadata metadata) throws IOException {
        if (tis.hasFile()) {
            return;
        }
        if (autoDetectParserConfig.getSpoolToDisk() == null) {
            return;
        }
        if (autoDetectParserConfig.getSpoolToDisk() == 0L) {
            tis.getPath();
            metadata.set("Content-Length", Long.toString(tis.getLength()));
            return;
        }
        if (metadata.get("Content-Length") != null) {
            long len = -1L;
            try {
                len = Long.parseLong(metadata.get("Content-Length"));
                if (len > autoDetectParserConfig.getSpoolToDisk()) {
                    tis.getPath();
                    metadata.set("Content-Length", Long.toString(tis.getLength()));
                }
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
    }

    private void initializeEmbeddedDocumentExtractor(Metadata metadata, ParseContext context) {
        if (context.get(EmbeddedDocumentExtractor.class) != null) {
            return;
        }
        Parser p = context.get(Parser.class);
        if (p == null) {
            context.set(Parser.class, this);
        }
        EmbeddedDocumentExtractor edx = this.autoDetectParserConfig.getEmbeddedDocumentExtractorFactory().newInstance(metadata, context);
        context.set(EmbeddedDocumentExtractor.class, edx);
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata) throws IOException, SAXException, TikaException {
        ParseContext context = new ParseContext();
        context.set(Parser.class, this);
        this.parse(stream, handler, metadata, context);
    }

    private SecureContentHandler createSecureContentHandler(ContentHandler handler, TikaInputStream tis, AutoDetectParserConfig config) {
        SecureContentHandler sch = new SecureContentHandler(handler, tis);
        if (config == null) {
            return sch;
        }
        if (config.getOutputThreshold() != null) {
            sch.setOutputThreshold(config.getOutputThreshold());
        }
        if (config.getMaximumCompressionRatio() != null) {
            sch.setMaximumCompressionRatio(config.getMaximumCompressionRatio());
        }
        if (config.getMaximumDepth() != null) {
            sch.setMaximumDepth(config.getMaximumDepth());
        }
        if (config.getMaximumPackageEntryDepth() != null) {
            sch.setMaximumPackageEntryDepth(config.getMaximumPackageEntryDepth());
        }
        return sch;
    }
}

