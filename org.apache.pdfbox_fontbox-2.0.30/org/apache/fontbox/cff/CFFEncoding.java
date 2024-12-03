/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import org.apache.fontbox.cff.CFFStandardString;
import org.apache.fontbox.encoding.Encoding;

public abstract class CFFEncoding
extends Encoding {
    CFFEncoding() {
    }

    public void add(int code, int sid, String name) {
        this.addCharacterEncoding(code, name);
    }

    protected void add(int code, int sid) {
        this.addCharacterEncoding(code, CFFStandardString.getName(sid));
    }
}

