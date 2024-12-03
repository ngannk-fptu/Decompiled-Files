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
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;

public class ShaderType3
extends PDFShader {
    private Point2D center1;
    private Point2D center2;
    private float radius1;
    private float radius2;
    private float minT = 0.0f;
    private float maxT = 1.0f;
    private boolean extendStart = false;
    private boolean extendEnd = false;
    private PDFFunction[] functions;

    public ShaderType3() {
        super(3);
    }

    @Override
    public void parse(PDFObject shaderObj) throws IOException {
        PDFObject functionObj;
        PDFObject coordsObj = shaderObj.getDictRef("Coords");
        if (coordsObj == null) {
            throw new PDFParseException("No coordinates found!");
        }
        PDFObject[] coords = coordsObj.getArray();
        this.center1 = new Point2D.Float(coords[0].getFloatValue(), coords[1].getFloatValue());
        this.center2 = new Point2D.Float(coords[3].getFloatValue(), coords[4].getFloatValue());
        this.radius1 = coords[2].getFloatValue();
        this.radius2 = coords[5].getFloatValue();
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
        return PDFPaint.getPaint(new Type3Paint());
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

    class Type3PaintContext
    implements PaintContext {
        private ColorModel colorModel;
        private AffineTransform invXform;
        private double dx1x0;
        private double dy1y0;
        private double dr1r0;
        private double sqr0;
        private double denom;

        Type3PaintContext(ColorModel colorModel, AffineTransform xform) {
            this.colorModel = colorModel;
            this.dx1x0 = ShaderType3.this.center2.getX() - ShaderType3.this.center1.getX();
            this.dy1y0 = ShaderType3.this.center2.getY() - ShaderType3.this.center1.getY();
            this.dr1r0 = ShaderType3.this.radius2 - ShaderType3.this.radius1;
            this.sqr0 = ShaderType3.this.radius1 * ShaderType3.this.radius1;
            this.denom = this.dx1x0 * this.dx1x0 + this.dy1y0 * this.dy1y0 - this.dr1r0 * this.dr1r0;
            try {
                this.invXform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                e.printStackTrace();
            }
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
            PDFColorSpace shadeCSpace = ShaderType3.this.getColorSpace();
            PDFFunction[] functions = ShaderType3.this.getFunctions();
            int numComponents = cs.getNumComponents();
            float[] c1 = new float[2];
            float[] inputs = new float[1];
            float[] outputs = new float[shadeCSpace.getNumComponents()];
            float[] outputRBG = new float[numComponents];
            int[] data = new int[w * h * (numComponents + 1)];
            float lastInput = Float.POSITIVE_INFINITY;
            float tol = PDFShader.TOLERANCE * (ShaderType3.this.getMaxT() - ShaderType3.this.getMinT());
            boolean advance = true;
            for (int j = 0; j < h; ++j) {
                for (int i = 0; i < w; ++i) {
                    float t;
                    this.invXform.transform(new float[]{x + i, y + j}, 0, c1, 0, 1);
                    boolean render = true;
                    float[] s = this.calculateInputValues(c1[0], c1[1]);
                    if (s[1] >= 0.0f && s[1] <= 1.0f) {
                        s[1] = s[1];
                    } else if (ShaderType3.this.extendEnd && s[1] >= 0.0f && (double)ShaderType3.this.radius1 + (double)s[1] * this.dr1r0 >= 0.0) {
                        s[1] = s[1];
                    } else if (s[0] >= 0.0f && s[0] <= 1.0f) {
                        s[1] = s[0];
                    } else if (ShaderType3.this.extendStart && s[1] <= 0.0f && (double)ShaderType3.this.radius1 + (double)s[1] * this.dr1r0 >= 0.0) {
                        s[1] = s[1];
                    } else if (ShaderType3.this.extendStart && s[0] <= 1.0f && (double)ShaderType3.this.radius1 + (double)s[0] * this.dr1r0 >= 0.0) {
                        s[1] = s[0];
                    } else {
                        render = false;
                    }
                    if (!render) continue;
                    inputs[0] = t = ShaderType3.this.getMinT() + s[1] * (ShaderType3.this.getMaxT() - ShaderType3.this.getMinT());
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

        private float[] calculateInputValues(float x, float y) {
            double p = -((double)x - ShaderType3.this.center1.getX()) * this.dx1x0 - ((double)y - ShaderType3.this.center1.getY()) * this.dy1y0 - (double)ShaderType3.this.radius1 * this.dr1r0;
            double q = Math.pow((double)x - ShaderType3.this.center1.getX(), 2.0) + Math.pow((double)y - ShaderType3.this.center1.getY(), 2.0) - this.sqr0;
            double root = Math.sqrt(p * p - this.denom * q);
            float root1 = (float)((-p + root) / this.denom);
            float root2 = (float)((-p - root) / this.denom);
            if (this.denom < 0.0) {
                return new float[]{root1, root2};
            }
            return new float[]{root2, root1};
        }
    }

    class Type3Paint
    implements Paint {
        @Override
        public PaintContext createContext(ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
            ColorSpace cs = ColorSpace.getInstance(1000);
            ComponentColorModel model = new ComponentColorModel(cs, true, false, 3, 0);
            return new Type3PaintContext(model, xform);
        }

        @Override
        public int getTransparency() {
            return 3;
        }
    }
}

