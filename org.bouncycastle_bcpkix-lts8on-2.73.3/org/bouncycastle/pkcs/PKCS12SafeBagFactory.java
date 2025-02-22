/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.pkcs.ContentInfo
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.SafeBag
 */
package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.pkcs.ContentInfo;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.SafeBag;
import org.bouncycastle.cms.CMSEncryptedData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.pkcs.PKCS12SafeBag;
import org.bouncycastle.pkcs.PKCSException;

public class PKCS12SafeBagFactory {
    private ASN1Sequence safeBagSeq;

    public PKCS12SafeBagFactory(ContentInfo info) {
        if (info.getContentType().equals((ASN1Primitive)PKCSObjectIdentifiers.encryptedData)) {
            throw new IllegalArgumentException("encryptedData requires constructor with decryptor.");
        }
        this.safeBagSeq = ASN1Sequence.getInstance((Object)ASN1OctetString.getInstance((Object)info.getContent()).getOctets());
    }

    public PKCS12SafeBagFactory(ContentInfo info, InputDecryptorProvider inputDecryptorProvider) throws PKCSException {
        if (info.getContentType().equals((ASN1Primitive)PKCSObjectIdentifiers.encryptedData)) {
            CMSEncryptedData encData = new CMSEncryptedData(org.bouncycastle.asn1.cms.ContentInfo.getInstance((Object)info));
            try {
                this.safeBagSeq = ASN1Sequence.getInstance((Object)encData.getContent(inputDecryptorProvider));
            }
            catch (CMSException e) {
                throw new PKCSException("unable to extract data: " + e.getMessage(), e);
            }
            return;
        }
        throw new IllegalArgumentException("encryptedData requires constructor with decryptor.");
    }

    public PKCS12SafeBag[] getSafeBags() {
        PKCS12SafeBag[] safeBags = new PKCS12SafeBag[this.safeBagSeq.size()];
        for (int i = 0; i != this.safeBagSeq.size(); ++i) {
            safeBags[i] = new PKCS12SafeBag(SafeBag.getInstance((Object)this.safeBagSeq.getObjectAt(i)));
        }
        return safeBags;
    }
}

