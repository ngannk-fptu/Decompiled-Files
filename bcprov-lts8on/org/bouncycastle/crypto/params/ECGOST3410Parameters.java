/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.params;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;

public class ECGOST3410Parameters
extends ECNamedDomainParameters {
    private final ASN1ObjectIdentifier publicKeyParamSet;
    private final ASN1ObjectIdentifier digestParamSet;
    private final ASN1ObjectIdentifier encryptionParamSet;

    public ECGOST3410Parameters(ECDomainParameters ecParameters, ASN1ObjectIdentifier publicKeyParamSet, ASN1ObjectIdentifier digestParamSet) {
        this(ecParameters, publicKeyParamSet, digestParamSet, null);
    }

    public ECGOST3410Parameters(ECDomainParameters ecParameters, ASN1ObjectIdentifier publicKeyParamSet, ASN1ObjectIdentifier digestParamSet, ASN1ObjectIdentifier encryptionParamSet) {
        super(publicKeyParamSet, ecParameters);
        if (ecParameters instanceof ECNamedDomainParameters && !publicKeyParamSet.equals(((ECNamedDomainParameters)ecParameters).getName())) {
            throw new IllegalArgumentException("named parameters do not match publicKeyParamSet value");
        }
        this.publicKeyParamSet = publicKeyParamSet;
        this.digestParamSet = digestParamSet;
        this.encryptionParamSet = encryptionParamSet;
    }

    public ASN1ObjectIdentifier getPublicKeyParamSet() {
        return this.publicKeyParamSet;
    }

    public ASN1ObjectIdentifier getDigestParamSet() {
        return this.digestParamSet;
    }

    public ASN1ObjectIdentifier getEncryptionParamSet() {
        return this.encryptionParamSet;
    }
}

