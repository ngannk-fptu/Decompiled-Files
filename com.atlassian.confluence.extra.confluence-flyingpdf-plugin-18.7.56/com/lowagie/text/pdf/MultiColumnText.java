/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Phrase;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfDocument;
import java.util.ArrayList;
import java.util.List;

public class MultiColumnText
implements Element {
    public static final float AUTOMATIC = -1.0f;
    private float desiredHeight;
    private float totalHeight;
    private boolean overflow;
    private float top;
    private ColumnText columnText;
    private List<ColumnDef> columnDefs = new ArrayList<ColumnDef>();
    private boolean simple = true;
    private int currentColumn = 0;
    private float nextY = -1.0f;
    private boolean columnsRightToLeft = false;
    private PdfDocument document;

    public MultiColumnText() {
        this(-1.0f);
    }

    public MultiColumnText(float height) {
        this.desiredHeight = height;
        this.top = -1.0f;
        this.columnText = new ColumnText(null);
        this.totalHeight = 0.0f;
    }

    public MultiColumnText(float top, float height) {
        this.desiredHeight = height;
        this.top = top;
        this.nextY = top;
        this.columnText = new ColumnText(null);
        this.totalHeight = 0.0f;
    }

    public boolean isOverflow() {
        return this.overflow;
    }

    public void useColumnParams(ColumnText sourceColumn) {
        this.columnText.setSimpleVars(sourceColumn);
    }

    public void addColumn(float[] left, float[] right) {
        ColumnDef nextDef = new ColumnDef(left, right);
        if (!nextDef.isSimple()) {
            this.simple = false;
        }
        this.columnDefs.add(nextDef);
    }

    public void addSimpleColumn(float left, float right) {
        ColumnDef newCol = new ColumnDef(left, right);
        this.columnDefs.add(newCol);
    }

    public void addRegularColumns(float left, float right, float gutterWidth, int numColumns) {
        float currX = left;
        float width = right - left;
        float colWidth = (width - gutterWidth * (float)(numColumns - 1)) / (float)numColumns;
        for (int i = 0; i < numColumns; ++i) {
            this.addSimpleColumn(currX, currX + colWidth);
            currX += colWidth + gutterWidth;
        }
    }

    public void addText(Phrase phrase) {
        this.columnText.addText(phrase);
    }

    public void addText(Chunk chunk) {
        this.columnText.addText(chunk);
    }

    public void addElement(Element element) throws DocumentException {
        if (this.simple) {
            this.columnText.addElement(element);
        } else if (element instanceof Phrase) {
            this.columnText.addText((Phrase)element);
        } else if (element instanceof Chunk) {
            this.columnText.addText((Chunk)element);
        } else {
            throw new DocumentException(MessageLocalization.getComposedMessage("can.t.add.1.to.multicolumntext.with.complex.columns", element.getClass()));
        }
    }

    public float write(PdfContentByte canvas, PdfDocument document, float documentY) throws DocumentException {
        this.document = document;
        this.columnText.setCanvas(canvas);
        if (this.columnDefs.isEmpty()) {
            throw new DocumentException(MessageLocalization.getComposedMessage("multicolumntext.has.no.columns"));
        }
        this.overflow = false;
        float currentHeight = 0.0f;
        boolean done = false;
        try {
            while (!done) {
                if (this.top == -1.0f) {
                    this.top = document.getVerticalPosition(true);
                } else if (this.nextY == -1.0f) {
                    this.nextY = document.getVerticalPosition(true);
                }
                ColumnDef currentDef = this.columnDefs.get(this.getCurrentColumn());
                this.columnText.setYLine(this.top);
                float[] left = currentDef.resolvePositions(4);
                float[] right = currentDef.resolvePositions(8);
                if (document.isMarginMirroring() && document.getPageNumber() % 2 == 0) {
                    int i;
                    float delta = document.rightMargin() - document.left();
                    left = (float[])left.clone();
                    right = (float[])right.clone();
                    for (i = 0; i < left.length; i += 2) {
                        int n = i;
                        left[n] = left[n] - delta;
                    }
                    for (i = 0; i < right.length; i += 2) {
                        int n = i;
                        right[n] = right[n] - delta;
                    }
                }
                currentHeight = Math.max(currentHeight, this.getHeight(left, right));
                if (currentDef.isSimple()) {
                    this.columnText.setSimpleColumn(left[2], left[3], right[0], right[1]);
                } else {
                    this.columnText.setColumns(left, right);
                }
                int result = this.columnText.go();
                if ((result & 1) != 0) {
                    done = true;
                    this.top = this.columnText.getYLine();
                    continue;
                }
                if (this.shiftCurrentColumn()) {
                    this.top = this.nextY;
                    continue;
                }
                this.totalHeight += currentHeight;
                if (this.desiredHeight != -1.0f && this.totalHeight >= this.desiredHeight) {
                    this.overflow = true;
                    break;
                }
                documentY = this.nextY;
                this.newPage();
                currentHeight = 0.0f;
            }
        }
        catch (DocumentException ex) {
            ex.printStackTrace();
            throw ex;
        }
        if (this.desiredHeight == -1.0f && this.columnDefs.size() == 1) {
            currentHeight = documentY - this.columnText.getYLine();
        }
        return currentHeight;
    }

    private void newPage() throws DocumentException {
        this.resetCurrentColumn();
        if (this.desiredHeight == -1.0f) {
            this.nextY = -1.0f;
            this.top = -1.0f;
        } else {
            this.top = this.nextY;
        }
        this.totalHeight = 0.0f;
        if (this.document != null) {
            this.document.newPage();
        }
    }

    private float getHeight(float[] left, float[] right) {
        int i;
        float max = Float.MIN_VALUE;
        float min = Float.MAX_VALUE;
        for (i = 0; i < left.length; i += 2) {
            if (left.length <= i + 1) continue;
            min = Math.min(min, left[i + 1]);
            max = Math.max(max, left[i + 1]);
        }
        for (i = 0; i < right.length; i += 2) {
            if (right.length <= i + 1) continue;
            min = Math.min(min, right[i + 1]);
            max = Math.max(max, right[i + 1]);
        }
        return max - min;
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
        return 40;
    }

    @Override
    public ArrayList<Element> getChunks() {
        return null;
    }

    @Override
    public boolean isContent() {
        return true;
    }

    @Override
    public boolean isNestable() {
        return false;
    }

    private float getColumnBottom() {
        if (this.desiredHeight == -1.0f) {
            return this.document.bottom();
        }
        return Math.max(this.top - (this.desiredHeight - this.totalHeight), this.document.bottom());
    }

    public void nextColumn() throws DocumentException {
        this.currentColumn = (this.currentColumn + 1) % this.columnDefs.size();
        this.top = this.nextY;
        if (this.currentColumn == 0) {
            this.newPage();
        }
    }

    public int getCurrentColumn() {
        if (this.columnsRightToLeft) {
            return this.columnDefs.size() - this.currentColumn - 1;
        }
        return this.currentColumn;
    }

    public void resetCurrentColumn() {
        this.currentColumn = 0;
    }

    public boolean shiftCurrentColumn() {
        if (this.currentColumn + 1 < this.columnDefs.size()) {
            ++this.currentColumn;
            return true;
        }
        return false;
    }

    public void setColumnsRightToLeft(boolean direction) {
        this.columnsRightToLeft = direction;
    }

    public void setSpaceCharRatio(float spaceCharRatio) {
        this.columnText.setSpaceCharRatio(spaceCharRatio);
    }

    public void setRunDirection(int runDirection) {
        this.columnText.setRunDirection(runDirection);
    }

    public void setArabicOptions(int arabicOptions) {
        this.columnText.setArabicOptions(arabicOptions);
    }

    public void setAlignment(int alignment) {
        this.columnText.setAlignment(alignment);
    }

    private class ColumnDef {
        private float[] left;
        private float[] right;

        ColumnDef(float[] newLeft, float[] newRight) {
            this.left = newLeft;
            this.right = newRight;
        }

        ColumnDef(float leftPosition, float rightPosition) {
            this.left = new float[4];
            this.left[0] = leftPosition;
            this.left[1] = MultiColumnText.this.top;
            this.left[2] = leftPosition;
            this.left[3] = MultiColumnText.this.desiredHeight == -1.0f || MultiColumnText.this.top == -1.0f ? -1.0f : MultiColumnText.this.top - MultiColumnText.this.desiredHeight;
            this.right = new float[4];
            this.right[0] = rightPosition;
            this.right[1] = MultiColumnText.this.top;
            this.right[2] = rightPosition;
            this.right[3] = MultiColumnText.this.desiredHeight == -1.0f || MultiColumnText.this.top == -1.0f ? -1.0f : MultiColumnText.this.top - MultiColumnText.this.desiredHeight;
        }

        float[] resolvePositions(int side) {
            if (side == 4) {
                return this.resolvePositions(this.left);
            }
            return this.resolvePositions(this.right);
        }

        private float[] resolvePositions(float[] positions) {
            if (!this.isSimple()) {
                positions[1] = MultiColumnText.this.top;
                return positions;
            }
            if (MultiColumnText.this.top == -1.0f) {
                throw new RuntimeException("resolvePositions called with top=AUTOMATIC (-1).  Top position must be set befure lines can be resolved");
            }
            positions[1] = MultiColumnText.this.top;
            positions[3] = MultiColumnText.this.getColumnBottom();
            return positions;
        }

        private boolean isSimple() {
            return this.left.length == 4 && this.right.length == 4 && this.left[0] == this.left[2] && this.right[0] == this.right[2];
        }
    }
}

