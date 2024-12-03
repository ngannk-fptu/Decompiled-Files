/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.fdf.FDFAnnotation;
import org.w3c.dom.Element;

public class FDFAnnotationCaret
extends FDFAnnotation {
    public static final String SUBTYPE = "Caret";

    public FDFAnnotationCaret() {
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
    }

    public FDFAnnotationCaret(COSDictionary a) {
        super(a);
    }

    public FDFAnnotationCaret(Element element) throws IOException {
        super(element);
        this.annot.setName(COSName.SUBTYPE, SUBTYPE);
        this.initFringe(element);
        String symbol = element.getAttribute("symbol");
        if (symbol != null && !symbol.isEmpty()) {
            this.setSymbol(symbol);
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

    public final void setSymbol(String symbol) {
        String newSymbol = "None";
        if ("paragraph".equals(symbol)) {
            newSymbol = "P";
        }
        this.annot.setString(COSName.SY, newSymbol);
    }

    public String getSymbol() {
        return this.annot.getString(COSName.SY);
    }
}

