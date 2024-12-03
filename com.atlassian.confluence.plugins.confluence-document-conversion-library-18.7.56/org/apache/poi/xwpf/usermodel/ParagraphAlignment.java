/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum ParagraphAlignment {
    START(1),
    CENTER(2),
    END(3),
    BOTH(4),
    MEDIUM_KASHIDA(5),
    DISTRIBUTE(6),
    NUM_TAB(7),
    HIGH_KASHIDA(8),
    LOW_KASHIDA(9),
    THAI_DISTRIBUTE(10),
    LEFT(11),
    RIGHT(12);

    private static final Map<Integer, ParagraphAlignment> imap;
    private final int value;

    private ParagraphAlignment(int val) {
        this.value = val;
    }

    public static ParagraphAlignment valueOf(int type) {
        ParagraphAlignment err = imap.get(type);
        if (err == null) {
            throw new IllegalArgumentException("Unknown paragraph alignment: " + type);
        }
        return err;
    }

    public int getValue() {
        return this.value;
    }

    static {
        imap = new HashMap<Integer, ParagraphAlignment>();
        for (ParagraphAlignment p : ParagraphAlignment.values()) {
            imap.put(p.getValue(), p);
        }
    }
}

