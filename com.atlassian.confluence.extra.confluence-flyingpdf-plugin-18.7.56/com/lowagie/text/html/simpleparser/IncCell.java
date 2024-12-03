/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.html.simpleparser;

import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Phrase;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.html.Markup;
import com.lowagie.text.html.simpleparser.ChainedProperties;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.utils.NumberUtilities;
import java.util.ArrayList;

public class IncCell
implements TextElementArray {
    private final PdfPCell cell;
    private ArrayList<Element> chunks = new ArrayList();

    public IncCell(String tag, ChainedProperties props) {
        this.cell = new PdfPCell((Phrase)null);
        this.cell.setVerticalAlignment(5);
        props.findProperty("colspan").flatMap(NumberUtilities::parseInt).ifPresent(this.cell::setColspan);
        if (tag.equals("th")) {
            this.cell.setHorizontalAlignment(1);
        }
        props.findProperty("align").ifPresent(align -> {
            if ("center".equalsIgnoreCase((String)align)) {
                this.cell.setHorizontalAlignment(1);
            } else if ("right".equalsIgnoreCase((String)align)) {
                this.cell.setHorizontalAlignment(2);
            } else if ("left".equalsIgnoreCase((String)align)) {
                this.cell.setHorizontalAlignment(0);
            } else if ("justify".equalsIgnoreCase((String)align)) {
                this.cell.setHorizontalAlignment(3);
            }
        });
        props.findProperty("valign").ifPresent(valign -> {
            if ("top".equalsIgnoreCase((String)valign)) {
                this.cell.setVerticalAlignment(4);
            } else if ("bottom".equalsIgnoreCase((String)valign)) {
                this.cell.setVerticalAlignment(6);
            }
        });
        float border = props.findProperty("border").flatMap(NumberUtilities::parseFloat).orElse(Float.valueOf(0.0f)).floatValue();
        this.cell.setBorderWidth(border);
        props.findProperty("cellpadding").flatMap(NumberUtilities::parseFloat).ifPresent(this.cell::setPadding);
        this.cell.setUseDescender(true);
        this.cell.setBackgroundColor(Markup.decodeColor(props.getProperty("bgcolor")));
    }

    @Override
    public boolean add(Element o) {
        this.cell.addElement(o);
        return true;
    }

    @Override
    public ArrayList<Element> getChunks() {
        return this.chunks;
    }

    @Override
    public boolean process(ElementListener listener) {
        return true;
    }

    @Override
    public int type() {
        return 30;
    }

    public PdfPCell getCell() {
        return this.cell;
    }

    @Override
    public boolean isContent() {
        return true;
    }

    @Override
    public boolean isNestable() {
        return true;
    }
}

