/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.dvcs.CertEtcToken
 *  org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder
 *  org.bouncycastle.asn1.dvcs.DVCSTime
 *  org.bouncycastle.asn1.dvcs.Data
 *  org.bouncycastle.asn1.dvcs.ServiceType
 *  org.bouncycastle.asn1.dvcs.TargetEtcChain
 *  org.bouncycastle.asn1.x509.Extension
 */
package org.bouncycastle.dvcs;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.dvcs.CertEtcToken;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.asn1.dvcs.TargetEtcChain;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.dvcs.DVCSException;
import org.bouncycastle.dvcs.DVCSRequest;
import org.bouncycastle.dvcs.DVCSRequestBuilder;
import org.bouncycastle.dvcs.TargetChain;

public class VPKCRequestBuilder
extends DVCSRequestBuilder {
    private List chains = new ArrayList();

    public VPKCRequestBuilder() {
        super(new DVCSRequestInformationBuilder(ServiceType.VPKC));
    }

    public void addTargetChain(X509CertificateHolder cert) {
        this.chains.add(new TargetEtcChain(new CertEtcToken(0, (ASN1Encodable)cert.toASN1Structure())));
    }

    public void addTargetChain(Extension extension) {
        this.chains.add(new TargetEtcChain(new CertEtcToken(extension)));
    }

    public void addTargetChain(TargetChain targetChain) {
        this.chains.add(targetChain.toASN1Structure());
    }

    public void setRequestTime(Date requestTime) {
        this.requestInformationBuilder.setRequestTime(new DVCSTime(requestTime));
    }

    public DVCSRequest build() throws DVCSException {
        Data data = new Data(this.chains.toArray(new TargetEtcChain[this.chains.size()]));
        return this.createDVCRequest(data);
    }
}

