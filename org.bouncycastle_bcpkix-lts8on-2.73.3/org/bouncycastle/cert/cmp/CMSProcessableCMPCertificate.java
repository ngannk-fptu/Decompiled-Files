/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.cmp.CMPCertificate
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 */
package org.bouncycastle.cert.cmp;

import java.io.IOException;
import java.io.OutputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cmp.CMPCertificate;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSTypedData;

public class CMSProcessableCMPCertificate
implements CMSTypedData {
    private final CMPCertificate cmpCert;

    public CMSProcessableCMPCertificate(X509CertificateHolder certificateHolder) {
        this(new CMPCertificate(certificateHolder.toASN1Structure()));
    }

    public CMSProcessableCMPCertificate(CMPCertificate cmpCertificate) {
        this.cmpCert = cmpCertificate;
    }

    @Override
    public void write(OutputStream out) throws IOException, CMSException {
        out.write(this.cmpCert.getEncoded());
    }

    @Override
    public Object getContent() {
        return this.cmpCert;
    }

    @Override
    public ASN1ObjectIdentifier getContentType() {
        return PKCSObjectIdentifiers.data;
    }
}

