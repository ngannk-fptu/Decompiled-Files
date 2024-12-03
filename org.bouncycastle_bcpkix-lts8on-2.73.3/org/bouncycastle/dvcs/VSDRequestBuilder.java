/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder
 *  org.bouncycastle.asn1.dvcs.DVCSTime
 *  org.bouncycastle.asn1.dvcs.Data
 *  org.bouncycastle.asn1.dvcs.ServiceType
 */
package org.bouncycastle.dvcs;

import java.io.IOException;
import java.util.Date;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.dvcs.DVCSException;
import org.bouncycastle.dvcs.DVCSRequest;
import org.bouncycastle.dvcs.DVCSRequestBuilder;

public class VSDRequestBuilder
extends DVCSRequestBuilder {
    public VSDRequestBuilder() {
        super(new DVCSRequestInformationBuilder(ServiceType.VSD));
    }

    public void setRequestTime(Date requestTime) {
        this.requestInformationBuilder.setRequestTime(new DVCSTime(requestTime));
    }

    public DVCSRequest build(CMSSignedData document) throws DVCSException {
        try {
            Data data = new Data(document.getEncoded());
            return this.createDVCRequest(data);
        }
        catch (IOException e) {
            throw new DVCSException("Failed to encode CMS signed data", e);
        }
    }
}

