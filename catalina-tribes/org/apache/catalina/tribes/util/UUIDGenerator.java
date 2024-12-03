/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.tribes.util;

import java.security.SecureRandom;
import java.util.Random;
import org.apache.catalina.tribes.util.StringManager;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public class UUIDGenerator {
    private static final Log log = LogFactory.getLog(UUIDGenerator.class);
    protected static final StringManager sm = StringManager.getManager("org.apache.catalina.tribes.util");
    public static final int UUID_LENGTH = 16;
    public static final int UUID_VERSION = 4;
    public static final int BYTES_PER_INT = 4;
    public static final int BITS_PER_BYTE = 8;
    protected static final SecureRandom secrand;
    protected static final Random rand;

    public static byte[] randomUUID(boolean secure) {
        byte[] result = new byte[16];
        return UUIDGenerator.randomUUID(secure, result, 0);
    }

    public static byte[] randomUUID(boolean secure, byte[] into, int offset) {
        if (offset + 16 > into.length) {
            throw new ArrayIndexOutOfBoundsException(sm.getString("uuidGenerator.unable.fit", Integer.toString(16), Integer.toString(into.length), Integer.toString(offset + 16)));
        }
        Random r = secure && secrand != null ? secrand : rand;
        UUIDGenerator.nextBytes(into, offset, 16, r);
        int n = 6 + offset;
        into[n] = (byte)(into[n] & 0xF);
        int n2 = 6 + offset;
        into[n2] = (byte)(into[n2] | 0x40);
        int n3 = 8 + offset;
        into[n3] = (byte)(into[n3] & 0x3F);
        int n4 = 8 + offset;
        into[n4] = (byte)(into[n4] | 0x80);
        return into;
    }

    public static void nextBytes(byte[] into, int offset, int length, Random r) {
        int numRequested = length;
        int numGot = 0;
        int rnd = 0;
        block0: while (true) {
            int i = 0;
            while (true) {
                if (i >= 4) continue block0;
                if (numGot == numRequested) {
                    return;
                }
                rnd = i == 0 ? r.nextInt() : rnd >> 8;
                into[offset + numGot] = (byte)rnd;
                ++numGot;
                ++i;
            }
            break;
        }
    }

    static {
        rand = new Random();
        long start = System.currentTimeMillis();
        secrand = new SecureRandom();
        secrand.nextInt();
        long time = System.currentTimeMillis() - start;
        if (time > 100L) {
            log.info((Object)sm.getString("uuidGenerator.createRandom", secrand.getAlgorithm(), time));
        }
    }
}

