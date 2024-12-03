/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Hex
 *  org.apache.commons.codec.digest.DigestUtils
 */
package com.atlassian.modzdetector;

import com.atlassian.modzdetector.HashAlgorithm;
import com.atlassian.modzdetector.IOUtils;
import com.atlassian.modzdetector.NullOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public class MD5HashAlgorithm
implements HashAlgorithm {
    public String getHash(InputStream stream) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            DigestInputStream dis = new DigestInputStream(stream, md5);
            IOUtils.copy(dis, new NullOutputStream());
            return new String(Hex.encodeHex((byte[])dis.getMessageDigest().digest()));
        }
        catch (IOException e) {
            return null;
        }
        catch (NoSuchAlgorithmException shouldNotHappen) {
            throw new RuntimeException(shouldNotHappen);
        }
    }

    public String getHash(byte[] bytes) {
        return DigestUtils.md5Hex((byte[])bytes);
    }

    public String toString() {
        return "MD5 HEX";
    }
}

