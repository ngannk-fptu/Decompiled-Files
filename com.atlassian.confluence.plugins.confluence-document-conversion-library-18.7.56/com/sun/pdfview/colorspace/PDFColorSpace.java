/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.colorspace;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFPaint;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.colorspace.AlternateColorSpace;
import com.sun.pdfview.colorspace.CMYKColorSpace;
import com.sun.pdfview.colorspace.CalGrayColor;
import com.sun.pdfview.colorspace.CalRGBColor;
import com.sun.pdfview.colorspace.IndexedColor;
import com.sun.pdfview.colorspace.LabColor;
import com.sun.pdfview.colorspace.PatternSpace;
import com.sun.pdfview.function.PDFFunction;
import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

public class PDFColorSpace {
    public static final int COLORSPACE_GRAY = 0;
    public static final int COLORSPACE_RGB = 1;
    public static final int COLORSPACE_CMYK = 2;
    public static final int COLORSPACE_PATTERN = 3;
    private static PDFColorSpace rgbSpace = new PDFColorSpace(ColorSpace.getInstance(1000));
    private static PDFColorSpace cmykSpace = new PDFColorSpace(new CMYKColorSpace());
    private static PDFColorSpace patternSpace = new PatternSpace();
    private static PDFColorSpace graySpace;
    ColorSpace cs;

    protected PDFColorSpace(ColorSpace cs) {
        this.cs = cs;
    }

    public static PDFColorSpace getColorSpace(int name) {
        switch (name) {
            case 0: {
                return graySpace;
            }
            case 1: {
                return rgbSpace;
            }
            case 2: {
                return cmykSpace;
            }
            case 3: {
                return patternSpace;
            }
        }
        throw new IllegalArgumentException("Unknown Color Space name: " + name);
    }

    public static PDFColorSpace getColorSpace(PDFObject csobj, Map<?, ?> resources) throws IOException {
        String name;
        PDFObject colorSpaces = null;
        if (resources != null) {
            colorSpaces = (PDFObject)resources.get("ColorSpace");
        }
        if (csobj.getType() == 4) {
            name = csobj.getStringValue();
            PDFColorSpace pdfColorSpace = PDFColorSpace.getColorSpaceByName(name);
            if (pdfColorSpace != null) {
                return pdfColorSpace;
            }
            if (colorSpaces != null) {
                csobj = colorSpaces.getDictRef(name);
            }
        }
        if (csobj == null) {
            return null;
        }
        if (csobj.getCache() != null) {
            return (PDFColorSpace)csobj.getCache();
        }
        PDFColorSpace value = null;
        PDFObject[] ary = csobj.getArray();
        name = ary[0].getStringValue();
        if (name.equals("CalGray")) {
            value = new PDFColorSpace(new CalGrayColor(ary[1]));
        } else if (name.equals("CalRGB")) {
            value = new PDFColorSpace(new CalRGBColor(ary[1]));
        } else if (name.equals("Lab")) {
            value = new PDFColorSpace(new LabColor(ary[1]));
        } else if (name.equals("ICCBased")) {
            ByteArrayInputStream bais = new ByteArrayInputStream(ary[1].getStream());
            ICC_Profile profile = ICC_Profile.getInstance(bais);
            value = new PDFColorSpace(new ICC_ColorSpace(profile));
        } else if (name.equals("Separation") || name.equals("DeviceN")) {
            PDFColorSpace alternate = PDFColorSpace.getColorSpace(ary[2], resources);
            PDFFunction function = PDFFunction.getFunction(ary[3]);
            value = new AlternateColorSpace(alternate, function);
        } else if (name.equals("Indexed") || name.equals("I")) {
            PDFColorSpace refspace = PDFColorSpace.getColorSpace(ary[1], resources);
            int count = ary[2].getIntValue();
            value = new IndexedColor(refspace, count, ary[3]);
        } else {
            if (name.equals("Pattern")) {
                if (ary.length == 1) {
                    return PDFColorSpace.getColorSpace(3);
                }
                PDFColorSpace base = PDFColorSpace.getColorSpace(ary[1], resources);
                return new PatternSpace(base);
            }
            PDFColorSpace pdfColorSpace = PDFColorSpace.getColorSpaceByName(name);
            if (pdfColorSpace != null) {
                return pdfColorSpace;
            }
            throw new PDFParseException("Unknown color space: " + name + " with " + ary[1]);
        }
        csobj.setCache(value);
        return value;
    }

    private static PDFColorSpace getColorSpaceByName(String name) {
        if (name.equals("DeviceGray") || name.equals("G")) {
            return PDFColorSpace.getColorSpace(0);
        }
        if (name.equals("DeviceRGB") || name.equals("RGB")) {
            return PDFColorSpace.getColorSpace(1);
        }
        if (name.equals("DeviceCMYK") || name.equals("CMYK")) {
            return PDFColorSpace.getColorSpace(2);
        }
        if (name.equals("Pattern")) {
            return PDFColorSpace.getColorSpace(3);
        }
        return null;
    }

    public int getNumComponents() {
        return this.cs.getNumComponents();
    }

    public PDFPaint getPaint(float[] components) {
        float[] rgb = this.cs.toRGB(components);
        return PDFPaint.getColorPaint(new Color(rgb[0], rgb[1], rgb[2]));
    }

    public ColorSpace getColorSpace() {
        return this.cs;
    }

    static {
        boolean useSGray = true;
        try {
            graySpace = new PDFColorSpace(!useSGray ? ColorSpace.getInstance(1003) : new ICC_ColorSpace(ICC_Profile.getInstance(PDFColorSpace.class.getResourceAsStream("sGray.icc"))));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

