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
 *  org.bouncycastle.asn1.dvcs.DVCSResponse
 */
package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.dvcs.DVCSConstructionException;
import org.bouncycastle.dvcs.DVCSMessage;

public class DVCSResponse
extends DVCSMessage {
    private org.bouncycastle.asn1.dvcs.DVCSResponse asn1;

    public DVCSResponse(CMSSignedData signedData) throws DVCSConstructionException {
        this(SignedData.getInstance((Object)signedData.toASN1Structure().getContent()).getEncapContentInfo());
    }

    public DVCSResponse(ContentInfo contentInfo) throws DVCSConstructionException {
        super(contentInfo);
        if (!DVCSObjectIdentifiers.id_ct_DVCSResponseData.equals((ASN1Primitive)contentInfo.getContentType())) {
            throw new DVCSConstructionException("ContentInfo not a DVCS Response");
        }
        try {
            this.asn1 = contentInfo.getContent().toASN1Primitive() instanceof ASN1Sequence ? org.bouncycastle.asn1.dvcs.DVCSResponse.getInstance((Object)contentInfo.getContent()) : org.bouncycastle.asn1.dvcs.DVCSResponse.getInstance((Object)ASN1OctetString.getInstance((Object)contentInfo.getContent()).getOctets());
        }
        catch (Exception e) {
            throw new DVCSConstructionException("Unable to parse content: " + e.getMessage(), e);
        }
    }

    @Override
    public ASN1Encodable getContent() {
        return this.asn1;
    }
}

