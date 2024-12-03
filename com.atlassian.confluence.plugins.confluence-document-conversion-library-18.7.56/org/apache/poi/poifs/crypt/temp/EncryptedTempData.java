/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.CountingOutputStream
 */
package org.apache.poi.poifs.crypt.temp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.io.output.CountingOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.util.RandomSingleton;
import org.apache.poi.util.TempFile;

public class EncryptedTempData {
    private static final Logger LOG = LogManager.getLogger(EncryptedTempData.class);
    private static final CipherAlgorithm cipherAlgorithm = CipherAlgorithm.aes128;
    private static final String PADDING = "PKCS5Padding";
    private final SecretKeySpec skeySpec;
    private final byte[] ivBytes = new byte[16];
    private final File tempFile;
    private CountingOutputStream outputStream;

    public EncryptedTempData() throws IOException {
        byte[] keyBytes = new byte[16];
        RandomSingleton.getInstance().nextBytes(this.ivBytes);
        RandomSingleton.getInstance().nextBytes(keyBytes);
        this.skeySpec = new SecretKeySpec(keyBytes, EncryptedTempData.cipherAlgorithm.jceId);
        this.tempFile = TempFile.createTempFile("poi-temp-data", ".tmp");
    }

    public OutputStream getOutputStream() throws IOException {
        Cipher ciEnc = CryptoFunctions.getCipher(this.skeySpec, cipherAlgorithm, ChainingMode.cbc, this.ivBytes, 1, PADDING);
        this.outputStream = new CountingOutputStream((OutputStream)new CipherOutputStream(new FileOutputStream(this.tempFile), ciEnc));
        return this.outputStream;
    }

    public InputStream getInputStream() throws IOException {
        Cipher ciDec = CryptoFunctions.getCipher(this.skeySpec, cipherAlgorithm, ChainingMode.cbc, this.ivBytes, 2, PADDING);
        return new CipherInputStream(new FileInputStream(this.tempFile), ciDec);
    }

    public long getByteCount() {
        return this.outputStream == null ? 0L : this.outputStream.getByteCount();
    }

    public void dispose() {
        if (!this.tempFile.delete()) {
            Supplier[] supplierArray = new Supplier[1];
            supplierArray[0] = this.tempFile::getAbsolutePath;
            LOG.atWarn().log("{} can't be removed (or was already removed).", supplierArray);
        }
    }
}

