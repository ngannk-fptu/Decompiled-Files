/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.jcajce.provider.asymmetric.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OutputStream;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;

public class PKCS12BagAttributeCarrierImpl
implements PKCS12BagAttributeCarrier {
    private Hashtable pkcs12Attributes;
    private Vector pkcs12Ordering;

    PKCS12BagAttributeCarrierImpl(Hashtable attributes, Vector ordering) {
        this.pkcs12Attributes = attributes;
        this.pkcs12Ordering = ordering;
    }

    public PKCS12BagAttributeCarrierImpl() {
        this(new Hashtable(), new Vector());
    }

    @Override
    public void setBagAttribute(ASN1ObjectIdentifier oid, ASN1Encodable attribute) {
        if (this.pkcs12Attributes.containsKey(oid)) {
            this.pkcs12Attributes.put(oid, attribute);
        } else {
            this.pkcs12Attributes.put(oid, attribute);
            this.pkcs12Ordering.addElement(oid);
        }
    }

    @Override
    public ASN1Encodable getBagAttribute(ASN1ObjectIdentifier oid) {
        return (ASN1Encodable)this.pkcs12Attributes.get(oid);
    }

    @Override
    public Enumeration getBagAttributeKeys() {
        return this.pkcs12Ordering.elements();
    }

    int size() {
        return this.pkcs12Ordering.size();
    }

    Hashtable getAttributes() {
        return this.pkcs12Attributes;
    }

    Vector getOrdering() {
        return this.pkcs12Ordering;
    }

    public void writeObject(ObjectOutputStream out) throws IOException {
        if (this.pkcs12Ordering.size() == 0) {
            out.writeObject(new Hashtable());
            out.writeObject(new Vector());
        } else {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ASN1OutputStream aOut = ASN1OutputStream.create(bOut);
            Enumeration e = this.getBagAttributeKeys();
            while (e.hasMoreElements()) {
                ASN1ObjectIdentifier oid = ASN1ObjectIdentifier.getInstance(e.nextElement());
                aOut.writeObject(oid);
                aOut.writeObject((ASN1Encodable)this.pkcs12Attributes.get(oid));
            }
            out.writeObject(bOut.toByteArray());
        }
    }

    public void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object obj = in.readObject();
        if (obj instanceof Hashtable) {
            this.pkcs12Attributes = (Hashtable)obj;
            this.pkcs12Ordering = (Vector)in.readObject();
        } else {
            ASN1ObjectIdentifier oid;
            ASN1InputStream aIn = new ASN1InputStream((byte[])obj);
            while ((oid = (ASN1ObjectIdentifier)aIn.readObject()) != null) {
                this.setBagAttribute(oid, aIn.readObject());
            }
        }
    }
}

