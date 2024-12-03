/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseField;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfDashPattern;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;

public class RadioCheckField
extends BaseField {
    public static final int TYPE_CHECK = 1;
    public static final int TYPE_CIRCLE = 2;
    public static final int TYPE_CROSS = 3;
    public static final int TYPE_DIAMOND = 4;
    public static final int TYPE_SQUARE = 5;
    public static final int TYPE_STAR = 6;
    private static String[] typeChars = new String[]{"4", "l", "8", "u", "n", "H"};
    private int checkType;
    private String onValue;
    private boolean checked;

    public RadioCheckField(PdfWriter writer, Rectangle box, String fieldName, String onValue) {
        super(writer, box, fieldName);
        this.setOnValue(onValue);
        this.setCheckType(1);
    }

    public int getCheckType() {
        return this.checkType;
    }

    public void setCheckType(int checkType) {
        if (checkType < 1 || checkType > 6) {
            checkType = 1;
        }
        this.checkType = checkType;
        this.setText(typeChars[checkType - 1]);
        try {
            this.setFont(BaseFont.createFont("ZapfDingbats", "Cp1252", false));
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public String getOnValue() {
        return this.onValue;
    }

    public void setOnValue(String onValue) {
        this.onValue = onValue;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public PdfAppearance getAppearance(boolean isRadio, boolean on) throws IOException, DocumentException {
        if (isRadio && this.checkType == 2) {
            return this.getAppearanceRadioCircle(on);
        }
        PdfAppearance app = this.getBorderAppearance();
        if (!on) {
            return app;
        }
        BaseFont ufont = this.getRealFont();
        boolean borderExtra = this.borderStyle == 2 || this.borderStyle == 3;
        float h = this.box.getHeight() - this.borderWidth * 2.0f;
        float bw2 = this.borderWidth;
        if (borderExtra) {
            h -= this.borderWidth * 2.0f;
            bw2 *= 2.0f;
        }
        float offsetX = borderExtra ? 2.0f * this.borderWidth : this.borderWidth;
        offsetX = Math.max(offsetX, 1.0f);
        float offX = Math.min(bw2, offsetX);
        float wt = this.box.getWidth() - 2.0f * offX;
        float ht = this.box.getHeight() - 2.0f * offX;
        float fsize = this.fontSize;
        if (fsize == 0.0f) {
            float bw = ufont.getWidthPoint(this.text, 1.0f);
            fsize = bw == 0.0f ? 12.0f : wt / bw;
            float nfsize = h / ufont.getFontDescriptor(1, 1.0f);
            fsize = Math.min(fsize, nfsize);
        }
        app.saveState();
        app.rectangle(offX, offX, wt, ht);
        app.clip();
        app.newPath();
        if (this.textColor == null) {
            app.resetGrayFill();
        } else {
            app.setColorFill(this.textColor);
        }
        app.beginText();
        app.setFontAndSize(ufont, fsize);
        app.setTextMatrix((this.box.getWidth() - ufont.getWidthPoint(this.text, fsize)) / 2.0f, (this.box.getHeight() - ufont.getAscentPoint(this.text, fsize)) / 2.0f);
        app.showText(this.text);
        app.endText();
        app.restoreState();
        return app;
    }

    public PdfAppearance getAppearanceRadioCircle(boolean on) {
        PdfAppearance app = PdfAppearance.createAppearance(this.writer, this.box.getWidth(), this.box.getHeight());
        switch (this.rotation) {
            case 90: {
                app.setMatrix(0.0f, 1.0f, -1.0f, 0.0f, this.box.getHeight(), 0.0f);
                break;
            }
            case 180: {
                app.setMatrix(-1.0f, 0.0f, 0.0f, -1.0f, this.box.getWidth(), this.box.getHeight());
                break;
            }
            case 270: {
                app.setMatrix(0.0f, -1.0f, 1.0f, 0.0f, 0.0f, this.box.getWidth());
            }
        }
        Rectangle box = new Rectangle(app.getBoundingBox());
        float cx = box.getWidth() / 2.0f;
        float cy = box.getHeight() / 2.0f;
        float r = (Math.min(box.getWidth(), box.getHeight()) - this.borderWidth) / 2.0f;
        if (r <= 0.0f) {
            return app;
        }
        if (this.backgroundColor != null) {
            app.setColorFill(this.backgroundColor);
            app.circle(cx, cy, r + this.borderWidth / 2.0f);
            app.fill();
        }
        if (this.borderWidth > 0.0f && this.borderColor != null) {
            app.setLineWidth(this.borderWidth);
            app.setColorStroke(this.borderColor);
            app.circle(cx, cy, r);
            app.stroke();
        }
        if (on) {
            if (this.textColor == null) {
                app.resetGrayFill();
            } else {
                app.setColorFill(this.textColor);
            }
            app.circle(cx, cy, r / 2.0f);
            app.fill();
        }
        return app;
    }

    public PdfFormField getRadioGroup(boolean noToggleToOff, boolean radiosInUnison) {
        PdfFormField field = PdfFormField.createRadioButton(this.writer, noToggleToOff);
        if (radiosInUnison) {
            field.setFieldFlags(0x2000000);
        }
        field.setFieldName(this.fieldName);
        if ((this.options & 1) != 0) {
            field.setFieldFlags(1);
        }
        if ((this.options & 2) != 0) {
            field.setFieldFlags(2);
        }
        field.setValueAsName(this.checked ? this.onValue : "Off");
        return field;
    }

    public PdfFormField getRadioField() throws IOException, DocumentException {
        return this.getField(true);
    }

    public PdfFormField getCheckField() throws IOException, DocumentException {
        return this.getField(false);
    }

    protected PdfFormField getField(boolean isRadio) throws IOException, DocumentException {
        PdfFormField field = null;
        field = isRadio ? PdfFormField.createEmpty(this.writer) : PdfFormField.createCheckBox(this.writer);
        field.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
        if (!isRadio) {
            field.setFieldName(this.fieldName);
            if ((this.options & 1) != 0) {
                field.setFieldFlags(1);
            }
            if ((this.options & 2) != 0) {
                field.setFieldFlags(2);
            }
            field.setValueAsName(this.checked ? this.onValue : "Off");
        }
        if (this.text != null) {
            field.setMKNormalCaption(this.text);
        }
        if (this.rotation != 0) {
            field.setMKRotation(this.rotation);
        }
        field.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0f)));
        PdfAppearance tpon = this.getAppearance(isRadio, true);
        PdfAppearance tpoff = this.getAppearance(isRadio, false);
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, this.onValue, tpon);
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, "Off", tpoff);
        field.setAppearanceState(this.checked ? this.onValue : "Off");
        PdfAppearance da = (PdfAppearance)tpon.getDuplicate();
        da.setFontAndSize(this.getRealFont(), this.fontSize);
        if (this.textColor == null) {
            da.setGrayFill(0.0f);
        } else {
            da.setColorFill(this.textColor);
        }
        field.setDefaultAppearanceString(da);
        if (this.borderColor != null) {
            field.setMKBorderColor(this.borderColor);
        }
        if (this.backgroundColor != null) {
            field.setMKBackgroundColor(this.backgroundColor);
        }
        switch (this.visibility) {
            case 1: {
                field.setFlags(6);
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                field.setFlags(36);
                break;
            }
            default: {
                field.setFlags(4);
            }
        }
        return field;
    }
}

