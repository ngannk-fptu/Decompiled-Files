/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DEROctetString
 *  org.bouncycastle.asn1.DERSet
 *  org.bouncycastle.asn1.cms.Attribute
 *  org.bouncycastle.asn1.cms.AttributeTable
 *  org.bouncycastle.asn1.cms.CMSAlgorithmProtection
 *  org.bouncycastle.asn1.cms.CMSAttributes
 *  org.bouncycastle.asn1.cms.Time
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAlgorithmProtection;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.cms.Time;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAttributeTableGenerator;

public class DefaultSignedAttributeTableGenerator
implements CMSAttributeTableGenerator {
    private final Hashtable table;

    public DefaultSignedAttributeTableGenerator() {
        this.table = new Hashtable();
    }

    public DefaultSignedAttributeTableGenerator(AttributeTable attributeTable) {
        this.table = attributeTable != null ? attributeTable.toHashtable() : new Hashtable();
    }

    protected Hashtable createStandardAttributeTable(Map parameters) {
        Attribute attr;
        ASN1ObjectIdentifier contentType;
        Hashtable std = DefaultSignedAttributeTableGenerator.copyHashTable(this.table);
        if (!std.containsKey(CMSAttributes.contentType) && (contentType = ASN1ObjectIdentifier.getInstance(parameters.get("contentType"))) != null) {
            attr = new Attribute(CMSAttributes.contentType, (ASN1Set)new DERSet((ASN1Encodable)contentType));
            std.put(attr.getAttrType(), attr);
        }
        if (!std.containsKey(CMSAttributes.signingTime)) {
            Date signingTime = new Date();
            attr = new Attribute(CMSAttributes.signingTime, (ASN1Set)new DERSet((ASN1Encodable)new Time(signingTime)));
            std.put(attr.getAttrType(), attr);
        }
        if (!std.containsKey(CMSAttributes.messageDigest)) {
            byte[] messageDigest = (byte[])parameters.get("digest");
            attr = new Attribute(CMSAttributes.messageDigest, (ASN1Set)new DERSet((ASN1Encodable)new DEROctetString(messageDigest)));
            std.put(attr.getAttrType(), attr);
        }
        if (!std.contains(CMSAttributes.cmsAlgorithmProtect)) {
            Attribute attr2 = new Attribute(CMSAttributes.cmsAlgorithmProtect, (ASN1Set)new DERSet((ASN1Encodable)new CMSAlgorithmProtection((AlgorithmIdentifier)parameters.get("digestAlgID"), 1, (AlgorithmIdentifier)parameters.get("signatureAlgID"))));
            std.put(attr2.getAttrType(), attr2);
        }
        return std;
    }

    @Override
    public AttributeTable getAttributes(Map parameters) {
        return new AttributeTable(this.createStandardAttributeTable(parameters));
    }

    private static Hashtable copyHashTable(Hashtable paramsMap) {
        Hashtable newTable = new Hashtable();
        Enumeration keys = paramsMap.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            newTable.put(key, paramsMap.get(key));
        }
        return newTable;
    }
}

