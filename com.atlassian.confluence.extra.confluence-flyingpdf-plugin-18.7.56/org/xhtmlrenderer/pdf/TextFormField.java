/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.TextField;
import java.io.IOException;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.AbstractFormField;
import org.xhtmlrenderer.pdf.ITextFSFont;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.util.Util;

public class TextFormField
extends AbstractFormField {
    private static final String FIELD_TYPE = "Text";
    private static final int DEFAULT_SIZE = 15;
    private int _baseline;
    private boolean multiline = false;

    public TextFormField(LayoutContext c, BlockBox box, int cssWidth, int cssHeight) {
        this.initDimensions(c, box, cssWidth, cssHeight);
        float fontSize = box.getStyle().getFSFont(c).getSize2D();
        this._baseline = (int)((float)(this.getHeight() / 2) + fontSize * 0.3f);
    }

    @Override
    protected void initDimensions(LayoutContext c, BlockBox box, int cssWidth, int cssHeight) {
        if (cssWidth != -1) {
            this.setWidth(cssWidth);
        } else {
            this.setWidth(c.getTextRenderer().getWidth(c.getFontContext(), box.getStyle().getFSFont(c), this.spaces(this.getSize(box.getElement()))));
        }
        if (cssHeight != -1) {
            this.setHeight(cssHeight);
            this.multiline = true;
        } else {
            this.setHeight((int)box.getStyle().getLineHeight(c));
        }
    }

    @Override
    protected String getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    public void paint(RenderingContext c, ITextOutputDevice outputDevice, BlockBox box) {
        PdfWriter writer = outputDevice.getWriter();
        Element elem = box.getElement();
        Rectangle targetArea = outputDevice.createLocalTargetArea(c, box);
        TextField field = new TextField(writer, targetArea, this.getFieldName(outputDevice, elem));
        String value = this.getValue(elem);
        field.setText(value);
        field.setMaxCharacterLength(this.getMaxLength(elem));
        try {
            PdfFormField formField = field.getTextField();
            if (this.multiline) {
                formField.setFieldFlags(4096);
            }
            this.createAppearance(c, outputDevice, box, formField, value);
            if (this.isReadOnly(elem)) {
                formField.setFieldFlags(1);
            }
            writer.addAnnotation(formField);
        }
        catch (IOException ioe) {
            System.out.println(ioe);
        }
        catch (DocumentException de) {
            System.out.println(de);
        }
    }

    private void createAppearance(RenderingContext c, ITextOutputDevice outputDevice, BlockBox box, PdfFormField field, String value) {
        PdfWriter writer = outputDevice.getWriter();
        ITextFSFont font = (ITextFSFont)box.getStyle().getFSFont(c);
        PdfContentByte cb = writer.getDirectContent();
        float width = outputDevice.getDeviceLength(this.getWidth());
        float height = outputDevice.getDeviceLength(this.getHeight());
        float fontSize = outputDevice.getDeviceLength(font.getSize2D());
        PdfAppearance tp = cb.createAppearance(width, height);
        PdfAppearance tp2 = (PdfAppearance)tp.getDuplicate();
        tp2.setFontAndSize(font.getFontDescription().getFont(), fontSize);
        FSColor color = box.getStyle().getColor();
        this.setFillColor(tp2, color);
        field.setDefaultAppearanceString(tp2);
        tp.beginVariableText();
        tp.saveState();
        tp.beginText();
        tp.setFontAndSize(font.getFontDescription().getFont(), fontSize);
        this.setFillColor(tp, color);
        tp.setTextMatrix(0.0f, height / 2.0f - fontSize * 0.3f);
        tp.showText(value);
        tp.endText();
        tp.restoreState();
        tp.endVariableText();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tp);
    }

    private int getSize(Element elem) {
        String sSize = elem.getAttribute("size");
        if (Util.isNullOrEmpty(sSize)) {
            return 15;
        }
        try {
            return Integer.parseInt(sSize.trim());
        }
        catch (NumberFormatException e) {
            return 15;
        }
    }

    private int getMaxLength(Element elem) {
        String sMaxLen = elem.getAttribute("maxlength");
        if (Util.isNullOrEmpty(sMaxLen)) {
            return 0;
        }
        try {
            return Integer.parseInt(sMaxLen.trim());
        }
        catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    protected String getValue(Element e) {
        String result = e.getAttribute("value");
        if (Util.isNullOrEmpty(result)) {
            return "";
        }
        return result;
    }

    @Override
    public int getBaseline() {
        return this._baseline;
    }

    @Override
    public boolean hasBaseline() {
        return true;
    }
}

