/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder
 *  org.bouncycastle.asn1.dvcs.Data
 *  org.bouncycastle.asn1.dvcs.ServiceType
 */
package org.bouncycastle.dvcs;

import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.dvcs.DVCSException;
import org.bouncycastle.dvcs.DVCSRequest;
import org.bouncycastle.dvcs.DVCSRequestBuilder;

public class CPDRequestBuilder
extends DVCSRequestBuilder {
    public CPDRequestBuilder() {
        super(new DVCSRequestInformationBuilder(ServiceType.CPD));
    }

    public DVCSRequest build(byte[] messageBytes) throws DVCSException {
        Data data = new Data(messageBytes);
        return this.createDVCRequest(data);
    }
}

