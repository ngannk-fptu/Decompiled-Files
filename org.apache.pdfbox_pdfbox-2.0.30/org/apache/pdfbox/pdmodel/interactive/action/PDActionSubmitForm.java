/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;

public class PDActionSubmitForm
extends PDAction {
    public static final String SUB_TYPE = "SubmitForm";

    public PDActionSubmitForm() {
        this.setSubType(SUB_TYPE);
    }

    public PDActionSubmitForm(COSDictionary a) {
        super(a);
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(this.action.getDictionaryObject(COSName.F));
    }

    public void setFile(PDFileSpecification fs) {
        this.action.setItem(COSName.F, (COSObjectable)fs);
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

