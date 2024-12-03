/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PdfOutline
extends PdfDictionary {
    private PdfIndirectReference reference;
    private int count = 0;
    private PdfOutline parent;
    private PdfDestination destination;
    private PdfAction action;
    protected List<PdfOutline> kids = new ArrayList<PdfOutline>();
    protected PdfWriter writer;
    private String tag;
    private boolean open;
    private Color color;
    private int style = 0;

    PdfOutline(PdfWriter writer) {
        super(OUTLINES);
        this.open = true;
        this.parent = null;
        this.writer = writer;
    }

    public PdfOutline(PdfOutline parent, PdfAction action, String title) {
        this(parent, action, title, true);
    }

    public PdfOutline(PdfOutline parent, PdfAction action, String title, boolean open) {
        this.action = action;
        this.initOutline(parent, title, open);
    }

    public PdfOutline(PdfOutline parent, PdfDestination destination, String title) {
        this(parent, destination, title, true);
    }

    public PdfOutline(PdfOutline parent, PdfDestination destination, String title, boolean open) {
        this.destination = destination;
        this.initOutline(parent, title, open);
    }

    public PdfOutline(PdfOutline parent, PdfAction action, PdfString title) {
        this(parent, action, title, true);
    }

    public PdfOutline(PdfOutline parent, PdfAction action, PdfString title, boolean open) {
        this(parent, action, title.toString(), open);
    }

    public PdfOutline(PdfOutline parent, PdfDestination destination, PdfString title) {
        this(parent, destination, title, true);
    }

    public PdfOutline(PdfOutline parent, PdfDestination destination, PdfString title, boolean open) {
        this(parent, destination, title.toString(), true);
    }

    public PdfOutline(PdfOutline parent, PdfAction action, Paragraph title) {
        this(parent, action, title, true);
    }

    public PdfOutline(PdfOutline parent, PdfAction action, Paragraph title, boolean open) {
        StringBuilder buf = new StringBuilder();
        for (Element o : title.getChunks()) {
            Chunk chunk = (Chunk)o;
            buf.append(chunk.getContent());
        }
        this.action = action;
        this.initOutline(parent, buf.toString(), open);
    }

    public PdfOutline(PdfOutline parent, PdfDestination destination, Paragraph title) {
        this(parent, destination, title, true);
    }

    public PdfOutline(PdfOutline parent, PdfDestination destination, Paragraph title, boolean open) {
        StringBuilder buf = new StringBuilder();
        for (Element o : title.getChunks()) {
            Chunk chunk = (Chunk)o;
            buf.append(chunk.getContent());
        }
        this.destination = destination;
        this.initOutline(parent, buf.toString(), open);
    }

    void initOutline(PdfOutline parent, String title, boolean open) {
        this.open = open;
        this.parent = parent;
        this.writer = parent.writer;
        this.put(PdfName.TITLE, new PdfString(title, "UnicodeBig"));
        parent.addKid(this);
        if (this.destination != null && !this.destination.hasPage()) {
            this.setDestinationPage(this.writer.getCurrentPage());
        }
    }

    public void setIndirectReference(PdfIndirectReference reference) {
        this.reference = reference;
    }

    public PdfIndirectReference indirectReference() {
        return this.reference;
    }

    public PdfOutline parent() {
        return this.parent;
    }

    public boolean setDestinationPage(PdfIndirectReference pageReference) {
        if (this.destination == null) {
            return false;
        }
        return this.destination.addPage(pageReference);
    }

    public PdfDestination getPdfDestination() {
        return this.destination;
    }

    int getCount() {
        return this.count;
    }

    void setCount(int count) {
        this.count = count;
    }

    public int level() {
        if (this.parent == null) {
            return 0;
        }
        return this.parent.level() + 1;
    }

    @Override
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        if (this.color != null && !this.color.equals(Color.black)) {
            this.put(PdfName.C, new PdfArray(new float[]{(float)this.color.getRed() / 255.0f, (float)this.color.getGreen() / 255.0f, (float)this.color.getBlue() / 255.0f}));
        }
        int flag = 0;
        if ((this.style & 1) != 0) {
            flag |= 2;
        }
        if ((this.style & 2) != 0) {
            flag |= 1;
        }
        if (flag != 0) {
            this.put(PdfName.F, new PdfNumber(flag));
        }
        if (this.parent != null) {
            this.put(PdfName.PARENT, this.parent.indirectReference());
        }
        if (this.destination != null && this.destination.hasPage()) {
            this.put(PdfName.DEST, this.destination);
        }
        if (this.action != null) {
            this.put(PdfName.A, this.action);
        }
        if (this.count != 0) {
            this.put(PdfName.COUNT, new PdfNumber(this.count));
        }
        super.toPdf(writer, os);
    }

    public void addKid(PdfOutline outline) {
        this.kids.add(outline);
    }

    public List<PdfOutline> getKids() {
        return this.kids;
    }

    public void setKids(List<PdfOutline> kids) {
        this.kids = kids;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        PdfString title = (PdfString)this.get(PdfName.TITLE);
        return title.toString();
    }

    public void setTitle(String title) {
        this.put(PdfName.TITLE, new PdfString(title, "UnicodeBig"));
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getStyle() {
        return this.style;
    }

    public void setStyle(int style) {
        this.style = style;
    }
}

