/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.pattern;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPage;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.PDFParser;
import com.sun.pdfview.PDFRenderer;
import com.sun.pdfview.pattern.PDFPattern;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PatternType1
extends PDFPattern {
    public static final int PAINT_COLORED = 1;
    public static final int PAINT_UNCOLORED = 2;
    public static final int TILE_CONSTANT = 1;
    public static final int TILE_NODISTORT = 2;
    public static final int TILE_FASTER = 3;
    private HashMap<String, PDFObject> resources;
    private int paintType;
    private int tilingType;
    private Rectangle2D bbox;
    private int xStep;
    private int yStep;
    private byte[] data;

    public PatternType1() {
        super(1);
    }

    @Override
    protected void parse(PDFObject patternObj, Map rsrc) throws IOException {
        this.data = patternObj.getStream();
        this.resources = patternObj.getDictRef("Resources").getDictionary();
        this.paintType = patternObj.getDictRef("PaintType").getIntValue();
        this.tilingType = patternObj.getDictRef("TilingType").getIntValue();
        PDFObject bboxObj = patternObj.getDictRef("BBox");
        this.bbox = new Rectangle2D.Float(bboxObj.getAt(0).getFloatValue(), bboxObj.getAt(1).getFloatValue(), bboxObj.getAt(2).getFloatValue(), bboxObj.getAt(3).getFloatValue());
        this.xStep = patternObj.getDictRef("XStep").getIntValue();
        this.yStep = patternObj.getDictRef("YStep").getIntValue();
    }

    @Override
    public PDFPaint getPaint(PDFPaint basePaint) {
        Rectangle2D.Double anchor = new Rectangle2D.Double(this.getBBox().getMinX(), this.getBBox().getMinY(), this.getXStep(), this.getYStep());
        final PDFPage page = new PDFPage(this.getBBox(), 0);
        if (basePaint != null) {
            page.addFillPaint(basePaint);
            page.addStrokePaint(basePaint);
        }
        PDFParser prc = new PDFParser(page, this.data, this.getResources());
        prc.go(true);
        int width = (int)this.getBBox().getWidth();
        int height = (int)this.getBBox().getHeight();
        Paint paint = new Paint(){

            @Override
            public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
                ColorSpace cs = ColorSpace.getInstance(1000);
                ComponentColorModel model = new ComponentColorModel(cs, true, false, 3, 0);
                Rectangle2D devBBox = xform.createTransformedShape(PatternType1.this.getBBox()).getBounds2D();
                double[] steps = new double[]{PatternType1.this.getXStep(), PatternType1.this.getYStep()};
                xform.deltaTransform(steps, 0, steps, 0, 1);
                int width = (int)Math.ceil(devBBox.getWidth());
                int height = (int)Math.ceil(devBBox.getHeight());
                BufferedImage img = (BufferedImage)page.getImage(width, height, null, null, false, true);
                return new Type1PaintContext(model, devBBox, (float)steps[0], (float)steps[1], img.getData());
            }

            @Override
            public int getTransparency() {
                return 3;
            }
        };
        return new TilingPatternPaint(paint, this);
    }

    public HashMap<String, PDFObject> getResources() {
        return this.resources;
    }

    public int getPaintType() {
        return this.paintType;
    }

    public int getTilingType() {
        return this.tilingType;
    }

    public Rectangle2D getBBox() {
        return this.bbox;
    }

    public int getXStep() {
        return this.xStep;
    }

    public int getYStep() {
        return this.yStep;
    }

    class Type1PaintContext
    implements PaintContext {
        private ColorModel colorModel;
        private Rectangle2D bbox;
        private float xstep;
        private float ystep;
        private Raster data;

        Type1PaintContext(ColorModel colorModel, Rectangle2D bbox, float xstep, float ystep, Raster data) {
            this.colorModel = colorModel;
            this.bbox = bbox;
            this.xstep = xstep;
            this.ystep = ystep;
            this.data = data;
        }

        @Override
        public void dispose() {
            this.colorModel = null;
            this.bbox = null;
            this.data = null;
        }

        @Override
        public ColorModel getColorModel() {
            return this.colorModel;
        }

        @Override
        public Raster getRaster(int x, int y, int w, int h) {
            ColorSpace cs = this.getColorModel().getColorSpace();
            int numComponents = cs.getNumComponents();
            int[] imgData = new int[w * h * (numComponents + 1)];
            int useXStep = (int)Math.abs(Math.ceil(this.xstep));
            int useYStep = (int)Math.abs(Math.ceil(this.ystep));
            int[] emptyPixel = new int[numComponents + 1];
            int[] usePixel = new int[numComponents + 1];
            for (int j = 0; j < h; ++j) {
                for (int i = 0; i < w; ++i) {
                    int xloc = x + i - (int)Math.ceil(this.bbox.getX());
                    int yloc = y + j - (int)Math.ceil(this.bbox.getY());
                    yloc %= useYStep;
                    if ((xloc %= useXStep) < 0) {
                        xloc = useXStep + xloc;
                    }
                    if (yloc < 0) {
                        yloc = useYStep + yloc;
                    }
                    int[] pixel = emptyPixel;
                    if (xloc < this.data.getWidth() && yloc < this.data.getHeight()) {
                        pixel = this.data.getPixel(xloc, yloc, usePixel);
                    }
                    int base = (j * w + i) * (numComponents + 1);
                    for (int c = 0; c < pixel.length; ++c) {
                        imgData[base + c] = pixel[c];
                    }
                }
            }
            WritableRaster raster = this.getColorModel().createCompatibleWritableRaster(w, h);
            raster.setPixels(0, 0, w, h, imgData);
            Raster child = raster.createTranslatedChild(x, y);
            return child;
        }
    }

    class TilingPatternPaint
    extends PDFPaint {
        private PatternType1 pattern;

        public TilingPatternPaint(Paint paint, PatternType1 pattern) {
            super(paint);
            this.pattern = pattern;
        }

        @Override
        public Rectangle2D fill(PDFRenderer state, Graphics2D g, GeneralPath s) {
            AffineTransform at = g.getTransform();
            Shape xformed = s.createTransformedShape(at);
            state.push();
            state.setTransform(state.getInitialTransform());
            state.transform(this.pattern.getTransform());
            try {
                at = state.getTransform().createInverse();
            }
            catch (NoninvertibleTransformException noninvertibleTransformException) {
                // empty catch block
            }
            xformed = at.createTransformedShape(xformed);
            g.setComposite(AlphaComposite.getInstance(3));
            g.setPaint(this.getPaint());
            g.fill(xformed);
            state.pop();
            return s.createTransformedShape(g.getTransform()).getBounds2D();
        }
    }
}

