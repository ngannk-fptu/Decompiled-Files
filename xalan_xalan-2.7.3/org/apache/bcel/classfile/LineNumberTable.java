/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public final class LineNumberTable
extends Attribute
implements Iterable<LineNumber> {
    private static final int MAX_LINE_LENGTH = 72;
    private LineNumber[] lineNumberTable;

    LineNumberTable(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, (LineNumber[])null, constantPool);
        int lineNumberTableLength = input.readUnsignedShort();
        this.lineNumberTable = new LineNumber[lineNumberTableLength];
        for (int i = 0; i < lineNumberTableLength; ++i) {
            this.lineNumberTable[i] = new LineNumber(input);
        }
    }

    public LineNumberTable(int nameIndex, int length, LineNumber[] lineNumberTable, ConstantPool constantPool) {
        super((byte)4, nameIndex, length, constantPool);
        this.lineNumberTable = lineNumberTable != null ? lineNumberTable : LineNumber.EMPTY_ARRAY;
        Args.requireU2(this.lineNumberTable.length, "lineNumberTable.length");
    }

    public LineNumberTable(LineNumberTable c) {
        this(c.getNameIndex(), c.getLength(), c.getLineNumberTable(), c.getConstantPool());
    }

    @Override
    public void accept(Visitor v) {
        v.visitLineNumberTable(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        LineNumberTable c = (LineNumberTable)this.clone();
        c.lineNumberTable = new LineNumber[this.lineNumberTable.length];
        Arrays.setAll(c.lineNumberTable, i -> this.lineNumberTable[i].copy());
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.lineNumberTable.length);
        for (LineNumber lineNumber : this.lineNumberTable) {
            lineNumber.dump(file);
        }
    }

    public LineNumber[] getLineNumberTable() {
        return this.lineNumberTable;
    }

    public int getSourceLine(int pos) {
        int l = 0;
        int r = this.lineNumberTable.length - 1;
        if (r < 0) {
            return -1;
        }
        int minIndex = -1;
        int min = -1;
        do {
            int i;
            int j;
            if ((j = this.lineNumberTable[i = l + r >>> 1].getStartPC()) == pos) {
                return this.lineNumberTable[i].getLineNumber();
            }
            if (pos < j) {
                r = i - 1;
            } else {
                l = i + 1;
            }
            if (j >= pos || j <= min) continue;
            min = j;
            minIndex = i;
        } while (l <= r);
        if (minIndex < 0) {
            return -1;
        }
        return this.lineNumberTable[minIndex].getLineNumber();
    }

    public int getTableLength() {
        return this.lineNumberTable == null ? 0 : this.lineNumberTable.length;
    }

    @Override
    public Iterator<LineNumber> iterator() {
        return Stream.of(this.lineNumberTable).iterator();
    }

    public void setLineNumberTable(LineNumber[] lineNumberTable) {
        this.lineNumberTable = lineNumberTable;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        StringBuilder line = new StringBuilder();
        String newLine = System.getProperty("line.separator", "\n");
        for (int i = 0; i < this.lineNumberTable.length; ++i) {
            line.append(this.lineNumberTable[i].toString());
            if (i < this.lineNumberTable.length - 1) {
                line.append(", ");
            }
            if (line.length() <= 72 || i >= this.lineNumberTable.length - 1) continue;
            line.append(newLine);
            buf.append((CharSequence)line);
            line.setLength(0);
        }
        buf.append((CharSequence)line);
        return buf.toString();
    }
}

