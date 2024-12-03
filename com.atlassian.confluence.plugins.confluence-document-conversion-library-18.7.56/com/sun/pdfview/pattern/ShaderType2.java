/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.pattern;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.colorspace.PDFColorSpace;
import com.sun.pdfview.function.PDFFunction;
import com.sun.pdfview.pattern.PDFShader;
import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;

public class ShaderType2
extends PDFShader {
    private Point2D axisStart;
    private Point2D axisEnd;
    private float minT = 0.0f;
    private float maxT = 1.0f;
    private boolean extendStart = false;
    private boolean extendEnd = false;
    private PDFFunction[] functions;

    public ShaderType2() {
        super(2);
    }

    @Override
    public void parse(PDFObject shaderObj) throws IOException {
        PDFObject functionObj;
        PDFObject coordsObj = shaderObj.getDictRef("Coords");
        if (coordsObj == null) {
            throw new PDFParseException("No coordinates found!");
        }
        PDFObject[] coords = coordsObj.getArray();
        Point2D.Float start = new Point2D.Float(coords[0].getFloatValue(), coords[1].getFloatValue());
        Point2D.Float end = new Point2D.Float(coords[2].getFloatValue(), coords[3].getFloatValue());
        this.setAxisStart(start);
        this.setAxisEnd(end);
        PDFObject domainObj = shaderObj.getDictRef("Domain");
        if (domainObj != null) {
            PDFObject[] domain = domainObj.getArray();
            this.setMinT(domain[0].getFloatValue());
            this.setMaxT(domain[1].getFloatValue());
        }
        if ((functionObj = shaderObj.getDictRef("Function")) == null) {
            throw new PDFParseException("No function defined for shader!");
        }
        PDFObject[] functionArray = functionObj.getArray();
        PDFFunction[] functions = new PDFFunction[functionArray.length];
        for (int i = 0; i < functions.length; ++i) {
            functions[i] = PDFFunction.getFunction(functionArray[i]);
        }
        this.setFunctions(functions);
        PDFObject extendObj = shaderObj.getDictRef("Extend");
        if (extendObj != null) {
            PDFObject[] extendArray = extendObj.getArray();
            this.setExtendStart(extendArray[0].getBooleanValue());
            this.setExtendEnd(extendArray[1].getBooleanValue());
        }
    }

    @Override
    public PDFPaint getPaint() {
        return PDFPaint.getPaint(new Type2Paint());
    }

    public Point2D getAxisStart() {
        return this.axisStart;
    }

    protected void setAxisStart(Point2D axisStart) {
        this.axisStart = axisStart;
    }

    public Point2D getAxisEnd() {
        return this.axisEnd;
    }

    protected void setAxisEnd(Point2D axisEnd) {
        this.axisEnd = axisEnd;
    }

    public float getMinT() {
        return this.minT;
    }

    protected void setMinT(float minT) {
        this.minT = minT;
    }

    public float getMaxT() {
        return this.maxT;
    }

    protected void setMaxT(float maxT) {
        this.maxT = maxT;
    }

    public boolean getExtendStart() {
        return this.extendStart;
    }

    protected void setExtendStart(boolean extendStart) {
        this.extendStart = extendStart;
    }

    public boolean getExtendEnd() {
        return this.extendEnd;
    }

    protected void setExtendEnd(boolean extendEnd) {
        this.extendEnd = extendEnd;
    }

    public PDFFunction[] getFunctions() {
        return this.functions;
    }

    protected void setFunctions(PDFFunction[] functions) {
        this.functions = functions;
    }

    class Type2PaintContext
    implements PaintContext {
        private ColorModel colorModel;
        private Point2D start;
        private Point2D end;
        private float dt1t0;
        private double dx1x0;
        private double dy1y0;
        private double sqdx1x0psqdy1y0;

        Type2PaintContext(ColorModel colorModel, Point2D start, Point2D end) {
            this.colorModel = colorModel;
            this.start = start;
            this.end = end;
            this.dt1t0 = ShaderType2.this.getMaxT() - ShaderType2.this.getMinT();
            this.dx1x0 = end.getX() - start.getX();
            this.dy1y0 = end.getY() - start.getY();
            this.sqdx1x0psqdy1y0 = this.dx1x0 * this.dx1x0 + this.dy1y0 * this.dy1y0;
        }

        @Override
        public void dispose() {
            this.colorModel = null;
        }

        @Override
        public ColorModel getColorModel() {
            return this.colorModel;
        }

        @Override
        public Raster getRaster(int x, int y, int w, int h) {
            ColorSpace cs = this.getColorModel().getColorSpace();
            PDFColorSpace shadeCSpace = ShaderType2.this.getColorSpace();
            PDFFunction[] functions = ShaderType2.this.getFunctions();
            int numComponents = cs.getNumComponents();
            float x0 = (float)this.start.getX();
            float y0 = (float)this.start.getY();
            float[] inputs = new float[1];
            float[] outputs = new float[shadeCSpace.getNumComponents()];
            float[] outputRBG = new float[numComponents];
            int[] data = new int[w * h * (numComponents + 1)];
            float lastInput = Float.POSITIVE_INFINITY;
            float tol = PDFShader.TOLERANCE * (ShaderType2.this.getMaxT() - ShaderType2.this.getMinT());
            for (int j = 0; j < h; ++j) {
                for (int i = 0; i < w; ++i) {
                    boolean render = true;
                    float xp = this.getXPrime(i + x, j + y, x0, y0);
                    float t = 0.0f;
                    if (xp >= 0.0f && xp <= 1.0f) {
                        t = ShaderType2.this.getMinT() + this.dt1t0 * xp;
                    } else if (xp < 0.0f && ShaderType2.this.extendStart) {
                        t = ShaderType2.this.getMinT();
                    } else if (xp > 1.0f && ShaderType2.this.extendEnd) {
                        t = ShaderType2.this.getMaxT();
                    } else {
                        render = false;
                    }
                    if (!render) continue;
                    inputs[0] = t;
                    if (Math.abs(lastInput - t) > tol) {
                        if (functions.length == 1) {
                            functions[0].calculate(inputs, 0, outputs, 0);
                        } else {
                            for (int c = 0; c < functions.length; ++c) {
                                functions[c].calculate(inputs, 0, outputs, c);
                            }
                        }
                        outputRBG = !shadeCSpace.getColorSpace().isCS_sRGB() ? shadeCSpace.getColorSpace().toRGB(outputs) : outputs;
                        lastInput = t;
                    }
                    int base = (j * w + i) * (numComponents + 1);
                    for (int c = 0; c < numComponents; ++c) {
                        data[base + c] = (int)(outputRBG[c] * 255.0f);
                    }
                    data[base + numComponents] = 255;
                }
            }
            WritableRaster raster = this.getColorModel().createCompatibleWritableRaster(w, h);
            raster.setPixels(0, 0, w, h, data);
            Raster child = raster.createTranslatedChild(x, y);
            return child;
        }

        private float getXPrime(float x, float y, float x0, float y0) {
            double tp = (this.dx1x0 * (double)(x - x0) + this.dy1y0 * (double)(y - y0)) / this.sqdx1x0psqdy1y0;
            return (float)tp;
        }
    }

    class Type2Paint
    implements Paint {
        @Override
        public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
            ColorSpace cs = ColorSpace.getInstance(1000);
            ComponentColorModel model = new ComponentColorModel(cs, true, false, 3, 0);
            Point2D devStart = xform.transform(ShaderType2.this.getAxisStart(), null);
            Point2D devEnd = xform.transform(ShaderType2.this.getAxisEnd(), null);
            return new Type2PaintContext(model, devStart, devEnd);
        }

        @Override
        public int getTransparency() {
            return 3;
        }
    }
}

