/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.codec.binary;

import java.io.InputStream;
import org.apache.commons.codec.CodecPolicy;
import org.apache.commons.codec.binary.Base16;
import org.apache.commons.codec.binary.BaseNCodecInputStream;

public class Base16InputStream
extends BaseNCodecInputStream {
    public Base16InputStream(InputStream inputStream) {
        this(inputStream, false);
    }

    public Base16InputStream(InputStream inputStream, boolean doEncode) {
        this(inputStream, doEncode, false);
    }

    public Base16InputStream(InputStream inputStream, boolean doEncode, boolean lowerCase) {
        this(inputStream, doEncode, lowerCase, CodecPolicy.LENIENT);
    }

    public Base16InputStream(InputStream inputStream, boolean doEncode, boolean lowerCase, CodecPolicy decodingPolicy) {
        super(inputStream, new Base16(lowerCase, decodingPolicy), doEncode);
    }
}

