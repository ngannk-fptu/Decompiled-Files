/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.spec;

import java.security.spec.EncodedKeySpec;

public class RawEncodedKeySpec
extends EncodedKeySpec {
    public RawEncodedKeySpec(byte[] bytes) {
        super(bytes);
    }

    @Override
    public String getFormat() {
        return "RAW";
    }
}

