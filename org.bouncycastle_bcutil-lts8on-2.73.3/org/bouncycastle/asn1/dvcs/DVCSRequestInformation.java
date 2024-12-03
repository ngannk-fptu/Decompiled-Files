/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1GeneralizedTime
 *  org.bouncycastle.asn1.ASN1Integer
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x509.Extensions
 *  org.bouncycastle.asn1.x509.GeneralNames
 *  org.bouncycastle.asn1.x509.PolicyInformation
 */
package org.bouncycastle.asn1.dvcs;

import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.dvcs.DVCSTime;
import org.bouncycastle.asn1.dvcs.ServiceType;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class DVCSRequestInformation
extends ASN1Object {
    private int version = 1;
    private ServiceType service;
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

    private DVCSRequestInformation(ASN1Sequence seq) {
        int i = 0;
        if (seq.getObjectAt(0) instanceof ASN1Integer) {
            ASN1Integer encVersion = ASN1Integer.getInstance((Object)seq.getObjectAt(i++));
            this.version = encVersion.intValueExact();
        } else {
            this.version = 1;
        }
        this.service = ServiceType.getInstance(seq.getObjectAt(i++));
        while (i < seq.size()) {
            ASN1Encodable x = seq.getObjectAt(i);
            if (x instanceof ASN1Integer) {
                this.nonce = ASN1Integer.getInstance((Object)x).getValue();
            } else if (x instanceof ASN1GeneralizedTime) {
                this.requestTime = DVCSTime.getInstance(x);
            } else if (x instanceof ASN1TaggedObject) {
                ASN1TaggedObject t = ASN1TaggedObject.getInstance((Object)x);
                int tagNo = t.getTagNo();
                switch (tagNo) {
                    case 0: {
                        this.requester = GeneralNames.getInstance((ASN1TaggedObject)t, (boolean)false);
                        break;
                    }
                    case 1: {
                        this.requestPolicy = PolicyInformation.getInstance((Object)ASN1Sequence.getInstance((ASN1TaggedObject)t, (boolean)false));
                        break;
                    }
                    case 2: {
                        this.dvcs = GeneralNames.getInstance((ASN1TaggedObject)t, (boolean)false);
                        break;
                    }
                    case 3: {
                        this.dataLocations = GeneralNames.getInstance((ASN1TaggedObject)t, (boolean)false);
                        break;
                    }
                    case 4: {
                        this.extensions = Extensions.getInstance((ASN1TaggedObject)t, (boolean)false);
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("unknown tag number encountered: " + tagNo);
                    }
                }
            } else {
                this.requestTime = DVCSTime.getInstance(x);
            }
            ++i;
        }
    }

    public static DVCSRequestInformation getInstance(Object obj) {
        if (obj instanceof DVCSRequestInformation) {
            return (DVCSRequestInformation)((Object)obj);
        }
        if (obj != null) {
            return new DVCSRequestInformation(ASN1Sequence.getInstance((Object)obj));
        }
        return null;
    }

    public static DVCSRequestInformation getInstance(ASN1TaggedObject obj, boolean explicit) {
        return DVCSRequestInformation.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    public ASN1Primitive toASN1Primitive() {
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
        return new DERSequence(v);
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("DVCSRequestInformation {\n");
        if (this.version != 1) {
            s.append("version: " + this.version + "\n");
        }
        s.append("service: " + (Object)((Object)this.service) + "\n");
        if (this.nonce != null) {
            s.append("nonce: " + this.nonce + "\n");
        }
        if (this.requestTime != null) {
            s.append("requestTime: " + (Object)((Object)this.requestTime) + "\n");
        }
        if (this.requester != null) {
            s.append("requester: " + this.requester + "\n");
        }
        if (this.requestPolicy != null) {
            s.append("requestPolicy: " + this.requestPolicy + "\n");
        }
        if (this.dvcs != null) {
            s.append("dvcs: " + this.dvcs + "\n");
        }
        if (this.dataLocations != null) {
            s.append("dataLocations: " + this.dataLocations + "\n");
        }
        if (this.extensions != null) {
            s.append("extensions: " + this.extensions + "\n");
        }
        s.append("}\n");
        return s.toString();
    }

    public int getVersion() {
        return this.version;
    }

    public ServiceType getService() {
        return this.service;
    }

    public BigInteger getNonce() {
        return this.nonce;
    }

    public DVCSTime getRequestTime() {
        return this.requestTime;
    }

    public GeneralNames getRequester() {
        return this.requester;
    }

    public PolicyInformation getRequestPolicy() {
        return this.requestPolicy;
    }

    public GeneralNames getDVCS() {
        return this.dvcs;
    }

    public GeneralNames getDataLocations() {
        return this.dataLocations;
    }

    public Extensions getExtensions() {
        return this.extensions;
    }
}

