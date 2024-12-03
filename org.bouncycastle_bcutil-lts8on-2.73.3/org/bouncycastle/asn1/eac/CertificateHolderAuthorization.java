/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1OctetString
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.util.Integers
 */
package org.bouncycastle.asn1.eac;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.eac.BidirectionalMap;
import org.bouncycastle.asn1.eac.EACObjectIdentifiers;
import org.bouncycastle.asn1.eac.EACTagged;
import org.bouncycastle.util.Integers;

public class CertificateHolderAuthorization
extends ASN1Object {
    public static final ASN1ObjectIdentifier id_role_EAC = EACObjectIdentifiers.bsi_de.branch("3.1.2.1");
    public static final int CVCA = 192;
    public static final int DV_DOMESTIC = 128;
    public static final int DV_FOREIGN = 64;
    public static final int IS = 0;
    public static final int RADG4 = 2;
    public static final int RADG3 = 1;
    static Map RightsDecodeMap = new HashMap();
    static BidirectionalMap AuthorizationRole = new BidirectionalMap();
    private ASN1ObjectIdentifier oid;
    private byte accessRights;

    public static String getRoleDescription(int i) {
        return (String)AuthorizationRole.get(Integers.valueOf((int)i));
    }

    public static int getFlag(String description) {
        Integer i = (Integer)AuthorizationRole.getReverse(description);
        if (i == null) {
            throw new IllegalArgumentException("Unknown value " + description);
        }
        return i;
    }

    private void setPrivateData(ASN1Sequence seq) {
        ASN1Primitive obj = (ASN1Primitive)seq.getObjectAt(0);
        if (!(obj instanceof ASN1ObjectIdentifier)) {
            throw new IllegalArgumentException("no Oid in CerticateHolderAuthorization");
        }
        this.oid = (ASN1ObjectIdentifier)obj;
        obj = (ASN1Primitive)seq.getObjectAt(1);
        if (!(obj instanceof ASN1TaggedObject)) {
            throw new IllegalArgumentException("No access rights in CerticateHolderAuthorization");
        }
        ASN1TaggedObject tObj = ASN1TaggedObject.getInstance((Object)obj, (int)64, (int)19);
        this.accessRights = ASN1OctetString.getInstance((Object)tObj.getBaseUniversal(false, 4)).getOctets()[0];
    }

    public CertificateHolderAuthorization(ASN1ObjectIdentifier oid, int rights) throws IOException {
        this.setOid(oid);
        this.setAccessRights((byte)rights);
    }

    public CertificateHolderAuthorization(ASN1TaggedObject aSpe) throws IOException {
        if (!aSpe.hasTag(64, 76)) {
            throw new IllegalArgumentException("Unrecognized object in CerticateHolderAuthorization");
        }
        this.setPrivateData(ASN1Sequence.getInstance((Object)aSpe.getBaseUniversal(false, 16)));
    }

    public int getAccessRights() {
        return this.accessRights & 0xFF;
    }

    private void setAccessRights(byte rights) {
        this.accessRights = rights;
    }

    public ASN1ObjectIdentifier getOid() {
        return this.oid;
    }

    private void setOid(ASN1ObjectIdentifier oid) {
        this.oid = oid;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add((ASN1Encodable)this.oid);
        v.add((ASN1Encodable)EACTagged.create(19, new byte[]{this.accessRights}));
        return EACTagged.create(76, (ASN1Sequence)new DERSequence(v));
    }

    static {
        RightsDecodeMap.put(Integers.valueOf((int)2), "RADG4");
        RightsDecodeMap.put(Integers.valueOf((int)1), "RADG3");
        AuthorizationRole.put(Integers.valueOf((int)192), "CVCA");
        AuthorizationRole.put(Integers.valueOf((int)128), "DV_DOMESTIC");
        AuthorizationRole.put(Integers.valueOf((int)64), "DV_FOREIGN");
        AuthorizationRole.put(Integers.valueOf((int)0), "IS");
    }
}

