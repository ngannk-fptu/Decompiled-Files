/*
 * Decompiled with CFR 0.152.
 */
package jersey.repackaged.org.objectweb.asm;

import jersey.repackaged.org.objectweb.asm.Label;

class Edge {
    static final int NORMAL = 0;
    static final int EXCEPTION = Integer.MAX_VALUE;
    int info;
    Label successor;
    Edge next;

    Edge() {
    }
}

