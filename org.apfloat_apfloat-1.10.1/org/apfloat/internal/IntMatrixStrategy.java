/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.internal.ApfloatInternalException;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.MatrixStrategy;
import org.apfloat.spi.Util;

public class IntMatrixStrategy
implements MatrixStrategy {
    @Override
    public void transpose(ArrayAccess arrayAccess, int n1, int n2) throws ApfloatRuntimeException {
        int[] data = arrayAccess.getIntData();
        int offset = arrayAccess.getOffset();
        if (n1 != (n1 & -n1) || n2 != (n2 & -n2) || n1 <= 0 || n2 <= 0) {
            throw new ApfloatInternalException("Matrix size must be a power of two, not " + n1 + " x " + n2);
        }
        if (n1 == n2) {
            IntMatrixStrategy.transposeSquare(data, offset, n1, n1);
        } else if (n2 == 2 * n1) {
            IntMatrixStrategy.transposeSquare(data, offset, n1, n2);
            IntMatrixStrategy.transposeSquare(data, offset + n1, n1, n2);
            IntMatrixStrategy.permuteToHalfWidth(data, offset, n1, n2);
        } else if (n1 == 2 * n2) {
            IntMatrixStrategy.permuteToDoubleWidth(data, offset, n1, n2);
            IntMatrixStrategy.transposeSquare(data, offset, n2, n1);
            IntMatrixStrategy.transposeSquare(data, offset + n2, n2, n1);
        } else {
            throw new ApfloatInternalException("Must be n1 = n2, n1 = 2*n2 or n2 = 2*n1; matrix is " + n1 + " x " + n2);
        }
    }

    @Override
    public void transposeSquare(ArrayAccess arrayAccess, int n1, int n2) throws ApfloatRuntimeException {
        IntMatrixStrategy.transposeSquare(arrayAccess.getIntData(), arrayAccess.getOffset(), n1, n2);
    }

    @Override
    public void permuteToDoubleWidth(ArrayAccess arrayAccess, int n1, int n2) throws ApfloatRuntimeException {
        if (n1 != (n1 & -n1) || n2 != (n2 & -n2) || n1 <= 0 || n2 <= 0) {
            throw new ApfloatInternalException("Matrix size must be a power of two, not " + n1 + " x " + n2);
        }
        if (n1 < 2) {
            throw new ApfloatInternalException("Matrix height must be at least 2.");
        }
        IntMatrixStrategy.permuteToDoubleWidth(arrayAccess.getIntData(), arrayAccess.getOffset(), n1, n2);
    }

    @Override
    public void permuteToHalfWidth(ArrayAccess arrayAccess, int n1, int n2) throws ApfloatRuntimeException {
        if (n1 != (n1 & -n1) || n2 != (n2 & -n2) || n1 <= 0 || n2 <= 0) {
            throw new ApfloatInternalException("Matrix size must be a power of two, not " + n1 + " x " + n2);
        }
        IntMatrixStrategy.permuteToHalfWidth(arrayAccess.getIntData(), arrayAccess.getOffset(), n1, n2);
    }

    private static void moveBlock(int[] source, int sourceOffset, int sourceWidth, int[] dest, int destOffset, int destWidth, int b) {
        for (int i = 0; i < b; ++i) {
            System.arraycopy(source, sourceOffset, dest, destOffset, b);
            destOffset += destWidth;
            sourceOffset += sourceWidth;
        }
    }

    private static void transpose2blocks(int[] data, int offset1, int offset2, int width, int b) {
        int i = 0;
        int position1 = offset2;
        while (i < b) {
            int j = 0;
            int position2 = offset1 + i;
            while (j < b) {
                int tmp = data[position1 + j];
                data[position1 + j] = data[position2];
                data[position2] = tmp;
                ++j;
                position2 += width;
            }
            ++i;
            position1 += width;
        }
    }

    private static void transposeBlock(int[] data, int offset, int width, int b) {
        int i = 0;
        int position1 = offset;
        while (i < b) {
            int j = i + 1;
            int position2 = offset + j * width + i;
            while (j < b) {
                int tmp = data[position1 + j];
                data[position1 + j] = data[position2];
                data[position2] = tmp;
                ++j;
                position2 += width;
            }
            ++i;
            position1 += width;
        }
    }

    private static void transposeSquare(int[] data, int offset, int n1, int n2) {
        ApfloatContext ctx = ApfloatContext.getContext();
        int cacheBurstBlockSize = Util.round2down(ctx.getCacheBurst() / 8);
        int cacheBlockSize = Util.sqrt4down(ctx.getCacheL1Size() / 8);
        int cacheThreshold = Util.round2down(ctx.getCacheL2Size() / 8);
        if (n1 <= cacheBurstBlockSize || n1 <= cacheBlockSize) {
            IntMatrixStrategy.transposeBlock(data, offset, n2, n1);
        } else if (n1 * n2 <= cacheThreshold) {
            int b = cacheBurstBlockSize;
            int i = 0;
            int position1 = offset;
            while (i < n1) {
                IntMatrixStrategy.transposeBlock(data, position1 + i, n2, b);
                int j = i + b;
                int position2 = offset + j * n2 + i;
                while (j < n1) {
                    IntMatrixStrategy.transpose2blocks(data, position1 + j, position2, n2, b);
                    j += b;
                    position2 += b * n2;
                }
                i += b;
                position1 += b * n2;
            }
        } else {
            int b = cacheBlockSize;
            int[] tmp1 = new int[b * b];
            int[] tmp2 = new int[b * b];
            int i = 0;
            int position1 = offset;
            while (i < n1) {
                IntMatrixStrategy.moveBlock(data, position1 + i, n2, tmp1, 0, b, b);
                IntMatrixStrategy.transposeBlock(tmp1, 0, b, b);
                IntMatrixStrategy.moveBlock(tmp1, 0, b, data, position1 + i, n2, b);
                int j = i + b;
                int position2 = offset + j * n2 + i;
                while (j < n1) {
                    IntMatrixStrategy.moveBlock(data, position1 + j, n2, tmp1, 0, b, b);
                    IntMatrixStrategy.transposeBlock(tmp1, 0, b, b);
                    IntMatrixStrategy.moveBlock(data, position2, n2, tmp2, 0, b, b);
                    IntMatrixStrategy.transposeBlock(tmp2, 0, b, b);
                    IntMatrixStrategy.moveBlock(tmp2, 0, b, data, position1 + j, n2, b);
                    IntMatrixStrategy.moveBlock(tmp1, 0, b, data, position2, n2, b);
                    j += b;
                    position2 += b * n2;
                }
                i += b;
                position1 += b * n2;
            }
        }
    }

    private static void permuteToHalfWidth(int[] data, int offset, int n1, int n2) {
        if (n1 < 2) {
            return;
        }
        int twicen1 = 2 * n1;
        int halfn2 = n2 / 2;
        int[] tmp = new int[halfn2];
        boolean[] isRowDone = new boolean[twicen1];
        int j = 1;
        do {
            int o = j;
            int m = j;
            System.arraycopy(data, offset + halfn2 * m, tmp, 0, halfn2);
            isRowDone[m] = true;
            int n = m = m < n1 ? 2 * m : 2 * (m - n1) + 1;
            while (m != j) {
                isRowDone[m] = true;
                System.arraycopy(data, offset + halfn2 * m, data, offset + halfn2 * o, halfn2);
                o = m;
                m = m < n1 ? 2 * m : 2 * (m - n1) + 1;
            }
            System.arraycopy(tmp, 0, data, offset + halfn2 * o, halfn2);
            while (isRowDone[j]) {
                ++j;
            }
        } while (j < twicen1 - 1);
    }

    private static void permuteToDoubleWidth(int[] data, int offset, int n1, int n2) {
        if (n1 < 4) {
            return;
        }
        int halfn1 = n1 / 2;
        int[] tmp = new int[n2];
        boolean[] isRowDone = new boolean[n1];
        int j = 1;
        do {
            int o = j;
            int m = j;
            System.arraycopy(data, offset + n2 * m, tmp, 0, n2);
            isRowDone[m] = true;
            int n = m = (m & 1) != 0 ? m / 2 + halfn1 : m / 2;
            while (m != j) {
                isRowDone[m] = true;
                System.arraycopy(data, offset + n2 * m, data, offset + n2 * o, n2);
                o = m;
                m = (m & 1) != 0 ? m / 2 + halfn1 : m / 2;
            }
            System.arraycopy(tmp, 0, data, offset + n2 * o, n2);
            while (isRowDone[j]) {
                ++j;
            }
        } while (j < n1 - 1);
    }
}

