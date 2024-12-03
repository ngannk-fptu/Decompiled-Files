/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.DocumentException;
import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.IntHashtable;
import com.lowagie.text.pdf.PdfCopyFieldsImp;
import com.lowagie.text.pdf.PdfReader;
import java.io.OutputStream;

class PdfCopyFormsImp
extends PdfCopyFieldsImp {
    PdfCopyFormsImp(OutputStream os) throws DocumentException {
        super(os);
    }

    public void copyDocumentFields(PdfReader reader) throws DocumentException {
        if (!reader.isOpenedWithFullPermissions()) {
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("pdfreader.not.opened.with.owner.password"));
        }
        if (this.readers2intrefs.containsKey(reader)) {
            reader = new PdfReader(reader);
        } else {
            if (reader.isTampered()) {
                throw new DocumentException(MessageLocalization.getComposedMessage("the.document.was.reused"));
            }
            reader.consolidateNamedDestinations();
            reader.setTampered(true);
        }
        reader.shuffleSubsetNames();
        this.readers2intrefs.put(reader, new IntHashtable());
        this.fields.add(reader.getAcroFields());
        this.updateCalculationOrder(reader);
    }

    @Override
    void mergeFields() {
        for (AcroFields field : this.fields) {
            this.mergeWithMaster(field.getAllFields());
        }
    }
}

