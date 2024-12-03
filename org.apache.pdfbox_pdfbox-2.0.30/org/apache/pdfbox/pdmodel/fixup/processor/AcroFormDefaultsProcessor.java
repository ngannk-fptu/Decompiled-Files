/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.fixup.processor;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.fixup.processor.AbstractProcessor;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

public class AcroFormDefaultsProcessor
extends AbstractProcessor {
    public AcroFormDefaultsProcessor(PDDocument document) {
        super(document);
    }

    @Override
    public void process() {
        PDAcroForm acroForm = this.document.getDocumentCatalog().getAcroForm(null);
        if (acroForm != null) {
            this.verifyOrCreateDefaults(acroForm);
        }
    }

    private void verifyOrCreateDefaults(PDAcroForm acroForm) {
        COSDictionary fontDict;
        PDResources defaultResources;
        String adobeDefaultAppearanceString = "/Helv 0 Tf 0 g ";
        if (acroForm.getDefaultAppearance().length() == 0) {
            acroForm.setDefaultAppearance("/Helv 0 Tf 0 g ");
            acroForm.getCOSObject().setNeedToBeUpdated(true);
        }
        if ((defaultResources = acroForm.getDefaultResources()) == null) {
            defaultResources = new PDResources();
            acroForm.setDefaultResources(defaultResources);
            acroForm.getCOSObject().setNeedToBeUpdated(true);
        }
        if ((fontDict = defaultResources.getCOSObject().getCOSDictionary(COSName.FONT)) == null) {
            fontDict = new COSDictionary();
            defaultResources.getCOSObject().setItem(COSName.FONT, (COSBase)fontDict);
        }
        if (!fontDict.containsKey(COSName.HELV)) {
            defaultResources.put(COSName.HELV, PDType1Font.HELVETICA);
            defaultResources.getCOSObject().setNeedToBeUpdated(true);
            fontDict.setNeedToBeUpdated(true);
        }
        if (!fontDict.containsKey(COSName.ZA_DB)) {
            defaultResources.put(COSName.ZA_DB, PDType1Font.ZAPF_DINGBATS);
            defaultResources.getCOSObject().setNeedToBeUpdated(true);
            fontDict.setNeedToBeUpdated(true);
        }
    }
}

