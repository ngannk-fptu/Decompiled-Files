/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.agile;

import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.agile.AgileDecryptor;
import org.apache.poi.poifs.crypt.agile.AgileEncryptionHeader;
import org.apache.poi.poifs.crypt.agile.AgileEncryptionVerifier;
import org.apache.poi.poifs.crypt.agile.AgileEncryptor;
import org.apache.poi.poifs.crypt.agile.EncryptionDocument;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.XMLHelper;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class AgileEncryptionInfoBuilder
implements EncryptionInfoBuilder {
    @Override
    public void initialize(EncryptionInfo info, LittleEndianInput dis) throws IOException {
        EncryptionDocument ed = AgileEncryptionInfoBuilder.parseDescriptor((InputStream)((Object)dis));
        info.setHeader(new AgileEncryptionHeader(ed));
        info.setVerifier(new AgileEncryptionVerifier(ed));
        if (info.getVersionMajor() == EncryptionMode.agile.versionMajor && info.getVersionMinor() == EncryptionMode.agile.versionMinor) {
            AgileDecryptor dec = new AgileDecryptor();
            dec.setEncryptionInfo(info);
            info.setDecryptor(dec);
            AgileEncryptor enc = new AgileEncryptor();
            enc.setEncryptionInfo(info);
            info.setEncryptor(enc);
        }
    }

    @Override
    public void initialize(EncryptionInfo info, CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        if (cipherAlgorithm == null) {
            cipherAlgorithm = CipherAlgorithm.aes128;
        }
        if (cipherAlgorithm == CipherAlgorithm.rc4) {
            throw new EncryptedDocumentException("RC4 must not be used with agile encryption.");
        }
        if (hashAlgorithm == null) {
            hashAlgorithm = HashAlgorithm.sha1;
        }
        if (chainingMode == null) {
            chainingMode = ChainingMode.cbc;
        }
        if (chainingMode != ChainingMode.cbc && chainingMode != ChainingMode.cfb) {
            throw new EncryptedDocumentException("Agile encryption only supports CBC/CFB chaining.");
        }
        if (keyBits == -1) {
            keyBits = cipherAlgorithm.defaultKeySize;
        }
        if (blockSize == -1) {
            blockSize = cipherAlgorithm.blockSize;
        }
        boolean found = false;
        for (int ks : cipherAlgorithm.allowedKeySize) {
            found |= ks == keyBits;
        }
        if (!found) {
            throw new EncryptedDocumentException("KeySize " + keyBits + " not allowed for Cipher " + (Object)((Object)cipherAlgorithm));
        }
        info.setHeader(new AgileEncryptionHeader(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode));
        info.setVerifier(new AgileEncryptionVerifier(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode));
        AgileDecryptor dec = new AgileDecryptor();
        dec.setEncryptionInfo(info);
        info.setDecryptor(dec);
        AgileEncryptor enc = new AgileEncryptor();
        enc.setEncryptionInfo(info);
        info.setEncryptor(enc);
    }

    protected static EncryptionDocument parseDescriptor(String descriptor) {
        return AgileEncryptionInfoBuilder.parseDescriptor(new InputSource(descriptor));
    }

    protected static EncryptionDocument parseDescriptor(InputStream descriptor) {
        return AgileEncryptionInfoBuilder.parseDescriptor(new InputSource(descriptor));
    }

    private static EncryptionDocument parseDescriptor(InputSource descriptor) {
        try {
            Document doc = XMLHelper.newDocumentBuilder().parse(descriptor);
            EncryptionDocument ed = new EncryptionDocument();
            ed.parse(doc);
            return ed;
        }
        catch (IOException | SAXException e) {
            throw new EncryptedDocumentException("Unable to parse encryption descriptor", e);
        }
    }
}

