/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decrypt;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.decrypt.PDFDecrypter;
import java.nio.ByteBuffer;
import java.util.Map;

public class CryptFilterDecrypter
implements PDFDecrypter {
    private Map<String, PDFDecrypter> decrypters;
    private PDFDecrypter defaultStreamDecrypter;
    private PDFDecrypter defaultStringDecrypter;

    public CryptFilterDecrypter(Map<String, PDFDecrypter> decrypters, String defaultStreamCryptName, String defaultStringCryptName) throws PDFParseException {
        this.decrypters = decrypters;
        assert (this.decrypters.containsKey("Identity")) : "Crypt Filter map does not contain required Identity filter";
        this.defaultStreamDecrypter = this.decrypters.get(defaultStreamCryptName);
        if (this.defaultStreamDecrypter == null) {
            throw new PDFParseException("Unknown crypt filter specified as default for streams: " + defaultStreamCryptName);
        }
        this.defaultStringDecrypter = this.decrypters.get(defaultStringCryptName);
        if (this.defaultStringDecrypter == null) {
            throw new PDFParseException("Unknown crypt filter specified as default for strings: " + defaultStringCryptName);
        }
    }

    @Override
    public ByteBuffer decryptBuffer(String cryptFilterName, PDFObject streamObj, ByteBuffer streamBuf) throws PDFParseException {
        PDFDecrypter decrypter;
        if (cryptFilterName == null) {
            decrypter = this.defaultStreamDecrypter;
        } else {
            decrypter = this.decrypters.get(cryptFilterName);
            if (decrypter == null) {
                throw new PDFParseException("Unknown CryptFilter: " + cryptFilterName);
            }
        }
        return decrypter.decryptBuffer(null, cryptFilterName != null ? null : streamObj, streamBuf);
    }

    @Override
    public String decryptString(int objNum, int objGen, String inputBasicString) throws PDFParseException {
        return this.defaultStringDecrypter.decryptString(objNum, objGen, inputBasicString);
    }

    @Override
    public boolean isEncryptionPresent() {
        for (PDFDecrypter decrypter : this.decrypters.values()) {
            if (!decrypter.isEncryptionPresent()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isOwnerAuthorised() {
        for (PDFDecrypter decrypter : this.decrypters.values()) {
            if (!decrypter.isOwnerAuthorised()) continue;
            return true;
        }
        return false;
    }
}

