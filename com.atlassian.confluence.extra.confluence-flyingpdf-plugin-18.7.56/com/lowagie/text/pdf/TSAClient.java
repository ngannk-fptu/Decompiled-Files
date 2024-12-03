/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.pdf.PdfPKCS7;

public interface TSAClient {
    public int getTokenSizeEstimate();

    public byte[] getTimeStampToken(PdfPKCS7 var1, byte[] var2) throws Exception;
}

