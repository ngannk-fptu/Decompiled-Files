/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fixup.processor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.fixup.processor.PDDocumentProcessor;

public abstract class AbstractProcessor
implements PDDocumentProcessor {
    protected PDDocument document;

    protected AbstractProcessor(PDDocument document) {
        this.document = document;
    }
}

