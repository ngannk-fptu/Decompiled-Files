/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.DefaultCurrentApplication
 */
package com.atlassian.confluence.security.trust;

import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.security.auth.trustedapps.DefaultCurrentApplication;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

final class ConfluenceApplication
extends DefaultCurrentApplication {
    ConfluenceApplication(PublicKey publicKey, PrivateKey privateKey, String id) {
        super(publicKey, privateKey, id);
    }

    ConfluenceApplication(KeyPair keypair, String id) {
        this(keypair.getPublic(), keypair.getPrivate(), id);
    }

    PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    KeyPair getKeyPair() {
        return new KeyPair(this.publicKey, this.privateKey);
    }

    public String toString() {
        String str = "Confluence application keys; id: " + this.getID() + ", public key: " + this.getPublicKey();
        if (ConfluenceSystemProperties.isDevMode()) {
            str = str + ", private key: " + this.privateKey;
        }
        return str;
    }
}

