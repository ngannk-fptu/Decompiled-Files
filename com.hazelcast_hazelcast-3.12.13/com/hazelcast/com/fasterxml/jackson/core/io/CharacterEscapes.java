/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.com.fasterxml.jackson.core.io;

import com.hazelcast.com.fasterxml.jackson.core.SerializableString;
import com.hazelcast.com.fasterxml.jackson.core.io.CharTypes;
import java.io.Serializable;
import java.util.Arrays;

public abstract class CharacterEscapes
implements Serializable {
    public static final int ESCAPE_NONE = 0;
    public static final int ESCAPE_STANDARD = -1;
    public static final int ESCAPE_CUSTOM = -2;

    public abstract int[] getEscapeCodesForAscii();

    public abstract SerializableString getEscapeSequence(int var1);

    public static int[] standardAsciiEscapesForJSON() {
        int[] esc = CharTypes.get7BitOutputEscapes();
        return Arrays.copyOf(esc, esc.length);
    }
}

