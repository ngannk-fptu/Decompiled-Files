/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.pdf;

import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Rectangle;
import java.util.List;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.AbstractFormField;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextReplacedElementFactory;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;

public class RadioButtonFormField
extends AbstractFormField {
    private static final String FIELD_TYPE = "RadioButton";
    private ITextReplacedElementFactory _factory;
    private Box _box;

    @Override
    protected String getFieldType() {
        return FIELD_TYPE;
    }

    public RadioButtonFormField(ITextReplacedElementFactory factory, LayoutContext c, BlockBox box, int cssWidth, int cssHeight) {
        this._factory = factory;
        this._box = box;
        this.initDimensions(c, box, cssWidth, cssHeight);
    }

    @Override
    public void paint(RenderingContext c, ITextOutputDevice outputDevice, BlockBox box) {
        String fieldName = this.getFieldName(outputDevice, box.getElement());
        List radioBoxes = this._factory.getRadioButtons(fieldName);
        if (radioBoxes == null) {
            return;
        }
        PdfContentByte cb = outputDevice.getCurrentPage();
        PdfWriter writer = outputDevice.getWriter();
        PdfFormField group = PdfFormField.createRadioButton(writer, true);
        group.setFieldName(fieldName);
        RadioButtonFormField checked = this.getChecked(radioBoxes);
        if (checked != null) {
            group.setValueAsString(this.getValue(checked.getBox().getElement()));
        }
        for (RadioButtonFormField fieldElem : radioBoxes) {
            this.createField(c, outputDevice, cb, writer, group, fieldElem, checked);
        }
        writer.addAnnotation(group);
        this._factory.remove(fieldName);
    }

    private RadioButtonFormField getChecked(List fields) {
        RadioButtonFormField result = null;
        for (RadioButtonFormField f : fields) {
            if (!this.isChecked(f.getBox().getElement())) continue;
            result = f;
        }
        return result;
    }

    private void createField(RenderingContext c, ITextOutputDevice outputDevice, PdfContentByte cb, PdfWriter writer, PdfFormField group, RadioButtonFormField fieldElem, RadioButtonFormField checked) {
        Box box = fieldElem.getBox();
        Element e = box.getElement();
        String onValue = this.getValue(e);
        float width = outputDevice.getDeviceLength(fieldElem.getWidth());
        float height = outputDevice.getDeviceLength(fieldElem.getHeight());
        PdfFormField field = PdfFormField.createEmpty(writer);
        FSColor color = box.getStyle().getColor();
        FSColor darker = box.getEffBackgroundColor(c).darkenColor();
        this.createAppearances(cb, field, onValue, width, height, true, color, darker);
        this.createAppearances(cb, field, onValue, width, height, false, color, darker);
        field.setWidget(outputDevice.createTargetArea(c, box), PdfAnnotation.HIGHLIGHT_INVERT);
        Rectangle bounds = box.getContentAreaEdge(box.getAbsX(), box.getAbsY(), c);
        PageBox page = c.getRootLayer().getPage(c, bounds.y);
        field.setPlaceInPage(page.getPageNo() + 1);
        field.setBorderStyle(new PdfBorderDictionary(0.0f, 0));
        field.setAppearanceState(fieldElem == checked ? onValue : "Off");
        if (this.isReadOnly(e)) {
            field.setFieldFlags(1);
        }
        group.addKid(field);
    }

    private void createAppearances(PdfContentByte cb, PdfFormField field, String onValue, float width, float height, boolean normal, FSColor color, FSColor darker) {
        PdfAppearance tpOff = cb.createAppearance(width, height);
        PdfAppearance tpOn = cb.createAppearance(width, height);
        float diameter = Math.min(width, height);
        this.setStrokeColor(tpOff, color);
        this.setStrokeColor(tpOn, color);
        if (!normal) {
            this.setStrokeColor(tpOff, darker);
            this.setStrokeColor(tpOn, darker);
        }
        float strokeWidth = Math.max(1.0f, this.reduce(diameter));
        tpOff.setLineWidth(strokeWidth);
        tpOn.setLineWidth(strokeWidth);
        tpOff.circle(width / 2.0f, height / 2.0f, diameter / 2.0f - strokeWidth / 2.0f);
        tpOn.circle(width / 2.0f, height / 2.0f, diameter / 2.0f - strokeWidth / 2.0f);
        if (!normal) {
            tpOff.fillStroke();
            tpOn.fillStroke();
        } else {
            tpOff.stroke();
            tpOn.stroke();
        }
        this.setFillColor(tpOn, color);
        if (!normal) {
            tpOn.circle(width / 2.0f, height / 2.0f, diameter * 0.23f);
        } else {
            tpOn.circle(width / 2.0f, height / 2.0f, diameter * 0.2f);
        }
        tpOn.fill();
        if (normal) {
            field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpOff);
            field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, onValue, tpOn);
        } else {
            field.setAppearance(PdfAnnotation.APPEARANCE_DOWN, "Off", tpOff);
            field.setAppearance(PdfAnnotation.APPEARANCE_DOWN, onValue, tpOn);
        }
    }

    private float reduce(float value) {
        return Math.min(value, Math.max(1.0f, 0.05f * value));
    }

    @Override
    public void detach(LayoutContext c) {
        super.detach(c);
        this._factory.remove(this._box.getElement());
    }

    public Box getBox() {
        return this._box;
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

