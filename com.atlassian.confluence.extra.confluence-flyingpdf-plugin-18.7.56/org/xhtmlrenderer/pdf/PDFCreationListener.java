/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import org.xhtmlrenderer.pdf.ITextRenderer;

public interface PDFCreationListener {
    public void preOpen(ITextRenderer var1);

    public void preWrite(ITextRenderer var1, int var2);

    public void onClose(ITextRenderer var1);
}

