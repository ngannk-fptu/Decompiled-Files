/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.draw;

import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import org.apache.poi.hwmf.record.HwmfBinaryRasterOp;

public class HwmfROP2Composite
implements Composite {
    private final HwmfBinaryRasterOp op;

    public HwmfROP2Composite(HwmfBinaryRasterOp op) {
        this.op = op;
    }

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return new ROP2Context(this.op);
    }

    private static class ROP2Context
    implements CompositeContext {
        private final HwmfBinaryRasterOp op;

        public ROP2Context(HwmfBinaryRasterOp op) {
            this.op = op;
        }

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int w = Math.min(src.getWidth(), dstIn.getWidth());
            int h = Math.min(src.getHeight(), dstIn.getHeight());
            int[] srcPixels = new int[w];
            int[] dstPixels = new int[w];
            for (int y = 0; y < h; ++y) {
                src.getDataElements(0, y, w, 1, srcPixels);
                dstIn.getDataElements(0, y, w, 1, dstPixels);
                this.op.process(srcPixels, dstPixels);
                dstOut.setDataElements(0, y, w, 1, dstPixels);
            }
        }

        @Override
        public void dispose() {
        }
    }
}

