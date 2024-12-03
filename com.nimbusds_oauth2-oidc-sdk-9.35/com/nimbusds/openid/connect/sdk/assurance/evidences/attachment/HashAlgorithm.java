/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.Immutable
 */
package com.nimbusds.openid.connect.sdk.assurance.evidences.attachment;

import com.nimbusds.oauth2.sdk.id.Identifier;
import net.jcip.annotations.Immutable;

@Immutable
public final class HashAlgorithm
extends Identifier {
    private static final long serialVersionUID = -3699666147154820591L;
    public static final HashAlgorithm SHA_256 = new HashAlgorithm("sha-256");
    public static final HashAlgorithm SHA_384 = new HashAlgorithm("sha-384");
    public static final HashAlgorithm SHA_512 = new HashAlgorithm("sha-512");

    public HashAlgorithm(String name) {
        super(name.toLowerCase());
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof HashAlgorithm && this.toString().equals(object.toString());
    }
}

