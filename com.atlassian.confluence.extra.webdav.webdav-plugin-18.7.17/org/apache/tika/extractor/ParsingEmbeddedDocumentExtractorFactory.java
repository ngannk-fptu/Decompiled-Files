/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.extractor;

import org.apache.tika.config.Field;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.extractor.EmbeddedDocumentExtractorFactory;
import org.apache.tika.extractor.ParsingEmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;

public class ParsingEmbeddedDocumentExtractorFactory
implements EmbeddedDocumentExtractorFactory {
    private boolean writeFileNameToContent = true;

    @Field
    public void setWriteFileNameToContent(boolean writeFileNameToContent) {
        this.writeFileNameToContent = writeFileNameToContent;
    }

    @Override
    public EmbeddedDocumentExtractor newInstance(Metadata metadata, ParseContext parseContext) {
        ParsingEmbeddedDocumentExtractor ex = new ParsingEmbeddedDocumentExtractor(parseContext);
        ex.setWriteFileNameToContent(this.writeFileNameToContent);
        return ex;
    }
}

