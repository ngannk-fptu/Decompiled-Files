/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.BadElementException;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.SimpleCell;
import com.lowagie.text.Table;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPTableEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SimpleTable
extends Rectangle
implements PdfPTableEvent,
TextElementArray {
    private List<Element> content = new ArrayList<Element>();
    private float width = 0.0f;
    private float widthpercentage = 0.0f;
    private float cellspacing;
    private float cellpadding;
    private int alignment;

    public SimpleTable() {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.setBorder(15);
        this.setBorderWidth(2.0f);
    }

    public void addElement(SimpleCell element) throws BadElementException {
        if (!element.isCellgroup()) {
            throw new BadElementException(MessageLocalization.getComposedMessage("you.can.t.add.cells.to.a.table.directly.add.them.to.a.row.first"));
        }
        this.content.add(element);
    }

    public Table createTable() throws BadElementException {
        int i;
        SimpleCell cell;
        if (this.content.isEmpty()) {
            throw new BadElementException(MessageLocalization.getComposedMessage("trying.to.create.a.table.without.rows"));
        }
        SimpleCell row = (SimpleCell)this.content.get(0);
        int columns = 0;
        for (Element o2 : row.getContent()) {
            cell = (SimpleCell)o2;
            columns += cell.getColspan();
        }
        float[] widths = new float[columns];
        float[] widthpercentages = new float[columns];
        Table table = new Table(columns);
        Optional<HorizontalAlignment> of = HorizontalAlignment.of(this.alignment);
        table.setHorizontalAlignment(of.orElse(HorizontalAlignment.UNDEFINED));
        table.setSpacing(this.cellspacing);
        table.setPadding(this.cellpadding);
        table.cloneNonPositionParameters(this);
        for (Element o1 : this.content) {
            row = (SimpleCell)o1;
            int pos = 0;
            for (Element o : row.getContent()) {
                cell = (SimpleCell)o;
                table.addCell(cell.createCell(row));
                if (cell.getColspan() == 1) {
                    if (cell.getWidth() > 0.0f) {
                        widths[pos] = cell.getWidth();
                    }
                    if (cell.getWidthpercentage() > 0.0f) {
                        widthpercentages[pos] = cell.getWidthpercentage();
                    }
                }
                pos += cell.getColspan();
            }
        }
        float sumWidths = 0.0f;
        for (i = 0; i < columns; ++i) {
            if (widths[i] == 0.0f) {
                sumWidths = 0.0f;
                break;
            }
            sumWidths += widths[i];
        }
        if (sumWidths > 0.0f) {
            table.setWidth(sumWidths);
            table.setLocked(true);
            table.setWidths(widths);
        } else {
            for (i = 0; i < columns; ++i) {
                if (widthpercentages[i] == 0.0f) {
                    sumWidths = 0.0f;
                    break;
                }
                sumWidths += widthpercentages[i];
            }
            if (sumWidths > 0.0f) {
                table.setWidths(widthpercentages);
            }
        }
        if (this.width > 0.0f) {
            table.setWidth(this.width);
            table.setLocked(true);
        } else if (this.widthpercentage > 0.0f) {
            table.setWidth(this.widthpercentage);
        }
        return table;
    }

    public PdfPTable createPdfPTable() throws DocumentException {
        int i;
        SimpleCell cell;
        if (this.content.isEmpty()) {
            throw new BadElementException(MessageLocalization.getComposedMessage("trying.to.create.a.table.without.rows"));
        }
        SimpleCell row = (SimpleCell)this.content.get(0);
        int columns = 0;
        for (Element o2 : row.getContent()) {
            cell = (SimpleCell)o2;
            columns += cell.getColspan();
        }
        float[] widths = new float[columns];
        float[] widthpercentages = new float[columns];
        PdfPTable table = new PdfPTable(columns);
        table.setTableEvent(this);
        table.setHorizontalAlignment(this.alignment);
        for (Element o1 : this.content) {
            row = (SimpleCell)o1;
            int pos = 0;
            for (Element o : row.getContent()) {
                cell = (SimpleCell)o;
                if (Float.isNaN(cell.getSpacing_left())) {
                    cell.setSpacing_left(this.cellspacing / 2.0f);
                }
                if (Float.isNaN(cell.getSpacing_right())) {
                    cell.setSpacing_right(this.cellspacing / 2.0f);
                }
                if (Float.isNaN(cell.getSpacing_top())) {
                    cell.setSpacing_top(this.cellspacing / 2.0f);
                }
                if (Float.isNaN(cell.getSpacing_bottom())) {
                    cell.setSpacing_bottom(this.cellspacing / 2.0f);
                }
                cell.setPadding(this.cellpadding);
                table.addCell(cell.createPdfPCell(row));
                if (cell.getColspan() == 1) {
                    if (cell.getWidth() > 0.0f) {
                        widths[pos] = cell.getWidth();
                    }
                    if (cell.getWidthpercentage() > 0.0f) {
                        widthpercentages[pos] = cell.getWidthpercentage();
                    }
                }
                pos += cell.getColspan();
            }
        }
        float sumWidths = 0.0f;
        for (i = 0; i < columns; ++i) {
            if (widths[i] == 0.0f) {
                sumWidths = 0.0f;
                break;
            }
            sumWidths += widths[i];
        }
        if (sumWidths > 0.0f) {
            table.setTotalWidth(sumWidths);
            table.setWidths(widths);
        } else {
            for (i = 0; i < columns; ++i) {
                if (widthpercentages[i] == 0.0f) {
                    sumWidths = 0.0f;
                    break;
                }
                sumWidths += widthpercentages[i];
            }
            if (sumWidths > 0.0f) {
                table.setWidths(widthpercentages);
            }
        }
        if (this.width > 0.0f) {
            table.setTotalWidth(this.width);
        }
        if (this.widthpercentage > 0.0f) {
            table.setWidthPercentage(this.widthpercentage);
        }
        return table;
    }

    @Override
    public void tableLayout(PdfPTable table, float[][] widths, float[] heights, int headerRows, int rowStart, PdfContentByte[] canvases) {
        float[] width = widths[0];
        Rectangle rect = new Rectangle(width[0], heights[heights.length - 1], width[width.length - 1], heights[0]);
        rect.cloneNonPositionParameters(this);
        int bd = rect.getBorder();
        rect.setBorder(0);
        canvases[1].rectangle(rect);
        rect.setBorder(bd);
        rect.setBackgroundColor(null);
        canvases[2].rectangle(rect);
    }

    public float getCellpadding() {
        return this.cellpadding;
    }

    public void setCellpadding(float cellpadding) {
        this.cellpadding = cellpadding;
    }

    public float getCellspacing() {
        return this.cellspacing;
    }

    public void setCellspacing(float cellspacing) {
        this.cellspacing = cellspacing;
    }

    public int getAlignment() {
        return this.alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
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

    @Override
    public int type() {
        return 22;
    }

    @Override
    public boolean isNestable() {
        return true;
    }

    @Override
    public boolean add(Element o) {
        try {
            this.addElement((SimpleCell)o);
            return true;
        }
        catch (ClassCastException e) {
            return false;
        }
        catch (BadElementException e) {
            throw new ExceptionConverter(e);
        }
    }
}

