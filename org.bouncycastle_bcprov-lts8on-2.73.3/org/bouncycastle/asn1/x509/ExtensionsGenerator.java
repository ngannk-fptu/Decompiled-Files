/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.util.Arrays;

public class ExtensionsGenerator {
    private Hashtable extensions = new Hashtable();
    private Vector extOrdering = new Vector();
    private static final Set dupsAllowed;

    public void reset() {
        this.extensions = new Hashtable();
        this.extOrdering = new Vector();
    }

    public void addExtension(ASN1ObjectIdentifier oid, boolean critical, ASN1Encodable value) throws IOException {
        this.addExtension(oid, critical, value.toASN1Primitive().getEncoded("DER"));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void addExtension(ASN1ObjectIdentifier oid, boolean critical, byte[] value) {
        if (this.extensions.containsKey(oid)) {
            if (!dupsAllowed.contains(oid)) throw new IllegalArgumentException("extension " + oid + " already added");
            Extension existingExtension = (Extension)this.extensions.get(oid);
            ASN1Sequence seq1 = ASN1Sequence.getInstance(DEROctetString.getInstance(existingExtension.getExtnValue()).getOctets());
            ASN1Sequence seq2 = ASN1Sequence.getInstance(value);
            ASN1EncodableVector items = new ASN1EncodableVector(seq1.size() + seq2.size());
            Enumeration en = seq1.getObjects();
            while (en.hasMoreElements()) {
                items.add((ASN1Encodable)en.nextElement());
            }
            en = seq2.getObjects();
            while (en.hasMoreElements()) {
                items.add((ASN1Encodable)en.nextElement());
            }
            try {
                this.extensions.put(oid, new Extension(oid, critical, new DERSequence(items).getEncoded()));
                return;
            }
            catch (IOException e) {
                throw new ASN1ParsingException(e.getMessage(), e);
            }
        } else {
            this.extOrdering.addElement(oid);
            this.extensions.put(oid, new Extension(oid, critical, (ASN1OctetString)new DEROctetString(Arrays.clone(value))));
        }
    }

    public void addExtension(Extension extension) {
        if (this.extensions.containsKey(extension.getExtnId())) {
            throw new IllegalArgumentException("extension " + extension.getExtnId() + " already added");
        }
        this.extOrdering.addElement(extension.getExtnId());
        this.extensions.put(extension.getExtnId(), extension);
    }

    public void replaceExtension(ASN1ObjectIdentifier oid, boolean critical, ASN1Encodable value) throws IOException {
        this.replaceExtension(oid, critical, value.toASN1Primitive().getEncoded("DER"));
    }

    public void replaceExtension(ASN1ObjectIdentifier oid, boolean critical, byte[] value) {
        this.replaceExtension(new Extension(oid, critical, value));
    }

    public void replaceExtension(Extension extension) {
        if (!this.extensions.containsKey(extension.getExtnId())) {
            throw new IllegalArgumentException("extension " + extension.getExtnId() + " not present");
        }
        this.extensions.put(extension.getExtnId(), extension);
    }

    public void removeExtension(ASN1ObjectIdentifier oid) {
        if (!this.extensions.containsKey(oid)) {
            throw new IllegalArgumentException("extension " + oid + " not present");
        }
        this.extOrdering.removeElement(oid);
        this.extensions.remove(oid);
    }

    public boolean hasExtension(ASN1ObjectIdentifier oid) {
        return this.extensions.containsKey(oid);
    }

    public Extension getExtension(ASN1ObjectIdentifier oid) {
        return (Extension)this.extensions.get(oid);
    }

    public boolean isEmpty() {
        return this.extOrdering.isEmpty();
    }

    public Extensions generate() {
        Extension[] exts = new Extension[this.extOrdering.size()];
        for (int i = 0; i != this.extOrdering.size(); ++i) {
            exts[i] = (Extension)this.extensions.get(this.extOrdering.elementAt(i));
        }
        return new Extensions(exts);
    }

    public void addExtension(Extensions extensions) {
        ASN1ObjectIdentifier[] oids = extensions.getExtensionOIDs();
        for (int i = 0; i != oids.length; ++i) {
            ASN1ObjectIdentifier ident = oids[i];
            Extension ext = extensions.getExtension(ident);
            this.addExtension(ASN1ObjectIdentifier.getInstance(ident), ext.isCritical(), ext.getExtnValue().getOctets());
        }
    }

    static {
        HashSet<ASN1ObjectIdentifier> dups = new HashSet<ASN1ObjectIdentifier>();
        dups.add(Extension.subjectAlternativeName);
        dups.add(Extension.issuerAlternativeName);
        dups.add(Extension.subjectDirectoryAttributes);
        dups.add(Extension.certificateIssuer);
        dupsAllowed = Collections.unmodifiableSet(dups);
    }
}

