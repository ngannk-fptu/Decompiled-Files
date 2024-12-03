/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import org.apache.poi.util.Internal;

@Internal
public abstract class SprmUncompressor {
    protected SprmUncompressor() {
    }

    public static boolean getFlag(int x) {
        return x != 0;
    }
}

