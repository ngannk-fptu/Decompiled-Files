/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.awt.Color;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotation;
import org.w3c.dom.Element;

public class FDFAnnotationCircle
extends FDFAnnotation {
    public static final String SUBTYPE = "Circle";

    public FDFAnnotationCircle() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationCircle(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationCircle(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
        String color = element.getAttribute("interior-color");
        if (color != null && color.length() == 7 && color.charAt(0) == '#') {
            int colorValue = Integer.parseInt(color.substring(1, 7), 16);
            this.setInteriorColor(new Color(colorValue));
        }
        this.initFringe(element);
    }

    private void initFringe(Element element) throws IOException {
        String fringe = element.getAttribute("fringe");
        if (fringe != null && !fringe.isEmpty()) {
            String[] fringeValues = fringe.split(",");
            if (fringeValues.length != 4) {
                throw new IOException("Error: wrong amount of numbers in attribute 'fringe'");
            }
            PDRectangle rect = new PDRectangle();
            rect.setLowerLeftX(Float.parseFloat(fringeValues[0]));
            rect.setLowerLeftY(Float.parseFloat(fringeValues[1]));
            rect.setUpperRightX(Float.parseFloat(fringeValues[2]));
            rect.setUpperRightY(Float.parseFloat(fringeValues[3]));
            this.setFringe(rect);
        }
    }

    public final void setInteriorColor(Color color) {
        COSArray array = null;
        if (color != null) {
            float[] colors = color.getRGBColorComponents(null);
            array = new COSArray();
            array.setFloatArray(colors);
        }
        this.annot.setItem(COSName.IC, array);
    }

    public Color getInteriorColor() {
        float[] rgb;
        Color retval = null;
        COSArray array = (COSArray)this.annot.getDictionaryObject(COSName.IC);
        if (array != null && (rgb = array.toFloatArray()).length >= 3) {
            retval = new Color(rgb[0], rgb[1], rgb[2]);
        }
        return retval;
    }

    public final void setFringe(PDRectangle fringe) {
        this.annot.setItem(COSName.RD, (COSObjectable)fringe);
    }

    public PDRectangle getFringe() {
        COSArray rd = (COSArray)this.annot.getDictionaryObject(COSName.RD);
        if (rd != null) {
            return new PDRectangle(rd);
        }
        return null;
    }
}

