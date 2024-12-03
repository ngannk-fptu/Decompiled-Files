/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fixup;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.fixup.PDDocumentFixup;

public abstract class AbstractFixup
implements PDDocumentFixup {
    protected PDDocument document;

    protected AbstractFixup(PDDocument document) {
        this.document = document;
    }
}

