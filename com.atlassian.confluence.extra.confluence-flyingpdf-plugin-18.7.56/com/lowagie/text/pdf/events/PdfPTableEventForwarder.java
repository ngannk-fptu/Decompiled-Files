/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.events;

import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPTableEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PdfPTableEventForwarder
implements PdfPTableEvent {
    protected List<PdfPTableEvent> events = new ArrayList<PdfPTableEvent>();

    public void addTableEvent(PdfPTableEvent event) {
        this.events.add(event);
    }

    @Override
    public void tableLayout(PdfPTable table, float[][] widths, float[] heights, int headerRows, int rowStart, PdfContentByte[] canvases) {
        Iterator<PdfPTableEvent> iterator = this.events.iterator();
        while (iterator.hasNext()) {
            PdfPTableEvent event1;
            PdfPTableEvent event = event1 = iterator.next();
            event.tableLayout(table, widths, heights, headerRows, rowStart, canvases);
        }
    }
}

