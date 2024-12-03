/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.fdf.FDFField;
import org.apache.pdfbox.pdmodel.interactive.action.PDFormFieldAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;

public abstract class PDTerminalField
extends PDField {
    protected PDTerminalField(PDAcroForm acroForm) {
        super(acroForm);
    }

    PDTerminalField(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    public void setActions(PDFormFieldAdditionalActions actions) {
        this.getCOSObject().setItem(COSName.AA, (COSObjectable)actions);
    }

    @Override
    public int getFieldFlags() {
        int retval = 0;
        COSInteger ff = (COSInteger)this.getCOSObject().getDictionaryObject(COSName.FF);
        if (ff != null) {
            retval = ff.intValue();
        } else if (this.getParent() != null) {
            retval = this.getParent().getFieldFlags();
        }
        return retval;
    }

    @Override
    public String getFieldType() {
        String fieldType = this.getCOSObject().getNameAsString(COSName.FT);
        if (fieldType == null && this.getParent() != null) {
            fieldType = this.getParent().getFieldType();
        }
        return fieldType;
    }

    @Override
    public void importFDF(FDFField fdfField) throws IOException {
        super.importFDF(fdfField);
        Integer f = fdfField.getWidgetFieldFlags();
        for (PDAnnotationWidget widget : this.getWidgets()) {
            Integer clrF;
            if (f != null) {
                widget.setAnnotationFlags(f);
                continue;
            }
            Integer setF = fdfField.getSetWidgetFieldFlags();
            int annotFlags = widget.getAnnotationFlags();
            if (setF != null) {
                widget.setAnnotationFlags(annotFlags |= setF.intValue());
            }
            if ((clrF = fdfField.getClearWidgetFieldFlags()) == null) continue;
            int clrFValue = clrF;
            clrFValue = (int)((long)clrFValue ^ 0xFFFFFFFFL);
            widget.setAnnotationFlags(annotFlags &= clrFValue);
        }
    }

    @Override
    FDFField exportFDF() throws IOException {
        FDFField fdfField = new FDFField();
        fdfField.setPartialFieldName(this.getPartialName());
        fdfField.setValue(this.getCOSObject().getDictionaryObject(COSName.V));
        return fdfField;
    }

    @Override
    public List<PDAnnotationWidget> getWidgets() {
        ArrayList<PDAnnotationWidget> widgets = new ArrayList<PDAnnotationWidget>();
        COSArray kids = (COSArray)this.getCOSObject().getDictionaryObject(COSName.KIDS);
        if (kids == null) {
            widgets.add(new PDAnnotationWidget(this.getCOSObject()));
        } else if (kids.size() > 0) {
            for (int i = 0; i < kids.size(); ++i) {
                COSBase kid = kids.getObject(i);
                if (!(kid instanceof COSDictionary)) continue;
                widgets.add(new PDAnnotationWidget((COSDictionary)kid));
            }
        }
        return widgets;
    }

    public void setWidgets(List<PDAnnotationWidget> children) {
        COSArray kidsArray = COSArrayList.converterToCOSArray(children);
        this.getCOSObject().setItem(COSName.KIDS, (COSBase)kidsArray);
        for (PDAnnotationWidget widget : children) {
            widget.getCOSObject().setItem(COSName.PARENT, (COSObjectable)this);
        }
    }

    @Deprecated
    public PDAnnotationWidget getWidget() {
        return this.getWidgets().get(0);
    }

    protected final void applyChange() throws IOException {
        if (!this.getAcroForm().getNeedAppearances()) {
            this.constructAppearances();
        }
    }

    abstract void constructAppearances() throws IOException;
}

