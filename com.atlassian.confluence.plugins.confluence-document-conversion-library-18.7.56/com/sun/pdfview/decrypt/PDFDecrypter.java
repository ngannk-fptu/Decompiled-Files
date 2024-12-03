/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decrypt;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import java.nio.ByteBuffer;

public interface PDFDecrypter {
    public ByteBuffer decryptBuffer(String var1, PDFObject var2, ByteBuffer var3) throws PDFParseException;

    public String decryptString(int var1, int var2, String var3) throws PDFParseException;

    public boolean isOwnerAuthorised();

    public boolean isEncryptionPresent();
}

