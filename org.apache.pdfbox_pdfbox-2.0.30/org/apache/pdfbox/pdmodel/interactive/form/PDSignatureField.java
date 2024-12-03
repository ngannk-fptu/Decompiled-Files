/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.HashSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSeedValue;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField;

public class PDSignatureField
extends PDTerminalField {
    private static final Log LOG = LogFactory.getLog(PDSignatureField.class);

    public PDSignatureField(PDAcroForm acroForm) throws IOException {
        super(acroForm);
        this.getCOSObject().setItem(COSName.FT, (COSBase)COSName.SIG);
        PDAnnotationWidget firstWidget = this.getWidgets().get(0);
        firstWidget.setLocked(true);
        firstWidget.setPrinted(true);
        this.setPartialName(this.generatePartialName());
    }

    PDSignatureField(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    private String generatePartialName() {
        String fieldName = "Signature";
        HashSet<String> nameSet = new HashSet<String>();
        for (PDField field : this.getAcroForm().getFieldTree()) {
            nameSet.add(field.getPartialName());
        }
        int i = 1;
        while (nameSet.contains(fieldName + i)) {
            ++i;
        }
        return fieldName + i;
    }

    @Deprecated
    public void setSignature(PDSignature value) throws IOException {
        this.setValue(value);
    }

    public PDSignature getSignature() {
        return this.getValue();
    }

    public void setValue(PDSignature value) throws IOException {
        this.getCOSObject().setItem(COSName.V, (COSObjectable)value);
        this.applyChange();
    }

    @Override
    public void setValue(String value) {
        throw new UnsupportedOperationException("Signature fields don't support setting the value as String - use setValue(PDSignature value) instead");
    }

    public void setDefaultValue(PDSignature value) throws IOException {
        this.getCOSObject().setItem(COSName.DV, (COSObjectable)value);
    }

    public PDSignature getValue() {
        COSBase value = this.getCOSObject().getDictionaryObject(COSName.V);
        if (value instanceof COSDictionary) {
            return new PDSignature((COSDictionary)value);
        }
        return null;
    }

    public PDSignature getDefaultValue() {
        COSBase value = this.getCOSObject().getDictionaryObject(COSName.DV);
        if (value == null) {
            return null;
        }
        return new PDSignature((COSDictionary)value);
    }

    @Override
    public String getValueAsString() {
        PDSignature signature = this.getValue();
        return signature != null ? signature.toString() : "";
    }

    public PDSeedValue getSeedValue() {
        COSDictionary dict = (COSDictionary)this.getCOSObject().getDictionaryObject(COSName.SV);
        PDSeedValue sv = null;
        if (dict != null) {
            sv = new PDSeedValue(dict);
        }
        return sv;
    }

    public void setSeedValue(PDSeedValue sv) {
        if (sv != null) {
            this.getCOSObject().setItem(COSName.SV, (COSObjectable)sv);
        }
    }

    @Override
    void constructAppearances() throws IOException {
        PDAnnotationWidget widget = this.getWidgets().get(0);
        if (widget != null) {
            if (widget.getRectangle() == null || widget.getRectangle().getHeight() == 0.0f && widget.getRectangle().getWidth() == 0.0f || widget.isNoView() || widget.isHidden()) {
                return;
            }
            LOG.warn((Object)"Appearance generation for signature fields not implemented here. You need to generate/update that manually, see the CreateVisibleSignature*.java files in the examples subproject of the source code download");
        }
    }
}

