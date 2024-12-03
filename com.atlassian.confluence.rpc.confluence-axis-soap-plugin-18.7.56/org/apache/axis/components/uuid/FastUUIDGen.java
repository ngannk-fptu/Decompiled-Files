/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.uuid;

import java.security.SecureRandom;
import java.util.Random;
import org.apache.axis.components.uuid.UUIDGen;

public class FastUUIDGen
implements UUIDGen {
    private static Random secureRandom;
    private static String nodeStr;
    private static int clockSequence;
    private long lastTime = 0L;

    private static String getNodeHexValue() {
        long node = 0L;
        long nodeValue = 0L;
        while ((node = FastUUIDGen.getBitsValue(nodeValue, 47, 47)) == 0L) {
            nodeValue = secureRandom.nextLong();
        }
        return FastUUIDGen.leftZeroPadString(Long.toHexString(node |= 0x800000000000L), 12);
    }

    private static int getClockSequence() {
        return secureRandom.nextInt(16384);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String nextUUID() {
        long time = System.currentTimeMillis();
        long timestamp = time * 10000L;
        timestamp += 122192927672762368L;
        timestamp += 327237632L;
        FastUUIDGen fastUUIDGen = this;
        synchronized (fastUUIDGen) {
            if (time - this.lastTime <= 0L) {
                clockSequence = clockSequence + 1 & 0x3FFF;
            }
            this.lastTime = time;
        }
        long timeLow = FastUUIDGen.getBitsValue(timestamp, 32, 32);
        long timeMid = FastUUIDGen.getBitsValue(timestamp, 48, 16);
        long timeHi = FastUUIDGen.getBitsValue(timestamp, 64, 16) | 0x1000L;
        long clockSeqLow = FastUUIDGen.getBitsValue(clockSequence, 8, 8);
        long clockSeqHi = FastUUIDGen.getBitsValue(clockSequence, 16, 8) | 0x80L;
        String timeLowStr = FastUUIDGen.leftZeroPadString(Long.toHexString(timeLow), 8);
        String timeMidStr = FastUUIDGen.leftZeroPadString(Long.toHexString(timeMid), 4);
        String timeHiStr = FastUUIDGen.leftZeroPadString(Long.toHexString(timeHi), 4);
        String clockSeqHiStr = FastUUIDGen.leftZeroPadString(Long.toHexString(clockSeqHi), 2);
        String clockSeqLowStr = FastUUIDGen.leftZeroPadString(Long.toHexString(clockSeqLow), 2);
        StringBuffer result = new StringBuffer(36);
        result.append(timeLowStr).append("-");
        result.append(timeMidStr).append("-");
        result.append(timeHiStr).append("-");
        result.append(clockSeqHiStr).append(clockSeqLowStr);
        result.append("-").append(nodeStr);
        return result.toString();
    }

    private static long getBitsValue(long value, int startBit, int bitLen) {
        return value << 64 - startBit >>> 64 - bitLen;
    }

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

    static {
        try {
            secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
        }
        catch (Exception e) {
            secureRandom = new Random();
        }
        nodeStr = FastUUIDGen.getNodeHexValue();
        clockSequence = FastUUIDGen.getClockSequence();
    }
}

