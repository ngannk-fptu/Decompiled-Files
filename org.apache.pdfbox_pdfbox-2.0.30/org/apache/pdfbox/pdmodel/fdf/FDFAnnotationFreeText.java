/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.fdf;

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
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotation;
import org.w3c.dom.Element;

public class FDFAnnotationFreeText
extends FDFAnnotation {
    private static final Log LOG = LogFactory.getLog(FDFAnnotationFreeText.class);
    public static final String SUBTYPE = "FreeText";

    public FDFAnnotationFreeText() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationFreeText(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationFreeText(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
        this.setJustification(element.getAttribute("justification"));
        XPath xpath = XPathFactory.newInstance().newXPath();
        try {
            this.setDefaultAppearance(xpath.evaluate("defaultappearance", element));
            this.setDefaultStyle(xpath.evaluate("defaultstyle", element));
        }
        catch (XPathExpressionException ex) {
            LOG.debug((Object)"Error while evaluating XPath expression");
        }
        this.initCallout(element);
        String rotation = element.getAttribute("rotation");
        if (rotation != null && !rotation.isEmpty()) {
            this.setRotation(Integer.parseInt(rotation));
        }
        this.initFringe(element);
        String lineEndingStyle = element.getAttribute("head");
        if (lineEndingStyle != null && !lineEndingStyle.isEmpty()) {
            this.setLineEndingStyle(lineEndingStyle);
        }
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

    private void initCallout(Element element) {
        String callout = element.getAttribute("callout");
        if (callout != null && !callout.isEmpty()) {
            String[] calloutValues = callout.split(",");
            float[] values = new float[calloutValues.length];
            for (int i = 0; i < calloutValues.length; ++i) {
                values[i] = Float.parseFloat(calloutValues[i]);
            }
            this.setCallout(values);
        }
    }

    public void setCallout(float[] callout) {
        COSArray newCallout = new COSArray();
        newCallout.setFloatArray(callout);
        this.annot.setItem(COSName.CL, (COSBase)newCallout);
    }

    public float[] getCallout() {
        COSArray array = (COSArray)this.annot.getDictionaryObject(COSName.CL);
        if (array != null) {
            return array.toFloatArray();
        }
        return null;
    }

    public final void setJustification(String justification) {
        int quadding = 0;
        if ("centered".equals(justification)) {
            quadding = 1;
        } else if ("right".equals(justification)) {
            quadding = 2;
        }
        this.annot.setInt(COSName.Q, quadding);
    }

    public String getJustification() {
        return "" + this.annot.getInt(COSName.Q, 0);
    }

    public final void setRotation(int rotation) {
        this.annot.setInt(COSName.ROTATE, rotation);
    }

    public String getRotation() {
        return this.annot.getString(COSName.ROTATE);
    }

    public final void setDefaultAppearance(String appearance) {
        this.annot.setString(COSName.DA, appearance);
    }

    public String getDefaultAppearance() {
        return this.annot.getString(COSName.DA);
    }

    public final void setDefaultStyle(String style) {
        this.annot.setString(COSName.DS, style);
    }

    public String getDefaultStyle() {
        return this.annot.getString(COSName.DS);
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

    public final void setLineEndingStyle(String style) {
        this.annot.setName(COSName.LE, style);
    }

    public String getLineEndingStyle() {
        return this.annot.getNameAsString(COSName.LE);
    }
}

