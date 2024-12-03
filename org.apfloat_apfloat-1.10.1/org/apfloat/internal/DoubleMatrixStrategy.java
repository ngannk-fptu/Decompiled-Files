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

public class DoubleMatrixStrategy
implements MatrixStrategy {
    @Override
    public void transpose(ArrayAccess arrayAccess, int n1, int n2) throws ApfloatRuntimeException {
        double[] data = arrayAccess.getDoubleData();
        int offset = arrayAccess.getOffset();
        if (n1 != (n1 & -n1) || n2 != (n2 & -n2) || n1 <= 0 || n2 <= 0) {
            throw new ApfloatInternalException("Matrix size must be a power of two, not " + n1 + " x " + n2);
        }
        if (n1 == n2) {
            DoubleMatrixStrategy.transposeSquare(data, offset, n1, n1);
        } else if (n2 == 2 * n1) {
            DoubleMatrixStrategy.transposeSquare(data, offset, n1, n2);
            DoubleMatrixStrategy.transposeSquare(data, offset + n1, n1, n2);
            DoubleMatrixStrategy.permuteToHalfWidth(data, offset, n1, n2);
        } else if (n1 == 2 * n2) {
            DoubleMatrixStrategy.permuteToDoubleWidth(data, offset, n1, n2);
            DoubleMatrixStrategy.transposeSquare(data, offset, n2, n1);
            DoubleMatrixStrategy.transposeSquare(data, offset + n2, n2, n1);
        } else {
            throw new ApfloatInternalException("Must be n1 = n2, n1 = 2*n2 or n2 = 2*n1; matrix is " + n1 + " x " + n2);
        }
    }

    @Override
    public void transposeSquare(ArrayAccess arrayAccess, int n1, int n2) throws ApfloatRuntimeException {
        DoubleMatrixStrategy.transposeSquare(arrayAccess.getDoubleData(), arrayAccess.getOffset(), n1, n2);
    }

    @Override
    public void permuteToDoubleWidth(ArrayAccess arrayAccess, int n1, int n2) throws ApfloatRuntimeException {
        if (n1 != (n1 & -n1) || n2 != (n2 & -n2) || n1 <= 0 || n2 <= 0) {
            throw new ApfloatInternalException("Matrix size must be a power of two, not " + n1 + " x " + n2);
        }
        if (n1 < 2) {
            throw new ApfloatInternalException("Matrix height must be at least 2.");
        }
        DoubleMatrixStrategy.permuteToDoubleWidth(arrayAccess.getDoubleData(), arrayAccess.getOffset(), n1, n2);
    }

    @Override
    public void permuteToHalfWidth(ArrayAccess arrayAccess, int n1, int n2) throws ApfloatRuntimeException {
        if (n1 != (n1 & -n1) || n2 != (n2 & -n2) || n1 <= 0 || n2 <= 0) {
            throw new ApfloatInternalException("Matrix size must be a power of two, not " + n1 + " x " + n2);
        }
        DoubleMatrixStrategy.permuteToHalfWidth(arrayAccess.getDoubleData(), arrayAccess.getOffset(), n1, n2);
    }

    private static void moveBlock(double[] source, int sourceOffset, int sourceWidth, double[] dest, int destOffset, int destWidth, int b) {
        for (int i = 0; i < b; ++i) {
            System.arraycopy(source, sourceOffset, dest, destOffset, b);
            destOffset += destWidth;
            sourceOffset += sourceWidth;
        }
    }

    private static void transpose2blocks(double[] data, int offset1, int offset2, int width, int b) {
        int i = 0;
        int position1 = offset2;
        while (i < b) {
            int j = 0;
            int position2 = offset1 + i;
            while (j < b) {
                double tmp = data[position1 + j];
                data[position1 + j] = data[position2];
                data[position2] = tmp;
                ++j;
                position2 += width;
            }
            ++i;
            position1 += width;
        }
    }

    private static void transposeBlock(double[] data, int offset, int width, int b) {
        int i = 0;
        int position1 = offset;
        while (i < b) {
            int j = i + 1;
            int position2 = offset + j * width + i;
            while (j < b) {
                double tmp = data[position1 + j];
                data[position1 + j] = data[position2];
                data[position2] = tmp;
                ++j;
                position2 += width;
            }
            ++i;
            position1 += width;
        }
    }

    private static void transposeSquare(double[] data, int offset, int n1, int n2) {
        ApfloatContext ctx = ApfloatContext.getContext();
        int cacheBurstBlockSize = Util.round2down(ctx.getCacheBurst() / 8);
        int cacheBlockSize = Util.sqrt4down(ctx.getCacheL1Size() / 8);
        int cacheThreshold = Util.round2down(ctx.getCacheL2Size() / 8);
        if (n1 <= cacheBurstBlockSize || n1 <= cacheBlockSize) {
            DoubleMatrixStrategy.transposeBlock(data, offset, n2, n1);
        } else if (n1 * n2 <= cacheThreshold) {
            int b = cacheBurstBlockSize;
            int i = 0;
            int position1 = offset;
            while (i < n1) {
                DoubleMatrixStrategy.transposeBlock(data, position1 + i, n2, b);
                int j = i + b;
                int position2 = offset + j * n2 + i;
                while (j < n1) {
                    DoubleMatrixStrategy.transpose2blocks(data, position1 + j, position2, n2, b);
                    j += b;
                    position2 += b * n2;
                }
                i += b;
                position1 += b * n2;
            }
        } else {
            int b = cacheBlockSize;
            double[] tmp1 = new double[b * b];
            double[] tmp2 = new double[b * b];
            int i = 0;
            int position1 = offset;
            while (i < n1) {
                DoubleMatrixStrategy.moveBlock(data, position1 + i, n2, tmp1, 0, b, b);
                DoubleMatrixStrategy.transposeBlock(tmp1, 0, b, b);
                DoubleMatrixStrategy.moveBlock(tmp1, 0, b, data, position1 + i, n2, b);
                int j = i + b;
                int position2 = offset + j * n2 + i;
                while (j < n1) {
                    DoubleMatrixStrategy.moveBlock(data, position1 + j, n2, tmp1, 0, b, b);
                    DoubleMatrixStrategy.transposeBlock(tmp1, 0, b, b);
                    DoubleMatrixStrategy.moveBlock(data, position2, n2, tmp2, 0, b, b);
                    DoubleMatrixStrategy.transposeBlock(tmp2, 0, b, b);
                    DoubleMatrixStrategy.moveBlock(tmp2, 0, b, data, position1 + j, n2, b);
                    DoubleMatrixStrategy.moveBlock(tmp1, 0, b, data, position2, n2, b);
                    j += b;
                    position2 += b * n2;
                }
                i += b;
                position1 += b * n2;
            }
        }
    }

    private static void permuteToHalfWidth(double[] data, int offset, int n1, int n2) {
        if (n1 < 2) {
            return;
        }
        int twicen1 = 2 * n1;
        int halfn2 = n2 / 2;
        double[] tmp = new double[halfn2];
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

    private static void permuteToDoubleWidth(double[] data, int offset, int n1, int n2) {
        if (n1 < 4) {
            return;
        }
        int halfn1 = n1 / 2;
        double[] tmp = new double[n2];
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

