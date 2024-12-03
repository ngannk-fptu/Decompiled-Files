/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.cms;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAttributeTableGenerator;
import org.bouncycastle.cms.CMSEnvelopedGenerator;
import org.bouncycastle.util.Arrays;

public class CMSAuthenticatedGenerator
extends CMSEnvelopedGenerator {
    protected CMSAttributeTableGenerator authGen;
    protected CMSAttributeTableGenerator unauthGen;

    public void setAuthenticatedAttributeGenerator(CMSAttributeTableGenerator authGen) {
        this.authGen = authGen;
    }

    public void setUnauthenticatedAttributeGenerator(CMSAttributeTableGenerator unauthGen) {
        this.unauthGen = unauthGen;
    }

    protected Map getBaseParameters(ASN1ObjectIdentifier contentType, AlgorithmIdentifier digAlgId, AlgorithmIdentifier macAlgId, byte[] hash) {
        HashMap<String, Object> param = new HashMap<String, Object>();
        param.put("contentType", contentType);
        param.put("digestAlgID", digAlgId);
        param.put("digest", Arrays.clone((byte[])hash));
        param.put("macAlgID", macAlgId);
        return param;
    }
}

