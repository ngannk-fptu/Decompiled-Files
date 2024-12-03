/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.pkcs;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class PBMAC1Params
extends ASN1Object
implements PKCSObjectIdentifiers {
    private AlgorithmIdentifier func;
    private AlgorithmIdentifier scheme;

    public static PBMAC1Params getInstance(Object obj) {
        if (obj instanceof PBMAC1Params) {
            return (PBMAC1Params)obj;
        }
        if (obj != null) {
            return new PBMAC1Params(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    public PBMAC1Params(AlgorithmIdentifier keyDevFunc, AlgorithmIdentifier encScheme) {
        this.func = keyDevFunc;
        this.scheme = encScheme;
    }

    private PBMAC1Params(ASN1Sequence obj) {
        Enumeration e = obj.getObjects();
        ASN1Sequence funcSeq = ASN1Sequence.getInstance(((ASN1Encodable)e.nextElement()).toASN1Primitive());
        this.func = funcSeq.getObjectAt(0).equals(id_PBKDF2) ? new AlgorithmIdentifier(id_PBKDF2, PBKDF2Params.getInstance(funcSeq.getObjectAt(1))) : AlgorithmIdentifier.getInstance(funcSeq);
        this.scheme = AlgorithmIdentifier.getInstance(e.nextElement());
    }

    public AlgorithmIdentifier getKeyDerivationFunc() {
        return this.func;
    }

    public AlgorithmIdentifier getMessageAuthScheme() {
        return this.scheme;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(2);
        v.add(this.func);
        v.add(this.scheme);
        return new DERSequence(v);
    }
}

