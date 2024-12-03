/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.LongModConstants;
import org.apfloat.internal.LongTableFNT;
import org.apfloat.internal.LongWTables;
import org.apfloat.internal.ParallelRunnable;
import org.apfloat.internal.ParallelRunner;
import org.apfloat.internal.Parallelizable;
import org.apfloat.internal.Scramble;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.NTTStepStrategy;

public class LongNTTStepStrategy
extends LongTableFNT
implements NTTStepStrategy,
Parallelizable {
    @Override
    public void multiplyElements(ArrayAccess arrayAccess, int startRow, int startColumn, int rows, int columns, long length, long totalTransformLength, boolean isInverse, int modulus) throws ApfloatRuntimeException {
        ParallelRunnable parallelRunnable = this.createMultiplyElementsParallelRunnable(arrayAccess, startRow, startColumn, rows, columns, length, totalTransformLength, isInverse, modulus);
        ParallelRunner.runParallel(parallelRunnable);
    }

    @Override
    public void transformRows(ArrayAccess arrayAccess, int length, int count, boolean isInverse, boolean permute, int modulus) throws ApfloatRuntimeException {
        ParallelRunnable parallelRunnable = this.createTransformRowsParallelRunnable(arrayAccess, length, count, isInverse, permute, modulus);
        ParallelRunner.runParallel(parallelRunnable);
    }

    @Override
    public long getMaxTransformLength() {
        return 0x1800000000000L;
    }

    protected ParallelRunnable createMultiplyElementsParallelRunnable(final ArrayAccess arrayAccess, final int startRow, final int startColumn, int rows, final int columns, long length, long totalTransformLength, boolean isInverse, int modulus) throws ApfloatRuntimeException {
        this.setModulus(LongModConstants.MODULUS[modulus]);
        final long w = isInverse ? this.getInverseNthRoot(LongModConstants.PRIMITIVE_ROOT[modulus], length) : this.getForwardNthRoot(LongModConstants.PRIMITIVE_ROOT[modulus], length);
        final long scaleFactor = isInverse ? this.modDivide(1L, totalTransformLength) : 1L;
        ParallelRunnable parallelRunnable = new ParallelRunnable(rows){

            @Override
            public Runnable getRunnable(int strideStartRow, int strideRows) {
                ArrayAccess subArrayAccess = arrayAccess.subsequence(strideStartRow * columns, strideRows * columns);
                return new MultiplyRunnable(subArrayAccess, startRow + strideStartRow, startColumn, strideRows, columns, w, scaleFactor);
            }
        };
        return parallelRunnable;
    }

    protected ParallelRunnable createTransformRowsParallelRunnable(final ArrayAccess arrayAccess, final int length, int count, final boolean isInverse, boolean permute, int modulus) throws ApfloatRuntimeException {
        this.setModulus(LongModConstants.MODULUS[modulus]);
        final long[] wTable = isInverse ? LongWTables.getInverseWTable(modulus, length) : LongWTables.getWTable(modulus, length);
        final int[] permutationTable = permute ? Scramble.createScrambleTable(length) : null;
        ParallelRunnable parallelRunnable = new ParallelRunnable(count){

            @Override
            public Runnable getRunnable(int startIndex, int strideCount) {
                ArrayAccess subArrayAccess = arrayAccess.subsequence(startIndex * length, strideCount * length);
                return new TableFNTRunnable(length, isInverse, subArrayAccess, wTable, permutationTable);
            }
        };
        return parallelRunnable;
    }

    private class MultiplyRunnable
    implements Runnable {
        private ArrayAccess arrayAccess;
        private int startRow;
        private int startColumn;
        private int rows;
        private int columns;
        private long w;
        private long scaleFactor;

        public MultiplyRunnable(ArrayAccess arrayAccess, int startRow, int startColumn, int rows, int columns, long w, long scaleFactor) {
            this.arrayAccess = arrayAccess;
            this.startRow = startRow;
            this.startColumn = startColumn;
            this.rows = rows;
            this.columns = columns;
            this.w = w;
            this.scaleFactor = scaleFactor;
        }

        @Override
        public void run() {
            long[] data = this.arrayAccess.getLongData();
            int position = this.arrayAccess.getOffset();
            long rowFactor = LongNTTStepStrategy.this.modPow(this.w, this.startRow);
            long columnFactor = LongNTTStepStrategy.this.modPow(this.w, this.startColumn);
            long rowStartFactor = LongNTTStepStrategy.this.modMultiply(this.scaleFactor, LongNTTStepStrategy.this.modPow(rowFactor, this.startColumn));
            for (int i = 0; i < this.rows; ++i) {
                long factor = rowStartFactor;
                int j = 0;
                while (j < this.columns) {
                    data[position] = LongNTTStepStrategy.this.modMultiply(data[position], factor);
                    factor = LongNTTStepStrategy.this.modMultiply(factor, rowFactor);
                    ++j;
                    ++position;
                }
                rowFactor = LongNTTStepStrategy.this.modMultiply(rowFactor, this.w);
                rowStartFactor = LongNTTStepStrategy.this.modMultiply(rowStartFactor, columnFactor);
            }
        }
    }

    private class TableFNTRunnable
    implements Runnable {
        private int length;
        private boolean isInverse;
        private ArrayAccess arrayAccess;
        private long[] wTable;
        private int[] permutationTable;

        public TableFNTRunnable(int length, boolean isInverse, ArrayAccess arrayAccess, long[] wTable, int[] permutationTable) {
            this.length = length;
            this.isInverse = isInverse;
            this.arrayAccess = arrayAccess;
            this.wTable = wTable;
            this.permutationTable = permutationTable;
        }

        @Override
        public void run() {
            int maxI = this.arrayAccess.getLength();
            for (int i = 0; i < maxI; i += this.length) {
                ArrayAccess arrayAccess = this.arrayAccess.subsequence(i, this.length);
                if (this.isInverse) {
                    LongNTTStepStrategy.this.inverseTableFNT(arrayAccess, this.wTable, this.permutationTable);
                    continue;
                }
                LongNTTStepStrategy.this.tableFNT(arrayAccess, this.wTable, this.permutationTable);
            }
        }
    }
}

