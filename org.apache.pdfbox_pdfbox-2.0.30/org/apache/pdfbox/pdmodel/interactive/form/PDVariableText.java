/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDDefaultAppearanceString;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField;

public abstract class PDVariableText
extends PDTerminalField {
    public static final int QUADDING_LEFT = 0;
    public static final int QUADDING_CENTERED = 1;
    public static final int QUADDING_RIGHT = 2;

    PDVariableText(PDAcroForm acroForm) {
        super(acroForm);
    }

    PDVariableText(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public String getDefaultAppearance() {
        COSBase base = this.getInheritableAttribute(COSName.DA);
        if (!(base instanceof COSString)) {
            return null;
        }
        COSString defaultAppearance = (COSString)base;
        return defaultAppearance.getString();
    }

    PDDefaultAppearanceString getDefaultAppearanceString() throws IOException {
        COSBase base = this.getInheritableAttribute(COSName.DA);
        COSString da = null;
        if (base instanceof COSString) {
            da = (COSString)base;
        }
        PDResources dr = this.getAcroForm().getDefaultResources();
        return new PDDefaultAppearanceString(da, dr);
    }

    public void setDefaultAppearance(String daValue) {
        this.getCOSObject().setString(COSName.DA, daValue);
    }

    public String getDefaultStyleString() {
        COSString defaultStyleString = (COSString)this.getCOSObject().getDictionaryObject(COSName.DS);
        return defaultStyleString.getString();
    }

    public void setDefaultStyleString(String defaultStyleString) {
        if (defaultStyleString != null) {
            this.getCOSObject().setItem(COSName.DS, (COSBase)new COSString(defaultStyleString));
        } else {
            this.getCOSObject().removeItem(COSName.DS);
        }
    }

    public int getQ() {
        int retval = 0;
        COSNumber number = (COSNumber)this.getInheritableAttribute(COSName.Q);
        if (number != null) {
            retval = number.intValue();
        }
        return retval;
    }

    public void setQ(int q) {
        this.getCOSObject().setInt(COSName.Q, q);
    }

    public String getRichTextValue() throws IOException {
        return this.getStringOrStream(this.getInheritableAttribute(COSName.RV));
    }

    public void setRichTextValue(String richTextValue) {
        if (richTextValue != null) {
            this.getCOSObject().setItem(COSName.RV, (COSBase)new COSString(richTextValue));
        } else {
            this.getCOSObject().removeItem(COSName.RV);
        }
    }

    protected final String getStringOrStream(COSBase base) {
        if (base == null) {
            return "";
        }
        if (base instanceof COSString) {
            return ((COSString)base).getString();
        }
        if (base instanceof COSStream) {
            return ((COSStream)base).toTextString();
        }
        return "";
    }
}

