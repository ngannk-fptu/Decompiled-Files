/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.IOException;
import java.io.Serializable;
import org.apache.tika.config.ConfigBase;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.extractor.EmbeddedDocumentExtractorFactory;
import org.apache.tika.extractor.ParsingEmbeddedDocumentExtractorFactory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.writefilter.MetadataWriteFilterFactory;
import org.apache.tika.parser.DigestingParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.ContentHandlerDecoratorFactory;
import org.w3c.dom.Element;
import org.xml.sax.ContentHandler;

public class AutoDetectParserConfig
extends ConfigBase
implements Serializable {
    private static ContentHandlerDecoratorFactory NOOP_CONTENT_HANDLER_DECORATOR_FACTORY = new ContentHandlerDecoratorFactory(){

        @Override
        public ContentHandler decorate(ContentHandler contentHandler, Metadata metadata) {
            return contentHandler;
        }

        @Override
        public ContentHandler decorate(ContentHandler contentHandler, Metadata metadata, ParseContext parseContext) {
            return contentHandler;
        }
    };
    public static AutoDetectParserConfig DEFAULT = new AutoDetectParserConfig();
    private Long spoolToDisk = null;
    private Long outputThreshold = null;
    private Long maximumCompressionRatio = null;
    private Integer maximumDepth = null;
    private Integer maximumPackageEntryDepth = null;
    private MetadataWriteFilterFactory metadataWriteFilterFactory = null;
    private EmbeddedDocumentExtractorFactory embeddedDocumentExtractorFactory = new ParsingEmbeddedDocumentExtractorFactory();
    private ContentHandlerDecoratorFactory contentHandlerDecoratorFactory = NOOP_CONTENT_HANDLER_DECORATOR_FACTORY;
    private DigestingParser.DigesterFactory digesterFactory = null;

    public static AutoDetectParserConfig load(Element element) throws TikaConfigException, IOException {
        return AutoDetectParserConfig.buildSingle("autoDetectParserConfig", AutoDetectParserConfig.class, element, DEFAULT);
    }

    public AutoDetectParserConfig(Long spoolToDisk, Long outputThreshold, Long maximumCompressionRatio, Integer maximumDepth, Integer maximumPackageEntryDepth) {
        this.spoolToDisk = spoolToDisk;
        this.outputThreshold = outputThreshold;
        this.maximumCompressionRatio = maximumCompressionRatio;
        this.maximumDepth = maximumDepth;
        this.maximumPackageEntryDepth = maximumPackageEntryDepth;
    }

    public AutoDetectParserConfig() {
    }

    public Long getSpoolToDisk() {
        return this.spoolToDisk;
    }

    public void setSpoolToDisk(long spoolToDisk) {
        this.spoolToDisk = spoolToDisk;
    }

    public Long getOutputThreshold() {
        return this.outputThreshold;
    }

    public void setOutputThreshold(long outputThreshold) {
        this.outputThreshold = outputThreshold;
    }

    public Long getMaximumCompressionRatio() {
        return this.maximumCompressionRatio;
    }

    public void setMaximumCompressionRatio(long maximumCompressionRatio) {
        this.maximumCompressionRatio = maximumCompressionRatio;
    }

    public Integer getMaximumDepth() {
        return this.maximumDepth;
    }

    public void setMaximumDepth(int maximumDepth) {
        this.maximumDepth = maximumDepth;
    }

    public Integer getMaximumPackageEntryDepth() {
        return this.maximumPackageEntryDepth;
    }

    public void setMaximumPackageEntryDepth(int maximumPackageEntryDepth) {
        this.maximumPackageEntryDepth = maximumPackageEntryDepth;
    }

    public MetadataWriteFilterFactory getMetadataWriteFilterFactory() {
        return this.metadataWriteFilterFactory;
    }

    public void setMetadataWriteFilterFactory(MetadataWriteFilterFactory metadataWriteFilterFactory) {
        this.metadataWriteFilterFactory = metadataWriteFilterFactory;
    }

    public void setEmbeddedDocumentExtractorFactory(EmbeddedDocumentExtractorFactory embeddedDocumentExtractorFactory) {
        this.embeddedDocumentExtractorFactory = embeddedDocumentExtractorFactory;
    }

    public EmbeddedDocumentExtractorFactory getEmbeddedDocumentExtractorFactory() {
        return this.embeddedDocumentExtractorFactory;
    }

    public void setContentHandlerDecoratorFactory(ContentHandlerDecoratorFactory contentHandlerDecoratorFactory) {
        this.contentHandlerDecoratorFactory = contentHandlerDecoratorFactory;
    }

    public ContentHandlerDecoratorFactory getContentHandlerDecoratorFactory() {
        return this.contentHandlerDecoratorFactory;
    }

    public void setDigesterFactory(DigestingParser.DigesterFactory digesterFactory) {
        this.digesterFactory = digesterFactory;
    }

    public DigestingParser.DigesterFactory getDigesterFactory() {
        return this.digesterFactory;
    }

    public String toString() {
        return "AutoDetectParserConfig{spoolToDisk=" + this.spoolToDisk + ", outputThreshold=" + this.outputThreshold + ", maximumCompressionRatio=" + this.maximumCompressionRatio + ", maximumDepth=" + this.maximumDepth + ", maximumPackageEntryDepth=" + this.maximumPackageEntryDepth + ", metadataWriteFilterFactory=" + this.metadataWriteFilterFactory + ", embeddedDocumentExtractorFactory=" + this.embeddedDocumentExtractorFactory + ", contentHandlerDecoratorFactory=" + this.contentHandlerDecoratorFactory + ", digesterFactory=" + this.digesterFactory + '}';
    }
}

