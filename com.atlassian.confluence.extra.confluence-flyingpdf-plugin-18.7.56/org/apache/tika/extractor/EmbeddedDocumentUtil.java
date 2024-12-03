/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.ParsingEmbeddedDocumentExtractor;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.CompositeParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ParserDecorator;
import org.apache.tika.parser.PasswordProvider;
import org.apache.tika.utils.ExceptionUtils;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class EmbeddedDocumentUtil
implements Serializable {
    private final ParseContext context;
    private final EmbeddedDocumentExtractor embeddedDocumentExtractor;
    private TikaConfig tikaConfig;
    private MimeTypes mimeTypes;
    private Detector detector;

    public EmbeddedDocumentUtil(ParseContext context) {
        this.context = context;
        this.embeddedDocumentExtractor = EmbeddedDocumentUtil.getEmbeddedDocumentExtractor(context);
    }

    public static EmbeddedDocumentExtractor getEmbeddedDocumentExtractor(ParseContext context) {
        EmbeddedDocumentExtractor extractor = context.get(EmbeddedDocumentExtractor.class);
        if (extractor == null) {
            Parser embeddedParser = context.get(Parser.class);
            if (embeddedParser == null) {
                TikaConfig tikaConfig = context.get(TikaConfig.class);
                if (tikaConfig == null) {
                    context.set(Parser.class, new AutoDetectParser());
                } else {
                    context.set(Parser.class, new AutoDetectParser(tikaConfig));
                }
            }
            extractor = new ParsingEmbeddedDocumentExtractor(context);
        }
        return extractor;
    }

    public PasswordProvider getPasswordProvider() {
        return this.context.get(PasswordProvider.class);
    }

    public Detector getDetector() {
        Detector localDetector = this.context.get(Detector.class);
        if (localDetector != null) {
            return localDetector;
        }
        if (this.detector != null) {
            return this.detector;
        }
        this.detector = this.getTikaConfig().getDetector();
        return this.detector;
    }

    public MimeTypes getMimeTypes() {
        MimeTypes localMimeTypes = this.context.get(MimeTypes.class);
        if (localMimeTypes != null) {
            return localMimeTypes;
        }
        if (this.mimeTypes != null) {
            return this.mimeTypes;
        }
        this.mimeTypes = this.getTikaConfig().getMimeRepository();
        return this.mimeTypes;
    }

    public TikaConfig getTikaConfig() {
        if (this.tikaConfig == null) {
            this.tikaConfig = this.context.get(TikaConfig.class);
            if (this.tikaConfig == null) {
                this.tikaConfig = TikaConfig.getDefaultConfig();
            }
        }
        return this.tikaConfig;
    }

    public String getExtension(TikaInputStream is, Metadata metadata) {
        String mimeString = metadata.get("Content-Type");
        MimeTypes localMimeTypes = this.getMimeTypes();
        MimeType mimeType = null;
        boolean detected = false;
        if (mimeString != null) {
            try {
                mimeType = localMimeTypes.forName(mimeString);
            }
            catch (MimeTypeException mimeTypeException) {
                // empty catch block
            }
        }
        if (mimeType == null) {
            try {
                MediaType mediaType = this.getDetector().detect(is, metadata);
                mimeType = localMimeTypes.forName(mediaType.toString());
                detected = true;
                is.reset();
            }
            catch (IOException iOException) {
            }
            catch (MimeTypeException mimeTypeException) {
                // empty catch block
            }
        }
        if (mimeType != null) {
            if (detected) {
                metadata.set("Content-Type", mimeType.toString());
            }
            return mimeType.getExtension();
        }
        return ".bin";
    }

    @Deprecated
    public TikaConfig getConfig() {
        TikaConfig config = this.context.get(TikaConfig.class);
        if (config == null) {
            config = TikaConfig.getDefaultConfig();
        }
        return config;
    }

    public static void recordException(Throwable t, Metadata m) {
        String ex = ExceptionUtils.getFilteredStackTrace(t);
        m.add(TikaCoreProperties.TIKA_META_EXCEPTION_WARNING, ex);
    }

    public static void recordEmbeddedStreamException(Throwable t, Metadata m) {
        String ex = ExceptionUtils.getFilteredStackTrace(t);
        m.add(TikaCoreProperties.TIKA_META_EXCEPTION_EMBEDDED_STREAM, ex);
    }

    public boolean shouldParseEmbedded(Metadata m) {
        return this.getEmbeddedDocumentExtractor().shouldParseEmbedded(m);
    }

    private EmbeddedDocumentExtractor getEmbeddedDocumentExtractor() {
        return this.embeddedDocumentExtractor;
    }

    public void parseEmbedded(InputStream inputStream, ContentHandler handler, Metadata metadata, boolean outputHtml) throws IOException, SAXException {
        this.embeddedDocumentExtractor.parseEmbedded(inputStream, handler, metadata, outputHtml);
    }

    public static Parser tryToFindExistingLeafParser(Class clazz, ParseContext context) {
        Parser p = context.get(Parser.class);
        if (EmbeddedDocumentUtil.equals(p, clazz)) {
            return p;
        }
        Parser returnParser = null;
        if (p != null) {
            if (p instanceof ParserDecorator) {
                p = EmbeddedDocumentUtil.findInDecorated((ParserDecorator)p, clazz);
            }
            if (EmbeddedDocumentUtil.equals(p, clazz)) {
                return p;
            }
            if (p instanceof CompositeParser) {
                returnParser = EmbeddedDocumentUtil.findInComposite((CompositeParser)p, clazz, context);
            }
        }
        if (returnParser != null && EmbeddedDocumentUtil.equals(returnParser, clazz)) {
            return returnParser;
        }
        return null;
    }

    private static Parser findInDecorated(ParserDecorator p, Class clazz) {
        Parser candidate = p.getWrappedParser();
        if (EmbeddedDocumentUtil.equals(candidate, clazz)) {
            return candidate;
        }
        if (candidate instanceof ParserDecorator) {
            candidate = EmbeddedDocumentUtil.findInDecorated((ParserDecorator)candidate, clazz);
        }
        return candidate;
    }

    private static Parser findInComposite(CompositeParser p, Class clazz, ParseContext context) {
        for (Parser candidate : p.getAllComponentParsers()) {
            if (EmbeddedDocumentUtil.equals(candidate, clazz)) {
                return candidate;
            }
            if (candidate instanceof ParserDecorator) {
                candidate = EmbeddedDocumentUtil.findInDecorated((ParserDecorator)candidate, clazz);
            }
            if (EmbeddedDocumentUtil.equals(candidate, clazz)) {
                return candidate;
            }
            if (candidate instanceof CompositeParser) {
                candidate = EmbeddedDocumentUtil.findInComposite((CompositeParser)candidate, clazz, context);
            }
            if (!EmbeddedDocumentUtil.equals(candidate, clazz)) continue;
            return candidate;
        }
        return null;
    }

    private static boolean equals(Parser parser, Class clazz) {
        if (parser == null) {
            return false;
        }
        return parser.getClass().equals(clazz);
    }
}

