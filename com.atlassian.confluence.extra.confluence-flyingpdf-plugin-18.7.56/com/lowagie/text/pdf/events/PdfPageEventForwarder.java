/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.events;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPageEvent;
import com.lowagie.text.pdf.PdfWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PdfPageEventForwarder
implements PdfPageEvent {
    protected List<PdfPageEvent> events = new ArrayList<PdfPageEvent>();

    public void addPageEvent(PdfPageEvent event) {
        this.events.add(event);
    }

    @Override
    public void onOpenDocument(PdfWriter writer, Document document) {
        Iterator<PdfPageEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent event1;
            PdfPageEvent event = event1 = iterator.next();
            event.onOpenDocument(writer, document);
        }
    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        Iterator<PdfPageEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent event1;
            PdfPageEvent event = event1 = iterator.next();
            event.onStartPage(writer, document);
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        Iterator<PdfPageEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent event1;
            PdfPageEvent event = event1 = iterator.next();
            event.onEndPage(writer, document);
        }
    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        Iterator<PdfPageEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent event1;
            PdfPageEvent event = event1 = iterator.next();
            event.onCloseDocument(writer, document);
        }
    }

    @Override
    public void onParagraph(PdfWriter writer, Document document, float paragraphPosition) {
        Iterator<PdfPageEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent event1;
            PdfPageEvent event = event1 = iterator.next();
            event.onParagraph(writer, document, paragraphPosition);
        }
    }

    @Override
    public void onParagraphEnd(PdfWriter writer, Document document, float paragraphPosition) {
        Iterator<PdfPageEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent event1;
            PdfPageEvent event = event1 = iterator.next();
            event.onParagraphEnd(writer, document, paragraphPosition);
        }
    }

    @Override
    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
        Iterator<PdfPageEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent event1;
            PdfPageEvent event = event1 = iterator.next();
            event.onChapter(writer, document, paragraphPosition, title);
        }
    }

    @Override
    public void onChapterEnd(PdfWriter writer, Document document, float position) {
        Iterator<PdfPageEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent event1;
            PdfPageEvent event = event1 = iterator.next();
            event.onChapterEnd(writer, document, position);
        }
    }

    @Override
    public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth, Paragraph title) {
        Iterator<PdfPageEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent event1;
            PdfPageEvent event = event1 = iterator.next();
            event.onSection(writer, document, paragraphPosition, depth, title);
        }
    }

    @Override
    public void onSectionEnd(PdfWriter writer, Document document, float position) {
        Iterator<PdfPageEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent event1;
            PdfPageEvent event = event1 = iterator.next();
            event.onSectionEnd(writer, document, position);
        }
    }

    @Override
    public void onGenericTag(PdfWriter writer, Document document, Rectangle rect, String text) {
        Iterator<PdfPageEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPageEvent event1;
            PdfPageEvent event = event1 = iterator.next();
            event.onGenericTag(writer, document, rect, text);
        }
    }
}

