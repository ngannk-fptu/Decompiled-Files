/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.eac.CertificateHolderReference;

public class CertificationAuthorityReference
extends CertificateHolderReference {
    public CertificationAuthorityReference(String countryCode, String holderMnemonic, String sequenceNumber) {
        super(countryCode, holderMnemonic, sequenceNumber);
    }

    CertificationAuthorityReference(byte[] contents) {
        super(contents);
    }
}

