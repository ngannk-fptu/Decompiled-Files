/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;

public class PDActionImportData
extends PDAction {
    public static final String SUB_TYPE = "ImportData";

    public PDActionImportData() {
        this.setSubType(SUB_TYPE);
    }

    public PDActionImportData(COSDictionary a) {
        super(a);
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(this.action.getDictionaryObject(COSName.F));
    }

    public void setFile(PDFileSpecification fs) {
        this.action.setItem(COSName.F, (COSObjectable)fs);
    }
}

