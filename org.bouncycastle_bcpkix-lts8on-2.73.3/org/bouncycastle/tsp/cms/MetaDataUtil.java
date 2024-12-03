/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1String
 *  org.bouncycastle.asn1.cms.Attributes
 *  org.bouncycastle.asn1.cms.MetaData
 */
package org.bouncycastle.tsp.cms;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.cms.Attributes;
import org.bouncycastle.asn1.cms.MetaData;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.DigestCalculator;

class MetaDataUtil {
    private final MetaData metaData;

    MetaDataUtil(MetaData metaData) {
        this.metaData = metaData;
    }

    void initialiseMessageImprintDigestCalculator(DigestCalculator calculator) throws CMSException {
        if (this.metaData != null && this.metaData.isHashProtected()) {
            try {
                calculator.getOutputStream().write(this.metaData.getEncoded("DER"));
            }
            catch (IOException e) {
                throw new CMSException("unable to initialise calculator from metaData: " + e.getMessage(), e);
            }
        }
    }

    String getFileName() {
        if (this.metaData != null) {
            return this.convertString((ASN1String)this.metaData.getFileNameUTF8());
        }
        return null;
    }

    String getMediaType() {
        if (this.metaData != null) {
            return this.convertString((ASN1String)this.metaData.getMediaTypeIA5());
        }
        return null;
    }

    Attributes getOtherMetaData() {
        if (this.metaData != null) {
            return this.metaData.getOtherMetaData();
        }
        return null;
    }

    private String convertString(ASN1String s) {
        if (s != null) {
            return s.toString();
        }
        return null;
    }
}

