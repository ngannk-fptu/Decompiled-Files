/*
 * Decompiled with CFR 0.152.
 */
package org.xerial.snappy;

import java.io.IOException;
import java.nio.ByteBuffer;

public class BitShuffleNative {
    public native int shuffle(Object var1, int var2, int var3, int var4, Object var5, int var6) throws IOException;

    public native int shuffleDirectBuffer(ByteBuffer var1, int var2, int var3, int var4, ByteBuffer var5, int var6) throws IOException;

    public native int unshuffle(Object var1, int var2, int var3, int var4, Object var5, int var6) throws IOException;

    public native int unshuffleDirectBuffer(ByteBuffer var1, int var2, int var3, int var4, ByteBuffer var5, int var6) throws IOException;
}

