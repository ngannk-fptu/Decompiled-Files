/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.form.AppearanceGeneratorHelper;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDChoice;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;

public final class PDListBox
extends PDChoice {
    public PDListBox(PDAcroForm acroForm) {
        super(acroForm);
    }

    PDListBox(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public int getTopIndex() {
        return this.getCOSObject().getInt(COSName.TI, 0);
    }

    public void setTopIndex(Integer topIndex) {
        if (topIndex != null) {
            this.getCOSObject().setInt(COSName.TI, (int)topIndex);
        } else {
            this.getCOSObject().removeItem(COSName.TI);
        }
    }

    @Override
    void constructAppearances() throws IOException {
        AppearanceGeneratorHelper apHelper = new AppearanceGeneratorHelper(this);
        apHelper.setAppearanceValue("");
    }
}

