/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.util.buf;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class CharsetUtil {
    private CharsetUtil() {
    }

    public static boolean isAsciiSuperset(Charset charset) {
        CharsetDecoder decoder = charset.newDecoder();
        ByteBuffer inBytes = ByteBuffer.allocate(1);
        for (int i = 0; i < 128; ++i) {
            CharBuffer outChars;
            inBytes.clear();
            inBytes.put((byte)i);
            inBytes.flip();
            try {
                outChars = decoder.decode(inBytes);
            }
            catch (CharacterCodingException e) {
                return false;
            }
            try {
                if (outChars.get() == i) continue;
                return false;
            }
            catch (BufferUnderflowException e) {
                return false;
            }
        }
        return true;
    }
}

