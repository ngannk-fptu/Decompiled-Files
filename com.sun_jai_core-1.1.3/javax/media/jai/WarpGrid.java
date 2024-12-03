/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.geom.Point2D;
import javax.media.jai.JaiI18N;
import javax.media.jai.Warp;

public final class WarpGrid
extends Warp {
    private int xStart;
    private int yStart;
    private int xEnd;
    private int yEnd;
    private int xStep;
    private int yStep;
    private int xNumCells;
    private int yNumCells;
    private float[] xWarpPos;
    private float[] yWarpPos;

    private void initialize(int xStart, int xStep, int xNumCells, int yStart, int yStep, int yNumCells, float[] warpPositions) {
        this.xStart = xStart;
        this.yStart = yStart;
        this.xEnd = xStart + xStep * xNumCells;
        this.yEnd = yStart + yStep * yNumCells;
        this.xStep = xStep;
        this.yStep = yStep;
        this.xNumCells = xNumCells;
        this.yNumCells = yNumCells;
        int xNumGrids = xNumCells + 1;
        int yNumGrids = yNumCells + 1;
        int numNodes = yNumGrids * xNumGrids;
        this.xWarpPos = new float[numNodes];
        this.yWarpPos = new float[numNodes];
        int index = 0;
        for (int idx = 0; idx < numNodes; ++idx) {
            this.xWarpPos[idx] = warpPositions[index++];
            this.yWarpPos[idx] = warpPositions[index++];
        }
    }

    public WarpGrid(int xStart, int xStep, int xNumCells, int yStart, int yStep, int yNumCells, float[] warpPositions) {
        if (warpPositions.length != 2 * (xNumCells + 1) * (yNumCells + 1)) {
            throw new IllegalArgumentException(JaiI18N.getString("WarpGrid0"));
        }
        this.initialize(xStart, xStep, xNumCells, yStart, yStep, yNumCells, warpPositions);
    }

    public WarpGrid(Warp master, int xStart, int xStep, int xNumCells, int yStart, int yStep, int yNumCells) {
        int size = 2 * (xNumCells + 1) * (yNumCells + 1);
        float[] warpPositions = new float[size];
        warpPositions = master.warpSparseRect(xStart, yStart, xNumCells * xStep + 1, yNumCells * yStep + 1, xStep, yStep, warpPositions);
        this.initialize(xStart, xStep, xNumCells, yStart, yStep, yNumCells, warpPositions);
    }

    public int getXStart() {
        return this.xStart;
    }

    public int getYStart() {
        return this.yStart;
    }

    public int getXStep() {
        return this.xStep;
    }

    public int getYStep() {
        return this.yStep;
    }

    public int getXNumCells() {
        return this.xNumCells;
    }

    public int getYNumCells() {
        return this.yNumCells;
    }

    public float[] getXWarpPos() {
        return this.xWarpPos;
    }

    public float[] getYWarpPos() {
        return this.yWarpPos;
    }

    private float[] noWarpSparseRect(int x1, int x2, int y1, int y2, int periodX, int periodY, int offset, int stride, float[] destRect) {
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        for (int j = y1; j <= y2; j += periodY) {
            int index = offset;
            offset += stride;
            for (int i = x1; i <= x2; i += periodX) {
                destRect[index++] = i;
                destRect[index++] = j;
            }
        }
        return destRect;
    }

    public float[] warpSparseRect(int x, int y, int width, int height, int periodX, int periodY, float[] destRect) {
        int periods;
        int stride = 2 * ((width + periodX - 1) / periodX);
        if (destRect == null) {
            destRect = new float[stride * ((height + periodY - 1) / periodY)];
        }
        int x1 = x;
        int x2 = x + width - 1;
        int y1 = y;
        int y2 = y + height - 1;
        if (y1 >= this.yEnd || y2 < this.yStart || x1 >= this.xEnd || x2 < this.xStart) {
            return this.noWarpSparseRect(x1, x2, y1, y2, periodX, periodY, 0, stride, destRect);
        }
        if (y1 < this.yStart) {
            periods = (this.yStart - y1 + periodY - 1) / periodY;
            this.noWarpSparseRect(x1, x2, y1, this.yStart - 1, periodX, periodY, 0, stride, destRect);
            y1 += periods * periodY;
        }
        if (y2 >= this.yEnd) {
            periods = (this.yEnd - y + periodY - 1) / periodY;
            this.noWarpSparseRect(x1, x2, y + periods * periodY, y2, periodX, periodY, periods * stride, stride, destRect);
            y2 = y + (periods - 1) * periodY;
        }
        if (x1 < this.xStart) {
            periods = (this.xStart - x1 + periodX - 1) / periodX;
            this.noWarpSparseRect(x1, this.xStart - 1, y1, y2, periodX, periodY, (y1 - y) / periodY * stride, stride, destRect);
            x1 += periods * periodX;
        }
        if (x2 >= this.xEnd) {
            periods = (this.xEnd - x + periodX - 1) / periodX;
            this.noWarpSparseRect(x + periods * periodX, x2, y1, y2, periodX, periodY, (y1 - y) / periodY * stride + periods * 2, stride, destRect);
            x2 = x + (periods - 1) * periodX;
        }
        int[] cellPoints = new int[this.xNumCells];
        for (int i = x1; i <= x2; i += periodX) {
            int n = (i - this.xStart) / this.xStep;
            cellPoints[n] = cellPoints[n] + 1;
        }
        int offset = (y1 - y) / periodY * stride + (x1 - x) / periodX * 2;
        int xNumGrids = this.xNumCells + 1;
        float deltaX = (float)periodX / (float)this.xStep;
        for (int j = y1; j <= y2; j += periodY) {
            int index = offset;
            offset += stride;
            int yCell = (j - this.yStart) / this.yStep;
            int yGrid = this.yStart + yCell * this.yStep;
            float yFrac = ((float)j + 0.5f - (float)yGrid) / (float)this.yStep;
            float deltaTop = (1.0f - yFrac) * deltaX;
            float deltaBottom = yFrac * deltaX;
            int i = x1;
            while (i <= x2) {
                int xCell = (i - this.xStart) / this.xStep;
                int xGrid = this.xStart + xCell * this.xStep;
                float xFrac = ((float)i + 0.5f - (float)xGrid) / (float)this.xStep;
                int nodeOffset = yCell * xNumGrids + xCell;
                float wx0 = this.xWarpPos[nodeOffset];
                float wy0 = this.yWarpPos[nodeOffset];
                float wx1 = this.xWarpPos[++nodeOffset];
                float wy1 = this.yWarpPos[nodeOffset];
                float wx2 = this.xWarpPos[nodeOffset += this.xNumCells];
                float wy2 = this.yWarpPos[nodeOffset];
                float wx3 = this.xWarpPos[++nodeOffset];
                float wy3 = this.yWarpPos[nodeOffset];
                float s = wx0 + (wx1 - wx0) * xFrac;
                float t = wy0 + (wy1 - wy0) * xFrac;
                float u = wx2 + (wx3 - wx2) * xFrac;
                float v = wy2 + (wy3 - wy2) * xFrac;
                float wx = s + (u - s) * yFrac;
                float wy = t + (v - t) * yFrac;
                float dx = (wx1 - wx0) * deltaTop + (wx3 - wx2) * deltaBottom;
                float dy = (wy1 - wy0) * deltaTop + (wy3 - wy2) * deltaBottom;
                int nPoints = cellPoints[xCell];
                for (int k = 0; k < nPoints; ++k) {
                    destRect[index++] = wx - 0.5f;
                    destRect[index++] = wy - 0.5f;
                    wx += dx;
                    wy += dy;
                    i += periodX;
                }
            }
        }
        return destRect;
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        float[] sxy = this.warpSparseRect((int)destPt.getX(), (int)destPt.getY(), 2, 2, 1, 1, null);
        double wtRight = destPt.getX() - (double)((int)destPt.getX());
        double wtLeft = 1.0 - wtRight;
        double wtBottom = destPt.getY() - (double)((int)destPt.getY());
        double wtTop = 1.0 - wtBottom;
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation(((double)sxy[0] * wtLeft + (double)sxy[2] * wtRight) * wtTop + ((double)sxy[4] * wtLeft + (double)sxy[6] * wtRight) * wtBottom, ((double)sxy[1] * wtLeft + (double)sxy[3] * wtRight) * wtTop + ((double)sxy[5] * wtLeft + (double)sxy[7] * wtRight) * wtBottom);
        return pt;
    }
}

