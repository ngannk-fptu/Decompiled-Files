/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.io.Serializable;
import javax.media.jai.JaiI18N;

public abstract class Warp
implements Serializable {
    protected Warp() {
    }

    public int[] warpRect(int x, int y, int width, int height, int subsampleBitsH, int subsampleBitsV, int[] destRect) {
        if (destRect != null && destRect.length < width * height * 2) {
            throw new IllegalArgumentException(JaiI18N.getString("Warp0"));
        }
        return this.warpSparseRect(x, y, width, height, 1, 1, subsampleBitsH, subsampleBitsV, destRect);
    }

    public float[] warpRect(int x, int y, int width, int height, float[] destRect) {
        if (destRect != null && destRect.length < width * height * 2) {
            throw new IllegalArgumentException(JaiI18N.getString("Warp0"));
        }
        return this.warpSparseRect(x, y, width, height, 1, 1, destRect);
    }

    public int[] warpPoint(int x, int y, int subsampleBitsH, int subsampleBitsV, int[] destRect) {
        if (destRect != null && destRect.length < 2) {
            throw new IllegalArgumentException(JaiI18N.getString("Warp0"));
        }
        return this.warpSparseRect(x, y, 1, 1, 1, 1, subsampleBitsH, subsampleBitsV, destRect);
    }

    public float[] warpPoint(int x, int y, float[] destRect) {
        if (destRect != null && destRect.length < 2) {
            throw new IllegalArgumentException(JaiI18N.getString("Warp0"));
        }
        return this.warpSparseRect(x, y, 1, 1, 1, 1, destRect);
    }

    public int[] warpSparseRect(int x, int y, int width, int height, int periodX, int periodY, int subsampleBitsH, int subsampleBitsV, int[] destRect) {
        int nVals = 2 * ((width + periodX - 1) / periodX) * ((height + periodY - 1) / periodY);
        if (destRect != null && destRect.length < nVals) {
            throw new IllegalArgumentException(JaiI18N.getString("Warp0"));
        }
        float[] fdestRect = this.warpSparseRect(x, y, width, height, periodX, periodY, null);
        int size = fdestRect.length;
        if (destRect == null) {
            destRect = new int[size];
        }
        int precH = 1 << subsampleBitsH;
        int precV = 1 << subsampleBitsV;
        for (int i = 0; i < size; i += 2) {
            destRect[i] = (int)Math.floor(fdestRect[i] * (float)precH);
            destRect[i + 1] = (int)Math.floor(fdestRect[i + 1] * (float)precV);
        }
        return destRect;
    }

    public abstract float[] warpSparseRect(int var1, int var2, int var3, int var4, int var5, int var6, float[] var7);

    public Rectangle mapSourceRect(Rectangle sourceRect) {
        return null;
    }

    public Rectangle mapDestRect(Rectangle destRect) {
        float thisY;
        float thisX;
        int i;
        if (destRect == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int x = destRect.x;
        int y = destRect.y;
        int w = destRect.width;
        int h = destRect.height;
        float[] warpPoints = new float[Math.max(w * 2, (h - 2) * 2)];
        int length = w * 2;
        this.warpSparseRect(x, y, w, 1, 1, 1, warpPoints);
        float minX = warpPoints[0];
        float maxX = warpPoints[0];
        float minY = warpPoints[1];
        float maxY = warpPoints[1];
        for (i = 2; i < length; i += 2) {
            thisX = warpPoints[i];
            thisY = warpPoints[i + 1];
            if (thisX < minX) {
                minX = thisX;
            } else if (thisX > maxX) {
                maxX = thisX;
            }
            if (thisY < minY) {
                minY = thisY;
                continue;
            }
            if (!(thisY > maxY)) continue;
            maxY = thisY;
        }
        this.warpSparseRect(x, y + h - 1, w, 1, 1, 1, warpPoints);
        for (i = 0; i < length; i += 2) {
            thisX = warpPoints[i];
            thisY = warpPoints[i + 1];
            if (thisX < minX) {
                minX = thisX;
            } else if (thisX > maxX) {
                maxX = thisX;
            }
            if (thisY < minY) {
                minY = thisY;
                continue;
            }
            if (!(thisY > maxY)) continue;
            maxY = thisY;
        }
        length = (h - 2) * 2;
        this.warpSparseRect(x, y + 1, 1, h - 2, 1, 1, warpPoints);
        for (i = 0; i < length; i += 2) {
            thisX = warpPoints[i];
            thisY = warpPoints[i + 1];
            if (thisX < minX) {
                minX = thisX;
            } else if (thisX > maxX) {
                maxX = thisX;
            }
            if (thisY < minY) {
                minY = thisY;
                continue;
            }
            if (!(thisY > maxY)) continue;
            maxY = thisY;
        }
        this.warpSparseRect(x + w - 1, y + 1, 1, h - 2, 1, 1, warpPoints);
        for (i = 0; i < length; i += 2) {
            thisX = warpPoints[i];
            thisY = warpPoints[i + 1];
            if (thisX < minX) {
                minX = thisX;
            } else if (thisX > maxX) {
                maxX = thisX;
            }
            if (thisY < minY) {
                minY = thisY;
                continue;
            }
            if (!(thisY > maxY)) continue;
            maxY = thisY;
        }
        x = (int)Math.floor(minX);
        y = (int)Math.floor(minY);
        w = (int)Math.ceil(maxX - (float)x) + 1;
        h = (int)Math.ceil(maxY - (float)y) + 1;
        return new Rectangle(x, y, w, h);
    }

    public Point2D mapDestPoint(Point2D destPt) {
        if (destPt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        float[] sourceXY = this.warpSparseRect((int)destPt.getX(), (int)destPt.getY(), 1, 1, 1, 1, null);
        Point2D pt = (Point2D)destPt.clone();
        pt.setLocation(sourceXY[0], sourceXY[1]);
        return pt;
    }

    public Point2D mapSourcePoint(Point2D sourcePt) {
        if (sourcePt == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return null;
    }
}

