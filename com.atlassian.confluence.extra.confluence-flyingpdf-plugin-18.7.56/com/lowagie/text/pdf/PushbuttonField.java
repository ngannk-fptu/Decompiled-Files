/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.BaseField;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfAppearance;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfBorderDictionary;
import com.lowagie.text.pdf.PdfDashPattern;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfFormField;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfNumber;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.io.IOException;

public class PushbuttonField
extends BaseField {
    public static final int LAYOUT_LABEL_ONLY = 1;
    public static final int LAYOUT_ICON_ONLY = 2;
    public static final int LAYOUT_ICON_TOP_LABEL_BOTTOM = 3;
    public static final int LAYOUT_LABEL_TOP_ICON_BOTTOM = 4;
    public static final int LAYOUT_ICON_LEFT_LABEL_RIGHT = 5;
    public static final int LAYOUT_LABEL_LEFT_ICON_RIGHT = 6;
    public static final int LAYOUT_LABEL_OVER_ICON = 7;
    public static final int SCALE_ICON_ALWAYS = 1;
    public static final int SCALE_ICON_NEVER = 2;
    public static final int SCALE_ICON_IS_TOO_BIG = 3;
    public static final int SCALE_ICON_IS_TOO_SMALL = 4;
    private int layout = 1;
    private Image image;
    private PdfTemplate template;
    private int scaleIcon = 1;
    private boolean proportionalIcon = true;
    private float iconVerticalAdjustment = 0.5f;
    private float iconHorizontalAdjustment = 0.5f;
    private boolean iconFitToBounds;
    private PdfTemplate tp;
    private PRIndirectReference iconReference;

    public PushbuttonField(PdfWriter writer, Rectangle box, String fieldName) {
        super(writer, box, fieldName);
    }

    public int getLayout() {
        return this.layout;
    }

    public void setLayout(int layout) {
        if (layout < 1 || layout > 7) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("layout.out.of.bounds"));
        }
        this.layout = layout;
    }

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
        this.template = null;
    }

    public PdfTemplate getTemplate() {
        return this.template;
    }

    public void setTemplate(PdfTemplate template) {
        this.template = template;
        this.image = null;
    }

    public int getScaleIcon() {
        return this.scaleIcon;
    }

    public void setScaleIcon(int scaleIcon) {
        if (scaleIcon < 1 || scaleIcon > 4) {
            scaleIcon = 1;
        }
        this.scaleIcon = scaleIcon;
    }

    public boolean isProportionalIcon() {
        return this.proportionalIcon;
    }

    public void setProportionalIcon(boolean proportionalIcon) {
        this.proportionalIcon = proportionalIcon;
    }

    public float getIconVerticalAdjustment() {
        return this.iconVerticalAdjustment;
    }

    public void setIconVerticalAdjustment(float iconVerticalAdjustment) {
        if (iconVerticalAdjustment < 0.0f) {
            iconVerticalAdjustment = 0.0f;
        } else if (iconVerticalAdjustment > 1.0f) {
            iconVerticalAdjustment = 1.0f;
        }
        this.iconVerticalAdjustment = iconVerticalAdjustment;
    }

    public float getIconHorizontalAdjustment() {
        return this.iconHorizontalAdjustment;
    }

    public void setIconHorizontalAdjustment(float iconHorizontalAdjustment) {
        if (iconHorizontalAdjustment < 0.0f) {
            iconHorizontalAdjustment = 0.0f;
        } else if (iconHorizontalAdjustment > 1.0f) {
            iconHorizontalAdjustment = 1.0f;
        }
        this.iconHorizontalAdjustment = iconHorizontalAdjustment;
    }

    private float calculateFontSize(float w, float h) throws IOException, DocumentException {
        BaseFont ufont = this.getRealFont();
        float fsize = this.fontSize;
        if (fsize == 0.0f) {
            float bw = ufont.getWidthPoint(this.text, 1.0f);
            fsize = bw == 0.0f ? 12.0f : w / bw;
            float nfsize = h / (1.0f - ufont.getFontDescriptor(3, 1.0f));
            if ((fsize = Math.min(fsize, nfsize)) < 4.0f) {
                fsize = 4.0f;
            }
        }
        return fsize;
    }

    public PdfAppearance getAppearance() throws IOException, DocumentException {
        PdfAppearance app = this.getBorderAppearance();
        Rectangle box = new Rectangle(app.getBoundingBox());
        if ((this.text == null || this.text.length() == 0) && (this.layout == 1 || this.image == null && this.template == null && this.iconReference == null)) {
            return app;
        }
        if (this.layout == 2 && this.image == null && this.template == null && this.iconReference == null) {
            return app;
        }
        BaseFont ufont = this.getRealFont();
        boolean borderExtra = this.borderStyle == 2 || this.borderStyle == 3;
        float h = box.getHeight() - this.borderWidth * 2.0f;
        float bw2 = this.borderWidth;
        if (borderExtra) {
            h -= this.borderWidth * 2.0f;
            bw2 *= 2.0f;
        }
        float offsetX = borderExtra ? 2.0f * this.borderWidth : this.borderWidth;
        offsetX = Math.max(offsetX, 1.0f);
        float offX = Math.min(bw2, offsetX);
        this.tp = null;
        float textX = Float.NaN;
        float textY = 0.0f;
        float fsize = this.fontSize;
        float wt = box.getWidth() - 2.0f * offX - 2.0f;
        float ht = box.getHeight() - 2.0f * offX;
        float adj = this.iconFitToBounds ? 0.0f : offX + 1.0f;
        int nlayout = this.layout;
        if (this.image == null && this.template == null && this.iconReference == null) {
            nlayout = 1;
        }
        Rectangle iconBox = null;
        block18: while (true) {
            switch (nlayout) {
                case 1: 
                case 7: {
                    if (this.text != null && this.text.length() > 0 && wt > 0.0f && ht > 0.0f) {
                        fsize = this.calculateFontSize(wt, ht);
                        textX = (box.getWidth() - ufont.getWidthPoint(this.text, fsize)) / 2.0f;
                        textY = (box.getHeight() - ufont.getFontDescriptor(1, fsize)) / 2.0f;
                    }
                }
                case 2: {
                    if (nlayout != 7 && nlayout != 2) break block18;
                    iconBox = new Rectangle(box.getLeft() + adj, box.getBottom() + adj, box.getRight() - adj, box.getTop() - adj);
                    break block18;
                }
                case 3: {
                    if (this.text == null || this.text.length() == 0 || wt <= 0.0f || ht <= 0.0f) {
                        nlayout = 2;
                        continue block18;
                    }
                    float nht = box.getHeight() * 0.35f - offX;
                    fsize = nht > 0.0f ? this.calculateFontSize(wt, nht) : 4.0f;
                    textX = (box.getWidth() - ufont.getWidthPoint(this.text, fsize)) / 2.0f;
                    textY = offX - ufont.getFontDescriptor(3, fsize);
                    iconBox = new Rectangle(box.getLeft() + adj, textY + fsize, box.getRight() - adj, box.getTop() - adj);
                    break block18;
                }
                case 4: {
                    if (this.text == null || this.text.length() == 0 || wt <= 0.0f || ht <= 0.0f) {
                        nlayout = 2;
                        continue block18;
                    }
                    float nht = box.getHeight() * 0.35f - offX;
                    fsize = nht > 0.0f ? this.calculateFontSize(wt, nht) : 4.0f;
                    textX = (box.getWidth() - ufont.getWidthPoint(this.text, fsize)) / 2.0f;
                    textY = box.getHeight() - offX - fsize;
                    if (textY < offX) {
                        textY = offX;
                    }
                    iconBox = new Rectangle(box.getLeft() + adj, box.getBottom() + adj, box.getRight() - adj, textY + ufont.getFontDescriptor(3, fsize));
                    break block18;
                }
                case 6: {
                    if (this.text == null || this.text.length() == 0 || wt <= 0.0f || ht <= 0.0f) {
                        nlayout = 2;
                        continue block18;
                    }
                    float nw = box.getWidth() * 0.35f - offX;
                    fsize = nw > 0.0f ? this.calculateFontSize(wt, nw) : 4.0f;
                    if (ufont.getWidthPoint(this.text, fsize) >= wt) {
                        nlayout = 1;
                        fsize = this.fontSize;
                        continue block18;
                    }
                    textX = offX + 1.0f;
                    textY = (box.getHeight() - ufont.getFontDescriptor(1, fsize)) / 2.0f;
                    iconBox = new Rectangle(textX + ufont.getWidthPoint(this.text, fsize), box.getBottom() + adj, box.getRight() - adj, box.getTop() - adj);
                    break block18;
                }
                case 5: {
                    if (this.text == null || this.text.length() == 0 || wt <= 0.0f || ht <= 0.0f) {
                        nlayout = 2;
                        continue block18;
                    }
                    float nw = box.getWidth() * 0.35f - offX;
                    fsize = nw > 0.0f ? this.calculateFontSize(wt, nw) : 4.0f;
                    if (ufont.getWidthPoint(this.text, fsize) >= wt) {
                        nlayout = 1;
                        fsize = this.fontSize;
                        continue block18;
                    }
                    textX = box.getWidth() - ufont.getWidthPoint(this.text, fsize) - offX - 1.0f;
                    textY = (box.getHeight() - ufont.getFontDescriptor(1, fsize)) / 2.0f;
                    iconBox = new Rectangle(box.getLeft() + adj, box.getBottom() + adj, textX - 1.0f, box.getTop() - adj);
                }
            }
            break;
        }
        if (textY < box.getBottom() + offX) {
            textY = box.getBottom() + offX;
        }
        if (iconBox != null && (iconBox.getWidth() <= 0.0f || iconBox.getHeight() <= 0.0f)) {
            iconBox = null;
        }
        boolean haveIcon = false;
        float boundingBoxWidth = 0.0f;
        float boundingBoxHeight = 0.0f;
        PdfArray matrix = null;
        if (iconBox != null) {
            PdfDictionary dic;
            if (this.image != null) {
                this.tp = new PdfTemplate(this.writer);
                this.tp.setBoundingBox(new Rectangle(this.image));
                this.writer.addDirectTemplateSimple(this.tp, PdfName.FRM);
                this.tp.addImage(this.image, this.image.getWidth(), 0.0f, 0.0f, this.image.getHeight(), 0.0f, 0.0f);
                haveIcon = true;
                boundingBoxWidth = this.tp.getBoundingBox().getWidth();
                boundingBoxHeight = this.tp.getBoundingBox().getHeight();
            } else if (this.template != null) {
                this.tp = new PdfTemplate(this.writer);
                this.tp.setBoundingBox(new Rectangle(this.template.getWidth(), this.template.getHeight()));
                this.writer.addDirectTemplateSimple(this.tp, PdfName.FRM);
                this.tp.addTemplate(this.template, this.template.getBoundingBox().getLeft(), this.template.getBoundingBox().getBottom());
                haveIcon = true;
                boundingBoxWidth = this.tp.getBoundingBox().getWidth();
                boundingBoxHeight = this.tp.getBoundingBox().getHeight();
            } else if (this.iconReference != null && (dic = (PdfDictionary)PdfReader.getPdfObject(this.iconReference)) != null) {
                Rectangle r2 = PdfReader.getNormalizedRectangle(dic.getAsArray(PdfName.BBOX));
                matrix = dic.getAsArray(PdfName.MATRIX);
                haveIcon = true;
                boundingBoxWidth = r2.getWidth();
                boundingBoxHeight = r2.getHeight();
            }
        }
        if (haveIcon) {
            float icx = iconBox.getWidth() / boundingBoxWidth;
            float icy = iconBox.getHeight() / boundingBoxHeight;
            if (this.proportionalIcon) {
                switch (this.scaleIcon) {
                    case 3: {
                        icx = Math.min(icx, icy);
                        icx = Math.min(icx, 1.0f);
                        break;
                    }
                    case 4: {
                        icx = Math.min(icx, icy);
                        icx = Math.max(icx, 1.0f);
                        break;
                    }
                    case 2: {
                        icx = 1.0f;
                        break;
                    }
                    default: {
                        icx = Math.min(icx, icy);
                    }
                }
                icy = icx;
            } else {
                switch (this.scaleIcon) {
                    case 3: {
                        icx = Math.min(icx, 1.0f);
                        icy = Math.min(icy, 1.0f);
                        break;
                    }
                    case 4: {
                        icx = Math.max(icx, 1.0f);
                        icy = Math.max(icy, 1.0f);
                        break;
                    }
                    case 2: {
                        icy = 1.0f;
                        icx = 1.0f;
                        break;
                    }
                }
            }
            float xpos = iconBox.getLeft() + (iconBox.getWidth() - boundingBoxWidth * icx) * this.iconHorizontalAdjustment;
            float ypos = iconBox.getBottom() + (iconBox.getHeight() - boundingBoxHeight * icy) * this.iconVerticalAdjustment;
            app.saveState();
            app.rectangle(iconBox.getLeft(), iconBox.getBottom(), iconBox.getWidth(), iconBox.getHeight());
            app.clip();
            app.newPath();
            if (this.tp != null) {
                app.addTemplate(this.tp, icx, 0.0f, 0.0f, icy, xpos, ypos);
            } else {
                float cox = 0.0f;
                float coy = 0.0f;
                if (matrix != null && matrix.size() == 6) {
                    PdfNumber nm = matrix.getAsNumber(4);
                    if (nm != null) {
                        cox = nm.floatValue();
                    }
                    if ((nm = matrix.getAsNumber(5)) != null) {
                        coy = nm.floatValue();
                    }
                }
                app.addTemplateReference(this.iconReference, PdfName.FRM, icx, 0.0f, 0.0f, icy, xpos - cox * icx, ypos - coy * icy);
            }
            app.restoreState();
        }
        if (!Float.isNaN(textX)) {
            app.saveState();
            app.rectangle(offX, offX, box.getWidth() - 2.0f * offX, box.getHeight() - 2.0f * offX);
            app.clip();
            app.newPath();
            if (this.textColor == null) {
                app.resetGrayFill();
            } else {
                app.setColorFill(this.textColor);
            }
            app.beginText();
            app.setFontAndSize(ufont, fsize);
            app.setTextMatrix(textX, textY);
            app.showText(this.text);
            app.endText();
            app.restoreState();
        }
        return app;
    }

    public PdfFormField getField() throws IOException, DocumentException {
        PdfFormField field = PdfFormField.createPushButton(this.writer);
        field.setWidget(this.box, PdfAnnotation.HIGHLIGHT_INVERT);
        if (this.fieldName != null) {
            field.setFieldName(this.fieldName);
            if ((this.options & 1) != 0) {
                field.setFieldFlags(1);
            }
            if ((this.options & 2) != 0) {
                field.setFieldFlags(2);
            }
        }
        if (this.text != null) {
            field.setMKNormalCaption(this.text);
        }
        if (this.rotation != 0) {
            field.setMKRotation(this.rotation);
        }
        field.setBorderStyle(new PdfBorderDictionary(this.borderWidth, this.borderStyle, new PdfDashPattern(3.0f)));
        PdfAppearance tpa = this.getAppearance();
        field.setAppearance(PdfAnnotation.APPEARANCE_NORMAL, tpa);
        PdfAppearance da = (PdfAppearance)tpa.getDuplicate();
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
        if (this.tp != null) {
            field.setMKNormalIcon(this.tp);
        }
        field.setMKTextPosition(this.layout - 1);
        PdfName scale = PdfName.A;
        if (this.scaleIcon == 3) {
            scale = PdfName.B;
        } else if (this.scaleIcon == 4) {
            scale = PdfName.S;
        } else if (this.scaleIcon == 2) {
            scale = PdfName.N;
        }
        field.setMKIconFit(scale, this.proportionalIcon ? PdfName.P : PdfName.A, this.iconHorizontalAdjustment, this.iconVerticalAdjustment, this.iconFitToBounds);
        return field;
    }

    public boolean isIconFitToBounds() {
        return this.iconFitToBounds;
    }

    public void setIconFitToBounds(boolean iconFitToBounds) {
        this.iconFitToBounds = iconFitToBounds;
    }

    public PRIndirectReference getIconReference() {
        return this.iconReference;
    }

    public void setIconReference(PRIndirectReference iconReference) {
        this.iconReference = iconReference;
    }
}

