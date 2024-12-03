/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.contentstream.operator.graphics;

import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.OperatorProcessor;

public abstract class GraphicsOperatorProcessor
extends OperatorProcessor {
    protected PDFGraphicsStreamEngine context;

    @Override
    public void setContext(PDFStreamEngine context) {
        super.setContext(context);
        this.context = (PDFGraphicsStreamEngine)context;
    }
}

