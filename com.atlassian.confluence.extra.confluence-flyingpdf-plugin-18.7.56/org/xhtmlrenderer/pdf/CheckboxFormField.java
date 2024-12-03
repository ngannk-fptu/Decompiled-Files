/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.RadioCheckField;
import java.awt.Color;
import java.io.IOException;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.AbstractFormField;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.RenderingContext;

public class CheckboxFormField
extends AbstractFormField {
    private static final String FIELD_TYPE = "Checkbox";

    public CheckboxFormField(LayoutContext c, BlockBox box, int cssWidth, int cssHeight) {
        this.initDimensions(c, box, cssWidth, cssHeight);
    }

    @Override
    protected String getFieldType() {
        return FIELD_TYPE;
    }

    @Override
    public void paint(RenderingContext c, ITextOutputDevice outputDevice, BlockBox box) {
        PdfContentByte cb = outputDevice.getCurrentPage();
        PdfWriter writer = outputDevice.getWriter();
        Element elm = box.getElement();
        Rectangle targetArea = outputDevice.createLocalTargetArea(c, box);
        String onValue = this.getValue(elm);
        RadioCheckField field = new RadioCheckField(writer, targetArea, this.getFieldName(outputDevice, elm), onValue);
        field.setChecked(this.isChecked(elm));
        field.setCheckType(1);
        field.setBorderStyle(0);
        field.setBorderColor(Color.black);
        field.setBorderWidth(1.0f);
        try {
            PdfFormField formField = field.getCheckField();
            if (this.isReadOnly(elm)) {
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

    @Override
    public int getBaseline() {
        return 0;
    }

    @Override
    public boolean hasBaseline() {
        return false;
    }
}

