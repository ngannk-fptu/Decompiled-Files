/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.BufferedInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.selectors.modifiedselector.Algorithm;

public class ChecksumAlgorithm
implements Algorithm {
    private String algorithm = "CRC";
    private Checksum checksum = null;

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm != null ? algorithm.toUpperCase(Locale.ENGLISH) : null;
    }

    public void initChecksum() {
        if (this.checksum != null) {
            return;
        }
        if ("CRC".equals(this.algorithm)) {
            this.checksum = new CRC32();
        } else if ("ADLER".equals(this.algorithm)) {
            this.checksum = new Adler32();
        } else {
            throw new BuildException(new NoSuchAlgorithmException());
        }
    }

    @Override
    public boolean isValid() {
        return "CRC".equals(this.algorithm) || "ADLER".equals(this.algorithm);
    }

    @Override
    public String getValue(File file) {
        this.initChecksum();
        if (file.canRead()) {
            String string;
            this.checksum.reset();
            CheckedInputStream check = new CheckedInputStream(new BufferedInputStream(Files.newInputStream(file.toPath(), new OpenOption[0])), this.checksum);
            try {
                while (check.read() != -1) {
                }
                string = Long.toString(check.getChecksum().getValue());
            }
            catch (Throwable throwable) {
                try {
                    try {
                        check.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                    throw throwable;
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            check.close();
            return string;
        }
        return null;
    }

    public String toString() {
        return String.format("<ChecksumAlgorithm:algorithm=%s>", this.algorithm);
    }
}

