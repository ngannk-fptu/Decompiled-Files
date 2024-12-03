/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERUTF8String
 *  org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers
 */
package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.cert.crmf.Control;

public class AuthenticatorControl
implements Control {
    private static final ASN1ObjectIdentifier type = CRMFObjectIdentifiers.id_regCtrl_authenticator;
    private final ASN1UTF8String token;

    public AuthenticatorControl(ASN1UTF8String token) {
        this.token = token;
    }

    public AuthenticatorControl(String token) {
        this.token = new DERUTF8String(token);
    }

    @Override
    public ASN1ObjectIdentifier getType() {
        return type;
    }

    @Override
    public ASN1Encodable getValue() {
        return this.token;
    }
}

