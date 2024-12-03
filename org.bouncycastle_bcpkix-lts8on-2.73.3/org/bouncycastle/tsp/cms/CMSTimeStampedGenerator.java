/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERIA5String
 *  org.bouncycastle.asn1.DERUTF8String
 *  org.bouncycastle.asn1.cms.Attributes
 *  org.bouncycastle.asn1.cms.MetaData
 */
package org.bouncycastle.tsp.cms;

import java.net.URI;
import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.MetaData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.cms.MetaDataUtil;

public class CMSTimeStampedGenerator {
    protected MetaData metaData;
    protected URI dataUri;

    public void setDataUri(URI dataUri) {
        this.dataUri = dataUri;
    }

    public void setMetaData(boolean hashProtected, String fileName, String mediaType) {
        this.setMetaData(hashProtected, fileName, mediaType, null);
    }

    public void setMetaData(boolean hashProtected, String fileName, String mediaType, Attributes attributes) {
        DERUTF8String asn1FileName = null;
        if (fileName != null) {
            asn1FileName = new DERUTF8String(fileName);
        }
        DERIA5String asn1MediaType = null;
        if (mediaType != null) {
            asn1MediaType = new DERIA5String(mediaType);
        }
        this.setMetaData(hashProtected, (ASN1UTF8String)asn1FileName, (ASN1IA5String)asn1MediaType, attributes);
    }

    private void setMetaData(boolean hashProtected, ASN1UTF8String fileName, ASN1IA5String mediaType, Attributes attributes) {
        this.metaData = new MetaData(ASN1Boolean.getInstance((boolean)hashProtected), fileName, mediaType, attributes);
    }

    public void initialiseMessageImprintDigestCalculator(DigestCalculator calculator) throws CMSException {
        MetaDataUtil util = new MetaDataUtil(this.metaData);
        util.initialiseMessageImprintDigestCalculator(calculator);
    }
}

