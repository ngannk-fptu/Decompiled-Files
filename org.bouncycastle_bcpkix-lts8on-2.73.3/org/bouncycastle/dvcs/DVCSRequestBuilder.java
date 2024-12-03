/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.cms.ContentInfo
 *  org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers
 *  org.bouncycastle.asn1.dvcs.DVCSRequest
 *  org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder
 *  org.bouncycastle.asn1.dvcs.Data
 *  org.bouncycastle.asn1.x509.ExtensionsGenerator
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.GeneralNames
 */
package org.bouncycastle.dvcs;

import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.dvcs.DVCSObjectIdentifiers;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformationBuilder;
import org.bouncycastle.asn1.dvcs.Data;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.dvcs.DVCSException;
import org.bouncycastle.dvcs.DVCSRequest;

public abstract class DVCSRequestBuilder {
    private final ExtensionsGenerator extGenerator = new ExtensionsGenerator();
    private final CMSSignedDataGenerator signedDataGen = new CMSSignedDataGenerator();
    protected final DVCSRequestInformationBuilder requestInformationBuilder;

    protected DVCSRequestBuilder(DVCSRequestInformationBuilder requestInformationBuilder) {
        this.requestInformationBuilder = requestInformationBuilder;
    }

    public void setNonce(BigInteger nonce) {
        this.requestInformationBuilder.setNonce(nonce);
    }

    public void setRequester(GeneralName requester) {
        this.requestInformationBuilder.setRequester(requester);
    }

    public void setDVCS(GeneralName dvcs) {
        this.requestInformationBuilder.setDVCS(dvcs);
    }

    public void setDVCS(GeneralNames dvcs) {
        this.requestInformationBuilder.setDVCS(dvcs);
    }

    public void setDataLocations(GeneralName dataLocation) {
        this.requestInformationBuilder.setDataLocations(dataLocation);
    }

    public void setDataLocations(GeneralNames dataLocations) {
        this.requestInformationBuilder.setDataLocations(dataLocations);
    }

    public void addExtension(ASN1ObjectIdentifier oid, boolean isCritical, ASN1Encodable value) throws DVCSException {
        try {
            this.extGenerator.addExtension(oid, isCritical, value);
        }
        catch (IOException e) {
            throw new DVCSException("cannot encode extension: " + e.getMessage(), e);
        }
    }

    protected DVCSRequest createDVCRequest(Data data) throws DVCSException {
        if (!this.extGenerator.isEmpty()) {
            this.requestInformationBuilder.setExtensions(this.extGenerator.generate());
        }
        org.bouncycastle.asn1.dvcs.DVCSRequest request = new org.bouncycastle.asn1.dvcs.DVCSRequest(this.requestInformationBuilder.build(), data);
        return new DVCSRequest(new ContentInfo(DVCSObjectIdentifiers.id_ct_DVCSRequestData, (ASN1Encodable)request));
    }
}

