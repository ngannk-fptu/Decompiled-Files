/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.eac.operator.jcajce;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Signature;
import org.bouncycastle.eac.operator.jcajce.EACHelper;

class NamedEACHelper
extends EACHelper {
    private final String providerName;

    NamedEACHelper(String providerName) {
        this.providerName = providerName;
    }

    @Override
    protected Signature createSignature(String type) throws NoSuchProviderException, NoSuchAlgorithmException {
        return Signature.getInstance(type, this.providerName);
    }
}

