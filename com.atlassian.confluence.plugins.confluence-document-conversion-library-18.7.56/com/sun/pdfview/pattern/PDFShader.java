/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.pattern;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.colorspace.PDFColorSpace;
import com.sun.pdfview.pattern.ShaderType2;
import com.sun.pdfview.pattern.ShaderType3;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Map;

public abstract class PDFShader {
    public static final int FUNCTION_SHADING = 1;
    public static final int AXIAL_SHADING = 2;
    public static final int RADIAL_SHADING = 3;
    public static final int FREE_FORM_SHADING = 4;
    public static final int LATTICE_SHADING = 5;
    public static final int COONS_PATCH_MESH_SHADING = 6;
    public static final int TENSOR_PRODUCTS_MESH_SHADING = 7;
    public static float TOLERANCE = 1.0E-4f;
    private int type;
    private PDFColorSpace colorSpace;
    private PDFPaint background;
    private Rectangle2D bbox;

    protected PDFShader(int type) {
        this.type = type;
    }

    public static PDFShader getShader(PDFObject shaderObj, Map resources) throws IOException {
        PDFObject bboxObj;
        PDFShader shader = (PDFShader)shaderObj.getCache();
        if (shader != null) {
            return shader;
        }
        PDFObject typeObj = shaderObj.getDictRef("ShadingType");
        if (typeObj == null) {
            throw new PDFParseException("No shader type defined!");
        }
        int type = typeObj.getIntValue();
        switch (type) {
            case 2: {
                shader = new ShaderType2();
                break;
            }
            case 3: {
                shader = new ShaderType3();
                break;
            }
            default: {
                throw new PDFParseException("Unsupported shader type: " + type);
            }
        }
        PDFObject csObj = shaderObj.getDictRef("ColorSpace");
        if (csObj == null) {
            throw new PDFParseException("No colorspace defined!");
        }
        PDFColorSpace cs = PDFColorSpace.getColorSpace(csObj, resources);
        shader.setColorSpace(cs);
        PDFObject bgObj = shaderObj.getDictRef("Background");
        if (bgObj != null) {
            PDFObject[] bgObjs = bgObj.getArray();
            float[] bgArray = new float[bgObjs.length];
            for (int i = 0; i < bgArray.length; ++i) {
                bgArray[i] = bgObjs[i].getFloatValue();
            }
            PDFPaint paint = cs.getPaint(bgArray);
            shader.setBackground(paint);
        }
        if ((bboxObj = shaderObj.getDictRef("BBox")) != null) {
            PDFObject[] rectObj = bboxObj.getArray();
            float minX = rectObj[0].getFloatValue();
            float minY = rectObj[1].getFloatValue();
            float maxX = rectObj[2].getFloatValue();
            float maxY = rectObj[3].getFloatValue();
            Rectangle2D.Float bbox = new Rectangle2D.Float(minX, minY, maxX - minX, maxY - minY);
            shader.setBBox(bbox);
        }
        shader.parse(shaderObj);
        shaderObj.setCache(shader);
        return shader;
    }

    public int getType() {
        return this.type;
    }

    public PDFColorSpace getColorSpace() {
        return this.colorSpace;
    }

    protected void setColorSpace(PDFColorSpace colorSpace) {
        this.colorSpace = colorSpace;
    }

    public PDFPaint getBackground() {
        return this.background;
    }

    protected void setBackground(PDFPaint background) {
        this.background = background;
    }

    public Rectangle2D getBBox() {
        return this.bbox;
    }

    protected void setBBox(Rectangle2D bbox) {
        this.bbox = bbox;
    }

    public abstract void parse(PDFObject var1) throws IOException;

    public abstract PDFPaint getPaint();
}

