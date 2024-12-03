/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.pdf.PdfAcroForm;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Point;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.AbstractFormField;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.RenderingContext;

public class EmptyReplacedElement
extends AbstractFormField {
    private static final String FIELD_TYPE = "Hidden";
    private int _width;
    private int _height;
    private Point _location = new Point(0, 0);

    public EmptyReplacedElement(int width, int height) {
        this._width = width;
        this._height = height;
    }

    @Override
    public void paint(RenderingContext c, ITextOutputDevice outputDevice, BlockBox box) {
        PdfContentByte cb = outputDevice.getCurrentPage();
        PdfWriter writer = outputDevice.getWriter();
        PdfAcroForm acroForm = writer.getAcroForm();
        Element elem = box.getElement();
        String name = this.getFieldName(outputDevice, elem);
        String value = this.getValue(elem);
        if (value.length() > 127) {
            value = value.substring(0, 127);
        }
        acroForm.addHiddenField(name, value);
    }

    @Override
    public int getIntrinsicWidth() {
        return this._width;
    }

    @Override
    public int getIntrinsicHeight() {
        return this._height;
    }

    @Override
    public Point getLocation() {
        return this._location;
    }

    @Override
    public void setLocation(int x, int y) {
        this._location = new Point(0, 0);
    }

    @Override
    protected String getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    public void detach(LayoutContext c) {
    }

    @Override
    public boolean isRequiresInteractivePaint() {
        return false;
    }

    @Override
    public boolean hasBaseline() {
        return false;
    }

    @Override
    public int getBaseline() {
        return 0;
    }
}

