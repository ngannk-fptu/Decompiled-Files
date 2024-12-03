/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.alignment.VerticalAlignment;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPCellEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class SimpleCell
extends Rectangle
implements PdfPCellEvent,
TextElementArray {
    public static final boolean ROW = true;
    public static final boolean CELL = false;
    private List<Element> content = new ArrayList<Element>();
    private float width = 0.0f;
    private float widthpercentage = 0.0f;
    private float spacing_left = Float.NaN;
    private float spacing_right = Float.NaN;
    private float spacing_top = Float.NaN;
    private float spacing_bottom = Float.NaN;
    private float padding_left = Float.NaN;
    private float padding_right = Float.NaN;
    private float padding_top = Float.NaN;
    private float padding_bottom = Float.NaN;
    private int colspan = 1;
    private int horizontalAlignment = -1;
    private int verticalAlignment = -1;
    private boolean cellgroup = false;
    protected boolean useAscender = false;
    protected boolean useDescender = false;
    protected boolean useBorderPadding;

    public SimpleCell(boolean row) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.cellgroup = row;
        this.setBorder(15);
    }

    public void addElement(Element element) throws BadElementException {
        if (this.cellgroup) {
            if (element instanceof SimpleCell) {
                if (((SimpleCell)element).isCellgroup()) {
                    throw new BadElementException(MessageLocalization.getComposedMessage("you.can.t.add.one.row.to.another.row"));
                }
                this.content.add(element);
                return;
            }
            throw new BadElementException(MessageLocalization.getComposedMessage("you.can.only.add.cells.to.rows.no.objects.of.type.1", element.getClass().getName()));
        }
        if (element.type() != 12 && element.type() != 11 && element.type() != 17 && element.type() != 10 && element.type() != 14 && element.type() != 50 && element.type() != 32 && element.type() != 33 && element.type() != 36 && element.type() != 34 && element.type() != 35) {
            throw new BadElementException(MessageLocalization.getComposedMessage("you.can.t.add.an.element.of.type.1.to.a.simplecell", element.getClass().getName()));
        }
        this.content.add(element);
    }

    public Cell createCell(SimpleCell rowAttributes) throws BadElementException {
        Cell cell = new Cell();
        cell.cloneNonPositionParameters(rowAttributes);
        cell.softCloneNonPositionParameters(this);
        cell.setColspan(this.colspan);
        Optional<HorizontalAlignment> hAlignment = HorizontalAlignment.of(this.horizontalAlignment);
        cell.setHorizontalAlignment(hAlignment.orElse(HorizontalAlignment.UNDEFINED));
        Optional<VerticalAlignment> vAlignment = VerticalAlignment.of(this.verticalAlignment);
        cell.setVerticalAlignment(vAlignment.orElse(VerticalAlignment.UNDEFINED));
        cell.setUseAscender(this.useAscender);
        cell.setUseBorderPadding(this.useBorderPadding);
        cell.setUseDescender(this.useDescender);
        Iterator<Element> iterator = this.content.iterator();
        while (iterator.hasNext()) {
            Element o;
            Element element = o = iterator.next();
            cell.addElement(element);
        }
        return cell;
    }

    public PdfPCell createPdfPCell(SimpleCell rowAttributes) {
        float p;
        float sp_bottom;
        float sp_top;
        float sp_right;
        float sp_left;
        PdfPCell cell = new PdfPCell();
        cell.setBorder(0);
        SimpleCell tmp = new SimpleCell(false);
        tmp.setSpacing_left(this.spacing_left);
        tmp.setSpacing_right(this.spacing_right);
        tmp.setSpacing_top(this.spacing_top);
        tmp.setSpacing_bottom(this.spacing_bottom);
        tmp.cloneNonPositionParameters(rowAttributes);
        tmp.softCloneNonPositionParameters(this);
        cell.setCellEvent(tmp);
        cell.setHorizontalAlignment(rowAttributes.horizontalAlignment);
        cell.setVerticalAlignment(rowAttributes.verticalAlignment);
        cell.setUseAscender(rowAttributes.useAscender);
        cell.setUseBorderPadding(rowAttributes.useBorderPadding);
        cell.setUseDescender(rowAttributes.useDescender);
        cell.setColspan(this.colspan);
        if (this.horizontalAlignment != -1) {
            cell.setHorizontalAlignment(this.horizontalAlignment);
        }
        if (this.verticalAlignment != -1) {
            cell.setVerticalAlignment(this.verticalAlignment);
        }
        if (this.useAscender) {
            cell.setUseAscender(this.useAscender);
        }
        if (this.useBorderPadding) {
            cell.setUseBorderPadding(this.useBorderPadding);
        }
        if (this.useDescender) {
            cell.setUseDescender(this.useDescender);
        }
        if (Float.isNaN(sp_left = this.spacing_left)) {
            sp_left = 0.0f;
        }
        if (Float.isNaN(sp_right = this.spacing_right)) {
            sp_right = 0.0f;
        }
        if (Float.isNaN(sp_top = this.spacing_top)) {
            sp_top = 0.0f;
        }
        if (Float.isNaN(sp_bottom = this.spacing_bottom)) {
            sp_bottom = 0.0f;
        }
        if (Float.isNaN(p = this.padding_left)) {
            p = 0.0f;
        }
        cell.setPaddingLeft(p + sp_left);
        p = this.padding_right;
        if (Float.isNaN(p)) {
            p = 0.0f;
        }
        cell.setPaddingRight(p + sp_right);
        p = this.padding_top;
        if (Float.isNaN(p)) {
            p = 0.0f;
        }
        cell.setPaddingTop(p + sp_top);
        p = this.padding_bottom;
        if (Float.isNaN(p)) {
            p = 0.0f;
        }
        cell.setPaddingBottom(p + sp_bottom);
        Iterator<Element> iterator = this.content.iterator();
        while (iterator.hasNext()) {
            Element o;
            Element element = o = iterator.next();
            cell.addElement(element);
        }
        return cell;
    }

    @Override
    public void cellLayout(PdfPCell cell, Rectangle position, PdfContentByte[] canvases) {
        float sp_bottom;
        float sp_top;
        float sp_right;
        float sp_left = this.spacing_left;
        if (Float.isNaN(sp_left)) {
            sp_left = 0.0f;
        }
        if (Float.isNaN(sp_right = this.spacing_right)) {
            sp_right = 0.0f;
        }
        if (Float.isNaN(sp_top = this.spacing_top)) {
            sp_top = 0.0f;
        }
        if (Float.isNaN(sp_bottom = this.spacing_bottom)) {
            sp_bottom = 0.0f;
        }
        Rectangle rect = new Rectangle(position.getLeft(sp_left), position.getBottom(sp_bottom), position.getRight(sp_right), position.getTop(sp_top));
        rect.cloneNonPositionParameters(this);
        canvases[1].rectangle(rect);
        rect.setBackgroundColor(null);
        canvases[2].rectangle(rect);
    }

    public void setPadding(float padding) {
        if (Float.isNaN(this.padding_right)) {
            this.setPadding_right(padding);
        }
        if (Float.isNaN(this.padding_left)) {
            this.setPadding_left(padding);
        }
        if (Float.isNaN(this.padding_top)) {
            this.setPadding_top(padding);
        }
        if (Float.isNaN(this.padding_bottom)) {
            this.setPadding_bottom(padding);
        }
    }

    public int getColspan() {
        return this.colspan;
    }

    public void setColspan(int colspan) {
        if (colspan > 0) {
            this.colspan = colspan;
        }
    }

    public float getPadding_bottom() {
        return this.padding_bottom;
    }

    public void setPadding_bottom(float padding_bottom) {
        this.padding_bottom = padding_bottom;
    }

    public float getPadding_left() {
        return this.padding_left;
    }

    public void setPadding_left(float padding_left) {
        this.padding_left = padding_left;
    }

    public float getPadding_right() {
        return this.padding_right;
    }

    public void setPadding_right(float padding_right) {
        this.padding_right = padding_right;
    }

    public float getPadding_top() {
        return this.padding_top;
    }

    public void setPadding_top(float padding_top) {
        this.padding_top = padding_top;
    }

    public float getSpacing_left() {
        return this.spacing_left;
    }

    public float getSpacing_right() {
        return this.spacing_right;
    }

    public float getSpacing_top() {
        return this.spacing_top;
    }

    public float getSpacing_bottom() {
        return this.spacing_bottom;
    }

    public void setSpacing(float spacing) {
        this.spacing_left = spacing;
        this.spacing_right = spacing;
        this.spacing_top = spacing;
        this.spacing_bottom = spacing;
    }

    public void setSpacing_left(float spacing) {
        this.spacing_left = spacing;
    }

    public void setSpacing_right(float spacing) {
        this.spacing_right = spacing;
    }

    public void setSpacing_top(float spacing) {
        this.spacing_top = spacing;
    }

    public void setSpacing_bottom(float spacing) {
        this.spacing_bottom = spacing;
    }

    public boolean isCellgroup() {
        return this.cellgroup;
    }

    public void setCellgroup(boolean cellgroup) {
        this.cellgroup = cellgroup;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public void setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidthpercentage() {
        return this.widthpercentage;
    }

    public void setWidthpercentage(float widthpercentage) {
        this.widthpercentage = widthpercentage;
    }

    public boolean isUseAscender() {
        return this.useAscender;
    }

    public void setUseAscender(boolean useAscender) {
        this.useAscender = useAscender;
    }

    public boolean isUseBorderPadding() {
        return this.useBorderPadding;
    }

    public void setUseBorderPadding(boolean useBorderPadding) {
        this.useBorderPadding = useBorderPadding;
    }

    public boolean isUseDescender() {
        return this.useDescender;
    }

    public void setUseDescender(boolean useDescender) {
        this.useDescender = useDescender;
    }

    List<Element> getContent() {
        return this.content;
    }

    @Override
    public boolean add(Element o) {
        try {
            this.addElement(o);
            return true;
        }
        catch (ClassCastException e) {
            return false;
        }
        catch (BadElementException e) {
            throw new ExceptionConverter(e);
        }
    }

    @Override
    public int type() {
        return 20;
    }
}

