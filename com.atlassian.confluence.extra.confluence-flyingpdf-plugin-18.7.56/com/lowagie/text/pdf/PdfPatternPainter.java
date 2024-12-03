/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPattern;
import com.lowagie.text.pdf.PdfSpotColor;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

public final class PdfPatternPainter
extends PdfTemplate {
    float xstep;
    float ystep;
    boolean stencil = false;
    Color defaultColor;

    private PdfPatternPainter() {
        this.type = 3;
    }

    PdfPatternPainter(PdfWriter wr) {
        super(wr);
        this.type = 3;
    }

    PdfPatternPainter(PdfWriter wr, Color defaultColor) {
        this(wr);
        this.stencil = true;
        this.defaultColor = defaultColor == null ? Color.gray : defaultColor;
    }

    public void setXStep(float xstep) {
        this.xstep = xstep;
    }

    public void setYStep(float ystep) {
        this.ystep = ystep;
    }

    public float getXStep() {
        return this.xstep;
    }

    public float getYStep() {
        return this.ystep;
    }

    public boolean isStencil() {
        return this.stencil;
    }

    public void setPatternMatrix(float a, float b, float c, float d, float e, float f) {
        this.setMatrix(a, b, c, d, e, f);
    }

    PdfPattern getPattern() {
        return new PdfPattern(this);
    }

    PdfPattern getPattern(int compressionLevel) {
        return new PdfPattern(this, compressionLevel);
    }

    @Override
    public PdfContentByte getDuplicate() {
        PdfPatternPainter tpl = new PdfPatternPainter();
        tpl.writer = this.writer;
        tpl.pdf = this.pdf;
        tpl.thisReference = this.thisReference;
        tpl.pageResources = this.pageResources;
        tpl.bBox = new Rectangle(this.bBox);
        tpl.xstep = this.xstep;
        tpl.ystep = this.ystep;
        tpl.matrix = this.matrix;
        tpl.stencil = this.stencil;
        tpl.defaultColor = this.defaultColor;
        return tpl;
    }

    public Color getDefaultColor() {
        return this.defaultColor;
    }

    @Override
    public void setGrayFill(float gray) {
        this.checkNoColor();
        super.setGrayFill(gray);
    }

    @Override
    public void resetGrayFill() {
        this.checkNoColor();
        super.resetGrayFill();
    }

    @Override
    public void setGrayStroke(float gray) {
        this.checkNoColor();
        super.setGrayStroke(gray);
    }

    @Override
    public void resetGrayStroke() {
        this.checkNoColor();
        super.resetGrayStroke();
    }

    @Override
    public void setRGBColorFillF(float red, float green, float blue) {
        this.checkNoColor();
        super.setRGBColorFillF(red, green, blue);
    }

    @Override
    public void resetRGBColorFill() {
        this.checkNoColor();
        super.resetRGBColorFill();
    }

    @Override
    public void setRGBColorStrokeF(float red, float green, float blue) {
        this.checkNoColor();
        super.setRGBColorStrokeF(red, green, blue);
    }

    @Override
    public void resetRGBColorStroke() {
        this.checkNoColor();
        super.resetRGBColorStroke();
    }

    @Override
    public void setCMYKColorFillF(float cyan, float magenta, float yellow, float black) {
        this.checkNoColor();
        super.setCMYKColorFillF(cyan, magenta, yellow, black);
    }

    @Override
    public void resetCMYKColorFill() {
        this.checkNoColor();
        super.resetCMYKColorFill();
    }

    @Override
    public void setCMYKColorStrokeF(float cyan, float magenta, float yellow, float black) {
        this.checkNoColor();
        super.setCMYKColorStrokeF(cyan, magenta, yellow, black);
    }

    @Override
    public void resetCMYKColorStroke() {
        this.checkNoColor();
        super.resetCMYKColorStroke();
    }

    @Override
    public void addImage(Image image, float a, float b, float c, float d, float e, float f) throws DocumentException {
        if (this.stencil && !image.isMask()) {
            this.checkNoColor();
        }
        super.addImage(image, a, b, c, d, e, f);
    }

    @Override
    public void setCMYKColorFill(int cyan, int magenta, int yellow, int black) {
        this.checkNoColor();
        super.setCMYKColorFill(cyan, magenta, yellow, black);
    }

    @Override
    public void setCMYKColorStroke(int cyan, int magenta, int yellow, int black) {
        this.checkNoColor();
        super.setCMYKColorStroke(cyan, magenta, yellow, black);
    }

    @Override
    public void setRGBColorFill(int red, int green, int blue) {
        this.checkNoColor();
        super.setRGBColorFill(red, green, blue);
    }

    @Override
    public void setRGBColorStroke(int red, int green, int blue) {
        this.checkNoColor();
        super.setRGBColorStroke(red, green, blue);
    }

    @Override
    public void setColorStroke(Color color) {
        this.checkNoColor();
        super.setColorStroke(color);
    }

    @Override
    public void setColorFill(Color color) {
        this.checkNoColor();
        super.setColorFill(color);
    }

    @Override
    public void setColorFill(PdfSpotColor sp, float tint) {
        this.checkNoColor();
        super.setColorFill(sp, tint);
    }

    @Override
    public void setColorStroke(PdfSpotColor sp, float tint) {
        this.checkNoColor();
        super.setColorStroke(sp, tint);
    }

    @Override
    public void setPatternFill(PdfPatternPainter p) {
        this.checkNoColor();
        super.setPatternFill(p);
    }

    @Override
    public void setPatternFill(PdfPatternPainter p, Color color, float tint) {
        this.checkNoColor();
        super.setPatternFill(p, color, tint);
    }

    @Override
    public void setPatternStroke(PdfPatternPainter p, Color color, float tint) {
        this.checkNoColor();
        super.setPatternStroke(p, color, tint);
    }

    @Override
    public void setPatternStroke(PdfPatternPainter p) {
        this.checkNoColor();
        super.setPatternStroke(p);
    }

    void checkNoColor() {
        if (this.stencil) {
            throw new RuntimeException(MessageLocalization.getComposedMessage("colors.are.not.allowed.in.uncolored.tile.patterns"));
        }
    }
}

