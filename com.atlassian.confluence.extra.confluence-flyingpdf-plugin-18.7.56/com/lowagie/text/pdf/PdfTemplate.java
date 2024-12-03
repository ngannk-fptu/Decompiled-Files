/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PageResources;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormXObject;
import com.lowagie.text.pdf.PdfIndirectReference;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfOCG;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfStream;
import com.lowagie.text.pdf.PdfTransparencyGroup;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;

public class PdfTemplate
extends PdfContentByte {
    public static final int TYPE_TEMPLATE = 1;
    public static final int TYPE_IMPORTED = 2;
    public static final int TYPE_PATTERN = 3;
    protected int type = 1;
    protected PdfIndirectReference thisReference;
    protected PageResources pageResources;
    protected Rectangle bBox = new Rectangle(0.0f, 0.0f);
    protected PdfArray matrix;
    protected PdfTransparencyGroup group;
    protected PdfOCG layer;

    protected PdfTemplate() {
        super(null);
    }

    PdfTemplate(PdfWriter wr) {
        super(wr);
        this.pageResources = new PageResources();
        this.pageResources.addDefaultColor(wr.getDefaultColorspace());
        this.thisReference = this.writer.getPdfIndirectReference();
    }

    public static PdfTemplate createTemplate(PdfWriter writer, float width, float height) {
        return PdfTemplate.createTemplate(writer, width, height, null);
    }

    static PdfTemplate createTemplate(PdfWriter writer, float width, float height, PdfName forcedName) {
        PdfTemplate template = new PdfTemplate(writer);
        template.setWidth(width);
        template.setHeight(height);
        writer.addDirectTemplateSimple(template, forcedName);
        return template;
    }

    public void setWidth(float width) {
        this.bBox.setLeft(0.0f);
        this.bBox.setRight(width);
    }

    public void setHeight(float height) {
        this.bBox.setBottom(0.0f);
        this.bBox.setTop(height);
    }

    public float getWidth() {
        return this.bBox.getWidth();
    }

    public float getHeight() {
        return this.bBox.getHeight();
    }

    public Rectangle getBoundingBox() {
        return this.bBox;
    }

    public void setBoundingBox(Rectangle bBox) {
        this.bBox = bBox;
    }

    public void setLayer(PdfOCG layer) {
        this.layer = layer;
    }

    public PdfOCG getLayer() {
        return this.layer;
    }

    public void setMatrix(float a, float b, float c, float d, float e, float f) {
        this.matrix = new PdfArray();
        this.matrix.add(new PdfNumber(a));
        this.matrix.add(new PdfNumber(b));
        this.matrix.add(new PdfNumber(c));
        this.matrix.add(new PdfNumber(d));
        this.matrix.add(new PdfNumber(e));
        this.matrix.add(new PdfNumber(f));
    }

    PdfArray getMatrix() {
        return this.matrix;
    }

    public PdfIndirectReference getIndirectReference() {
        if (this.thisReference == null) {
            this.thisReference = this.writer.getPdfIndirectReference();
        }
        return this.thisReference;
    }

    public void beginVariableText() {
        this.content.append("/Tx BMC ");
    }

    public void endVariableText() {
        this.content.append("EMC ");
    }

    PdfObject getResources() {
        return this.getPageResources().getResources();
    }

    PdfStream getFormXObject(int compressionLevel) throws IOException {
        return new PdfFormXObject(this, compressionLevel);
    }

    @Override
    public PdfContentByte getDuplicate() {
        PdfTemplate tpl = new PdfTemplate();
        tpl.writer = this.writer;
        tpl.pdf = this.pdf;
        tpl.thisReference = this.thisReference;
        tpl.pageResources = this.pageResources;
        tpl.bBox = new Rectangle(this.bBox);
        tpl.group = this.group;
        tpl.layer = this.layer;
        if (this.matrix != null) {
            tpl.matrix = new PdfArray(this.matrix);
        }
        tpl.separator = this.separator;
        return tpl;
    }

    public int getType() {
        return this.type;
    }

    @Override
    PageResources getPageResources() {
        return this.pageResources;
    }

    public PdfTransparencyGroup getGroup() {
        return this.group;
    }

    public void setGroup(PdfTransparencyGroup group) {
        this.group = group;
    }
}

