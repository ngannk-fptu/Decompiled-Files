/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.oer.its.ieee1609dot2.PsidGroupPermissions
 *  org.bouncycastle.oer.its.ieee1609dot2.SequenceOfPsidGroupPermissions
 *  org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate$Builder
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.CrlSeries
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.PsidSsp
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSsp
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSsp$Builder
 *  org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8
 */
package org.bouncycastle.its;

import org.bouncycastle.its.ITSCertificate;
import org.bouncycastle.its.ITSValidityPeriod;
import org.bouncycastle.oer.its.ieee1609dot2.PsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.SequenceOfPsidGroupPermissions;
import org.bouncycastle.oer.its.ieee1609dot2.ToBeSignedCertificate;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.CrlSeries;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.HashedId3;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.PsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.SequenceOfPsidSsp;
import org.bouncycastle.oer.its.ieee1609dot2.basetypes.UINT8;

public class ITSCertificateBuilder {
    protected final ToBeSignedCertificate.Builder tbsCertificateBuilder;
    protected final ITSCertificate issuer;
    protected UINT8 version = new UINT8(3);
    protected HashedId3 cracaId = new HashedId3(new byte[3]);
    protected CrlSeries crlSeries = new CrlSeries(0);

    public ITSCertificateBuilder(ToBeSignedCertificate.Builder tbsCertificateBuilder) {
        this(null, tbsCertificateBuilder);
    }

    public ITSCertificateBuilder(ITSCertificate issuer, ToBeSignedCertificate.Builder tbsCertificateBuilder) {
        this.issuer = issuer;
        this.tbsCertificateBuilder = tbsCertificateBuilder;
        this.tbsCertificateBuilder.setCracaId(this.cracaId);
        this.tbsCertificateBuilder.setCrlSeries(this.crlSeries);
    }

    public ITSCertificate getIssuer() {
        return this.issuer;
    }

    public ITSCertificateBuilder setVersion(int version) {
        this.version = new UINT8(version);
        return this;
    }

    public ITSCertificateBuilder setCracaId(byte[] cracaId) {
        this.cracaId = new HashedId3(cracaId);
        this.tbsCertificateBuilder.setCracaId(this.cracaId);
        return this;
    }

    public ITSCertificateBuilder setCrlSeries(int crlSeries) {
        this.crlSeries = new CrlSeries(crlSeries);
        this.tbsCertificateBuilder.setCrlSeries(this.crlSeries);
        return this;
    }

    public ITSCertificateBuilder setValidityPeriod(ITSValidityPeriod validityPeriod) {
        this.tbsCertificateBuilder.setValidityPeriod(validityPeriod.toASN1Structure());
        return this;
    }

    public ITSCertificateBuilder setCertIssuePermissions(PsidGroupPermissions ... permissions) {
        this.tbsCertificateBuilder.setCertIssuePermissions(SequenceOfPsidGroupPermissions.builder().addGroupPermission(permissions).createSequenceOfPsidGroupPermissions());
        return this;
    }

    public ITSCertificateBuilder setAppPermissions(PsidSsp ... psidSsps) {
        SequenceOfPsidSsp.Builder bldr = SequenceOfPsidSsp.builder();
        for (int i = 0; i != psidSsps.length; ++i) {
            bldr.setItem(new PsidSsp[]{psidSsps[i]});
        }
        this.tbsCertificateBuilder.setAppPermissions(bldr.createSequenceOfPsidSsp());
        return this;
    }
}

