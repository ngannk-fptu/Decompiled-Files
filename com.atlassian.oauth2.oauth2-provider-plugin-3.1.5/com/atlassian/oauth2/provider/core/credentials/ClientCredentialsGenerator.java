/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.codec.binary.Hex
 */
package com.atlassian.oauth2.provider.core.credentials;

import javax.crypto.KeyGenerator;
import org.apache.commons.codec.binary.Hex;

public class ClientCredentialsGenerator {
    public String generate(Length length) {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(length.keysize);
        return Hex.encodeHexString((byte[])keyGenerator.generateKey().getEncoded());
    }

    public static enum Length {
        THIRTY_TWO(128),
        FORTY_EIGHT(192),
        SIXTY_FOUR(256);

        private final int keysize;

        private Length(int keysize) {
            this.keysize = keysize;
        }
    }
}

