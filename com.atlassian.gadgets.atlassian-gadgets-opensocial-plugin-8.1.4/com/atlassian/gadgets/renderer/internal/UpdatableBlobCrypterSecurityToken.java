/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.shindig.auth.BlobCrypterSecurityToken
 *  org.apache.shindig.common.crypto.BlobCrypter
 *  org.apache.shindig.common.crypto.BlobCrypterException
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.gadgets.renderer.internal;

import org.apache.shindig.auth.BlobCrypterSecurityToken;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.apache.shindig.common.crypto.BlobCrypterException;
import org.springframework.beans.factory.annotation.Qualifier;

class UpdatableBlobCrypterSecurityToken
extends BlobCrypterSecurityToken {
    public UpdatableBlobCrypterSecurityToken(@Qualifier(value="blobCrypter") BlobCrypter crypter, String container, String domain) {
        super(crypter, container, domain);
    }

    public String getUpdatedToken() {
        try {
            return this.encrypt();
        }
        catch (BlobCrypterException e) {
            return null;
        }
    }
}

