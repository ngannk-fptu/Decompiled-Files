/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fixup;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.fixup.AbstractFixup;
import org.apache.pdfbox.pdmodel.fixup.processor.AcroFormDefaultsProcessor;
import org.apache.pdfbox.pdmodel.fixup.processor.AcroFormGenerateAppearancesProcessor;
import org.apache.pdfbox.pdmodel.fixup.processor.AcroFormOrphanWidgetsProcessor;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

public class AcroFormDefaultFixup
extends AbstractFixup {
    public AcroFormDefaultFixup(PDDocument document) {
        super(document);
    }

    @Override
    public void apply() {
        new AcroFormDefaultsProcessor(this.document).process();
        PDAcroForm acroForm = this.document.getDocumentCatalog().getAcroForm(null);
        if (acroForm != null && acroForm.getNeedAppearances()) {
            if (acroForm.getFields().isEmpty()) {
                new AcroFormOrphanWidgetsProcessor(this.document).process();
            }
            new AcroFormGenerateAppearancesProcessor(this.document).process();
        }
    }
}

