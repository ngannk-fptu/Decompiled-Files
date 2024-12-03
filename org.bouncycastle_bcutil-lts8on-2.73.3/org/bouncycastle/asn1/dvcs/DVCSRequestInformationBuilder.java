/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.GeneralName
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.asn1.x509.PolicyInformation
 *  org.bouncycastle.util.BigIntegers
 */
package org.bouncycastle.asn1.dvcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.dvcs.DVCSRequestInformation;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;
import org.bouncycastle.util.BigIntegers;

public class DVCSRequestInformationBuilder {
    private int version = 1;
    private final ServiceType service;
    private DVCSRequestInformation initialInfo;
    private BigInteger nonce;
    private DVCSTime requestTime;
    private GeneralNames requester;
    private PolicyInformation requestPolicy;
    private GeneralNames dvcs;
    private GeneralNames dataLocations;
    private Extensions extensions;
    private static final int DEFAULT_VERSION = 1;
    private static final int TAG_REQUESTER = 0;
    private static final int TAG_REQUEST_POLICY = 1;
    private static final int TAG_DVCS = 2;
    private static final int TAG_DATA_LOCATIONS = 3;
    private static final int TAG_EXTENSIONS = 4;

    public DVCSRequestInformationBuilder(ServiceType service) {
        this.service = service;
    }

    public DVCSRequestInformationBuilder(DVCSRequestInformation initialInfo) {
        this.initialInfo = initialInfo;
        this.service = initialInfo.getService();
        this.version = initialInfo.getVersion();
        this.nonce = initialInfo.getNonce();
        this.requestTime = initialInfo.getRequestTime();
        this.requestPolicy = initialInfo.getRequestPolicy();
        this.dvcs = initialInfo.getDVCS();
        this.dataLocations = initialInfo.getDataLocations();
    }

    public DVCSRequestInformation build() {
        ASN1EncodableVector v = new ASN1EncodableVector(9);
        if (this.version != 1) {
            v.add((ASN1Encodable)new ASN1Integer((long)this.version));
        }
        v.add((ASN1Encodable)this.service);
        if (this.nonce != null) {
            v.add((ASN1Encodable)new ASN1Integer(this.nonce));
        }
        if (this.requestTime != null) {
            v.add((ASN1Encodable)this.requestTime);
        }
        int[] tags = new int[]{0, 1, 2, 3, 4};
        ASN1Encodable[] taggedObjects = new ASN1Encodable[]{this.requester, this.requestPolicy, this.dvcs, this.dataLocations, this.extensions};
        for (int i = 0; i < tags.length; ++i) {
            int tag = tags[i];
            ASN1Encodable taggedObject = taggedObjects[i];
            if (taggedObject == null) continue;
            v.add((ASN1Encodable)new DERTaggedObject(false, tag, taggedObject));
        }
        return DVCSRequestInformation.getInstance(new DERSequence(v));
    }

    public void setVersion(int version) {
        if (this.initialInfo != null) {
            throw new IllegalStateException("cannot change version in existing DVCSRequestInformation");
        }
        this.version = version;
    }

    public void setNonce(BigInteger nonce) {
        if (this.initialInfo != null) {
            if (this.initialInfo.getNonce() == null) {
                this.nonce = nonce;
            } else {
                byte[] initialBytes = this.initialInfo.getNonce().toByteArray();
                byte[] newBytes = BigIntegers.asUnsignedByteArray((BigInteger)nonce);
                byte[] nonceBytes = new byte[initialBytes.length + newBytes.length];
                System.arraycopy(initialBytes, 0, nonceBytes, 0, initialBytes.length);
                System.arraycopy(newBytes, 0, nonceBytes, initialBytes.length, newBytes.length);
                this.nonce = new BigInteger(nonceBytes);
            }
        }
        this.nonce = nonce;
    }

    public void setRequestTime(DVCSTime requestTime) {
        if (this.initialInfo != null) {
            throw new IllegalStateException("cannot change request time in existing DVCSRequestInformation");
        }
        this.requestTime = requestTime;
    }

    public void setRequester(GeneralName requester) {
        this.setRequester(new GeneralNames(requester));
    }

    public void setRequester(GeneralNames requester) {
        this.requester = requester;
    }

    public void setRequestPolicy(PolicyInformation requestPolicy) {
        if (this.initialInfo != null) {
            throw new IllegalStateException("cannot change request policy in existing DVCSRequestInformation");
        }
        this.requestPolicy = requestPolicy;
    }

    public void setDVCS(GeneralName dvcs) {
        this.setDVCS(new GeneralNames(dvcs));
    }

    public void setDVCS(GeneralNames dvcs) {
        this.dvcs = dvcs;
    }

    public void setDataLocations(GeneralName dataLocation) {
        this.setDataLocations(new GeneralNames(dataLocation));
    }

    public void setDataLocations(GeneralNames dataLocations) {
        this.dataLocations = dataLocations;
    }

    public void setExtensions(Extensions extensions) {
        if (this.initialInfo != null) {
            throw new IllegalStateException("cannot change extensions in existing DVCSRequestInformation");
        }
        this.extensions = extensions;
    }
}

