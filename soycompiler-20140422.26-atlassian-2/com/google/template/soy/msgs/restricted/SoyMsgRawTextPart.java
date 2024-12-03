/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Charsets
 */
package com.google.template.soy.msgs.restricted;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.template.soy.msgs.restricted.SoyMsgPart;
import java.util.Arrays;

public abstract class SoyMsgRawTextPart
extends SoyMsgPart {
    private static final int BYTES_PER_CHAR = 2;

    public static SoyMsgRawTextPart of(String rawText) {
        byte[] utf8Bytes = rawText.getBytes(Charsets.UTF_8);
        if (utf8Bytes.length < rawText.length() * 2) {
            return new Utf8SoyMsgRawTextPart(utf8Bytes);
        }
        return new CharArraySoyMsgRawTextPart(rawText.toCharArray());
    }

    public abstract String getRawText();

    public final String toString() {
        return this.getRawText();
    }

    SoyMsgRawTextPart() {
    }

    @VisibleForTesting
    static final class CharArraySoyMsgRawTextPart
    extends SoyMsgRawTextPart {
        private final char[] charArray;

        CharArraySoyMsgRawTextPart(char[] charArray) {
            this.charArray = charArray;
        }

        public boolean equals(Object other) {
            return other.getClass() == CharArraySoyMsgRawTextPart.class && Arrays.equals(this.charArray, ((CharArraySoyMsgRawTextPart)other).charArray);
        }

        public int hashCode() {
            return this.getClass().hashCode() + Arrays.hashCode(this.charArray);
        }

        @Override
        public String getRawText() {
            return new String(this.charArray);
        }
    }

    @VisibleForTesting
    static final class Utf8SoyMsgRawTextPart
    extends SoyMsgRawTextPart {
        private final byte[] utf8Bytes;

        Utf8SoyMsgRawTextPart(byte[] utf8Bytes) {
            this.utf8Bytes = utf8Bytes;
        }

        public boolean equals(Object other) {
            return other.getClass() == Utf8SoyMsgRawTextPart.class && Arrays.equals(this.utf8Bytes, ((Utf8SoyMsgRawTextPart)other).utf8Bytes);
        }

        public int hashCode() {
            return this.getClass().hashCode() + Arrays.hashCode(this.utf8Bytes);
        }

        @Override
        public String getRawText() {
            return new String(this.utf8Bytes, Charsets.UTF_8);
        }
    }
}

