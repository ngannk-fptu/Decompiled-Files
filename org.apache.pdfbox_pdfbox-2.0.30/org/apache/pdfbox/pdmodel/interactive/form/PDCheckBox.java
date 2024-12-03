/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.Set;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceEntry;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;

public final class PDCheckBox
extends PDButton {
    public PDCheckBox(PDAcroForm acroForm) {
        super(acroForm);
    }

    PDCheckBox(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public boolean isChecked() {
        return this.getValue().compareTo(this.getOnValue()) == 0;
    }

    public void check() throws IOException {
        this.setValue(this.getOnValue());
    }

    public void unCheck() throws IOException {
        this.setValue(COSName.Off.getName());
    }

    public String getOnValue() {
        PDAppearanceEntry normalAppearance;
        PDAnnotationWidget widget = this.getWidgets().get(0);
        PDAppearanceDictionary apDictionary = widget.getAppearance();
        if (apDictionary != null && (normalAppearance = apDictionary.getNormalAppearance()) != null) {
            Set<COSName> entries = normalAppearance.getSubDictionary().keySet();
            for (COSName entry : entries) {
                if (COSName.Off.compareTo(entry) == 0) continue;
                return entry.getName();
            }
        }
        return "";
    }
}

