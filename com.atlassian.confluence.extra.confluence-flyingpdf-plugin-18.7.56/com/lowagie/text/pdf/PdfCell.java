/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Anchor;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfChunk;
import com.lowagie.text.pdf.PdfLine;
import java.util.ArrayList;
import java.util.Iterator;

public class PdfCell
extends Rectangle {
    private java.util.List<PdfLine> lines;
    private PdfLine line;
    private java.util.List<Image> images;
    private float leading;
    private int rownumber;
    private int rowspan;
    private float cellspacing;
    private float cellpadding;
    private boolean header = false;
    private float contentHeight = 0.0f;
    private boolean useAscender;
    private boolean useDescender;
    private boolean useBorderPadding;
    private int verticalAlignment;
    private PdfLine firstLine;
    private PdfLine lastLine;
    private int groupNumber;

    public PdfCell(Cell cell, int rownumber, float left, float right, float top, float cellspacing, float cellpadding) {
        super(left, top, right, top);
        this.cloneNonPositionParameters(cell);
        this.cellpadding = cellpadding;
        this.cellspacing = cellspacing;
        this.verticalAlignment = cell.getVerticalAlignment();
        this.useAscender = cell.isUseAscender();
        this.useDescender = cell.isUseDescender();
        this.useBorderPadding = cell.isUseBorderPadding();
        this.lines = new ArrayList<PdfLine>();
        this.images = new ArrayList<Image>();
        this.leading = cell.getLeading();
        int alignment = cell.getHorizontalAlignment();
        left += cellspacing + cellpadding;
        right -= cellspacing + cellpadding;
        left += this.getBorderWidthInside(4);
        right -= this.getBorderWidthInside(8);
        this.contentHeight = 0.0f;
        this.rowspan = cell.getRowspan();
        Iterator i = cell.getElements();
        block7: while (i.hasNext()) {
            ArrayList<Element> chunks;
            Element element = (Element)i.next();
            switch (element.type()) {
                case 32: 
                case 33: 
                case 34: 
                case 35: 
                case 36: {
                    this.addImage((Image)element, left, right, 0.4f * this.leading, alignment);
                    continue block7;
                }
                case 14: {
                    if (this.line != null && this.line.size() > 0) {
                        this.line.resetAlignment();
                        this.addLine(this.line);
                    }
                    this.addList((List)element, left, right, alignment);
                    this.line = new PdfLine(left, right, alignment, this.leading);
                    continue block7;
                }
            }
            ArrayList<PdfAction> allActions = new ArrayList<PdfAction>();
            this.processActions(element, null, allActions);
            int aCounter = 0;
            float currentLineLeading = this.leading;
            float currentLeft = left;
            float currentRight = right;
            if (element instanceof Phrase) {
                currentLineLeading = ((Phrase)element).getLeading();
            }
            if (element instanceof Paragraph) {
                Paragraph p = (Paragraph)element;
                currentLeft += p.getIndentationLeft();
                currentRight -= p.getIndentationRight();
            }
            if (this.line == null) {
                this.line = new PdfLine(currentLeft, currentRight, alignment, currentLineLeading);
            }
            if ((chunks = element.getChunks()).isEmpty()) {
                this.addLine(this.line);
                this.line = new PdfLine(currentLeft, currentRight, alignment, currentLineLeading);
            } else {
                for (Object e : chunks) {
                    PdfChunk overflow;
                    Chunk c = (Chunk)e;
                    PdfChunk chunk = new PdfChunk(c, (PdfAction)allActions.get(aCounter++));
                    while ((overflow = this.line.add(chunk)) != null) {
                        this.addLine(this.line);
                        this.line = new PdfLine(currentLeft, currentRight, alignment, currentLineLeading);
                        chunk = overflow;
                    }
                }
            }
            switch (element.type()) {
                case 12: 
                case 13: 
                case 16: {
                    this.line.resetAlignment();
                    this.flushCurrentLine();
                }
            }
        }
        this.flushCurrentLine();
        if (this.lines.size() > cell.getMaxLines()) {
            String more;
            while (this.lines.size() > cell.getMaxLines()) {
                this.removeLine(this.lines.size() - 1);
            }
            if (cell.getMaxLines() > 0 && (more = cell.getShowTruncation()) != null && more.length() > 0) {
                this.lastLine = this.lines.get(this.lines.size() - 1);
                if (this.lastLine.size() >= 0) {
                    PdfChunk lastChunk = this.lastLine.getChunk(this.lastLine.size() - 1);
                    float moreWidth = new PdfChunk(more, lastChunk).width();
                    while (lastChunk.toString().length() > 0 && lastChunk.width() + moreWidth > right - left) {
                        lastChunk.setValue(lastChunk.toString().substring(0, lastChunk.length() - 1));
                    }
                    lastChunk.setValue(lastChunk.toString() + more);
                } else {
                    this.lastLine.add(new PdfChunk(new Chunk(more), null));
                }
            }
        }
        if (this.useDescender && this.lastLine != null) {
            this.contentHeight -= this.lastLine.getDescender();
        }
        if (!this.lines.isEmpty()) {
            this.firstLine = this.lines.get(0);
            float firstLineRealHeight = this.firstLineRealHeight();
            this.contentHeight -= this.firstLine.height();
            this.firstLine.height = firstLineRealHeight;
            this.contentHeight += firstLineRealHeight;
        }
        float newBottom = top - this.contentHeight - 2.0f * this.cellpadding() - 2.0f * this.cellspacing();
        this.setBottom(newBottom -= this.getBorderWidthInside(1) + this.getBorderWidthInside(2));
        this.rownumber = rownumber;
    }

    private void addList(List list, float left, float right, int alignment) {
        ArrayList<PdfAction> allActions = new ArrayList<PdfAction>();
        this.processActions(list, null, allActions);
        int aCounter = 0;
        Iterator<Element> iterator = list.getItems().iterator();
        block4: while (iterator.hasNext()) {
            Element o1;
            Element ele = o1 = iterator.next();
            switch (ele.type()) {
                case 15: {
                    ListItem item = (ListItem)ele;
                    this.line = new PdfLine(left + item.getIndentationLeft(), right, alignment, item.getLeading());
                    this.line.setListItem(item);
                    for (Element o : item.getChunks()) {
                        PdfChunk overflow;
                        PdfChunk chunk = new PdfChunk((Chunk)o, (PdfAction)allActions.get(aCounter++));
                        while ((overflow = this.line.add(chunk)) != null) {
                            this.addLine(this.line);
                            this.line = new PdfLine(left + item.getIndentationLeft(), right, alignment, item.getLeading());
                            chunk = overflow;
                        }
                        this.line.resetAlignment();
                        this.addLine(this.line);
                        this.line = new PdfLine(left + item.getIndentationLeft(), right, alignment, this.leading);
                    }
                    continue block4;
                }
                case 14: {
                    List sublist = (List)ele;
                    this.addList(sublist, left + sublist.getIndentationLeft(), right, alignment);
                }
            }
        }
    }

    @Override
    public void setBottom(float value) {
        super.setBottom(value);
        float firstLineRealHeight = this.firstLineRealHeight();
        float totalHeight = this.ury - value;
        float nonContentHeight = this.cellpadding() * 2.0f + this.cellspacing() * 2.0f;
        float interiorHeight = totalHeight - (nonContentHeight += this.getBorderWidthInside(1) + this.getBorderWidthInside(2));
        float extraHeight = 0.0f;
        switch (this.verticalAlignment) {
            case 6: {
                extraHeight = interiorHeight - this.contentHeight;
                break;
            }
            case 5: {
                extraHeight = (interiorHeight - this.contentHeight) / 2.0f;
                break;
            }
            default: {
                extraHeight = 0.0f;
            }
        }
        extraHeight += this.cellpadding() + this.cellspacing();
        extraHeight += this.getBorderWidthInside(1);
        if (this.firstLine != null) {
            this.firstLine.height = firstLineRealHeight + extraHeight;
        }
    }

    @Override
    public float getLeft() {
        return super.getLeft(this.cellspacing);
    }

    @Override
    public float getRight() {
        return super.getRight(this.cellspacing);
    }

    @Override
    public float getTop() {
        return super.getTop(this.cellspacing);
    }

    @Override
    public float getBottom() {
        return super.getBottom(this.cellspacing);
    }

    private void addLine(PdfLine line) {
        this.lines.add(line);
        this.contentHeight += line.height();
        this.lastLine = line;
        this.line = null;
    }

    private PdfLine removeLine(int index) {
        PdfLine oldLine = this.lines.remove(index);
        this.contentHeight -= oldLine.height();
        if (index == 0 && !this.lines.isEmpty()) {
            this.firstLine = this.lines.get(0);
            float firstLineRealHeight = this.firstLineRealHeight();
            this.contentHeight -= this.firstLine.height();
            this.firstLine.height = firstLineRealHeight;
            this.contentHeight += firstLineRealHeight;
        }
        return oldLine;
    }

    private void flushCurrentLine() {
        if (this.line != null && this.line.size() > 0) {
            this.addLine(this.line);
        }
    }

    private float firstLineRealHeight() {
        PdfChunk chunk;
        float firstLineRealHeight = 0.0f;
        if (this.firstLine != null && (chunk = this.firstLine.getChunk(0)) != null) {
            Image image = chunk.getImage();
            firstLineRealHeight = image != null ? this.firstLine.getChunk(0).getImage().getScaledHeight() : (this.useAscender ? this.firstLine.getAscender() : this.leading);
        }
        return firstLineRealHeight;
    }

    private float getBorderWidthInside(int side) {
        float width = 0.0f;
        if (this.useBorderPadding) {
            switch (side) {
                case 4: {
                    width = this.getBorderWidthLeft();
                    break;
                }
                case 8: {
                    width = this.getBorderWidthRight();
                    break;
                }
                case 1: {
                    width = this.getBorderWidthTop();
                    break;
                }
                default: {
                    width = this.getBorderWidthBottom();
                }
            }
            if (!this.isUseVariableBorders()) {
                width /= 2.0f;
            }
        }
        return width;
    }

    private float addImage(Image i, float left, float right, float extraHeight, int alignment) {
        Image image = Image.getInstance(i);
        if (image.getScaledWidth() > right - left) {
            image.scaleToFit(right - left, Float.MAX_VALUE);
        }
        this.flushCurrentLine();
        if (this.line == null) {
            this.line = new PdfLine(left, right, alignment, this.leading);
        }
        PdfLine imageLine = this.line;
        right -= left;
        left = 0.0f;
        if ((image.getAlignment() & 2) == 2) {
            left = right - image.getScaledWidth();
        } else if ((image.getAlignment() & 1) == 1) {
            left += (right - left - image.getScaledWidth()) / 2.0f;
        }
        Chunk imageChunk = new Chunk(image, left, 0.0f);
        imageLine.add(new PdfChunk(imageChunk, null));
        this.addLine(imageLine);
        return imageLine.height();
    }

    public ArrayList<PdfLine> getLines(float top, float bottom) {
        float currentPosition = Math.min(this.getTop(), top);
        this.setTop(currentPosition + this.cellspacing);
        ArrayList<PdfLine> result = new ArrayList<PdfLine>();
        if (this.getTop() < bottom) {
            return result;
        }
        int size = this.lines.size();
        boolean aboveBottom = true;
        for (int i = 0; i < size && aboveBottom; ++i) {
            this.line = this.lines.get(i);
            float lineHeight = this.line.height();
            if ((currentPosition -= lineHeight) > bottom + this.cellpadding + this.getBorderWidthInside(2)) {
                result.add(this.line);
                continue;
            }
            aboveBottom = false;
        }
        float difference = 0.0f;
        if (!this.header) {
            if (aboveBottom) {
                this.lines = new ArrayList<PdfLine>();
                this.contentHeight = 0.0f;
            } else {
                size = result.size();
                for (int i = 0; i < size; ++i) {
                    this.line = this.removeLine(0);
                    difference += this.line.height();
                }
            }
        }
        if (difference > 0.0f) {
            for (Image image : this.images) {
                image.setAbsolutePosition(image.getAbsoluteX(), image.getAbsoluteY() - difference - this.leading);
            }
        }
        return result;
    }

    public ArrayList<Image> getImages(float top, float bottom) {
        if (this.getTop() < bottom) {
            return new ArrayList<Image>();
        }
        top = Math.min(this.getTop(), top);
        ArrayList<Image> result = new ArrayList<Image>();
        Iterator<Image> i = this.images.iterator();
        while (i.hasNext() && !this.header) {
            Image image = i.next();
            float height = image.getAbsoluteY();
            if (!(top - height > bottom + this.cellpadding)) continue;
            image.setAbsolutePosition(image.getAbsoluteX(), top - height);
            result.add(image);
            i.remove();
        }
        return result;
    }

    boolean isHeader() {
        return this.header;
    }

    void setHeader() {
        this.header = true;
    }

    boolean mayBeRemoved() {
        return this.header || this.lines.isEmpty() && this.images.isEmpty();
    }

    public int size() {
        return this.lines.size();
    }

    private float remainingLinesHeight() {
        if (this.lines.isEmpty()) {
            return 0.0f;
        }
        float result = 0.0f;
        int size = this.lines.size();
        Iterator<PdfLine> iterator = this.lines.iterator();
        while (iterator.hasNext()) {
            PdfLine line1;
            PdfLine line = line1 = iterator.next();
            result += line.height();
        }
        return result;
    }

    public float remainingHeight() {
        float result = 0.0f;
        Iterator<Image> iterator = this.images.iterator();
        while (iterator.hasNext()) {
            Image image1;
            Image image = image1 = iterator.next();
            result += image.getScaledHeight();
        }
        return this.remainingLinesHeight() + this.cellspacing + 2.0f * this.cellpadding + result;
    }

    public float leading() {
        return this.leading;
    }

    public int rownumber() {
        return this.rownumber;
    }

    public int rowspan() {
        return this.rowspan;
    }

    public float cellspacing() {
        return this.cellspacing;
    }

    public float cellpadding() {
        return this.cellpadding;
    }

    protected void processActions(Element element, PdfAction action, java.util.List<PdfAction> allActions) {
        String url;
        if (element.type() == 17 && (url = ((Anchor)element).getReference()) != null) {
            action = new PdfAction(url);
        }
        switch (element.type()) {
            case 11: 
            case 12: 
            case 13: 
            case 15: 
            case 16: 
            case 17: {
                Iterator<Object> i = ((ArrayList)((Object)element)).iterator();
                while (i.hasNext()) {
                    this.processActions((Element)i.next(), action, allActions);
                }
                break;
            }
            case 10: {
                allActions.add(action);
                break;
            }
            case 14: {
                Iterator<Object> i = ((List)element).getItems().iterator();
                while (i.hasNext()) {
                    this.processActions((Element)i.next(), action, allActions);
                }
                break;
            }
            default: {
                int n = element.getChunks().size();
                while (n-- > 0) {
                    allActions.add(action);
                }
                break block0;
            }
        }
    }

    public int getGroupNumber() {
        return this.groupNumber;
    }

    void setGroupNumber(int number) {
        this.groupNumber = number;
    }

    @Override
    public Rectangle rectangle(float top, float bottom) {
        Rectangle tmp = new Rectangle(this.getLeft(), this.getBottom(), this.getRight(), this.getTop());
        tmp.cloneNonPositionParameters(this);
        if (this.getTop() > top) {
            tmp.setTop(top);
            tmp.setBorder(this.border - (this.border & 1));
        }
        if (this.getBottom() < bottom) {
            tmp.setBottom(bottom);
            tmp.setBorder(this.border - (this.border & 2));
        }
        return tmp;
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
}

