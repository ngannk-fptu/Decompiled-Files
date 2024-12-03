/*
 * Decompiled with CFR 0.152.
 */
package org.mozilla.javascript.regexp;

import java.io.Serializable;
import org.mozilla.javascript.regexp.RECharSet;

class RECompiled
implements Serializable {
    private static final long serialVersionUID = -6144956577595844213L;
    final char[] source;
    int parenCount;
    int flags;
    byte[] program;
    int classCount;
    RECharSet[] classList;
    int anchorCh = -1;

    RECompiled(String str) {
        this.source = str.toCharArray();
    }
}

