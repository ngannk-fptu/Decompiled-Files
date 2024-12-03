/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.cms.CMSObjectIdentifiers
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.cms.EnvelopedData
 *  org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers
 *  org.bouncycastle.asn1.crmf.EncryptedKey
 *  org.bouncycastle.asn1.crmf.PKIArchiveOptions
 */
package org.bouncycastle.cert.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.crmf.EncryptedKey;
import org.bouncycastle.asn1.crmf.PKIArchiveOptions;
import org.bouncycastle.cert.crmf.CRMFException;
import org.bouncycastle.cert.crmf.Control;
import org.bouncycastle.cms.CMSEnvelopedData;
import org.bouncycastle.cms.CMSException;

public class PKIArchiveControl
implements Control {
    public static final int encryptedPrivKey = 0;
    public static final int keyGenParameters = 1;
    public static final int archiveRemGenPrivKey = 2;
    private static final ASN1ObjectIdentifier type = CRMFObjectIdentifiers.id_regCtrl_pkiArchiveOptions;
    private final PKIArchiveOptions pkiArchiveOptions;

    public PKIArchiveControl(PKIArchiveOptions pkiArchiveOptions) {
        this.pkiArchiveOptions = pkiArchiveOptions;
    }

    @Override
    public ASN1ObjectIdentifier getType() {
        return type;
    }

    @Override
    public ASN1Encodable getValue() {
        return this.pkiArchiveOptions;
    }

    public int getArchiveType() {
        return this.pkiArchiveOptions.getType();
    }

    public boolean isEnvelopedData() {
        EncryptedKey encKey = EncryptedKey.getInstance((Object)this.pkiArchiveOptions.getValue());
        return !encKey.isEncryptedValue();
    }

    public CMSEnvelopedData getEnvelopedData() throws CRMFException {
        try {
            EncryptedKey encKey = EncryptedKey.getInstance((Object)this.pkiArchiveOptions.getValue());
            EnvelopedData data = EnvelopedData.getInstance((Object)encKey.getValue());
            return new CMSEnvelopedData(new ContentInfo(CMSObjectIdentifiers.envelopedData, (ASN1Encodable)data));
        }
        catch (CMSException e) {
            throw new CRMFException("CMS parsing error: " + e.getMessage(), e.getCause());
        }
        catch (Exception e) {
            throw new CRMFException("CRMF parsing error: " + e.getMessage(), e);
        }
    }
}

