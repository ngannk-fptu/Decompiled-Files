/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.codec.binary;

import java.io.InputStream;
import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.BaseNCodecInputStream;

public class Base64InputStream
extends BaseNCodecInputStream {
    public Base64InputStream(InputStream in) {
        this(in, false);
    }

    public Base64InputStream(InputStream in, boolean doEncode) {
        super(in, new Base64(false), doEncode);
    }

    public Base64InputStream(InputStream in, boolean doEncode, int lineLength, byte[] lineSeparator) {
        super(in, new Base64(lineLength, lineSeparator), doEncode);
    }

    public Base64InputStream(InputStream in, boolean doEncode, int lineLength, byte[] lineSeparator, CodecPolicy decodingPolicy) {
        super(in, new Base64(lineLength, lineSeparator, false, decodingPolicy), doEncode);
    }
}

