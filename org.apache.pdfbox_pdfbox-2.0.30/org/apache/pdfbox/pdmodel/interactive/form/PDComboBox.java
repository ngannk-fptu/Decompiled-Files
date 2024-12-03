/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.form.AppearanceGeneratorHelper;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDChoice;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;

public final class PDComboBox
extends PDChoice {
    private static final int FLAG_EDIT = 262144;

    public PDComboBox(PDAcroForm acroForm) {
        super(acroForm);
        this.setCombo(true);
    }

    PDComboBox(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public boolean isEdit() {
        return this.getCOSObject().getFlag(COSName.FF, 262144);
    }

    public void setEdit(boolean edit) {
        this.getCOSObject().setFlag(COSName.FF, 262144, edit);
    }

    @Override
    void constructAppearances() throws IOException {
        AppearanceGeneratorHelper apHelper = new AppearanceGeneratorHelper(this);
        List<String> values = this.getValue();
        if (!values.isEmpty()) {
            apHelper.setAppearanceValue(values.get(0));
        } else {
            apHelper.setAppearanceValue("");
        }
    }
}

