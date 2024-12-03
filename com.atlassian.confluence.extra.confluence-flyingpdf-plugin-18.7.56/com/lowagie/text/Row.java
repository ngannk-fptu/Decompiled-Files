/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text;

import com.lowagie.text.Cell;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementListener;
import com.lowagie.text.Table;
import com.lowagie.text.TableRectangle;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.alignment.WithHorizontalAlignment;
import com.lowagie.text.error_messages.MessageLocalization;
import java.util.ArrayList;

public class Row
implements Element,
WithHorizontalAlignment {
    public static final int NULL = 0;
    public static final int CELL = 1;
    public static final int TABLE = 2;
    protected int columns;
    protected int currentColumn;
    protected boolean[] reserved;
    protected TableRectangle[] cells;
    protected int horizontalAlignment;

    protected Row(int columns) {
        this.columns = columns;
        this.reserved = new boolean[columns];
        this.cells = new TableRectangle[columns];
        this.currentColumn = 0;
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
        return 21;
    }

    @Override
    public ArrayList<Element> getChunks() {
        return new ArrayList<Element>();
    }

    @Override
    public boolean isContent() {
        return true;
    }

    @Override
    public boolean isNestable() {
        return false;
    }

    void deleteColumn(int column) {
        int i;
        if (column >= this.columns || column < 0) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("getcell.at.illegal.index.1", column));
        }
        --this.columns;
        boolean[] newReserved = new boolean[this.columns];
        Cell[] newCells = new Cell[this.columns];
        for (i = 0; i < column; ++i) {
            newReserved[i] = this.reserved[i];
            newCells[i] = this.cells[i];
            if (newCells[i] == null || i + newCells[i].getColspan() <= column) continue;
            newCells[i].setColspan(((Cell)this.cells[i]).getColspan() - 1);
        }
        for (i = column; i < this.columns; ++i) {
            newReserved[i] = this.reserved[i + 1];
            newCells[i] = this.cells[i + 1];
        }
        if (this.cells[column] != null && ((Cell)this.cells[column]).getColspan() > 1) {
            newCells[column] = this.cells[column];
            newCells[column].setColspan(newCells[column].getColspan() - 1);
        }
        this.reserved = newReserved;
        this.cells = newCells;
    }

    int addElement(TableRectangle element) {
        return this.addElement(element, this.currentColumn);
    }

    int addElement(TableRectangle element, int column) {
        int lColspan;
        if (element == null) {
            throw new NullPointerException(MessageLocalization.getComposedMessage("addcell.null.argument"));
        }
        if (column < 0 || column > this.columns) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("addcell.illegal.column.argument"));
        }
        if (this.getObjectID(element) != 1 && this.getObjectID(element) != 2) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("addcell.only.cells.or.tables.allowed"));
        }
        int n = lColspan = element instanceof Cell ? ((Cell)element).getColspan() : 1;
        if (!this.reserve(column, lColspan)) {
            return -1;
        }
        this.cells[column] = element;
        this.currentColumn += lColspan - 1;
        return column;
    }

    void setElement(TableRectangle aElement, int column) {
        if (this.reserved[column]) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("setelement.position.already.taken"));
        }
        this.cells[column] = aElement;
        if (aElement != null) {
            this.reserved[column] = true;
        }
    }

    boolean reserve(int column) {
        return this.reserve(column, 1);
    }

    boolean reserve(int column, int size) {
        if (column < 0 || column + size > this.columns) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("reserve.incorrect.column.size"));
        }
        for (int i = column; i < column + size; ++i) {
            if (this.reserved[i]) {
                for (int j = i; j >= column; --j) {
                    this.reserved[j] = false;
                }
                return false;
            }
            this.reserved[i] = true;
        }
        return true;
    }

    boolean isReserved(int column) {
        return this.reserved[column];
    }

    int getElementID(int column) {
        if (this.cells[column] == null) {
            return 0;
        }
        if (this.cells[column] instanceof Cell) {
            return 1;
        }
        if (this.cells[column] instanceof Table) {
            return 2;
        }
        return -1;
    }

    int getObjectID(Object element) {
        if (element == null) {
            return 0;
        }
        if (element instanceof Cell) {
            return 1;
        }
        if (element instanceof Table) {
            return 2;
        }
        return -1;
    }

    public TableRectangle getCell(int column) {
        if (column < 0 || column > this.columns) {
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("getcell.at.illegal.index.1.max.is.2", String.valueOf(column), String.valueOf(this.columns)));
        }
        return this.cells[column];
    }

    public boolean isEmpty() {
        for (int i = 0; i < this.columns; ++i) {
            if (this.cells[i] == null) continue;
            return false;
        }
        return true;
    }

    public int getColumns() {
        return this.columns;
    }

    public void setHorizontalAlignment(int value) {
        this.horizontalAlignment = value;
    }

    public int getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    @Override
    public void setHorizontalAlignment(HorizontalAlignment alignment) {
        if (alignment == null) {
            return;
        }
        this.horizontalAlignment = alignment.getId();
    }
}

