/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.draw;

import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.ArrayDeque;
import java.util.Deque;
import org.apache.poi.hwmf.record.HwmfTernaryRasterOp;

public class HwmfROP3Composite
implements Composite {
    private final HwmfTernaryRasterOp rop3;
    private final byte[] mask;
    private final int mask_width;
    private final int mask_height;
    private final int foreground;
    private final int background;
    private final Point2D startPnt;
    private final boolean hasPattern;

    public HwmfROP3Composite(AffineTransform at, Shape shape, HwmfTernaryRasterOp rop3, BufferedImage bitmap, Color background, Color foreground) {
        this.rop3 = rop3;
        if (bitmap == null) {
            this.mask_width = 1;
            this.mask_height = 1;
            this.mask = new byte[]{1};
        } else {
            this.mask_width = bitmap.getWidth();
            this.mask_height = bitmap.getHeight();
            this.mask = new byte[this.mask_width * this.mask_height];
            bitmap.getRaster().getDataElements(0, 0, this.mask_width, this.mask_height, this.mask);
        }
        this.background = background.getRGB();
        this.foreground = foreground.getRGB();
        Rectangle2D bnds = at.createTransformedShape(shape.getBounds2D()).getBounds2D();
        this.startPnt = new Point2D.Double(bnds.getMinX(), bnds.getMinY());
        this.hasPattern = rop3.calcCmd().contains("P");
    }

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return new Rop3Context();
    }

    private class Rop3Context
    implements CompositeContext {
        private final Deque<int[]> stack = new ArrayDeque<int[]>();

        private Rop3Context() {
        }

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int w = Math.min(src.getWidth(), dstIn.getWidth());
            int h = Math.min(src.getHeight(), dstIn.getHeight());
            int startX = (int)HwmfROP3Composite.this.startPnt.getX();
            int startY = (int)HwmfROP3Composite.this.startPnt.getY();
            int offsetY = dstIn.getSampleModelTranslateY();
            int offsetX = dstIn.getSampleModelTranslateX();
            int[] srcPixels = new int[w];
            int[] dstPixels = new int[w];
            int[] patPixels = HwmfROP3Composite.this.hasPattern ? new int[w] : null;
            for (int y = 0; y < h; ++y) {
                dstIn.getDataElements(0, y, w, 1, dstPixels);
                src.getDataElements(0, y, w, 1, srcPixels);
                this.fillPattern(patPixels, y, startX, startY, offsetX, offsetY);
                HwmfROP3Composite.this.rop3.process(this.stack, dstPixels, srcPixels, patPixels);
                assert (this.stack.size() == 1);
                int[] dstOutPixels = this.stack.pop();
                dstOut.setDataElements(0, y, w, 1, dstOutPixels);
            }
        }

        private void fillPattern(int[] patPixels, int y, int startX, int startY, int offsetX, int offsetY) {
            if (patPixels != null) {
                int offY2 = (startY + y + offsetY) % HwmfROP3Composite.this.mask_height;
                offY2 = offY2 < 0 ? HwmfROP3Composite.this.mask_height + offY2 : offY2;
                int maskBase = offY2 * HwmfROP3Composite.this.mask_width;
                for (int i = 0; i < patPixels.length; ++i) {
                    int offX2 = (startX + i + offsetX) % HwmfROP3Composite.this.mask_width;
                    offX2 = offX2 < 0 ? HwmfROP3Composite.this.mask_width + offX2 : offX2;
                    patPixels[i] = HwmfROP3Composite.this.mask[maskBase + offX2] == 0 ? HwmfROP3Composite.this.background : HwmfROP3Composite.this.foreground;
                }
            }
        }

        @Override
        public void dispose() {
        }
    }
}

