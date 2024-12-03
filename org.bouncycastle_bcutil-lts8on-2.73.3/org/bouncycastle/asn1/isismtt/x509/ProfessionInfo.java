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
 *  org.bouncycastle.asn1.ASN1PrintableString
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.ASN1TaggedObject
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERPrintableString
 *  org.bouncycastle.asn1.DERSequence
 *  org.bouncycastle.asn1.DERTaggedObject
 *  org.bouncycastle.asn1.x500.DirectoryString
 */
package org.bouncycastle.asn1.isismtt.x509;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1PrintableString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.isismtt.x509.NamingAuthority;
import org.bouncycastle.asn1.x500.DirectoryString;

public class ProfessionInfo
extends ASN1Object {
    public static final ASN1ObjectIdentifier Rechtsanwltin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".1");
    public static final ASN1ObjectIdentifier Rechtsanwalt = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".2");
    public static final ASN1ObjectIdentifier Rechtsbeistand = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".3");
    public static final ASN1ObjectIdentifier Steuerberaterin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".4");
    public static final ASN1ObjectIdentifier Steuerberater = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".5");
    public static final ASN1ObjectIdentifier Steuerbevollmchtigte = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".6");
    public static final ASN1ObjectIdentifier Steuerbevollmchtigter = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".7");
    public static final ASN1ObjectIdentifier Notarin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".8");
    public static final ASN1ObjectIdentifier Notar = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".9");
    public static final ASN1ObjectIdentifier Notarvertreterin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".10");
    public static final ASN1ObjectIdentifier Notarvertreter = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".11");
    public static final ASN1ObjectIdentifier Notariatsverwalterin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".12");
    public static final ASN1ObjectIdentifier Notariatsverwalter = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".13");
    public static final ASN1ObjectIdentifier Wirtschaftsprferin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".14");
    public static final ASN1ObjectIdentifier Wirtschaftsprfer = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".15");
    public static final ASN1ObjectIdentifier VereidigteBuchprferin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".16");
    public static final ASN1ObjectIdentifier VereidigterBuchprfer = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".17");
    public static final ASN1ObjectIdentifier Patentanwltin = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".18");
    public static final ASN1ObjectIdentifier Patentanwalt = new ASN1ObjectIdentifier(NamingAuthority.id_isismtt_at_namingAuthorities_RechtWirtschaftSteuern + ".19");
    private NamingAuthority namingAuthority;
    private ASN1Sequence professionItems;
    private ASN1Sequence professionOIDs;
    private String registrationNumber;
    private ASN1OctetString addProfessionInfo;

    public static ProfessionInfo getInstance(Object obj) {
        if (obj == null || obj instanceof ProfessionInfo) {
            return (ProfessionInfo)((Object)obj);
        }
        if (obj instanceof ASN1Sequence) {
            return new ProfessionInfo((ASN1Sequence)obj);
        }
        throw new IllegalArgumentException("illegal object in getInstance: " + obj.getClass().getName());
    }

    private ProfessionInfo(ASN1Sequence seq) {
        if (seq.size() > 5) {
            throw new IllegalArgumentException("Bad sequence size: " + seq.size());
        }
        Enumeration e = seq.getObjects();
        ASN1Encodable o = (ASN1Encodable)e.nextElement();
        if (o instanceof ASN1TaggedObject) {
            if (((ASN1TaggedObject)o).getTagNo() != 0) {
                throw new IllegalArgumentException("Bad tag number: " + ((ASN1TaggedObject)o).getTagNo());
            }
            this.namingAuthority = NamingAuthority.getInstance((ASN1TaggedObject)o, true);
            o = (ASN1Encodable)e.nextElement();
        }
        this.professionItems = ASN1Sequence.getInstance((Object)o);
        if (e.hasMoreElements()) {
            o = (ASN1Encodable)e.nextElement();
            if (o instanceof ASN1Sequence) {
                this.professionOIDs = ASN1Sequence.getInstance((Object)o);
            } else if (o instanceof ASN1PrintableString) {
                this.registrationNumber = ASN1PrintableString.getInstance((Object)o).getString();
            } else if (o instanceof ASN1OctetString) {
                this.addProfessionInfo = ASN1OctetString.getInstance((Object)o);
            } else {
                throw new IllegalArgumentException("Bad object encountered: " + o.getClass());
            }
        }
        if (e.hasMoreElements()) {
            o = (ASN1Encodable)e.nextElement();
            if (o instanceof ASN1PrintableString) {
                this.registrationNumber = ASN1PrintableString.getInstance((Object)o).getString();
            } else if (o instanceof DEROctetString) {
                this.addProfessionInfo = (DEROctetString)o;
            } else {
                throw new IllegalArgumentException("Bad object encountered: " + o.getClass());
            }
        }
        if (e.hasMoreElements()) {
            o = (ASN1Encodable)e.nextElement();
            if (o instanceof DEROctetString) {
                this.addProfessionInfo = (DEROctetString)o;
            } else {
                throw new IllegalArgumentException("Bad object encountered: " + o.getClass());
            }
        }
    }

    public ProfessionInfo(NamingAuthority namingAuthority, DirectoryString[] professionItems, ASN1ObjectIdentifier[] professionOIDs, String registrationNumber, ASN1OctetString addProfessionInfo) {
        this.namingAuthority = namingAuthority;
        this.professionItems = new DERSequence((ASN1Encodable[])professionItems);
        if (professionOIDs != null) {
            this.professionOIDs = new DERSequence((ASN1Encodable[])professionOIDs);
        }
        this.registrationNumber = registrationNumber;
        this.addProfessionInfo = addProfessionInfo;
    }

    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector vec = new ASN1EncodableVector(5);
        if (this.namingAuthority != null) {
            vec.add((ASN1Encodable)new DERTaggedObject(true, 0, (ASN1Encodable)this.namingAuthority));
        }
        vec.add((ASN1Encodable)this.professionItems);
        if (this.professionOIDs != null) {
            vec.add((ASN1Encodable)this.professionOIDs);
        }
        if (this.registrationNumber != null) {
            vec.add((ASN1Encodable)new DERPrintableString(this.registrationNumber, true));
        }
        if (this.addProfessionInfo != null) {
            vec.add((ASN1Encodable)this.addProfessionInfo);
        }
        return new DERSequence(vec);
    }

    public ASN1OctetString getAddProfessionInfo() {
        return this.addProfessionInfo;
    }

    public NamingAuthority getNamingAuthority() {
        return this.namingAuthority;
    }

    public DirectoryString[] getProfessionItems() {
        DirectoryString[] items = new DirectoryString[this.professionItems.size()];
        int count = 0;
        Enumeration e = this.professionItems.getObjects();
        while (e.hasMoreElements()) {
            items[count++] = DirectoryString.getInstance(e.nextElement());
        }
        return items;
    }

    public ASN1ObjectIdentifier[] getProfessionOIDs() {
        if (this.professionOIDs == null) {
            return new ASN1ObjectIdentifier[0];
        }
        ASN1ObjectIdentifier[] oids = new ASN1ObjectIdentifier[this.professionOIDs.size()];
        int count = 0;
        Enumeration e = this.professionOIDs.getObjects();
        while (e.hasMoreElements()) {
            oids[count++] = ASN1ObjectIdentifier.getInstance(e.nextElement());
        }
        return oids;
    }

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }
}

