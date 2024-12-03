/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.usermodel;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;

public interface Row
extends Iterable<Cell> {
    public Cell createCell(int var1);

    public Cell createCell(int var1, CellType var2);

    public void removeCell(Cell var1);

    public void setRowNum(int var1);

    public int getRowNum();

    public Cell getCell(int var1);

    public Cell getCell(int var1, MissingCellPolicy var2);

    public short getFirstCellNum();

    public short getLastCellNum();

    public int getPhysicalNumberOfCells();

    public void setHeight(short var1);

    public void setZeroHeight(boolean var1);

    public boolean getZeroHeight();

    public void setHeightInPoints(float var1);

    public short getHeight();

    public float getHeightInPoints();

    public boolean isFormatted();

    public CellStyle getRowStyle();

    public void setRowStyle(CellStyle var1);

    public Iterator<Cell> cellIterator();

    @Override
    default public Iterator<Cell> iterator() {
        return this.cellIterator();
    }

    @Override
    default public Spliterator<Cell> spliterator() {
        return Spliterators.spliterator(this.cellIterator(), (long)this.getPhysicalNumberOfCells(), 0);
    }

    public Sheet getSheet();

    public int getOutlineLevel();

    public void shiftCellsRight(int var1, int var2, int var3);

    public void shiftCellsLeft(int var1, int var2, int var3);

    public static enum MissingCellPolicy {
        RETURN_NULL_AND_BLANK,
        RETURN_BLANK_AS_NULL,
        CREATE_NULL_AS_BLANK;

    }
}

