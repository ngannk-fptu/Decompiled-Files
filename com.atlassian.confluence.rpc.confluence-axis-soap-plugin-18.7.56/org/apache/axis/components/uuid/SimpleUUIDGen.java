/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.uuid;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import org.apache.axis.components.uuid.UUIDGen;

public class SimpleUUIDGen
implements UUIDGen {
    private static final BigInteger countStart = new BigInteger("-12219292800000");
    private static final int clock_sequence = new Random().nextInt(16384);
    private static final byte ZERO = 48;
    private static final byte ONE = 49;
    private static Random secureRandom = null;

    private static final String leftZeroPadString(String bitString, int len) {
        if (bitString.length() < len) {
            int nbExtraZeros = len - bitString.length();
            StringBuffer extraZeros = new StringBuffer();
            for (int i = 0; i < nbExtraZeros; ++i) {
                extraZeros.append("0");
            }
            extraZeros.append(bitString);
            bitString = extraZeros.toString();
        }
        return bitString;
    }

    public String nextUUID() {
        BigInteger current = BigInteger.valueOf(System.currentTimeMillis());
        BigInteger countMillis = current.subtract(countStart);
        BigInteger count = countMillis.multiply(BigInteger.valueOf(10000L));
        byte[] bits = SimpleUUIDGen.leftZeroPadString(count.toString(2), 60).getBytes();
        byte[] time_low = new byte[32];
        for (int i = 0; i < 32; ++i) {
            time_low[i] = bits[bits.length - i - 1];
        }
        byte[] time_mid = new byte[16];
        for (int i = 0; i < 16; ++i) {
            time_mid[i] = bits[bits.length - 32 - i - 1];
        }
        byte[] time_hi_and_version = new byte[16];
        for (int i = 0; i < 12; ++i) {
            time_hi_and_version[i] = bits[bits.length - 48 - i - 1];
        }
        time_hi_and_version[12] = 49;
        time_hi_and_version[13] = 48;
        time_hi_and_version[14] = 48;
        time_hi_and_version[15] = 48;
        BigInteger clockSequence = BigInteger.valueOf(clock_sequence);
        byte[] clock_bits = SimpleUUIDGen.leftZeroPadString(clockSequence.toString(2), 14).getBytes();
        byte[] clock_seq_low = new byte[8];
        for (int i = 0; i < 8; ++i) {
            clock_seq_low[i] = clock_bits[clock_bits.length - i - 1];
        }
        byte[] clock_seq_hi_and_reserved = new byte[8];
        for (int i = 0; i < 6; ++i) {
            clock_seq_hi_and_reserved[i] = clock_bits[clock_bits.length - 8 - i - 1];
        }
        clock_seq_hi_and_reserved[6] = 48;
        clock_seq_hi_and_reserved[7] = 49;
        String timeLow = Long.toHexString(new BigInteger(new String(SimpleUUIDGen.reverseArray(time_low)), 2).longValue());
        timeLow = SimpleUUIDGen.leftZeroPadString(timeLow, 8);
        String timeMid = Long.toHexString(new BigInteger(new String(SimpleUUIDGen.reverseArray(time_mid)), 2).longValue());
        timeMid = SimpleUUIDGen.leftZeroPadString(timeMid, 4);
        String timeHiAndVersion = Long.toHexString(new BigInteger(new String(SimpleUUIDGen.reverseArray(time_hi_and_version)), 2).longValue());
        timeHiAndVersion = SimpleUUIDGen.leftZeroPadString(timeHiAndVersion, 4);
        String clockSeqHiAndReserved = Long.toHexString(new BigInteger(new String(SimpleUUIDGen.reverseArray(clock_seq_hi_and_reserved)), 2).longValue());
        clockSeqHiAndReserved = SimpleUUIDGen.leftZeroPadString(clockSeqHiAndReserved, 2);
        String clockSeqLow = Long.toHexString(new BigInteger(new String(SimpleUUIDGen.reverseArray(clock_seq_low)), 2).longValue());
        clockSeqLow = SimpleUUIDGen.leftZeroPadString(clockSeqLow, 2);
        long nodeValue = secureRandom.nextLong();
        nodeValue = Math.abs(nodeValue);
        while (nodeValue > 0x800000000000L) {
            nodeValue = secureRandom.nextLong();
            nodeValue = Math.abs(nodeValue);
        }
        BigInteger nodeInt = BigInteger.valueOf(nodeValue);
        byte[] node_bits = SimpleUUIDGen.leftZeroPadString(nodeInt.toString(2), 47).getBytes();
        byte[] node = new byte[48];
        for (int i = 0; i < 47; ++i) {
            node[i] = node_bits[node_bits.length - i - 1];
        }
        node[47] = 49;
        String theNode = Long.toHexString(new BigInteger(new String(SimpleUUIDGen.reverseArray(node)), 2).longValue());
        theNode = SimpleUUIDGen.leftZeroPadString(theNode, 12);
        StringBuffer result = new StringBuffer(timeLow);
        result.append("-");
        result.append(timeMid);
        result.append("-");
        result.append(timeHiAndVersion);
        result.append("-");
        result.append(clockSeqHiAndReserved);
        result.append(clockSeqLow);
        result.append("-");
        result.append(theNode);
        return result.toString().toUpperCase();
    }

    private static byte[] reverseArray(byte[] bits) {
        byte[] result = new byte[bits.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = bits[result.length - 1 - i];
        }
        return result;
    }

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
        }
        catch (Exception e) {
            secureRandom = new Random();
        }
    }
}

