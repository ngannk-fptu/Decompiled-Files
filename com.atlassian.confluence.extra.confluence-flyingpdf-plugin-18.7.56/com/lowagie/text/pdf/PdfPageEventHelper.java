/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfWriter;

public class PdfPageEventHelper
implements PdfPageEvent {
    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
    }

    @Override
    public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {
    }

    @Override
    public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {
    }

    @Override
    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
    }

    @Override
    public void onChapterEnd(PdfWriter writer, Document document, float position) {
    }

    @Override
    public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth, Paragraph title) {
    }

    @Override
    public void onSectionEnd(PdfWriter writer, Document document, float position) {
    }

    @Override
    public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
    }
}

