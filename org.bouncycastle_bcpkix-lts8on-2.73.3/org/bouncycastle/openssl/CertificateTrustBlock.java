/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.ASN1UTF8String
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.DERUTF8String
 */
package org.bouncycastle.openssl;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.ASN1UTF8String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.DERUTF8String;

public class CertificateTrustBlock {
    private ASN1Sequence uses;
    private ASN1Sequence prohibitions;
    private String alias;

    public CertificateTrustBlock(Set<ASN1ObjectIdentifier> uses) {
        this(null, uses, null);
    }

    public CertificateTrustBlock(String alias, Set<ASN1ObjectIdentifier> uses) {
        this(alias, uses, null);
    }

    public CertificateTrustBlock(String alias, Set<ASN1ObjectIdentifier> uses, Set<ASN1ObjectIdentifier> prohibitions) {
        this.alias = alias;
        this.uses = this.toSequence(uses);
        this.prohibitions = this.toSequence(prohibitions);
    }

    CertificateTrustBlock(byte[] encoded) {
        ASN1Sequence seq = ASN1Sequence.getInstance((Object)encoded);
        Enumeration en = seq.getObjects();
        while (en.hasMoreElements()) {
            ASN1Encodable obj = (ASN1Encodable)en.nextElement();
            if (obj instanceof ASN1Sequence) {
                this.uses = ASN1Sequence.getInstance((Object)obj);
                continue;
            }
            if (obj instanceof ASN1TaggedObject) {
                this.prohibitions = ASN1Sequence.getInstance((ASN1TaggedObject)((ASN1TaggedObject)obj), (boolean)false);
                continue;
            }
            if (!(obj instanceof ASN1UTF8String)) continue;
            this.alias = ASN1UTF8String.getInstance((Object)obj).getString();
        }
    }

    public String getAlias() {
        return this.alias;
    }

    public Set<ASN1ObjectIdentifier> getUses() {
        return this.toSet(this.uses);
    }

    public Set<ASN1ObjectIdentifier> getProhibitions() {
        return this.toSet(this.prohibitions);
    }

    private Set<ASN1ObjectIdentifier> toSet(ASN1Sequence seq) {
        if (seq != null) {
            HashSet<ASN1ObjectIdentifier> oids = new HashSet<ASN1ObjectIdentifier>(seq.size());
            Enumeration en = seq.getObjects();
            while (en.hasMoreElements()) {
                oids.add(ASN1ObjectIdentifier.getInstance(en.nextElement()));
            }
            return oids;
        }
        return Collections.EMPTY_SET;
    }

    private ASN1Sequence toSequence(Set<ASN1ObjectIdentifier> oids) {
        if (oids == null || oids.isEmpty()) {
            return null;
        }
        ASN1EncodableVector v = new ASN1EncodableVector();
        Iterator<ASN1ObjectIdentifier> it = oids.iterator();
        while (it.hasNext()) {
            v.add((ASN1Encodable)it.next());
        }
        return new DERSequence(v);
    }

    ASN1Sequence toASN1Sequence() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        if (this.uses != null) {
            v.add((ASN1Encodable)this.uses);
        }
        if (this.prohibitions != null) {
            v.add((ASN1Encodable)new DERTaggedObject(false, 0, (ASN1Encodable)this.prohibitions));
        }
        if (this.alias != null) {
            v.add((ASN1Encodable)new DERUTF8String(this.alias));
        }
        return new DERSequence(v);
    }
}

