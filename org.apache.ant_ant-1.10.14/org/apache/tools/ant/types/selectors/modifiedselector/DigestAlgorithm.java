/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.selectors.modifiedselector.Algorithm;

public class DigestAlgorithm
implements Algorithm {
    private static final int BYTE_MASK = 255;
    private static final int BUFFER_SIZE = 8192;
    private String algorithm = "MD5";
    private String provider = null;
    private MessageDigest messageDigest = null;
    private int readBufferSize = 8192;

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm != null ? algorithm.toUpperCase(Locale.ENGLISH) : null;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void initMessageDigest() {
        if (this.messageDigest != null) {
            return;
        }
        if (this.provider != null && !this.provider.isEmpty() && !"null".equals(this.provider)) {
            try {
                this.messageDigest = MessageDigest.getInstance(this.algorithm, this.provider);
            }
            catch (NoSuchAlgorithmException | NoSuchProviderException e) {
                throw new BuildException(e);
            }
        }
        try {
            this.messageDigest = MessageDigest.getInstance(this.algorithm);
        }
        catch (NoSuchAlgorithmException noalgo) {
            throw new BuildException(noalgo);
        }
    }

    @Override
    public boolean isValid() {
        return "SHA".equals(this.algorithm) || "MD5".equals(this.algorithm);
    }

    @Override
    public String getValue(File file) {
        String throwable2;
        if (!file.canRead()) {
            return null;
        }
        this.initMessageDigest();
        byte[] buf = new byte[this.readBufferSize];
        this.messageDigest.reset();
        DigestInputStream dis = new DigestInputStream(Files.newInputStream(file.toPath(), new OpenOption[0]), this.messageDigest);
        try {
            while (dis.read(buf, 0, this.readBufferSize) != -1) {
            }
            StringBuilder checksumSb = new StringBuilder();
            for (byte digestByte : this.messageDigest.digest()) {
                checksumSb.append(String.format("%02x", 0xFF & digestByte));
            }
            throwable2 = checksumSb.toString();
        }
        catch (Throwable throwable) {
            try {
                try {
                    dis.close();
                }
                catch (Throwable throwable3) {
                    throwable.addSuppressed(throwable3);
                }
                throw throwable;
            }
            catch (IOException ignored) {
                return null;
            }
        }
        dis.close();
        return throwable2;
    }

    public String toString() {
        return String.format("<DigestAlgorithm:algorithm=%s;provider=%s>", this.algorithm, this.provider);
    }
}

