/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.asm;

import org.springframework.asm.Label;

class Edge {
    static final int NORMAL = 0;
    static final int EXCEPTION = Integer.MAX_VALUE;
    int info;
    Label successor;
    Edge next;

    Edge() {
    }
}

