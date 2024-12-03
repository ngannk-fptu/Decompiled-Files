/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.core.util;

import com.thoughtworks.xstream.core.StringCodec;
import java.util.Base64;

public class Base64JavaUtilCodec
implements StringCodec {
    private final Base64.Decoder decoder;
    private final Base64.Encoder encoder;

    public Base64JavaUtilCodec() {
        this(Base64.getEncoder(), Base64.getMimeDecoder());
    }

    public Base64JavaUtilCodec(Base64.Encoder encoder, Base64.Decoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    public byte[] decode(String base64) {
        return this.decoder.decode(base64);
    }

    public String encode(byte[] data) {
        return this.encoder.encodeToString(data);
    }
}

