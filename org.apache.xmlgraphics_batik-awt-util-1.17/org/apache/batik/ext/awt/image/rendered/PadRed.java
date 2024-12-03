/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.DataBufferInt;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.AbstractTiledRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;

public class PadRed
extends AbstractRed {
    static final boolean DEBUG = false;
    PadMode padMode;
    RenderingHints hints;

    public PadRed(CachableRed src, Rectangle bounds, PadMode padMode, RenderingHints hints) {
        super(src, bounds, src.getColorModel(), PadRed.fixSampleModel(src, bounds), bounds.x, bounds.y, null);
        this.padMode = padMode;
        this.hints = hints;
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        CachableRed src = (CachableRed)this.getSources().get(0);
        Rectangle srcR = src.getBounds();
        Rectangle wrR = wr.getBounds();
        if (wrR.intersects(srcR)) {
            Rectangle r = wrR.intersection(srcR);
            WritableRaster srcWR = wr.createWritableChild(r.x, r.y, r.width, r.height, r.x, r.y, null);
            src.copyData(srcWR);
        }
        if (this.padMode == PadMode.ZERO_PAD) {
            this.handleZero(wr);
        } else if (this.padMode == PadMode.REPLICATE) {
            this.handleReplicate(wr);
        } else if (this.padMode == PadMode.WRAP) {
            this.handleWrap(wr);
        }
        return wr;
    }

    protected void handleZero(WritableRaster wr) {
        int h;
        int w;
        CachableRed src = (CachableRed)this.getSources().get(0);
        Rectangle srcR = src.getBounds();
        Rectangle wrR = wr.getBounds();
        ZeroRecter zr = ZeroRecter.getZeroRecter(wr);
        Rectangle ar = new Rectangle(wrR.x, wrR.y, wrR.width, wrR.height);
        Rectangle dr = new Rectangle(wrR.x, wrR.y, wrR.width, wrR.height);
        if (ar.x < srcR.x) {
            w = srcR.x - ar.x;
            if (w > ar.width) {
                w = ar.width;
            }
            dr.width = w;
            zr.zeroRect(dr);
            ar.x += w;
            ar.width -= w;
        }
        if (ar.y < srcR.y) {
            h = srcR.y - ar.y;
            if (h > ar.height) {
                h = ar.height;
            }
            dr.x = ar.x;
            dr.y = ar.y;
            dr.width = ar.width;
            dr.height = h;
            zr.zeroRect(dr);
            ar.y += h;
            ar.height -= h;
        }
        if (ar.y + ar.height > srcR.y + srcR.height) {
            h = ar.y + ar.height - (srcR.y + srcR.height);
            if (h > ar.height) {
                h = ar.height;
            }
            int y0 = ar.y + ar.height - h;
            dr.x = ar.x;
            dr.y = y0;
            dr.width = ar.width;
            dr.height = h;
            zr.zeroRect(dr);
            ar.height -= h;
        }
        if (ar.x + ar.width > srcR.x + srcR.width) {
            int x0;
            w = ar.x + ar.width - (srcR.x + srcR.width);
            if (w > ar.width) {
                w = ar.width;
            }
            dr.x = x0 = ar.x + ar.width - w;
            dr.y = ar.y;
            dr.width = w;
            dr.height = ar.height;
            zr.zeroRect(dr);
            ar.width -= w;
        }
    }

    protected void handleReplicate(WritableRaster wr) {
        int xLoc;
        int wrX;
        int repX;
        int repW;
        CachableRed src = (CachableRed)this.getSources().get(0);
        Rectangle srcR = src.getBounds();
        Rectangle wrR = wr.getBounds();
        int x = wrR.x;
        int y = wrR.y;
        int width = wrR.width;
        int height = wrR.height;
        int minX = srcR.x > x ? srcR.x : x;
        int maxX = srcR.x + srcR.width - 1 < x + width - 1 ? srcR.x + srcR.width - 1 : x + width - 1;
        int minY = srcR.y > y ? srcR.y : y;
        int maxY = srcR.y + srcR.height - 1 < y + height - 1 ? srcR.y + srcR.height - 1 : y + height - 1;
        int x0 = minX;
        int w = maxX - minX + 1;
        int y0 = minY;
        int h = maxY - minY + 1;
        if (w < 0) {
            x0 = 0;
            w = 0;
        }
        if (h < 0) {
            y0 = 0;
            h = 0;
        }
        Rectangle r = new Rectangle(x0, y0, w, h);
        if (y < srcR.y) {
            repW = r.width;
            repX = r.x;
            int wrX2 = r.x;
            int wrY = y;
            if (x + width - 1 <= srcR.x) {
                repW = 1;
                repX = srcR.x;
                wrX2 = x + width - 1;
            } else if (x >= srcR.x + srcR.width) {
                repW = 1;
                repX = srcR.x + srcR.width - 1;
                wrX2 = x;
            }
            WritableRaster wr1 = wr.createWritableChild(wrX2, wrY, repW, 1, repX, srcR.y, null);
            src.copyData(wr1);
            ++wrY;
            int endY = srcR.y;
            if (y + height < endY) {
                endY = y + height;
            }
            if (wrY < endY) {
                int[] pixels = wr.getPixels(wrX2, wrY - 1, repW, 1, (int[])null);
                while (wrY < srcR.y) {
                    wr.setPixels(wrX2, wrY, repW, 1, pixels);
                    ++wrY;
                }
            }
        }
        if (y + height > srcR.y + srcR.height) {
            repW = r.width;
            repX = r.x;
            int repY = srcR.y + srcR.height - 1;
            int wrX3 = r.x;
            int wrY = srcR.y + srcR.height;
            if (wrY < y) {
                wrY = y;
            }
            if (x + width <= srcR.x) {
                repW = 1;
                repX = srcR.x;
                wrX3 = x + width - 1;
            } else if (x >= srcR.x + srcR.width) {
                repW = 1;
                repX = srcR.x + srcR.width - 1;
                wrX3 = x;
            }
            WritableRaster wr1 = wr.createWritableChild(wrX3, wrY, repW, 1, repX, repY, null);
            src.copyData(wr1);
            int endY = y + height;
            if (++wrY < endY) {
                int[] pixels = wr.getPixels(wrX3, wrY - 1, repW, 1, (int[])null);
                while (wrY < endY) {
                    wr.setPixels(wrX3, wrY, repW, 1, pixels);
                    ++wrY;
                }
            }
        }
        if (x < srcR.x) {
            wrX = srcR.x;
            if (x + width <= srcR.x) {
                wrX = x + width - 1;
            }
            int[] pixels = wr.getPixels(wrX, y, 1, height, (int[])null);
            for (xLoc = x; xLoc < wrX; ++xLoc) {
                wr.setPixels(xLoc, y, 1, height, pixels);
            }
        }
        if (x + width > srcR.x + srcR.width) {
            wrX = srcR.x + srcR.width - 1;
            if (x >= srcR.x + srcR.width) {
                wrX = x;
            }
            int endX = x + width - 1;
            int[] pixels = wr.getPixels(wrX, y, 1, height, (int[])null);
            for (xLoc = wrX + 1; xLoc < endX; ++xLoc) {
                wr.setPixels(xLoc, y, 1, height, pixels);
            }
        }
    }

    protected void handleWrap(WritableRaster wr) {
        this.handleZero(wr);
    }

    protected static SampleModel fixSampleModel(CachableRed src, Rectangle bounds) {
        int h;
        int defSz = AbstractTiledRed.getDefaultTileSize();
        SampleModel sm = src.getSampleModel();
        int w = sm.getWidth();
        if (w < defSz) {
            w = defSz;
        }
        if (w > bounds.width) {
            w = bounds.width;
        }
        if ((h = sm.getHeight()) < defSz) {
            h = defSz;
        }
        if (h > bounds.height) {
            h = bounds.height;
        }
        return sm.createCompatibleSampleModel(w, h);
    }

    protected static class ZeroRecter_INT_PACK
    extends ZeroRecter {
        final int base;
        final int scanStride;
        final int[] pixels;
        final int[] zeros;
        final int x0;
        final int y0;

        public ZeroRecter_INT_PACK(WritableRaster wr) {
            super(wr);
            SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
            this.scanStride = sppsm.getScanlineStride();
            DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
            this.x0 = wr.getMinY();
            this.y0 = wr.getMinX();
            this.base = db.getOffset() + sppsm.getOffset(this.x0 - wr.getSampleModelTranslateX(), this.y0 - wr.getSampleModelTranslateY());
            this.pixels = db.getBankData()[0];
            this.zeros = (int[])(wr.getWidth() > 10 ? new int[wr.getWidth()] : null);
        }

        @Override
        public void zeroRect(Rectangle r) {
            int rbase = this.base + (r.x - this.x0) + (r.y - this.y0) * this.scanStride;
            if (r.width > 10) {
                for (int y = 0; y < r.height; ++y) {
                    int sp = rbase + y * this.scanStride;
                    System.arraycopy(this.zeros, 0, this.pixels, sp, r.width);
                }
            } else {
                int sp = rbase;
                int end = sp + r.width;
                int adj = this.scanStride - r.width;
                for (int y = 0; y < r.height; ++y) {
                    while (sp < end) {
                        this.pixels[sp++] = 0;
                    }
                    sp += adj;
                    end += this.scanStride;
                }
            }
        }
    }

    protected static class ZeroRecter {
        WritableRaster wr;
        int bands;
        static int[] zeros = null;

        public ZeroRecter(WritableRaster wr) {
            this.wr = wr;
            this.bands = wr.getSampleModel().getNumBands();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void zeroRect(Rectangle r) {
            ZeroRecter zeroRecter = this;
            synchronized (zeroRecter) {
                if (zeros == null || zeros.length < r.width * this.bands) {
                    zeros = new int[r.width * this.bands];
                }
            }
            for (int y = 0; y < r.height; ++y) {
                this.wr.setPixels(r.x, r.y + y, r.width, 1, zeros);
            }
        }

        public static ZeroRecter getZeroRecter(WritableRaster wr) {
            if (GraphicsUtil.is_INT_PACK_Data(wr.getSampleModel(), false)) {
                return new ZeroRecter_INT_PACK(wr);
            }
            return new ZeroRecter(wr);
        }

        public static void zeroRect(WritableRaster wr) {
            ZeroRecter zr = ZeroRecter.getZeroRecter(wr);
            zr.zeroRect(wr.getBounds());
        }
    }
}

