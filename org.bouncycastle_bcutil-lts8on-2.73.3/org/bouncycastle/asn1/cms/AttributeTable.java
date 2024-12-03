/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1EncodableVector
 *  org.bouncycastle.asn1.ASN1ObjectIdentifier
 *  org.bouncycastle.asn1.ASN1Set
 *  org.bouncycastle.asn1.DERSet
 */
package org.bouncycastle.asn1.cms;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.Attributes;

public class AttributeTable {
    private Hashtable attributes = new Hashtable();

    public AttributeTable(Hashtable attrs) {
        this.attributes = this.copyTable(attrs);
    }

    public AttributeTable(ASN1EncodableVector v) {
        for (int i = 0; i != v.size(); ++i) {
            Attribute a = Attribute.getInstance(v.get(i));
            this.addAttribute(a.getAttrType(), a);
        }
    }

    public AttributeTable(ASN1Set s) {
        for (int i = 0; i != s.size(); ++i) {
            Attribute a = Attribute.getInstance(s.getObjectAt(i));
            this.addAttribute(a.getAttrType(), a);
        }
    }

    public AttributeTable(Attribute attr) {
        this.addAttribute(attr.getAttrType(), attr);
    }

    public AttributeTable(Attributes attrs) {
        this(ASN1Set.getInstance((Object)attrs.toASN1Primitive()));
    }

    private void addAttribute(ASN1ObjectIdentifier oid, Attribute a) {
        Object value = this.attributes.get(oid);
        if (value == null) {
            this.attributes.put(oid, a);
        } else {
            Vector<Object> v;
            if (value instanceof Attribute) {
                v = new Vector<Object>();
                v.addElement(value);
                v.addElement((Object)a);
            } else {
                v = (Vector<Object>)value;
                v.addElement((Object)a);
            }
            this.attributes.put(oid, v);
        }
    }

    public Attribute get(ASN1ObjectIdentifier oid) {
        Object value = this.attributes.get(oid);
        if (value instanceof Vector) {
            return (Attribute)((Object)((Vector)value).elementAt(0));
        }
        return (Attribute)((Object)value);
    }

    public ASN1EncodableVector getAll(ASN1ObjectIdentifier oid) {
        ASN1EncodableVector v = new ASN1EncodableVector();
        Object value = this.attributes.get(oid);
        if (value instanceof Vector) {
            Enumeration e = ((Vector)value).elements();
            while (e.hasMoreElements()) {
                v.add((ASN1Encodable)((Attribute)((Object)e.nextElement())));
            }
        } else if (value != null) {
            v.add((ASN1Encodable)((Attribute)((Object)value)));
        }
        return v;
    }

    public int size() {
        int size = 0;
        Enumeration en = this.attributes.elements();
        while (en.hasMoreElements()) {
            Object o = en.nextElement();
            if (o instanceof Vector) {
                size += ((Vector)o).size();
                continue;
            }
            ++size;
        }
        return size;
    }

    public Hashtable toHashtable() {
        return this.copyTable(this.attributes);
    }

    public ASN1EncodableVector toASN1EncodableVector() {
        ASN1EncodableVector v = new ASN1EncodableVector();
        Enumeration e = this.attributes.elements();
        while (e.hasMoreElements()) {
            Object value = e.nextElement();
            if (value instanceof Vector) {
                Enumeration en = ((Vector)value).elements();
                while (en.hasMoreElements()) {
                    v.add((ASN1Encodable)Attribute.getInstance(en.nextElement()));
                }
                continue;
            }
            v.add((ASN1Encodable)Attribute.getInstance(value));
        }
        return v;
    }

    public Attributes toASN1Structure() {
        return new Attributes(this.toASN1EncodableVector());
    }

    private Hashtable copyTable(Hashtable in) {
        Hashtable out = new Hashtable();
        Enumeration e = in.keys();
        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            out.put(key, in.get(key));
        }
        return out;
    }

    public AttributeTable add(ASN1ObjectIdentifier attrType, ASN1Encodable attrValue) {
        AttributeTable newTable = new AttributeTable(this.attributes);
        newTable.addAttribute(attrType, new Attribute(attrType, (ASN1Set)new DERSet(attrValue)));
        return newTable;
    }

    public AttributeTable remove(ASN1ObjectIdentifier attrType) {
        AttributeTable newTable = new AttributeTable(this.attributes);
        newTable.attributes.remove(attrType);
        return newTable;
    }
}

