/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.fdf.FDFField;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

public class PDNonTerminalField
extends PDField {
    private static final Log LOG = LogFactory.getLog(PDNonTerminalField.class);

    public PDNonTerminalField(PDAcroForm acroForm) {
        super(acroForm);
    }

    PDNonTerminalField(PDAcroForm acroForm, COSDictionary field, PDNonTerminalField parent) {
        super(acroForm, field, parent);
    }

    @Override
    public int getFieldFlags() {
        int retval = 0;
        COSInteger ff = (COSInteger)this.getCOSObject().getDictionaryObject(COSName.FF);
        if (ff != null) {
            retval = ff.intValue();
        }
        return retval;
    }

    @Override
    void importFDF(FDFField fdfField) throws IOException {
        super.importFDF(fdfField);
        List<FDFField> fdfKids = fdfField.getKids();
        if (fdfKids == null) {
            return;
        }
        List<PDField> children = this.getChildren();
        for (int i = 0; i < fdfKids.size(); ++i) {
            for (PDField pdChild : children) {
                FDFField fdfChild = fdfKids.get(i);
                String fdfName = fdfChild.getPartialFieldName();
                if (fdfName == null || !fdfName.equals(pdChild.getPartialName())) continue;
                pdChild.importFDF(fdfChild);
            }
        }
    }

    @Override
    FDFField exportFDF() throws IOException {
        FDFField fdfField = new FDFField();
        fdfField.setPartialFieldName(this.getPartialName());
        fdfField.setValue(this.getValue());
        List<PDField> children = this.getChildren();
        ArrayList<FDFField> fdfChildren = new ArrayList<FDFField>(children.size());
        for (PDField child : children) {
            fdfChildren.add(child.exportFDF());
        }
        fdfField.setKids(fdfChildren);
        return fdfField;
    }

    public List<PDField> getChildren() {
        ArrayList<PDField> children = new ArrayList<PDField>();
        COSArray kids = this.getCOSObject().getCOSArray(COSName.KIDS);
        if (kids == null) {
            return children;
        }
        for (int i = 0; i < kids.size(); ++i) {
            COSBase kid = kids.getObject(i);
            if (!(kid instanceof COSDictionary)) continue;
            if (kid.getCOSObject() == this.getCOSObject()) {
                LOG.warn((Object)"Child field is same object as parent");
                continue;
            }
            PDField field = PDField.fromDictionary(this.getAcroForm(), (COSDictionary)kid, this);
            if (field == null) continue;
            children.add(field);
        }
        return children;
    }

    public void setChildren(List<PDField> children) {
        COSArray kidsArray = COSArrayList.converterToCOSArray(children);
        this.getCOSObject().setItem(COSName.KIDS, (COSBase)kidsArray);
    }

    @Override
    public String getFieldType() {
        return this.getCOSObject().getNameAsString(COSName.FT);
    }

    public COSBase getValue() {
        return this.getCOSObject().getDictionaryObject(COSName.V);
    }

    @Override
    public String getValueAsString() {
        COSBase fieldValue = this.getCOSObject().getDictionaryObject(COSName.V);
        return fieldValue != null ? fieldValue.toString() : "";
    }

    public void setValue(COSBase object) throws IOException {
        this.getCOSObject().setItem(COSName.V, object);
    }

    @Override
    public void setValue(String value) throws IOException {
        this.getCOSObject().setString(COSName.V, value);
    }

    public COSBase getDefaultValue() {
        return this.getCOSObject().getDictionaryObject(COSName.DV);
    }

    public void setDefaultValue(COSBase value) {
        this.getCOSObject().setItem(COSName.V, value);
    }

    @Override
    public List<PDAnnotationWidget> getWidgets() {
        return Collections.emptyList();
    }
}

