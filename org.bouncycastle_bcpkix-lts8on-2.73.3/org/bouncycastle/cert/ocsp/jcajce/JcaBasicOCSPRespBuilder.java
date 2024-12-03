/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
 */
package org.bouncycastle.cert.ocsp.jcajce;

import java.security.PublicKey;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.ocsp.BasicOCSPRespBuilder;
import org.bouncycastle.cert.ocsp.OCSPException;
import org.bouncycastle.cert.ocsp.jcajce.JcaRespID;
import org.bouncycastle.operator.DigestCalculator;

public class JcaBasicOCSPRespBuilder
extends BasicOCSPRespBuilder {
    public JcaBasicOCSPRespBuilder(X500Principal principal) {
        super(new JcaRespID(principal));
    }

    public JcaBasicOCSPRespBuilder(PublicKey key, DigestCalculator digCalc) throws OCSPException {
        super(SubjectPublicKeyInfo.getInstance((Object)key.getEncoded()), digCalc);
    }
}

