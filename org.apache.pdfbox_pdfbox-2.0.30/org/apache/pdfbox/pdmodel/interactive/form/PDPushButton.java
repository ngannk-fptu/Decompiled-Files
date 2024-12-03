/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDButton;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;

public class PDPushButton
extends PDButton {
    public PDPushButton(PDAcroForm acroForm) {
        super(acroForm);
        this.getCOSObject().setFlag(COSName.FF, 65536, true);
    }

    PDPushButton(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    @Override
    public List<String> getExportValues() {
        return Collections.emptyList();
    }

    @Override
    public void setExportValues(List<String> values) {
        if (values != null && !values.isEmpty()) {
            throw new IllegalArgumentException("A PDPushButton shall not use the Opt entry in the field dictionary");
        }
    }

    @Override
    public String getValue() {
        return "";
    }

    @Override
    public String getDefaultValue() {
        return "";
    }

    @Override
    public String getValueAsString() {
        return this.getValue();
    }

    @Override
    public Set<String> getOnValues() {
        return Collections.emptySet();
    }

    @Override
    void constructAppearances() throws IOException {
    }
}

