/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Object
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Primitive
 *  org.bouncycastle.asn1.ASN1Sequence
 *  org.bouncycastle.asn1.nist.NISTObjectIdentifiers
 *  org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers
 */
package org.bouncycastle.asn1.smime;

import java.util.Enumeration;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.smime.SMIMECapability;

public class SMIMECapabilities
extends ASN1Object {
    public static final ASN1ObjectIdentifier preferSignedData = PKCSObjectIdentifiers.preferSignedData;
    public static final ASN1ObjectIdentifier canNotDecryptAny = PKCSObjectIdentifiers.canNotDecryptAny;
    public static final ASN1ObjectIdentifier sMIMECapabilitesVersions = PKCSObjectIdentifiers.sMIMECapabilitiesVersions;
    public static final ASN1ObjectIdentifier aes256_CBC = NISTObjectIdentifiers.id_aes256_CBC;
    public static final ASN1ObjectIdentifier aes192_CBC = NISTObjectIdentifiers.id_aes192_CBC;
    public static final ASN1ObjectIdentifier aes128_CBC = NISTObjectIdentifiers.id_aes128_CBC;
    public static final ASN1ObjectIdentifier idea_CBC = new ASN1ObjectIdentifier("1.3.6.1.4.1.188.7.1.1.2");
    public static final ASN1ObjectIdentifier cast5_CBC = new ASN1ObjectIdentifier("1.2.840.113533.7.66.10");
    public static final ASN1ObjectIdentifier dES_CBC = new ASN1ObjectIdentifier("1.3.14.3.2.7");
    public static final ASN1ObjectIdentifier dES_EDE3_CBC = PKCSObjectIdentifiers.des_EDE3_CBC;
    public static final ASN1ObjectIdentifier rC2_CBC = PKCSObjectIdentifiers.RC2_CBC;
    private ASN1Sequence capabilities;

    public static SMIMECapabilities getInstance(Object o) {
        if (o == null || o instanceof SMIMECapabilities) {
            return (SMIMECapabilities)((Object)o);
        }
        if (o instanceof ASN1Sequence) {
            return new SMIMECapabilities((ASN1Sequence)o);
        }
        if (o instanceof Attribute) {
            return new SMIMECapabilities((ASN1Sequence)((Attribute)((Object)o)).getAttrValues().getObjectAt(0));
        }
        throw new IllegalArgumentException("unknown object in factory: " + o.getClass().getName());
    }

    public SMIMECapabilities(ASN1Sequence seq) {
        this.capabilities = seq;
    }

    public Vector getCapabilities(ASN1ObjectIdentifier capability) {
        Enumeration e = this.capabilities.getObjects();
        Vector<SMIMECapability> list = new Vector<SMIMECapability>();
        if (capability == null) {
            while (e.hasMoreElements()) {
                SMIMECapability cap = SMIMECapability.getInstance(e.nextElement());
                list.addElement(cap);
            }
        } else {
            while (e.hasMoreElements()) {
                SMIMECapability cap = SMIMECapability.getInstance(e.nextElement());
                if (!capability.equals((ASN1Primitive)cap.getCapabilityID())) continue;
                list.addElement(cap);
            }
        }
        return list;
    }

    public ASN1Primitive toASN1Primitive() {
        return this.capabilities;
    }
}

