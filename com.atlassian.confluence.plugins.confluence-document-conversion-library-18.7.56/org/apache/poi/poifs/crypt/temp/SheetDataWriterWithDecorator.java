/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.temp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.util.RandomSingleton;
import org.apache.poi.xssf.streaming.SheetDataWriter;

public class SheetDataWriterWithDecorator
extends SheetDataWriter {
    static final CipherAlgorithm cipherAlgorithm = CipherAlgorithm.aes128;
    SecretKeySpec skeySpec;
    byte[] ivBytes;

    void init() {
        if (this.skeySpec == null) {
            this.ivBytes = new byte[16];
            byte[] keyBytes = new byte[16];
            RandomSingleton.getInstance().nextBytes(this.ivBytes);
            RandomSingleton.getInstance().nextBytes(keyBytes);
            this.skeySpec = new SecretKeySpec(keyBytes, SheetDataWriterWithDecorator.cipherAlgorithm.jceId);
        }
    }

    @Override
    protected OutputStream decorateOutputStream(FileOutputStream fos) {
        this.init();
        Cipher ciEnc = CryptoFunctions.getCipher(this.skeySpec, cipherAlgorithm, ChainingMode.cbc, this.ivBytes, 1, "PKCS5Padding");
        return new CipherOutputStream(fos, ciEnc);
    }

    @Override
    protected InputStream decorateInputStream(FileInputStream fis) {
        Cipher ciDec = CryptoFunctions.getCipher(this.skeySpec, cipherAlgorithm, ChainingMode.cbc, this.ivBytes, 2, "PKCS5Padding");
        return new CipherInputStream(fis, ciDec);
    }
}

