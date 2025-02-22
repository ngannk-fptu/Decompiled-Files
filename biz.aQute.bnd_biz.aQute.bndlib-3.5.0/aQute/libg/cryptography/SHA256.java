/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.cryptography;

import aQute.libg.cryptography.Digest;
import aQute.libg.cryptography.Digester;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256
extends Digest {
    public static final String ALGORITHM = "SHA-256";

    public static Digester<SHA256> getDigester(OutputStream ... out) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        return new Digester<SHA256>(md, out){

            @Override
            public SHA256 digest() throws Exception {
                return new SHA256(this.md.digest());
            }

            @Override
            public SHA256 digest(byte[] bytes) {
                return new SHA256(bytes);
            }

            @Override
            public String getAlgorithm() {
                return SHA256.ALGORITHM;
            }
        };
    }

    public SHA256(byte[] b) {
        super(b, 32);
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    public static SHA256 digest(byte[] data) throws Exception {
        return SHA256.getDigester(new OutputStream[0]).from(data);
    }

    public static SHA256 digest(File f) throws NoSuchAlgorithmException, Exception {
        return SHA256.getDigester(new OutputStream[0]).from(f);
    }

    public static SHA256 digest(InputStream f) throws NoSuchAlgorithmException, Exception {
        return SHA256.getDigester(new OutputStream[0]).from(f);
    }
}

