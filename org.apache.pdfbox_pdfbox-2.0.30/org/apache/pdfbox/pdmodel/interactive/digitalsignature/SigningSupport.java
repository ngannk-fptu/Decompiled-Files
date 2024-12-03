/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;

public class SigningSupport
implements ExternalSigningSupport,
Closeable {
    private COSWriter cosWriter;

    public SigningSupport(COSWriter cosWriter) {
        this.cosWriter = cosWriter;
    }

    @Override
    public InputStream getContent() throws IOException {
        return this.cosWriter.getDataToSign();
    }

    @Override
    public void setSignature(byte[] signature) throws IOException {
        this.cosWriter.writeExternalSignature(signature);
    }

    @Override
    public void close() throws IOException {
        if (this.cosWriter != null) {
            try {
                this.cosWriter.close();
            }
            finally {
                this.cosWriter = null;
            }
        }
    }
}

