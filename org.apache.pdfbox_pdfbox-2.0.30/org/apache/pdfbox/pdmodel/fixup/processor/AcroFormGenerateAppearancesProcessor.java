/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.fixup.processor;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.fixup.processor.AbstractProcessor;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

public class AcroFormGenerateAppearancesProcessor
extends AbstractProcessor {
    private static final Log LOG = LogFactory.getLog(AcroFormGenerateAppearancesProcessor.class);

    public AcroFormGenerateAppearancesProcessor(PDDocument document) {
        super(document);
    }

    @Override
    public void process() {
        PDAcroForm acroForm = this.document.getDocumentCatalog().getAcroForm(null);
        if (acroForm != null && acroForm.getNeedAppearances()) {
            try {
                LOG.debug((Object)"trying to generate appearance streams for fields as NeedAppearances is true()");
                acroForm.refreshAppearances();
                acroForm.setNeedAppearances(false);
            }
            catch (IOException ioe) {
                LOG.debug((Object)"couldn't generate appearance stream for some fields - check output");
                LOG.debug((Object)ioe.getMessage());
            }
            catch (IllegalArgumentException iae) {
                LOG.debug((Object)"couldn't generate appearance stream for some fields - check output");
                LOG.debug((Object)iae.getMessage());
            }
        }
    }
}

