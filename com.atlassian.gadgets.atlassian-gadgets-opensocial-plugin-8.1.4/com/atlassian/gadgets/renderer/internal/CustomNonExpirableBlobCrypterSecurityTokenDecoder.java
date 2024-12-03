/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.shindig.common.crypto.BlobCrypter
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.gadgets.renderer.internal;

import com.atlassian.gadgets.renderer.internal.ContainerDomainProvider;
import com.atlassian.gadgets.renderer.internal.CustomBlobCrypterSecurityTokenDecoder;
import org.apache.shindig.common.crypto.BlobCrypter;
import org.springframework.beans.factory.annotation.Qualifier;

public class CustomNonExpirableBlobCrypterSecurityTokenDecoder
extends CustomBlobCrypterSecurityTokenDecoder {
    public CustomNonExpirableBlobCrypterSecurityTokenDecoder(@Qualifier(value="nonExpirableBlobCrypter") BlobCrypter crypter, ContainerDomainProvider containerDomainProvider) {
        super(crypter, containerDomainProvider);
    }
}

