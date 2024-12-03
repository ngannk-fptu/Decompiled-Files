/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Hex
 *  org.apache.commons.codec.digest.DigestUtils
 */
package com.atlassian.migration.agent.media.impl;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

public final class CryptoUtils {
    private CryptoUtils() {
    }

    public static String sha1(ByteBuffer data) {
        MessageDigest sha1Digest = DigestUtils.getSha1Digest();
        sha1Digest.update(data);
        byte[] hash = sha1Digest.digest();
        data.rewind();
        return Hex.encodeHexString((byte[])hash);
    }
}

