/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.cms.OriginatorInfo
 *  org.bouncycastle.util.Store
 */
package org.bouncycastle.cms;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.OriginatorInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSUtils;
import org.bouncycastle.cms.OriginatorInformation;
import org.bouncycastle.util.Store;

public class OriginatorInfoGenerator {
    private final List origCerts;
    private final List origCRLs;

    public OriginatorInfoGenerator(X509CertificateHolder origCert) {
        this.origCerts = new ArrayList(1);
        this.origCRLs = null;
        this.origCerts.add(origCert.toASN1Structure());
    }

    public OriginatorInfoGenerator(Store origCerts) throws CMSException {
        this(origCerts, null);
    }

    public OriginatorInfoGenerator(Store origCerts, Store origCRLs) throws CMSException {
        this.origCerts = origCerts != null ? CMSUtils.getCertificatesFromStore(origCerts) : null;
        this.origCRLs = origCRLs != null ? CMSUtils.getCRLsFromStore(origCRLs) : null;
    }

    public OriginatorInformation generate() {
        ASN1Set certSet = this.origCerts == null ? null : CMSUtils.createDerSetFromList(this.origCerts);
        ASN1Set crlSet = this.origCRLs == null ? null : CMSUtils.createDerSetFromList(this.origCRLs);
        return new OriginatorInformation(new OriginatorInfo(certSet, crlSet));
    }
}

