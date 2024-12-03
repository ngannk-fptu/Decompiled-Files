/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.rendering;

import java.awt.Color;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.pdmodel.common.function.PDFunctionTypeIdentity;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;

class SoftMask
implements Paint {
    private static final ColorModel ARGB_COLOR_MODEL = new BufferedImage(1, 1, 2).getColorModel();
    private final Paint paint;
    private final BufferedImage mask;
    private final Rectangle2D bboxDevice;
    private int bc = 0;
    private final PDFunction transferFunction;

    SoftMask(Paint paint, BufferedImage mask, Rectangle2D bboxDevice, PDColor backdropColor, PDFunction transferFunction) {
        this.paint = paint;
        this.mask = mask;
        this.bboxDevice = bboxDevice;
        this.transferFunction = transferFunction instanceof PDFunctionTypeIdentity ? null : transferFunction;
        if (backdropColor != null) {
            try {
                Color color = new Color(backdropColor.toRGB());
                this.bc = (299 * color.getRed() + 587 * color.getGreen() + 114 * color.getBlue()) / 1000;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    @Override
    public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
        PaintContext ctx = this.paint.createContext(cm, deviceBounds, userBounds, xform, hints);
        return new SoftPaintContext(ctx);
    }

    @Override
    public int getTransparency() {
        return 3;
    }

    private class SoftPaintContext
    implements PaintContext {
        private final PaintContext context;

        SoftPaintContext(PaintContext context) {
            this.context = context;
        }

        @Override
        public ColorModel getColorModel() {
            return ARGB_COLOR_MODEL;
        }

        @Override
        public Raster getRaster(int x1, int y1, int w, int h) {
            Raster raster = this.context.getRaster(x1, y1, w, h);
            ColorModel rasterCM = this.context.getColorModel();
            float[] input = null;
            Float[] map = null;
            if (SoftMask.this.transferFunction != null) {
                map = new Float[256];
                input = new float[1];
            }
            WritableRaster output = this.getColorModel().createCompatibleWritableRaster(w, h);
            x1 -= (int)SoftMask.this.bboxDevice.getX();
            y1 -= (int)SoftMask.this.bboxDevice.getY();
            int[] gray = new int[4];
            Object pixelInput = null;
            int[] pixelOutput = new int[4];
            for (int y = 0; y < h; ++y) {
                for (int x = 0; x < w; ++x) {
                    block10: {
                        pixelInput = raster.getDataElements(x, y, pixelInput);
                        pixelOutput[0] = rasterCM.getRed(pixelInput);
                        pixelOutput[1] = rasterCM.getGreen(pixelInput);
                        pixelOutput[2] = rasterCM.getBlue(pixelInput);
                        pixelOutput[3] = rasterCM.getAlpha(pixelInput);
                        gray[0] = 0;
                        if (x1 + x >= 0 && y1 + y >= 0 && x1 + x < SoftMask.this.mask.getWidth() && y1 + y < SoftMask.this.mask.getHeight()) {
                            SoftMask.this.mask.getRaster().getPixel(x1 + x, y1 + y, gray);
                            int g = gray[0];
                            if (SoftMask.this.transferFunction != null) {
                                try {
                                    if (map[g] != null) {
                                        pixelOutput[3] = Math.round((float)pixelOutput[3] * map[g].floatValue());
                                        break block10;
                                    }
                                    input[0] = (float)g / 255.0f;
                                    float f = SoftMask.this.transferFunction.eval(input)[0];
                                    map[g] = Float.valueOf(f);
                                    pixelOutput[3] = Math.round((float)pixelOutput[3] * f);
                                }
                                catch (IOException ex) {
                                    pixelOutput[3] = Math.round((float)pixelOutput[3] * ((float)SoftMask.this.bc / 255.0f));
                                }
                            } else {
                                pixelOutput[3] = Math.round((float)pixelOutput[3] * ((float)g / 255.0f));
                            }
                        } else {
                            pixelOutput[3] = Math.round((float)pixelOutput[3] * ((float)SoftMask.this.bc / 255.0f));
                        }
                    }
                    output.setPixel(x, y, pixelOutput);
                }
            }
            return output;
        }

        @Override
        public void dispose() {
            this.context.dispose();
        }
    }
}

