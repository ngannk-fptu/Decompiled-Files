/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import org.jfree.util.ObjectUtilities;

public class ObjectTable
implements Serializable {
    private static final long serialVersionUID = -3968322452944912066L;
    private int rows;
    private int columns;
    private transient Object[][] data;
    private int rowIncrement;
    private int columnIncrement;

    public ObjectTable() {
        this(5, 5);
    }

    public ObjectTable(int increment) {
        this(increment, increment);
    }

    public ObjectTable(int rowIncrement, int colIncrement) {
        if (rowIncrement < 1) {
            throw new IllegalArgumentException("Increment must be positive.");
        }
        if (colIncrement < 1) {
            throw new IllegalArgumentException("Increment must be positive.");
        }
        this.rows = 0;
        this.columns = 0;
        this.rowIncrement = rowIncrement;
        this.columnIncrement = colIncrement;
        this.data = new Object[rowIncrement][];
    }

    public int getColumnIncrement() {
        return this.columnIncrement;
    }

    public int getRowIncrement() {
        return this.rowIncrement;
    }

    protected void ensureRowCapacity(int row) {
        if (row >= this.data.length) {
            Object[][] enlarged = new Object[row + this.rowIncrement][];
            System.arraycopy(this.data, 0, enlarged, 0, this.data.length);
            this.data = enlarged;
        }
    }

    public void ensureCapacity(int row, int column) {
        if (row < 0) {
            throw new IndexOutOfBoundsException("Row is invalid. " + row);
        }
        if (column < 0) {
            throw new IndexOutOfBoundsException("Column is invalid. " + column);
        }
        this.ensureRowCapacity(row);
        Object[] current = this.data[row];
        if (current == null) {
            Object[] enlarged = new Object[Math.max(column + 1, this.columnIncrement)];
            this.data[row] = enlarged;
        } else if (column >= current.length) {
            Object[] enlarged = new Object[column + this.columnIncrement];
            System.arraycopy(current, 0, enlarged, 0, current.length);
            this.data[row] = enlarged;
        }
    }

    public int getRowCount() {
        return this.rows;
    }

    public int getColumnCount() {
        return this.columns;
    }

    protected Object getObject(int row, int column) {
        if (row < this.data.length) {
            Object[] current = this.data[row];
            if (current == null) {
                return null;
            }
            if (column < current.length) {
                return current[column];
            }
        }
        return null;
    }

    protected void setObject(int row, int column, Object object) {
        this.ensureCapacity(row, column);
        this.data[row][column] = object;
        this.rows = Math.max(this.rows, row + 1);
        this.columns = Math.max(this.columns, column + 1);
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectTable)) {
            return false;
        }
        ObjectTable ot = (ObjectTable)o;
        if (this.getRowCount() != ot.getRowCount()) {
            return false;
        }
        if (this.getColumnCount() != ot.getColumnCount()) {
            return false;
        }
        for (int r = 0; r < this.getRowCount(); ++r) {
            for (int c = 0; c < this.getColumnCount(); ++c) {
                if (ObjectUtilities.equal(this.getObject(r, c), ot.getObject(r, c))) continue;
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int result = this.rows;
        result = 29 * result + this.columns;
        return result;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        int rowCount = this.data.length;
        stream.writeInt(rowCount);
        for (int r = 0; r < rowCount; ++r) {
            Object[] column = this.data[r];
            stream.writeBoolean(column != null);
            if (column == null) continue;
            int columnCount = column.length;
            stream.writeInt(columnCount);
            for (int c = 0; c < columnCount; ++c) {
                this.writeSerializedData(stream, column[c]);
            }
        }
    }

    protected void writeSerializedData(ObjectOutputStream stream, Object o) throws IOException {
        stream.writeObject(o);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        int rowCount = stream.readInt();
        this.data = new Object[rowCount][];
        for (int r = 0; r < rowCount; ++r) {
            boolean isNotNull = stream.readBoolean();
            if (!isNotNull) continue;
            int columnCount = stream.readInt();
            Object[] column = new Object[columnCount];
            this.data[r] = column;
            for (int c = 0; c < columnCount; ++c) {
                column[c] = this.readSerializedData(stream);
            }
        }
    }

    protected Object readSerializedData(ObjectInputStream stream) throws ClassNotFoundException, IOException {
        return stream.readObject();
    }

    public void clear() {
        this.rows = 0;
        this.columns = 0;
        for (int i = 0; i < this.data.length; ++i) {
            if (this.data[i] == null) continue;
            Arrays.fill(this.data[i], null);
        }
    }

    protected void copyColumn(int oldColumn, int newColumn) {
        for (int i = 0; i < this.getRowCount(); ++i) {
            this.setObject(i, newColumn, this.getObject(i, oldColumn));
        }
    }

    protected void copyRow(int oldRow, int newRow) {
        this.ensureCapacity(newRow, this.getColumnCount());
        Object[] oldRowStorage = this.data[oldRow];
        if (oldRowStorage == null) {
            Object[] newRowStorage = this.data[newRow];
            if (newRowStorage != null) {
                Arrays.fill(newRowStorage, null);
            }
        } else {
            this.data[newRow] = (Object[])oldRowStorage.clone();
        }
    }

    protected void setData(Object[][] data, int colCount) {
        if (data == null) {
            throw new NullPointerException();
        }
        if (colCount < 0) {
            throw new IndexOutOfBoundsException();
        }
        this.data = data;
        this.rows = data.length;
        this.columns = colCount;
    }

    protected Object[][] getData() {
        return this.data;
    }
}

