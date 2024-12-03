/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x500.X500Name
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert.ocsp.jcajce;

import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.RespID;
import org.bouncycastle.operator.DigestCalculator;

public class JcaRespID
extends RespID {
    public JcaRespID(X500Principal name) {
        super(X500Name.getInstance((Object)name.getEncoded()));
    }

    public JcaRespID(PublicKey pubKey, DigestCalculator digCalc) throws OCSPException {
        super(SubjectPublicKeyInfo.getInstance((Object)pubKey.getEncoded()), digCalc);
    }
}

