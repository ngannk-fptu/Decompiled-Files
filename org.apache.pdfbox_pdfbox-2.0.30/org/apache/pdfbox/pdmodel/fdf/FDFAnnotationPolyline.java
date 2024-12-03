/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.awt.Color;
import java.io.IOException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotation;
import org.w3c.dom.Element;

public class FDFAnnotationPolyline
extends FDFAnnotation {
    private static final Log LOG = LogFactory.getLog(FDFAnnotationPolyline.class);
    public static final String SUBTYPE = "Polyline";

    public FDFAnnotationPolyline() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationPolyline(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationPolyline(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
        this.initVertices(element);
        this.initStyles(element);
    }

    private void initVertices(Element element) throws IOException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            String vertices = xpath.evaluate("vertices[1]", element);
            if (vertices == null || vertices.isEmpty()) {
                throw new IOException("Error: missing element 'vertices'");
            }
            String[] verticesValues = vertices.split(",|;");
            float[] values = new float[verticesValues.length];
            for (int i = 0; i < verticesValues.length; ++i) {
                values[i] = Float.parseFloat(verticesValues[i]);
            }
            this.setVertices(values);
        }
        catch (XPathExpressionException e) {
            LOG.debug((Object)"Error while evaluating XPath expression for polyline vertices");
        }
    }

    private void initStyles(Element element) {
        String color;
        String endStyle;
        String startStyle = element.getAttribute("head");
        if (startStyle != null && !startStyle.isEmpty()) {
            this.setStartPointEndingStyle(startStyle);
        }
        if ((endStyle = element.getAttribute("tail")) != null && !endStyle.isEmpty()) {
            this.setEndPointEndingStyle(endStyle);
        }
        if ((color = element.getAttribute("interior-color")) != null && color.length() == 7 && color.charAt(0) == '#') {
            int colorValue = Integer.parseInt(color.substring(1, 7), 16);
            this.setInteriorColor(new Color(colorValue));
        }
    }

    public void setVertices(float[] vertices) {
        COSArray newVertices = new COSArray();
        newVertices.setFloatArray(vertices);
        this.annot.setItem(COSName.VERTICES, (COSBase)newVertices);
    }

    public float[] getVertices() {
        COSArray array = (COSArray)this.annot.getDictionaryObject(COSName.VERTICES);
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
}

