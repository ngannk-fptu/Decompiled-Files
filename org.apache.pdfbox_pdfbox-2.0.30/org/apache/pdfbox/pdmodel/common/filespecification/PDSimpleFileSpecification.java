/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.filespecification;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;

public class PDSimpleFileSpecification
extends PDFileSpecification {
    private COSString file;

    public PDSimpleFileSpecification() {
        this.file = new COSString("");
    }

    public PDSimpleFileSpecification(COSString fileName) {
        this.file = fileName;
    }

    @Override
    public String getFile() {
        return this.file.getString();
    }

    @Override
    public void setFile(String fileName) {
        this.file = new COSString(fileName);
    }

    @Override
    public COSBase getCOSObject() {
        return this.file;
    }
}

