/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;

public class PDActionResetForm
extends PDAction {
    public static final String SUB_TYPE = "ResetForm";

    public PDActionResetForm() {
        this.setSubType(SUB_TYPE);
    }

    public PDActionResetForm(COSDictionary a) {
        super(a);
    }

    public COSArray getFields() {
        COSBase retval = this.action.getDictionaryObject(COSName.FIELDS);
        return retval instanceof COSArray ? (COSArray)retval : null;
    }

    public void setFields(COSArray array) {
        this.action.setItem(COSName.FIELDS, (COSBase)array);
    }

    public int getFlags() {
        return this.action.getInt(COSName.FLAGS, 0);
    }

    public void setFlags(int flags) {
        this.action.setInt(COSName.FLAGS, flags);
    }
}

