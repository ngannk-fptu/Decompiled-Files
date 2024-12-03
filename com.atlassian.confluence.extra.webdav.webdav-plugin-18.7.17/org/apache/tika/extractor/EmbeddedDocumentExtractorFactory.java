/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.extractor;

import java.io.Serializable;
import org.apache.tika.extractor.EmbeddedDocumentExtractor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;

public interface EmbeddedDocumentExtractorFactory
extends Serializable {
    public EmbeddedDocumentExtractor newInstance(Metadata var1, ParseContext var2);
}

