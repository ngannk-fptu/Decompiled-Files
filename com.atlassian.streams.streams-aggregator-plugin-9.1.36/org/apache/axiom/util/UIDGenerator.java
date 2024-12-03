/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.UUID;

public final class UIDGenerator {
    private static final long startTimeXorOperand;
    private static final long threadIdXorOperand;
    private static final long seqXorOperand;
    private static final ThreadLocal triplet;

    private UIDGenerator() {
    }

    private static void writeReverseLongHex(long value, StringBuilder buffer) {
        for (int i = 0; i < 16; ++i) {
            int n = (int)(value >> 4 * i) & 0xF;
            buffer.append((char)(n < 10 ? 48 + n : 97 + n - 10));
        }
    }

    private static void generateHex(StringBuilder buffer) {
        long[] values = (long[])triplet.get();
        long l = values[2];
        values[2] = l + 1L;
        UIDGenerator.writeReverseLongHex(l ^ seqXorOperand, buffer);
        UIDGenerator.writeReverseLongHex(values[1], buffer);
        UIDGenerator.writeReverseLongHex(values[0], buffer);
    }

    public static String generateContentId() {
        StringBuilder buffer = new StringBuilder();
        UIDGenerator.generateHex(buffer);
        buffer.append("@apache.org");
        return buffer.toString();
    }

    public static String generateMimeBoundary() {
        StringBuilder buffer = new StringBuilder("MIMEBoundary_");
        UIDGenerator.generateHex(buffer);
        return buffer.toString();
    }

    public static String generateUID() {
        StringBuilder buffer = new StringBuilder(48);
        UIDGenerator.generateHex(buffer);
        return buffer.toString();
    }

    public static String generateURNString() {
        return "urn:uuid:" + UUID.randomUUID();
    }

    public static URI generateURN() {
        try {
            return new URI(UIDGenerator.generateURNString());
        }
        catch (URISyntaxException ex) {
            throw new Error(ex);
        }
    }

    static {
        Random rand = new Random();
        threadIdXorOperand = rand.nextLong();
        startTimeXorOperand = rand.nextLong();
        seqXorOperand = rand.nextLong();
        triplet = new ThreadLocal(){

            protected Object initialValue() {
                long[] values = new long[3];
                values[0] = Thread.currentThread().getId() ^ threadIdXorOperand;
                values[1] = System.currentTimeMillis() ^ startTimeXorOperand;
                return values;
            }
        };
    }
}

