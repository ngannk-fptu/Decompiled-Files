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
 *  org.bouncycastle.asn1.x509.AlgorithmIdentifier
 */
package org.bouncycastle.cms;

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
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSAttributeTableGenerator;

public class DefaultAuthenticatedAttributeTableGenerator
implements CMSAttributeTableGenerator {
    private final Hashtable table;

    public DefaultAuthenticatedAttributeTableGenerator() {
        this.table = new Hashtable();
    }

    public DefaultAuthenticatedAttributeTableGenerator(AttributeTable attributeTable) {
        this.table = attributeTable != null ? attributeTable.toHashtable() : new Hashtable();
    }

    protected Hashtable createStandardAttributeTable(Map parameters) {
        Attribute attr;
        Hashtable std = new Hashtable();
        Enumeration en = this.table.keys();
        while (en.hasMoreElements()) {
            Object key = en.nextElement();
            std.put(key, this.table.get(key));
        }
        if (!std.containsKey(CMSAttributes.contentType)) {
            ASN1ObjectIdentifier contentType = ASN1ObjectIdentifier.getInstance(parameters.get("contentType"));
            attr = new Attribute(CMSAttributes.contentType, (ASN1Set)new DERSet((ASN1Encodable)contentType));
            std.put(attr.getAttrType(), attr);
        }
        if (!std.containsKey(CMSAttributes.messageDigest)) {
            byte[] messageDigest = (byte[])parameters.get("digest");
            attr = new Attribute(CMSAttributes.messageDigest, (ASN1Set)new DERSet((ASN1Encodable)new DEROctetString(messageDigest)));
            std.put(attr.getAttrType(), attr);
        }
        if (!std.contains(CMSAttributes.cmsAlgorithmProtect)) {
            Attribute attr2 = new Attribute(CMSAttributes.cmsAlgorithmProtect, (ASN1Set)new DERSet((ASN1Encodable)new CMSAlgorithmProtection((AlgorithmIdentifier)parameters.get("digestAlgID"), 2, (AlgorithmIdentifier)parameters.get("macAlgID"))));
            std.put(attr2.getAttrType(), attr2);
        }
        return std;
    }

    @Override
    public AttributeTable getAttributes(Map parameters) {
        return new AttributeTable(this.createStandardAttributeTable(parameters));
    }
}

