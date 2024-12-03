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

public class MD5
extends Digest {
    public static final String ALGORITHM = "MD5";

    public static Digester<MD5> getDigester(OutputStream ... out) throws Exception {
        return new Digester<MD5>(MessageDigest.getInstance(ALGORITHM), out){

            @Override
            public MD5 digest() throws Exception {
                return new MD5(this.md.digest());
            }

            @Override
            public MD5 digest(byte[] bytes) {
                return new MD5(bytes);
            }

            @Override
            public String getAlgorithm() {
                return MD5.ALGORITHM;
            }
        };
    }

    public MD5(byte[] digest) {
        super(digest, 16);
    }

    @Override
    public String getAlgorithm() {
        return ALGORITHM;
    }

    public static MD5 digest(byte[] data) throws Exception {
        return MD5.getDigester(new OutputStream[0]).from(data);
    }

    public static MD5 digest(File f) throws NoSuchAlgorithmException, Exception {
        return MD5.getDigester(new OutputStream[0]).from(f);
    }

    public static MD5 digest(InputStream f) throws NoSuchAlgorithmException, Exception {
        return MD5.getDigester(new OutputStream[0]).from(f);
    }
}

