/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xwpf.usermodel;

import java.util.HashMap;
import java.util.Map;

public enum TableRowHeightRule {
    AUTO(1),
    EXACT(2),
    AT_LEAST(3);

    private static Map<Integer, TableRowHeightRule> imap;
    private final int value;

    private TableRowHeightRule(int val) {
        this.value = val;
    }

    public static TableRowHeightRule valueOf(int type) {
        TableRowHeightRule err = imap.get(type);
        if (err == null) {
            throw new IllegalArgumentException("Unknown table row height rule: " + type);
        }
        return err;
    }

    public int getValue() {
        return this.value;
    }

    static {
        imap = new HashMap<Integer, TableRowHeightRule>();
        for (TableRowHeightRule p : TableRowHeightRule.values()) {
            imap.put(p.getValue(), p);
        }
    }
}

