/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.events;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PdfPCellEventForwarder
implements PdfPCellEvent {
    protected List<PdfPCellEvent> events = new ArrayList<PdfPCellEvent>();

    public void addCellEvent(PdfPCellEvent event) {
        this.events.add(event);
    }

    @Override
    public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
        Iterator<PdfPCellEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPCellEvent event1;
            PdfPCellEvent event = event1 = iterator.next();
            event.cellLayout(cell, position, canvases);
        }
    }
}

