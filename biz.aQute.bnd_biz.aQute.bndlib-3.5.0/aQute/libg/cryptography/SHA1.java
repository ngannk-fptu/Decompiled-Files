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

public class SHA1
extends Digest {
    public static final String ALGORITHM = "SHA-1";

    public static Digester<SHA1> getDigester(OutputStream ... out) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        return new Digester<SHA1>(md, out){

            @Override
            public SHA1 digest() throws Exception {
                return new SHA1(this.md.digest());
            }

            @Override
            public SHA1 digest(byte[] bytes) {
                return new SHA1(bytes);
            }

            @Override
            public String getAlgorithm() {
                return SHA1.ALGORITHM;
            }
        };
    }

    public SHA1(byte[] b) {
        super(b, 20);
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    public static SHA1 digest(byte[] data) throws Exception {
        return SHA1.getDigester(new OutputStream[0]).from(data);
    }

    public static SHA1 digest(File f) throws NoSuchAlgorithmException, Exception {
        return SHA1.getDigester(new OutputStream[0]).from(f);
    }

    public static SHA1 digest(InputStream f) throws NoSuchAlgorithmException, Exception {
        return SHA1.getDigester(new OutputStream[0]).from(f);
    }
}

