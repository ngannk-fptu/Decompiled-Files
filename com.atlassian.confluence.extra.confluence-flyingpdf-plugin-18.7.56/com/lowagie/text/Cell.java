/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.ElementTags;
import com.lowagie.text.List;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.TableRectangle;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.alignment.VerticalAlignment;
import com.lowagie.text.alignment.WithHorizontalAlignment;
import com.lowagie.text.alignment.WithVerticalAlignment;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfPCell;
import java.util.ArrayList;
import java.util.Iterator;

public class Cell
extends TableRectangle
implements TextElementArray,
WithHorizontalAlignment,
WithVerticalAlignment {
    protected java.util.List<Element> arrayList = null;
    protected int horizontalAlignment = -1;
    protected int verticalAlignment = -1;
    protected float width;
    protected boolean percentage = false;
    protected int colspan = 1;
    protected int rowspan = 1;
    float leading = Float.NaN;
    protected boolean header;
    protected int maxLines = Integer.MAX_VALUE;
    String showTruncation;
    protected boolean useAscender = false;
    protected boolean useDescender = false;
    protected boolean useBorderPadding;
    protected boolean groupChange = true;

    public Cell() {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.setBorder(-1);
        this.setBorderWidth(0.5f);
        this.arrayList = new ArrayList<Element>();
    }

    public Cell(boolean dummy) {
        this();
        this.arrayList.add(new Paragraph(0.0f));
    }

    public Cell(String content) {
        this();
        try {
            this.addElement(new Paragraph(content));
        }
        catch (BadElementException badElementException) {
            // empty catch block
        }
    }

    public Cell(Element element) throws BadElementException {
        this();
        if (element instanceof Phrase) {
            this.setLeading(((Phrase)element).getLeading());
        }
        this.addElement(element);
    }

    @Override
    public boolean process(ElementListener listener) {
        try {
            return listener.add(this);
        }
        catch (DocumentException de) {
            return false;
        }
    }

    @Override
    public int type() {
        return 20;
    }

    @Override
    public ArrayList<Element> getChunks() {
        ArrayList<Element> tmp = new ArrayList<Element>();
        for (Element o : this.arrayList) {
            tmp.addAll(o.getChunks());
        }
        return tmp;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public void setHorizontalAlignment(int value) {
        this.horizontalAlignment = value;
    }

    public void setHorizontalAlignment(String alignment) {
        this.setHorizontalAlignment(ElementTags.alignmentValue(alignment));
    }

    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public void setVerticalAlignment(int value) {
        this.verticalAlignment = value;
    }

    public void setVerticalAlignment(String alignment) {
        this.setVerticalAlignment(ElementTags.alignmentValue(alignment));
    }

    public void setWidth(float value) {
        this.width = value;
    }

    public void setWidth(String value) {
        if (value.endsWith("%")) {
            value = value.substring(0, value.length() - 1);
            this.percentage = true;
        }
        this.width = Integer.parseInt(value);
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    public String getWidthAsString() {
        String w = String.valueOf(this.width);
        if (w.endsWith(".0")) {
            w = w.substring(0, w.length() - 2);
        }
        if (this.percentage) {
            w = w + "%";
        }
        return w;
    }

    public void setColspan(int value) {
        this.colspan = value;
    }

    public int getColspan() {
        return this.colspan;
    }

    public void setRowspan(int value) {
        this.rowspan = value;
    }

    public int getRowspan() {
        return this.rowspan;
    }

    public void setLeading(float value) {
        this.leading = value;
    }

    public float getLeading() {
        if (Float.isNaN(this.leading)) {
            return 16.0f;
        }
        return this.leading;
    }

    public void setHeader(boolean value) {
        this.header = value;
    }

    public boolean isHeader() {
        return this.header;
    }

    public void setMaxLines(int value) {
        this.maxLines = value;
    }

    public int getMaxLines() {
        return this.maxLines;
    }

    public void setShowTruncation(String value) {
        this.showTruncation = value;
    }

    public String getShowTruncation() {
        return this.showTruncation;
    }

    public void setUseAscender(boolean use) {
        this.useAscender = use;
    }

    public boolean isUseAscender() {
        return this.useAscender;
    }

    public void setUseDescender(boolean use) {
        this.useDescender = use;
    }

    public boolean isUseDescender() {
        return this.useDescender;
    }

    public void setUseBorderPadding(boolean use) {
        this.useBorderPadding = use;
    }

    public boolean isUseBorderPadding() {
        return this.useBorderPadding;
    }

    public boolean getGroupChange() {
        return this.groupChange;
    }

    public void setGroupChange(boolean value) {
        this.groupChange = value;
    }

    public int size() {
        return this.arrayList.size();
    }

    public Iterator getElements() {
        return this.arrayList.iterator();
    }

    public void clear() {
        this.arrayList.clear();
    }

    public boolean isEmpty() {
        switch (this.size()) {
            case 0: {
                return true;
            }
            case 1: {
                Element element = this.arrayList.get(0);
                switch (element.type()) {
                    case 10: {
                        return ((Chunk)element).isEmpty();
                    }
                    case 11: 
                    case 12: 
                    case 17: {
                        return ((Phrase)element).isEmpty();
                    }
                    case 14: {
                        return ((List)element).isEmpty();
                    }
                }
                return false;
            }
        }
        return false;
    }

    void fill() {
        if (this.size() == 0) {
            this.arrayList.add(new Paragraph(0.0f));
        }
    }

    public boolean isTable() {
        return this.size() == 1 && this.arrayList.get(0).type() == 22;
    }

    public void addElement(Element element) throws BadElementException {
        if (this.isTable()) {
            Table table = (Table)this.arrayList.get(0);
            Cell tmp = new Cell(element);
            tmp.setBorder(0);
            tmp.setColspan(table.getColumns());
            table.addCell(tmp);
            return;
        }
        switch (element.type()) {
            case 15: 
            case 20: 
            case 21: {
                throw new BadElementException(MessageLocalization.getComposedMessage("you.can.t.add.listitems.rows.or.cells.to.a.cell"));
            }
            case 14: {
                List list = (List)element;
                if (Float.isNaN(this.leading)) {
                    this.setLeading(list.getTotalLeading());
                }
                if (list.isEmpty()) {
                    return;
                }
                this.arrayList.add(element);
                return;
            }
            case 11: 
            case 12: 
            case 17: {
                Phrase p = (Phrase)element;
                if (Float.isNaN(this.leading)) {
                    this.setLeading(p.getLeading());
                }
                if (p.isEmpty()) {
                    return;
                }
                this.arrayList.add(element);
                return;
            }
            case 10: {
                if (((Chunk)element).isEmpty()) {
                    return;
                }
                this.arrayList.add(element);
                return;
            }
            case 22: {
                Cell tmp;
                Table table = new Table(3);
                float[] widths = new float[3];
                widths[1] = ((Table)element).getWidth();
                switch (((Table)element).getAlignment()) {
                    case 0: {
                        widths[0] = 0.0f;
                        widths[2] = 100.0f - widths[1];
                        break;
                    }
                    case 1: {
                        widths[0] = (100.0f - widths[1]) / 2.0f;
                        widths[2] = widths[0];
                        break;
                    }
                    case 2: {
                        widths[0] = 100.0f - widths[1];
                        widths[2] = 0.0f;
                    }
                }
                table.setWidths(widths);
                if (this.arrayList.isEmpty()) {
                    table.addCell(Cell.getDummyCell());
                } else {
                    tmp = new Cell();
                    tmp.setBorder(0);
                    tmp.setColspan(3);
                    for (Element o : this.arrayList) {
                        tmp.add(o);
                    }
                    table.addCell(tmp);
                }
                tmp = new Cell();
                tmp.setBorder(0);
                table.addCell(tmp);
                table.insertTable((Table)element);
                tmp = new Cell();
                tmp.setBorder(0);
                table.addCell(tmp);
                table.addCell(Cell.getDummyCell());
                this.clear();
                this.arrayList.add(table);
                return;
            }
        }
        this.arrayList.add(element);
    }

    @Override
    public boolean add(Element o) {
        try {
            this.addElement(o);
            return true;
        }
        catch (ClassCastException cce) {
            throw new ClassCastException(MessageLocalization.getComposedMessage("you.can.only.add.objects.that.implement.the.element.interface"));
        }
        catch (BadElementException bee) {
            throw new ClassCastException(bee.getMessage());
        }
    }

    private static Cell getDummyCell() {
        Cell cell = new Cell(true);
        cell.setColspan(3);
        cell.setBorder(0);
        return cell;
    }

    public PdfPCell createPdfPCell() throws BadElementException {
        if (this.rowspan > 1) {
            throw new BadElementException(MessageLocalization.getComposedMessage("pdfpcells.can.t.have.a.rowspan.gt.1"));
        }
        if (this.isTable()) {
            return new PdfPCell(((Table)this.arrayList.get(0)).createPdfPTable());
        }
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(this.verticalAlignment);
        cell.setHorizontalAlignment(this.horizontalAlignment);
        cell.setColspan(this.colspan);
        cell.setUseBorderPadding(this.useBorderPadding);
        cell.setUseDescender(this.useDescender);
        cell.setLeading(this.getLeading(), 0.0f);
        cell.cloneNonPositionParameters(this);
        cell.setNoWrap(this.getMaxLines() == 1);
        Iterator i = this.getElements();
        while (i.hasNext()) {
            Element e = (Element)i.next();
            if (e.type() == 11 || e.type() == 12) {
                Paragraph p = new Paragraph((Phrase)e);
                p.setAlignment(this.horizontalAlignment);
                e = p;
            }
            cell.addElement(e);
        }
        return cell;
    }

    @Override
    public float getTop() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }

    @Override
    public float getBottom() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }

    @Override
    public float getLeft() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }

    @Override
    public float getRight() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }

    public float top(int margin) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }

    public float bottom(int margin) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }

    public float left(int margin) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }

    public float right(int margin) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.can.t.be.calculated.see.the.faq"));
    }

    public void setTop(int value) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.are.attributed.automagically.see.the.faq"));
    }

    public void setBottom(int value) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.are.attributed.automagically.see.the.faq"));
    }

    public void setLeft(int value) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.are.attributed.automagically.see.the.faq"));
    }

    public void setRight(int value) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("dimensions.of.a.cell.are.attributed.automagically.see.the.faq"));
    }

    @Override
    public void setHorizontalAlignment(HorizontalAlignment alignment) {
        if (alignment == null) {
            return;
        }
        this.horizontalAlignment = alignment.getId();
    }

    @Override
    public void setVerticalAlignment(VerticalAlignment alignment) {
        if (alignment == null) {
            return;
        }
        this.verticalAlignment = alignment.getId();
    }
}

