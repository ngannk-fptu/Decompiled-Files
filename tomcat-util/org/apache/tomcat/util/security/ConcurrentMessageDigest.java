/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.tomcat.util.res.StringManager;

public class ConcurrentMessageDigest {
    private static final StringManager sm = StringManager.getManager(ConcurrentMessageDigest.class);
    private static final String MD5 = "MD5";
    private static final String SHA1 = "SHA-1";
    private static final Map<String, Queue<MessageDigest>> queues = new ConcurrentHashMap<String, Queue<MessageDigest>>();

    private ConcurrentMessageDigest() {
    }

    public static byte[] digestMD5(byte[] ... input) {
        return ConcurrentMessageDigest.digest(MD5, input);
    }

    public static byte[] digestSHA1(byte[] ... input) {
        return ConcurrentMessageDigest.digest(SHA1, input);
    }

    public static byte[] digest(String algorithm, byte[] ... input) {
        return ConcurrentMessageDigest.digest(algorithm, 1, input);
    }

    public static byte[] digest(String algorithm, int iterations, byte[] ... input) {
        Queue<MessageDigest> queue = queues.get(algorithm);
        if (queue == null) {
            throw new IllegalStateException(sm.getString("concurrentMessageDigest.noDigest"));
        }
        MessageDigest md = queue.poll();
        if (md == null) {
            try {
                md = MessageDigest.getInstance(algorithm);
            }
            catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(sm.getString("concurrentMessageDigest.noDigest"), e);
            }
        }
        for (byte[] bytes : input) {
            md.update(bytes);
        }
        byte[] result = md.digest();
        if (iterations > 1) {
            for (int i = 1; i < iterations; ++i) {
                md.update(result);
                result = md.digest();
            }
        }
        queue.add(md);
        return result;
    }

    public static void init(String algorithm) throws NoSuchAlgorithmException {
        if (!queues.containsKey(algorithm)) {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            ConcurrentLinkedQueue<MessageDigest> queue = new ConcurrentLinkedQueue<MessageDigest>();
            queue.add(md);
            queues.putIfAbsent(algorithm, queue);
        }
    }

    static {
        try {
            ConcurrentMessageDigest.init(MD5);
            ConcurrentMessageDigest.init(SHA1);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(sm.getString("concurrentMessageDigest.noDigest"), e);
        }
    }
}

