/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.awt.Color;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotation;
import org.w3c.dom.Element;

public class FDFAnnotationLine
extends FDFAnnotation {
    public static final String SUBTYPE = "Line";

    public FDFAnnotationLine() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationLine(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationLine(Element element) throws IOException {
        super(element);
        String caption;
        String color;
        String endStyle;
        String startStyle;
        String leaderLineOffset;
        String leaderLineExtension;
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
        String startCoords = element.getAttribute("start");
        if (startCoords == null || startCoords.isEmpty()) {
            throw new IOException("Error: missing attribute 'start'");
        }
        String endCoords = element.getAttribute("end");
        if (endCoords == null || endCoords.isEmpty()) {
            throw new IOException("Error: missing attribute 'end'");
        }
        String line = startCoords + "," + endCoords;
        String[] lineValues = line.split(",");
        if (lineValues.length != 4) {
            throw new IOException("Error: wrong amount of line coordinates");
        }
        float[] values = new float[4];
        for (int i = 0; i < 4; ++i) {
            values[i] = Float.parseFloat(lineValues[i]);
        }
        this.setLine(values);
        String leaderLine = element.getAttribute("leaderLength");
        if (leaderLine != null && !leaderLine.isEmpty()) {
            this.setLeaderLength(Float.parseFloat(leaderLine));
        }
        if ((leaderLineExtension = element.getAttribute("leaderExtend")) != null && !leaderLineExtension.isEmpty()) {
            this.setLeaderExtend(Float.parseFloat(leaderLineExtension));
        }
        if ((leaderLineOffset = element.getAttribute("leaderOffset")) != null && !leaderLineOffset.isEmpty()) {
            this.setLeaderOffset(Float.parseFloat(leaderLineOffset));
        }
        if ((startStyle = element.getAttribute("head")) != null && !startStyle.isEmpty()) {
            this.setStartPointEndingStyle(startStyle);
        }
        if ((endStyle = element.getAttribute("tail")) != null && !endStyle.isEmpty()) {
            this.setEndPointEndingStyle(endStyle);
        }
        if ((color = element.getAttribute("interior-color")) != null && color.length() == 7 && color.charAt(0) == '#') {
            int colorValue = Integer.parseInt(color.substring(1, 7), 16);
            this.setInteriorColor(new Color(colorValue));
        }
        if ("yes".equals(caption = element.getAttribute("caption"))) {
            String captionStyle;
            String captionV;
            this.setCaption(true);
            String captionH = element.getAttribute("caption-offset-h");
            if (captionH != null && !captionH.isEmpty()) {
                this.setCaptionHorizontalOffset(Float.parseFloat(captionH));
            }
            if ((captionV = element.getAttribute("caption-offset-v")) != null && !captionV.isEmpty()) {
                this.setCaptionVerticalOffset(Float.parseFloat(captionV));
            }
            if ((captionStyle = element.getAttribute("caption-style")) != null && !captionStyle.isEmpty()) {
                this.setCaptionStyle(captionStyle);
            }
        }
    }

    public void setLine(float[] line) {
        COSArray newLine = new COSArray();
        newLine.setFloatArray(line);
        this.annot.setItem(COSName.L, (COSBase)newLine);
    }

    public float[] getLine() {
        COSArray array = (COSArray)this.annot.getDictionaryObject(COSName.L);
        if (array != null) {
            return array.toFloatArray();
        }
        return null;
    }

    public void setStartPointEndingStyle(String style) {
        COSArray array;
        if (style == null) {
            style = "None";
        }
        if ((array = (COSArray)this.annot.getDictionaryObject(COSName.LE)) == null) {
            array = new COSArray();
            array.add(COSName.getPDFName(style));
            array.add(COSName.getPDFName("None"));
            this.annot.setItem(COSName.LE, (COSBase)array);
        } else {
            array.setName(0, style);
        }
    }

    public String getStartPointEndingStyle() {
        String retval = "None";
        COSArray array = (COSArray)this.annot.getDictionaryObject(COSName.LE);
        if (array != null) {
            retval = array.getName(0);
        }
        return retval;
    }

    public void setEndPointEndingStyle(String style) {
        COSArray array;
        if (style == null) {
            style = "None";
        }
        if ((array = (COSArray)this.annot.getDictionaryObject(COSName.LE)) == null) {
            array = new COSArray();
            array.add(COSName.getPDFName("None"));
            array.add(COSName.getPDFName(style));
            this.annot.setItem(COSName.LE, (COSBase)array);
        } else {
            array.setName(1, style);
        }
    }

    public String getEndPointEndingStyle() {
        String retval = "None";
        COSArray array = (COSArray)this.annot.getDictionaryObject(COSName.LE);
        if (array != null) {
            retval = array.getName(1);
        }
        return retval;
    }

    public void setInteriorColor(Color color) {
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

    public void setCaption(boolean cap) {
        this.annot.setBoolean(COSName.CAP, cap);
    }

    public boolean getCaption() {
        return this.annot.getBoolean(COSName.CAP, false);
    }

    public float getLeaderLength() {
        return this.annot.getFloat(COSName.LL);
    }

    public void setLeaderLength(float leaderLength) {
        this.annot.setFloat(COSName.LL, leaderLength);
    }

    public float getLeaderExtend() {
        return this.annot.getFloat(COSName.LLE);
    }

    public void setLeaderExtend(float leaderExtend) {
        this.annot.setFloat(COSName.LLE, leaderExtend);
    }

    public float getLeaderOffset() {
        return this.annot.getFloat(COSName.LLO);
    }

    public void setLeaderOffset(float leaderOffset) {
        this.annot.setFloat(COSName.LLO, leaderOffset);
    }

    public String getCaptionStyle() {
        return this.annot.getString(COSName.CP);
    }

    public void setCaptionStyle(String captionStyle) {
        this.annot.setString(COSName.CP, captionStyle);
    }

    public void setCaptionHorizontalOffset(float offset) {
        COSArray array = (COSArray)this.annot.getDictionaryObject(COSName.CO);
        if (array == null) {
            array = new COSArray();
            array.setFloatArray(new float[]{offset, 0.0f});
            this.annot.setItem(COSName.CO, (COSBase)array);
        } else {
            array.set(0, new COSFloat(offset));
        }
    }

    public float getCaptionHorizontalOffset() {
        float retval = 0.0f;
        COSArray array = (COSArray)this.annot.getDictionaryObject(COSName.CO);
        if (array != null) {
            retval = array.toFloatArray()[0];
        }
        return retval;
    }

    public void setCaptionVerticalOffset(float offset) {
        COSArray array = (COSArray)this.annot.getDictionaryObject(COSName.CO);
        if (array == null) {
            array = new COSArray();
            array.setFloatArray(new float[]{0.0f, offset});
            this.annot.setItem(COSName.CO, (COSBase)array);
        } else {
            array.set(1, new COSFloat(offset));
        }
    }

    public float getCaptionVerticalOffset() {
        float retval = 0.0f;
        COSArray array = (COSArray)this.annot.getDictionaryObject(COSName.CO);
        if (array != null) {
            retval = array.toFloatArray()[1];
        }
        return retval;
    }
}

