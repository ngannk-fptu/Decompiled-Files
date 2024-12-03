/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import java.awt.Color;

public abstract class Barcode {
    public static final int EAN13 = 1;
    public static final int EAN8 = 2;
    public static final int UPCA = 3;
    public static final int UPCE = 4;
    public static final int SUPP2 = 5;
    public static final int SUPP5 = 6;
    public static final int POSTNET = 7;
    public static final int PLANET = 8;
    public static final int CODE128 = 9;
    public static final int CODE128_UCC = 10;
    public static final int CODE128_RAW = 11;
    public static final int CODABAR = 12;
    protected float x;
    protected float n;
    protected BaseFont font;
    protected float size;
    protected float baseline;
    protected float barHeight;
    protected int textAlignment;
    protected boolean generateChecksum;
    protected boolean checksumText;
    protected boolean startStopText;
    protected boolean extended;
    protected String code = "";
    protected boolean guardBars;
    protected int codeType;
    protected float inkSpreading = 0.0f;
    protected String altText;

    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getN() {
        return this.n;
    }

    public void setN(float n) {
        this.n = n;
    }

    public BaseFont getFont() {
        return this.font;
    }

    public void setFont(BaseFont font) {
        this.font = font;
    }

    public float getSize() {
        return this.size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public float getBaseline() {
        return this.baseline;
    }

    public void setBaseline(float baseline) {
        this.baseline = baseline;
    }

    public float getBarHeight() {
        return this.barHeight;
    }

    public void setBarHeight(float barHeight) {
        this.barHeight = barHeight;
    }

    public int getTextAlignment() {
        return this.textAlignment;
    }

    public void setTextAlignment(int textAlignment) {
        this.textAlignment = textAlignment;
    }

    public boolean isGenerateChecksum() {
        return this.generateChecksum;
    }

    public void setGenerateChecksum(boolean generateChecksum) {
        this.generateChecksum = generateChecksum;
    }

    public boolean isChecksumText() {
        return this.checksumText;
    }

    public void setChecksumText(boolean checksumText) {
        this.checksumText = checksumText;
    }

    public boolean isStartStopText() {
        return this.startStopText;
    }

    public void setStartStopText(boolean startStopText) {
        this.startStopText = startStopText;
    }

    public boolean isExtended() {
        return this.extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isGuardBars() {
        return this.guardBars;
    }

    public void setGuardBars(boolean guardBars) {
        this.guardBars = guardBars;
    }

    public int getCodeType() {
        return this.codeType;
    }

    public void setCodeType(int codeType) {
        this.codeType = codeType;
    }

    public abstract Rectangle getBarcodeSize();

    public abstract Rectangle placeBarcode(PdfContentByte var1, Color var2, Color var3);

    public PdfTemplate createTemplateWithBarcode(PdfContentByte cb, Color barColor, Color textColor) {
        PdfTemplate tp = cb.createTemplate(0.0f, 0.0f);
        Rectangle rect = this.placeBarcode(tp, barColor, textColor);
        tp.setBoundingBox(rect);
        return tp;
    }

    public Image createImageWithBarcode(PdfContentByte cb, Color barColor, Color textColor) {
        try {
            return Image.getInstance(this.createTemplateWithBarcode(cb, barColor, textColor));
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public abstract java.awt.Image createAwtImage(Color var1, Color var2);

    public float getInkSpreading() {
        return this.inkSpreading;
    }

    public void setInkSpreading(float inkSpreading) {
        this.inkSpreading = inkSpreading;
    }

    public String getAltText() {
        return this.altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }
}

