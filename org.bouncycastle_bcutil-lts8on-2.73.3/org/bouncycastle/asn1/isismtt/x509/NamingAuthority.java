/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1IA5String
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1String
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DERIA5String
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.x500.DirectoryString
 */
package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.isismtt.ISISMTTObjectIdentifiers;
import org.bouncycastle.asn1.x500.DirectoryString;

public class NamingAuthority
extends ASN1Object {
    public static final ASN1ObjectIdentifier id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern = new ASN1ObjectIdentifier(ISISMTTObjectIdentifiers.id_isismtt_at_namingAuthorities + ".1");
    private ASN1ObjectIdentifier namingAuthorityId;
    private String namingAuthorityUrl;
    private DirectoryString namingAuthorityText;

    public static NamingAuthority getInstance(Object obj) {
        if (obj == null || obj instanceof NamingAuthority) {
            return (NamingAuthority)((Object)obj);
        }
        if (obj instanceof ASN1Sequence) {
            return new NamingAuthority((ASN1Sequence)obj);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    public static NamingAuthority getInstance(ASN1TaggedObject obj, boolean explicit) {
        return NamingAuthority.getInstance(ASN1Sequence.getInstance((ASN1TaggedObject)obj, (boolean)explicit));
    }

    private NamingAuthority(ASN1Sequence seq) {
        ASN1Encodable o;
        if (seq.size() > 3) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        if (e.hasMoreElements()) {
            o = (ASN1Encodable)e.nextElement();
            if (o instanceof ASN1ObjectIdentifier) {
                this.namingAuthorityId = (ASN1ObjectIdentifier)o;
            } else if (o instanceof ASN1IA5String) {
                this.namingAuthorityUrl = ASN1IA5String.getInstance((Object)o).getString();
            } else if (o instanceof ASN1String) {
                this.namingAuthorityText = DirectoryString.getInstance((Object)o);
            } else {
                throw new IllegalArgumentException("Bad object encountered: " + o.getClass());
            }
        }
        if (e.hasMoreElements()) {
            o = (ASN1Encodable)e.nextElement();
            if (o instanceof ASN1IA5String) {
                this.namingAuthorityUrl = ASN1IA5String.getInstance((Object)o).getString();
            } else if (o instanceof ASN1String) {
                this.namingAuthorityText = DirectoryString.getInstance((Object)o);
            } else {
                throw new IllegalArgumentException("Bad object encountered: " + o.getClass());
            }
        }
        if (e.hasMoreElements()) {
            o = (ASN1Encodable)e.nextElement();
            if (o instanceof ASN1String) {
                this.namingAuthorityText = DirectoryString.getInstance((Object)o);
            } else {
                throw new IllegalArgumentException("Bad object encountered: " + o.getClass());
            }
        }
    }

    public ASN1ObjectIdentifier getNamingAuthorityId() {
        return this.namingAuthorityId;
    }

    public DirectoryString getNamingAuthorityText() {
        return this.namingAuthorityText;
    }

    public String getNamingAuthorityUrl() {
        return this.namingAuthorityUrl;
    }

    public NamingAuthority(ASN1ObjectIdentifier namingAuthorityId, String namingAuthorityUrl, DirectoryString namingAuthorityText) {
        this.namingAuthorityId = namingAuthorityId;
        this.namingAuthorityUrl = namingAuthorityUrl;
        this.namingAuthorityText = namingAuthorityText;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vec = new ASN1EncodableVector(3);
        if (this.namingAuthorityId != null) {
            vec.add((ASN1Encodable)this.namingAuthorityId);
        }
        if (this.namingAuthorityUrl != null) {
            vec.add((ASN1Encodable)new DERIA5String(this.namingAuthorityUrl, true));
        }
        if (this.namingAuthorityText != null) {
            vec.add((ASN1Encodable)this.namingAuthorityText);
        }
        return new DERSequence(vec);
    }
}

