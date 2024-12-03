/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decrypt;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.decrypt.PDFDecrypter;
import java.nio.ByteBuffer;

public class IdentityDecrypter
implements PDFDecrypter {
    private static IdentityDecrypter INSTANCE = new IdentityDecrypter();

    @Override
    public ByteBuffer decryptBuffer(String cryptFilterName, PDFObject streamObj, ByteBuffer streamBuf) throws PDFParseException {
        if (cryptFilterName != null) {
            throw new PDFParseException("This Encryption version does not support Crypt filters");
        }
        return streamBuf;
    }

    @Override
    public String decryptString(int objNum, int objGen, String inputBasicString) {
        return inputBasicString;
    }

    public static IdentityDecrypter getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean isEncryptionPresent() {
        return false;
    }

    @Override
    public boolean isOwnerAuthorised() {
        return false;
    }
}

