/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.compress.archivers.ArchiveEntry
 *  org.apache.commons.compress.archivers.zip.ZipArchiveEntry
 *  org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
 *  org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream
 *  org.apache.commons.compress.archivers.zip.ZipFile
 *  org.apache.commons.io.output.CloseShieldOutputStream
 */
package org.apache.poi.poifs.crypt.temp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.util.ZipEntrySource;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.RandomSingleton;
import org.apache.poi.util.TempFile;

public final class AesZipFileZipEntrySource
implements ZipEntrySource {
    private static final Logger LOG = LogManager.getLogger(AesZipFileZipEntrySource.class);
    private static final String PADDING = "PKCS5Padding";
    private final File tmpFile;
    private final ZipFile zipFile;
    private final Cipher ci;
    private boolean closed;

    private AesZipFileZipEntrySource(File tmpFile, Cipher ci) throws IOException {
        this.tmpFile = tmpFile;
        this.zipFile = new ZipFile(tmpFile);
        this.ci = ci;
        this.closed = false;
    }

    @Override
    public Enumeration<? extends ZipArchiveEntry> getEntries() {
        return this.zipFile.getEntries();
    }

    @Override
    public ZipArchiveEntry getEntry(String path) {
        return this.zipFile.getEntry(path);
    }

    @Override
    public InputStream getInputStream(ZipArchiveEntry entry) throws IOException {
        InputStream is = this.zipFile.getInputStream(entry);
        return new CipherInputStream(is, this.ci);
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.zipFile.close();
            if (!this.tmpFile.delete()) {
                LOG.atWarn().log("{} can't be removed (or was already removed).", (Object)this.tmpFile.getAbsolutePath());
            }
        }
        this.closed = true;
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    public static AesZipFileZipEntrySource createZipEntrySource(InputStream is) throws IOException {
        try {
            byte[] ivBytes = new byte[16];
            byte[] keyBytes = new byte[16];
            RandomSingleton.getInstance().nextBytes(ivBytes);
            RandomSingleton.getInstance().nextBytes(keyBytes);
            File tmpFile = TempFile.createTempFile("protectedXlsx", ".zip");
            try {
                AesZipFileZipEntrySource.copyToFile(is, tmpFile, keyBytes, ivBytes);
                AesZipFileZipEntrySource aesZipFileZipEntrySource = AesZipFileZipEntrySource.fileToSource(tmpFile, keyBytes, ivBytes);
                return aesZipFileZipEntrySource;
            }
            catch (IOException | RuntimeException e) {
                if (!tmpFile.delete()) {
                    LOG.atInfo().log("Temp file was not deleted, may already have been deleted by another method.");
                }
                throw e;
            }
        }
        finally {
            IOUtils.closeQuietly(is);
        }
    }

    private static void copyToFile(InputStream is, File tmpFile, byte[] keyBytes, byte[] ivBytes) throws IOException {
        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, CipherAlgorithm.aes128.jceId);
        Cipher ciEnc = CryptoFunctions.getCipher(skeySpec, CipherAlgorithm.aes128, ChainingMode.cbc, ivBytes, 1, PADDING);
        try (ZipArchiveInputStream zis = new ZipArchiveInputStream(is);
             FileOutputStream fos = new FileOutputStream(tmpFile);
             ZipArchiveOutputStream zos = new ZipArchiveOutputStream((OutputStream)fos);){
            ZipArchiveEntry ze;
            while ((ze = zis.getNextZipEntry()) != null) {
                ZipArchiveEntry zeNew = new ZipArchiveEntry(ze.getName());
                zeNew.setComment(ze.getComment());
                zeNew.setExtra(ze.getExtra());
                zeNew.setTime(ze.getTime());
                zos.putArchiveEntry((ArchiveEntry)zeNew);
                try (CipherOutputStream cos = new CipherOutputStream((OutputStream)CloseShieldOutputStream.wrap((OutputStream)zos), ciEnc);){
                    IOUtils.copy((InputStream)zis, cos);
                }
                zos.closeArchiveEntry();
            }
        }
    }

    private static AesZipFileZipEntrySource fileToSource(File tmpFile, byte[] keyBytes, byte[] ivBytes) throws IOException {
        SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, CipherAlgorithm.aes128.jceId);
        Cipher ciDec = CryptoFunctions.getCipher(skeySpec, CipherAlgorithm.aes128, ChainingMode.cbc, ivBytes, 2, PADDING);
        return new AesZipFileZipEntrySource(tmpFile, ciDec);
    }
}

