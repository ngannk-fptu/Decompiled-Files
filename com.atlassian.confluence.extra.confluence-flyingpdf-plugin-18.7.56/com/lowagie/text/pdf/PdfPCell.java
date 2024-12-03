/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfPCellEvent;
import com.lowagie.text.pdf.PdfPRow;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.events.PdfPCellEventForwarder;
import java.util.List;
import javax.annotation.Nullable;

public class PdfPCell
extends Rectangle {
    private ColumnText column = new ColumnText(null);
    private int verticalAlignment = 4;
    private float paddingLeft = 2.0f;
    private float paddingRight = 2.0f;
    private float paddingTop = 2.0f;
    private float paddingBottom = 2.0f;
    private float fixedHeight = 0.0f;
    private float minimumHeight;
    private boolean noWrap = false;
    private PdfPTable table;
    private int colspan = 1;
    private int rowspan = 1;
    private Image image;
    private PdfPCellEvent cellEvent;
    private boolean useDescender;
    private boolean useBorderPadding = false;
    protected Phrase phrase;
    private int rotation;

    public PdfPCell() {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.borderWidth = 0.5f;
        this.border = 15;
        this.column.setLeading(0.0f, 1.0f);
    }

    public PdfPCell(@Nullable Phrase phrase) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.borderWidth = 0.5f;
        this.border = 15;
        this.phrase = phrase;
        this.column.addText(this.phrase);
        this.column.setLeading(0.0f, 1.0f);
    }

    public PdfPCell(Image image) {
        this(image, false);
    }

    public PdfPCell(Image image, boolean fit) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.borderWidth = 0.5f;
        this.border = 15;
        if (fit) {
            this.image = image;
            this.column.setLeading(0.0f, 1.0f);
            this.setPadding(this.borderWidth / 2.0f);
        } else {
            this.phrase = new Phrase(new Chunk(image, 0.0f, 0.0f));
            this.column.addText(this.phrase);
            this.column.setLeading(0.0f, 1.0f);
            this.setPadding(0.0f);
        }
    }

    public PdfPCell(PdfPTable table) {
        this(table, null);
    }

    public PdfPCell(PdfPTable table, PdfPCell style) {
        super(0.0f, 0.0f, 0.0f, 0.0f);
        this.borderWidth = 0.5f;
        this.border = 15;
        this.column.setLeading(0.0f, 1.0f);
        this.table = table;
        table.setWidthPercentage(100.0f);
        table.setExtendLastRow(true);
        this.column.addElement(table);
        if (style != null) {
            this.cloneNonPositionParameters(style);
            this.verticalAlignment = style.verticalAlignment;
            this.paddingLeft = style.paddingLeft;
            this.paddingRight = style.paddingRight;
            this.paddingTop = style.paddingTop;
            this.paddingBottom = style.paddingBottom;
            this.colspan = style.colspan;
            this.rowspan = style.rowspan;
            this.cellEvent = style.cellEvent;
            this.useDescender = style.useDescender;
            this.useBorderPadding = style.useBorderPadding;
            this.rotation = style.rotation;
        } else {
            this.setPadding(0.0f);
        }
    }

    public PdfPCell(PdfPCell cell) {
        super(cell.llx, cell.lly, cell.urx, cell.ury);
        this.cloneNonPositionParameters(cell);
        this.verticalAlignment = cell.verticalAlignment;
        this.paddingLeft = cell.paddingLeft;
        this.paddingRight = cell.paddingRight;
        this.paddingTop = cell.paddingTop;
        this.paddingBottom = cell.paddingBottom;
        this.phrase = cell.phrase;
        this.fixedHeight = cell.fixedHeight;
        this.minimumHeight = cell.minimumHeight;
        this.noWrap = cell.noWrap;
        this.colspan = cell.colspan;
        this.rowspan = cell.rowspan;
        if (cell.table != null) {
            this.table = new PdfPTable(cell.table);
        }
        this.image = Image.getInstance(cell.image);
        this.cellEvent = cell.cellEvent;
        this.useDescender = cell.useDescender;
        this.column = ColumnText.duplicate(cell.column);
        this.useBorderPadding = cell.useBorderPadding;
        this.rotation = cell.rotation;
    }

    public void addElement(Element element) {
        if (this.table != null) {
            this.table = null;
            this.column.setText(null);
        }
        this.column.addElement(element);
    }

    public Phrase getPhrase() {
        return this.phrase;
    }

    public void setPhrase(Phrase phrase) {
        this.table = null;
        this.image = null;
        this.phrase = phrase;
        this.column.setText(this.phrase);
    }

    public int getHorizontalAlignment() {
        return this.column.getAlignment();
    }

    public void setHorizontalAlignment(int horizontalAlignment) {
        this.column.setAlignment(horizontalAlignment);
    }

    public int getVerticalAlignment() {
        return this.verticalAlignment;
    }

    public void setVerticalAlignment(int verticalAlignment) {
        if (this.table != null) {
            this.table.setExtendLastRow(verticalAlignment == 4);
        }
        this.verticalAlignment = verticalAlignment;
    }

    public float getEffectivePaddingLeft() {
        if (this.isUseBorderPadding()) {
            float border = this.getBorderWidthLeft() / (this.isUseVariableBorders() ? 1.0f : 2.0f);
            return this.paddingLeft + border;
        }
        return this.paddingLeft;
    }

    public float getPaddingLeft() {
        return this.paddingLeft;
    }

    public void setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public float getEffectivePaddingRight() {
        if (this.isUseBorderPadding()) {
            float border = this.getBorderWidthRight() / (this.isUseVariableBorders() ? 1.0f : 2.0f);
            return this.paddingRight + border;
        }
        return this.paddingRight;
    }

    public float getPaddingRight() {
        return this.paddingRight;
    }

    public void setPaddingRight(float paddingRight) {
        this.paddingRight = paddingRight;
    }

    public float getEffectivePaddingTop() {
        if (this.isUseBorderPadding()) {
            float border = this.getBorderWidthTop() / (this.isUseVariableBorders() ? 1.0f : 2.0f);
            return this.paddingTop + border;
        }
        return this.paddingTop;
    }

    public float getPaddingTop() {
        return this.paddingTop;
    }

    public void setPaddingTop(float paddingTop) {
        this.paddingTop = paddingTop;
    }

    public float getEffectivePaddingBottom() {
        if (this.isUseBorderPadding()) {
            float border = this.getBorderWidthBottom() / (this.isUseVariableBorders() ? 1.0f : 2.0f);
            return this.paddingBottom + border;
        }
        return this.paddingBottom;
    }

    public float getPaddingBottom() {
        return this.paddingBottom;
    }

    public void setPaddingBottom(float paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public void setPadding(float padding) {
        this.paddingBottom = padding;
        this.paddingTop = padding;
        this.paddingLeft = padding;
        this.paddingRight = padding;
    }

    public boolean isUseBorderPadding() {
        return this.useBorderPadding;
    }

    public void setUseBorderPadding(boolean use) {
        this.useBorderPadding = use;
    }

    public void setLeading(float fixedLeading, float multipliedLeading) {
        this.column.setLeading(fixedLeading, multipliedLeading);
    }

    public float getLeading() {
        return this.column.getLeading();
    }

    public float getMultipliedLeading() {
        return this.column.getMultipliedLeading();
    }

    public void setIndent(float indent) {
        this.column.setIndent(indent);
    }

    public float getIndent() {
        return this.column.getIndent();
    }

    public float getExtraParagraphSpace() {
        return this.column.getExtraParagraphSpace();
    }

    public void setExtraParagraphSpace(float extraParagraphSpace) {
        this.column.setExtraParagraphSpace(extraParagraphSpace);
    }

    public void setFixedHeight(float fixedHeight) {
        this.fixedHeight = fixedHeight;
        this.minimumHeight = 0.0f;
    }

    public float getFixedHeight() {
        return this.fixedHeight;
    }

    public boolean hasFixedHeight() {
        return this.getFixedHeight() > 0.0f;
    }

    public void setMinimumHeight(float minimumHeight) {
        this.minimumHeight = minimumHeight;
        this.fixedHeight = 0.0f;
    }

    public float getMinimumHeight() {
        return this.minimumHeight;
    }

    public boolean hasMinimumHeight() {
        return this.getMinimumHeight() > 0.0f;
    }

    public boolean isNoWrap() {
        return this.noWrap;
    }

    public void setNoWrap(boolean noWrap) {
        this.noWrap = noWrap;
    }

    public PdfPTable getTable() {
        return this.table;
    }

    void setTable(PdfPTable table) {
        this.table = table;
        this.column.setText(null);
        this.image = null;
        if (table != null) {
            table.setExtendLastRow(this.verticalAlignment == 4);
            this.column.addElement(table);
            table.setWidthPercentage(100.0f);
        }
    }

    public int getColspan() {
        return this.colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }

    public int getRowspan() {
        return this.rowspan;
    }

    public void setRowspan(int rowspan) {
        this.rowspan = rowspan;
    }

    public void setFollowingIndent(float indent) {
        this.column.setFollowingIndent(indent);
    }

    public float getFollowingIndent() {
        return this.column.getFollowingIndent();
    }

    public void setRightIndent(float indent) {
        this.column.setRightIndent(indent);
    }

    public float getRightIndent() {
        return this.column.getRightIndent();
    }

    public float getSpaceCharRatio() {
        return this.column.getSpaceCharRatio();
    }

    public void setSpaceCharRatio(float spaceCharRatio) {
        this.column.setSpaceCharRatio(spaceCharRatio);
    }

    public void setRunDirection(int runDirection) {
        this.column.setRunDirection(runDirection);
    }

    public int getRunDirection() {
        return this.column.getRunDirection();
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.column.setText(null);
        this.table = null;
        this.image = image;
    }

    public PdfPCellEvent getCellEvent() {
        return this.cellEvent;
    }

    public void setCellEvent(PdfPCellEvent cellEvent) {
        if (cellEvent == null) {
            this.cellEvent = null;
        } else if (this.cellEvent == null) {
            this.cellEvent = cellEvent;
        } else if (this.cellEvent instanceof PdfPCellEventForwarder) {
            ((PdfPCellEventForwarder)this.cellEvent).addCellEvent(cellEvent);
        } else {
            PdfPCellEventForwarder forward = new PdfPCellEventForwarder();
            forward.addCellEvent(this.cellEvent);
            forward.addCellEvent(cellEvent);
            this.cellEvent = forward;
        }
    }

    public int getArabicOptions() {
        return this.column.getArabicOptions();
    }

    public void setArabicOptions(int arabicOptions) {
        this.column.setArabicOptions(arabicOptions);
    }

    public boolean isUseAscender() {
        return this.column.isUseAscender();
    }

    public void setUseAscender(boolean useAscender) {
        this.column.setUseAscender(useAscender);
    }

    public boolean isUseDescender() {
        return this.useDescender;
    }

    public void setUseDescender(boolean useDescender) {
        this.useDescender = useDescender;
    }

    public ColumnText getColumn() {
        return this.column;
    }

    public List getCompositeElements() {
        return this.getColumn().compositeElements;
    }

    public void setColumn(ColumnText column) {
        this.column = column;
    }

    @Override
    public int getRotation() {
        return this.rotation;
    }

    @Override
    public void setRotation(int rotation) {
        if ((rotation %= 360) < 0) {
            rotation += 360;
        }
        if (rotation % 90 != 0) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("rotation.must.be.a.multiple.of.90"));
        }
        this.rotation = rotation;
    }

    void consumeHeight(float height) {
        float rightLimit = this.getRight() - this.getEffectivePaddingRight();
        float leftLimit = this.getLeft() + this.getEffectivePaddingLeft();
        float bry = height - this.getEffectivePaddingTop() - this.getEffectivePaddingBottom();
        if (this.getRotation() != 90 && this.getRotation() != 270) {
            this.column.setSimpleColumn(leftLimit, bry + 0.001f, rightLimit, 0.0f);
        } else {
            this.column.setSimpleColumn(0.0f, leftLimit, bry + 0.001f, rightLimit);
        }
        try {
            this.column.go(true);
        }
        catch (DocumentException documentException) {
            // empty catch block
        }
    }

    public float getMaxHeight() {
        boolean pivoted = this.getRotation() == 90 || this.getRotation() == 270;
        Image img = this.getImage();
        if (img != null) {
            img.scalePercent(100.0f);
            float refWidth = pivoted ? img.getScaledHeight() : img.getScaledWidth();
            float scale = (this.getRight() - this.getEffectivePaddingRight() - this.getEffectivePaddingLeft() - this.getLeft()) / refWidth;
            img.scalePercent(scale * 100.0f);
            float refHeight = pivoted ? img.getScaledWidth() : img.getScaledHeight();
            this.setBottom(this.getTop() - this.getEffectivePaddingTop() - this.getEffectivePaddingBottom() - refHeight);
        } else if (pivoted && this.hasFixedHeight() || this.getColumn() == null) {
            this.setBottom(this.getTop() - this.getFixedHeight());
        } else {
            float bottom;
            float left;
            float top;
            float right;
            ColumnText ct = ColumnText.duplicate(this.getColumn());
            if (pivoted) {
                right = 20000.0f;
                top = this.getRight() - this.getEffectivePaddingRight();
                left = 0.0f;
                bottom = this.getLeft() + this.getEffectivePaddingLeft();
            } else {
                right = this.isNoWrap() ? 20000.0f : this.getRight() - this.getEffectivePaddingRight();
                top = this.getTop() - this.getEffectivePaddingTop();
                left = this.getLeft() + this.getEffectivePaddingLeft();
                bottom = this.hasFixedHeight() ? this.getTop() + this.getEffectivePaddingBottom() - this.getFixedHeight() : -1.07374182E9f;
            }
            PdfPRow.setColumn(ct, left, bottom, right, top);
            try {
                ct.go(true);
            }
            catch (DocumentException e) {
                throw new ExceptionConverter(e);
            }
            if (pivoted) {
                this.setBottom(this.getTop() - this.getEffectivePaddingTop() - this.getEffectivePaddingBottom() - ct.getFilledWidth());
            } else {
                float yLine = ct.getYLine();
                if (this.isUseDescender()) {
                    yLine += ct.getDescender();
                }
                this.setBottom(yLine - this.getEffectivePaddingBottom());
            }
        }
        float height = this.getHeight();
        if (this.hasFixedHeight()) {
            height = this.getFixedHeight();
        } else if (height < this.getMinimumHeight()) {
            height = this.getMinimumHeight();
        }
        return height;
    }
}

