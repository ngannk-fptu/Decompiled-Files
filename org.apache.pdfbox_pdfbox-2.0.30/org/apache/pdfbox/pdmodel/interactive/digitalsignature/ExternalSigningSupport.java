/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import java.io.IOException;
import java.io.InputStream;

public interface ExternalSigningSupport {
    public InputStream getContent() throws IOException;

    public void setSignature(byte[] var1) throws IOException;
}

