/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.SignedData
 *  org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers
 *  org.bouncycastle.asn1.dvcs.DVCSRequest
 *  org.bouncycastle.asn1.dvcs.ServiceType
 *  org.bouncycastle.asn1.x509.GeneralName
 */
package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.dvcs.CCPDRequestData;
import org.bouncycastle.dvcs.CPDRequestData;
import org.bouncycastle.dvcs.DVCSConstructionException;
import org.bouncycastle.dvcs.DVCSMessage;
import org.bouncycastle.dvcs.DVCSRequestData;
import org.bouncycastle.dvcs.DVCSRequestInfo;
import org.bouncycastle.dvcs.VPKCRequestData;
import org.bouncycastle.dvcs.VSDRequestData;

public class DVCSRequest
extends DVCSMessage {
    private org.bouncycastle.asn1.dvcs.DVCSRequest asn1;
    private DVCSRequestInfo reqInfo;
    private DVCSRequestData data;

    public DVCSRequest(CMSSignedData signedData) throws DVCSConstructionException {
        this(SignedData.getInstance((Object)signedData.toASN1Structure().getContent()).getEncapContentInfo());
    }

    public DVCSRequest(ContentInfo contentInfo) throws DVCSConstructionException {
        super(contentInfo);
        if (!DVCSObjectIdentifiers.id_ct_DVCSRequestData.equals((ASN1Primitive)contentInfo.getContentType())) {
            throw new DVCSConstructionException("ContentInfo not a DVCS Request");
        }
        try {
            this.asn1 = contentInfo.getContent().toASN1Primitive() instanceof ASN1Sequence ? org.bouncycastle.asn1.dvcs.DVCSRequest.getInstance((Object)contentInfo.getContent()) : org.bouncycastle.asn1.dvcs.DVCSRequest.getInstance((Object)ASN1OctetString.getInstance((Object)contentInfo.getContent()).getOctets());
        }
        catch (Exception e) {
            throw new DVCSConstructionException("Unable to parse content: " + e.getMessage(), e);
        }
        this.reqInfo = new DVCSRequestInfo(this.asn1.getRequestInformation());
        int service = this.reqInfo.getServiceType();
        if (service == ServiceType.CPD.getValue().intValue()) {
            this.data = new CPDRequestData(this.asn1.getData());
        } else if (service == ServiceType.VSD.getValue().intValue()) {
            this.data = new VSDRequestData(this.asn1.getData());
        } else if (service == ServiceType.VPKC.getValue().intValue()) {
            this.data = new VPKCRequestData(this.asn1.getData());
        } else if (service == ServiceType.CCPD.getValue().intValue()) {
            this.data = new CCPDRequestData(this.asn1.getData());
        } else {
            throw new DVCSConstructionException("Unknown service type: " + service);
        }
    }

    @Override
    public ASN1Encodable getContent() {
        return this.asn1;
    }

    public DVCSRequestInfo getRequestInfo() {
        return this.reqInfo;
    }

    public DVCSRequestData getData() {
        return this.data;
    }

    public GeneralName getTransactionIdentifier() {
        return this.asn1.getTransactionIdentifier();
    }
}

