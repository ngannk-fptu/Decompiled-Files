/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;

public final class PDRadioButton
extends PDButton {
    private static final int FLAG_NO_TOGGLE_TO_OFF = 16384;

    public PDRadioButton(PDAcroForm acroForm) {
        super(acroForm);
        this.getCOSObject().setFlag(COSName.FF, 32768, true);
    }

    PDRadioButton(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public void setRadiosInUnison(boolean radiosInUnison) {
        this.getCOSObject().setFlag(COSName.FF, 0x2000000, radiosInUnison);
    }

    public boolean isRadiosInUnison() {
        return this.getCOSObject().getFlag(COSName.FF, 0x2000000);
    }

    public int getSelectedIndex() {
        int idx = 0;
        for (PDAnnotationWidget widget : this.getWidgets()) {
            if (!COSName.Off.equals(widget.getAppearanceState())) {
                return idx;
            }
            ++idx;
        }
        return -1;
    }

    public List<String> getSelectedExportValues() throws IOException {
        List<String> exportValues = this.getExportValues();
        ArrayList<String> selectedExportValues = new ArrayList<String>();
        if (exportValues.isEmpty()) {
            selectedExportValues.add(this.getValue());
            return selectedExportValues;
        }
        String fieldValue = this.getValue();
        int idx = 0;
        for (String onValue : this.getOnValues()) {
            if (onValue.compareTo(fieldValue) == 0) {
                selectedExportValues.add(exportValues.get(idx));
            }
            ++idx;
        }
        return selectedExportValues;
    }
}

