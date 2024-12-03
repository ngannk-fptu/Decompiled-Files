/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.codec.binary;

import java.io.InputStream;
import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.BaseNCodecInputStream;

public class Base32InputStream
extends BaseNCodecInputStream {
    public Base32InputStream(InputStream in) {
        this(in, false);
    }

    public Base32InputStream(InputStream in, boolean doEncode) {
        super(in, new Base32(false), doEncode);
    }

    public Base32InputStream(InputStream input, boolean doEncode, int lineLength, byte[] lineSeparator) {
        super(input, new Base32(lineLength, lineSeparator), doEncode);
    }

    public Base32InputStream(InputStream input, boolean doEncode, int lineLength, byte[] lineSeparator, CodecPolicy decodingPolicy) {
        super(input, new Base32(lineLength, lineSeparator, false, 61, decodingPolicy), doEncode);
    }
}

