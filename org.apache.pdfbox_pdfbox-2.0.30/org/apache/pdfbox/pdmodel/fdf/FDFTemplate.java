/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fdf;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.fdf.FDFField;
import org.apache.pdfbox.pdmodel.fdf.FDFNamedPageReference;

public class FDFTemplate
implements COSObjectable {
    private final COSDictionary template;

    public FDFTemplate() {
        this.template = new COSDictionary();
    }

    public FDFTemplate(COSDictionary t) {
        this.template = t;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.template;
    }

    public FDFNamedPageReference getTemplateReference() {
        FDFNamedPageReference retval = null;
        COSDictionary dict = (COSDictionary)this.template.getDictionaryObject(COSName.TREF);
        if (dict != null) {
            retval = new FDFNamedPageReference(dict);
        }
        return retval;
    }

    public void setTemplateReference(FDFNamedPageReference tRef) {
        this.template.setItem(COSName.TREF, (COSObjectable)tRef);
    }

    public List<FDFField> getFields() {
        COSArrayList retval = null;
        COSArray array = (COSArray)this.template.getDictionaryObject(COSName.FIELDS);
        if (array != null) {
            ArrayList<FDFField> fields = new ArrayList<FDFField>();
            for (int i = 0; i < array.size(); ++i) {
                fields.add(new FDFField((COSDictionary)array.getObject(i)));
            }
            retval = new COSArrayList(fields, array);
        }
        return retval;
    }

    public void setFields(List<FDFField> fields) {
        this.template.setItem(COSName.FIELDS, (COSBase)COSArrayList.converterToCOSArray(fields));
    }

    public boolean shouldRename() {
        return this.template.getBoolean(COSName.RENAME, false);
    }

    public void setRename(boolean value) {
        this.template.setBoolean(COSName.RENAME, value);
    }
}

