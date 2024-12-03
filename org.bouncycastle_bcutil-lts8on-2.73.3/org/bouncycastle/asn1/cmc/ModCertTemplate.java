/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Boolean
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.DERSequence
 */
package org.bouncycastle.asn1.cmc;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cmc.BodyPartList;
import org.bouncycastle.asn1.cmc.BodyPartPath;
import org.bouncycastle.asn1.crmf.CertTemplate;

public class ModCertTemplate
extends ASN1Object {
    private final BodyPartPath pkiDataReference;
    private final BodyPartList certReferences;
    private final boolean replace;
    private final CertTemplate certTemplate;

    public ModCertTemplate(BodyPartPath pkiDataReference, BodyPartList certReferences, boolean replace, CertTemplate certTemplate) {
        this.pkiDataReference = pkiDataReference;
        this.certReferences = certReferences;
        this.replace = replace;
        this.certTemplate = certTemplate;
    }

    private ModCertTemplate(ASN1Sequence seq) {
        if (seq.size() != 4 && seq.size() != 3) {
            throw new IllegalArgumentException("incorrect sequence size");
        }
        this.pkiDataReference = BodyPartPath.getInstance(seq.getObjectAt(0));
        this.certReferences = BodyPartList.getInstance(seq.getObjectAt(1));
        if (seq.size() == 4) {
            this.replace = ASN1Boolean.getInstance((Object)seq.getObjectAt(2)).isTrue();
            this.certTemplate = CertTemplate.getInstance(seq.getObjectAt(3));
        } else {
            this.replace = true;
            this.certTemplate = CertTemplate.getInstance(seq.getObjectAt(2));
        }
    }

    public static ModCertTemplate getInstance(Object o) {
        if (o instanceof ModCertTemplate) {
            return (ModCertTemplate)((Object)o);
        }
        if (o != null) {
            return new ModCertTemplate(ASN1Sequence.getInstance((Object)o));
        }
        return null;
    }

    public BodyPartPath getPkiDataReference() {
        return this.pkiDataReference;
    }

    public BodyPartList getCertReferences() {
        return this.certReferences;
    }

    public boolean isReplacingFields() {
        return this.replace;
    }

    public CertTemplate getCertTemplate() {
        return this.certTemplate;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector v = new ASN1EncodableVector(4);
        v.add((ASN1Encodable)this.pkiDataReference);
        v.add((ASN1Encodable)this.certReferences);
        if (!this.replace) {
            v.add((ASN1Encodable)ASN1Boolean.getInstance((boolean)this.replace));
        }
        v.add((ASN1Encodable)this.certTemplate);
        return new DERSequence(v);
    }
}

