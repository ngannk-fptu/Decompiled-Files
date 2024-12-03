/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.IntModConstants;
import org.apfloat.internal.IntTableFNT;
import org.apfloat.internal.IntWTables;
import org.apfloat.internal.ParallelRunnable;
import org.apfloat.internal.ParallelRunner;
import org.apfloat.internal.Parallelizable;
import org.apfloat.internal.Scramble;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.NTTStepStrategy;

public class IntNTTStepStrategy
extends IntTableFNT
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
        return 0x3000000L;
    }

    protected ParallelRunnable createMultiplyElementsParallelRunnable(final ArrayAccess arrayAccess, final int startRow, final int startColumn, int rows, final int columns, long length, long totalTransformLength, boolean isInverse, int modulus) throws ApfloatRuntimeException {
        this.setModulus(IntModConstants.MODULUS[modulus]);
        final int w = isInverse ? this.getInverseNthRoot(IntModConstants.PRIMITIVE_ROOT[modulus], length) : this.getForwardNthRoot(IntModConstants.PRIMITIVE_ROOT[modulus], length);
        final int scaleFactor = isInverse ? this.modDivide(1, (int)totalTransformLength) : 1;
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
        this.setModulus(IntModConstants.MODULUS[modulus]);
        final int[] wTable = isInverse ? IntWTables.getInverseWTable(modulus, length) : IntWTables.getWTable(modulus, length);
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
        private int w;
        private int scaleFactor;

        public MultiplyRunnable(ArrayAccess arrayAccess, int startRow, int startColumn, int rows, int columns, int w, int scaleFactor) {
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
            int[] data = this.arrayAccess.getIntData();
            int position = this.arrayAccess.getOffset();
            int rowFactor = IntNTTStepStrategy.this.modPow(this.w, this.startRow);
            int columnFactor = IntNTTStepStrategy.this.modPow(this.w, this.startColumn);
            int rowStartFactor = IntNTTStepStrategy.this.modMultiply(this.scaleFactor, IntNTTStepStrategy.this.modPow(rowFactor, this.startColumn));
            for (int i = 0; i < this.rows; ++i) {
                int factor = rowStartFactor;
                int j = 0;
                while (j < this.columns) {
                    data[position] = IntNTTStepStrategy.this.modMultiply(data[position], factor);
                    factor = IntNTTStepStrategy.this.modMultiply(factor, rowFactor);
                    ++j;
                    ++position;
                }
                rowFactor = IntNTTStepStrategy.this.modMultiply(rowFactor, this.w);
                rowStartFactor = IntNTTStepStrategy.this.modMultiply(rowStartFactor, columnFactor);
            }
        }
    }

    private class TableFNTRunnable
    implements Runnable {
        private int length;
        private boolean isInverse;
        private ArrayAccess arrayAccess;
        private int[] wTable;
        private int[] permutationTable;

        public TableFNTRunnable(int length, boolean isInverse, ArrayAccess arrayAccess, int[] wTable, int[] permutationTable) {
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
                    IntNTTStepStrategy.this.inverseTableFNT(arrayAccess, this.wTable, this.permutationTable);
                    continue;
                }
                IntNTTStepStrategy.this.tableFNT(arrayAccess, this.wTable, this.permutationTable);
            }
        }
    }
}

