/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.decrypt;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.decrypt.CryptFilterDecrypter;
import com.sun.pdfview.decrypt.EncryptionUnsupportedByPlatformException;
import com.sun.pdfview.decrypt.EncryptionUnsupportedByProductException;
import com.sun.pdfview.decrypt.IdentityDecrypter;
import com.sun.pdfview.decrypt.PDFAuthenticationFailureException;
import com.sun.pdfview.decrypt.PDFDecrypter;
import com.sun.pdfview.decrypt.PDFPassword;
import com.sun.pdfview.decrypt.StandardDecrypter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class PDFDecrypterFactory {
    public static final String CF_IDENTITY = "Identity";
    private static final int DEFAULT_KEY_LENGTH = 40;

    public static PDFDecrypter createDecryptor(PDFObject encryptDict, PDFObject documentId, PDFPassword password) throws IOException, EncryptionUnsupportedByPlatformException, EncryptionUnsupportedByProductException, PDFAuthenticationFailureException {
        password = PDFPassword.nonNullPassword(password);
        if (encryptDict == null) {
            return IdentityDecrypter.getInstance();
        }
        PDFObject filter = encryptDict.getDictRef("Filter");
        if (filter != null && "Standard".equals(filter.getStringValue())) {
            int v;
            PDFObject vObj = encryptDict.getDictRef("V");
            int n = v = vObj != null ? vObj.getIntValue() : 0;
            if (v == 1 || v == 2) {
                PDFObject lengthObj = encryptDict.getDictRef("Length");
                Integer length = lengthObj != null ? Integer.valueOf(lengthObj.getIntValue()) : null;
                return PDFDecrypterFactory.createStandardDecrypter(encryptDict, documentId, password, length, false, StandardDecrypter.EncryptionAlgorithm.RC4);
            }
            if (v == 4) {
                return PDFDecrypterFactory.createCryptFilterDecrypter(encryptDict, documentId, password, v);
            }
            throw new EncryptionUnsupportedByPlatformException("Unsupported encryption version: " + v);
        }
        if (filter == null) {
            throw new PDFParseException("No Filter specified in Encrypt dictionary");
        }
        throw new EncryptionUnsupportedByPlatformException("Unsupported encryption Filter: " + filter + "; only Standard is supported.");
    }

    private static PDFDecrypter createCryptFilterDecrypter(PDFObject encryptDict, PDFObject documentId, PDFPassword password, int v) throws PDFAuthenticationFailureException, IOException, EncryptionUnsupportedByPlatformException, EncryptionUnsupportedByProductException {
        assert (v >= 4) : "crypt filter decrypter not supported for standard encryption prior to version 4";
        boolean encryptMetadata = true;
        PDFObject encryptMetadataObj = encryptDict.getDictRef("EncryptMetadata");
        if (encryptMetadataObj != null && encryptMetadataObj.getType() == 1) {
            encryptMetadata = encryptMetadataObj.getBooleanValue();
        }
        HashMap<String, PDFDecrypter> cfDecrypters = new HashMap<String, PDFDecrypter>();
        PDFObject cfDict = encryptDict.getDictRef("CF");
        if (cfDict == null) {
            throw new PDFParseException("No CF value present in Encrypt dict for V4 encryption");
        }
        Iterator cfNameIt = cfDict.getDictKeys();
        while (cfNameIt.hasNext()) {
            PDFDecrypter cfDecrypter;
            String cfm;
            String cfName = (String)cfNameIt.next();
            PDFObject cryptFilter = cfDict.getDictRef(cfName);
            PDFObject lengthObj = cryptFilter.getDictRef("Length");
            Integer length = lengthObj != null ? Integer.valueOf(lengthObj.getIntValue() * 8) : null;
            PDFObject cfmObj = cryptFilter.getDictRef("CFM");
            String string = cfm = cfmObj != null ? cfmObj.getStringValue() : "None";
            if ("None".equals(cfm)) {
                cfDecrypter = IdentityDecrypter.getInstance();
            } else if ("V2".equals(cfm)) {
                cfDecrypter = PDFDecrypterFactory.createStandardDecrypter(encryptDict, documentId, password, length, encryptMetadata, StandardDecrypter.EncryptionAlgorithm.RC4);
            } else if ("AESV2".equals(cfm)) {
                cfDecrypter = PDFDecrypterFactory.createStandardDecrypter(encryptDict, documentId, password, length, encryptMetadata, StandardDecrypter.EncryptionAlgorithm.AESV2);
            } else {
                throw new UnsupportedOperationException("Unknown CryptFilter method: " + cfm);
            }
            cfDecrypters.put(cfName, cfDecrypter);
        }
        cfDecrypters.put(CF_IDENTITY, IdentityDecrypter.getInstance());
        PDFObject stmFObj = encryptDict.getDictRef("StmF");
        String defaultStreamFilter = stmFObj != null ? stmFObj.getStringValue() : CF_IDENTITY;
        PDFObject strFObj = encryptDict.getDictRef("StrF");
        String defaultStringFilter = strFObj != null ? strFObj.getStringValue() : CF_IDENTITY;
        return new CryptFilterDecrypter(cfDecrypters, defaultStreamFilter, defaultStringFilter);
    }

    private static PDFDecrypter createStandardDecrypter(PDFObject encryptDict, PDFObject documentId, PDFPassword password, Integer keyLength, boolean encryptMetadata, StandardDecrypter.EncryptionAlgorithm encryptionAlgorithm) throws PDFAuthenticationFailureException, IOException, EncryptionUnsupportedByPlatformException, EncryptionUnsupportedByProductException {
        PDFObject rObj;
        if (keyLength == null) {
            keyLength = 40;
        }
        if ((rObj = encryptDict.getDictRef("R")) == null) {
            throw new PDFParseException("No R entry present in Encrypt dictionary");
        }
        int revision = rObj.getIntValue();
        if (revision < 2 || revision > 4) {
            throw new EncryptionUnsupportedByPlatformException("Unsupported Standard security handler revision; R=" + revision);
        }
        PDFObject oObj = encryptDict.getDictRef("O");
        if (oObj == null) {
            throw new PDFParseException("No O entry present in Encrypt dictionary");
        }
        byte[] o = oObj.getStream();
        if (o.length != 32) {
            throw new PDFParseException("Expected owner key O value of 32 bytes; found " + o.length);
        }
        PDFObject uObj = encryptDict.getDictRef("U");
        if (uObj == null) {
            throw new PDFParseException("No U entry present in Encrypt dictionary");
        }
        byte[] u = uObj.getStream();
        if (u.length != 32) {
            throw new PDFParseException("Expected user key U value of 32 bytes; found " + o.length);
        }
        PDFObject pObj = encryptDict.getDictRef("P");
        if (pObj == null) {
            throw new PDFParseException("Required P entry in Encrypt dictionary not found");
        }
        return new StandardDecrypter(encryptionAlgorithm, documentId, keyLength, revision, o, u, pObj.getIntValue(), encryptMetadata, password);
    }
}

