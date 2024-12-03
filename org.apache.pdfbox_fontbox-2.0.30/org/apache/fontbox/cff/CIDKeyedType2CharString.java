/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import java.util.List;
import java.util.Locale;
import org.apache.fontbox.cff.Type2CharString;
import org.apache.fontbox.type1.Type1CharStringReader;

public class CIDKeyedType2CharString
extends Type2CharString {
    private final int cid;

    public CIDKeyedType2CharString(Type1CharStringReader font, String fontName, int cid, int gid, List<Object> sequence, int defaultWidthX, int nomWidthX) {
        super(font, fontName, String.format(Locale.US, "%04x", cid), gid, sequence, defaultWidthX, nomWidthX);
        this.cid = cid;
    }

    public int getCID() {
        return this.cid;
    }
}

