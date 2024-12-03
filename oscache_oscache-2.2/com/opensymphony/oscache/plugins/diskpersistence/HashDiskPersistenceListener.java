/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.plugins.diskpersistence;

import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.persistence.PersistenceListener;
import com.opensymphony.oscache.plugins.diskpersistence.AbstractDiskPersistenceListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashDiskPersistenceListener
extends AbstractDiskPersistenceListener {
    public static final String HASH_ALGORITHM_KEY = "cache.persistence.disk.hash.algorithm";
    public static final String DEFAULT_HASH_ALGORITHM = "MD5";
    protected MessageDigest md = null;

    public PersistenceListener configure(Config config) {
        try {
            if (config.getProperty(HASH_ALGORITHM_KEY) != null) {
                try {
                    this.md = MessageDigest.getInstance(config.getProperty(HASH_ALGORITHM_KEY));
                }
                catch (NoSuchAlgorithmException e) {
                    this.md = MessageDigest.getInstance(DEFAULT_HASH_ALGORITHM);
                }
            } else {
                this.md = MessageDigest.getInstance(DEFAULT_HASH_ALGORITHM);
            }
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("No hash algorithm available for disk persistence", e);
        }
        return super.configure(config);
    }

    protected char[] getCacheFileName(String key) {
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("Invalid key '" + key + "' specified to getCacheFile.");
        }
        byte[] digest = this.md.digest(key.getBytes());
        String hexString = HashDiskPersistenceListener.byteArrayToHexString(digest);
        return hexString.toCharArray();
    }

    static String byteArrayToHexString(byte[] in) {
        byte ch = 0;
        if (in == null || in.length <= 0) {
            return null;
        }
        String[] pseudo = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        StringBuffer out = new StringBuffer(in.length * 2);
        for (int i = 0; i < in.length; ++i) {
            ch = (byte)(in[i] & 0xF0);
            ch = (byte)(ch >>> 4);
            ch = (byte)(ch & 0xF);
            out.append(pseudo[ch]);
            ch = (byte)(in[i] & 0xF);
            out.append(pseudo[ch]);
        }
        String rslt = new String(out);
        return rslt;
    }
}

