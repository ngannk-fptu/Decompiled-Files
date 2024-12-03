/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum TableRowAlign {
    LEFT(5),
    CENTER(1),
    RIGHT(2);

    private static Map<Integer, TableRowAlign> imap;
    private final int value;

    private TableRowAlign(int val) {
        this.value = val;
    }

    public static TableRowAlign valueOf(int type) {
        TableRowAlign err = imap.get(type);
        if (err == null) {
            throw new IllegalArgumentException("Unknown table row alignment: " + type);
        }
        return err;
    }

    public int getValue() {
        return this.value;
    }

    static {
        imap = new HashMap<Integer, TableRowAlign>();
        for (TableRowAlign p : TableRowAlign.values()) {
            imap.put(p.getValue(), p);
        }
    }
}

