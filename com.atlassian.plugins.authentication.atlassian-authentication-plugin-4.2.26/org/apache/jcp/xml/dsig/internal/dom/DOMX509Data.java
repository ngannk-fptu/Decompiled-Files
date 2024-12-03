/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.security.auth.x500.X500Principal;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import org.apache.jcp.xml.dsig.internal.dom.DOMStructure;
import org.apache.jcp.xml.dsig.internal.dom.DOMUtils;
import org.apache.jcp.xml.dsig.internal.dom.DOMX509IssuerSerial;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMX509Data
extends DOMStructure
implements X509Data {
    private final List<Object> content;
    private CertificateFactory cf;

    public DOMX509Data(List<?> content) {
        if (content == null) {
            throw new NullPointerException("content cannot be null");
        }
        ArrayList contentCopy = new ArrayList(content);
        if (contentCopy.isEmpty()) {
            throw new IllegalArgumentException("content cannot be empty");
        }
        int size = contentCopy.size();
        for (int i = 0; i < size; ++i) {
            Object x509Type = contentCopy.get(i);
            if (x509Type instanceof String) {
                new X500Principal((String)x509Type);
                continue;
            }
            if (x509Type instanceof byte[] || x509Type instanceof X509Certificate || x509Type instanceof X509CRL || x509Type instanceof XMLStructure) continue;
            throw new ClassCastException("content[" + i + "] is not a valid X509Data type");
        }
        this.content = Collections.unmodifiableList(contentCopy);
    }

    public DOMX509Data(Element xdElem) throws MarshalException {
        ArrayList<Object> newContent = new ArrayList<Object>();
        for (Node firstChild = xdElem.getFirstChild(); firstChild != null; firstChild = firstChild.getNextSibling()) {
            if (firstChild.getNodeType() != 1) continue;
            Element childElem = (Element)firstChild;
            String localName = childElem.getLocalName();
            String namespace = childElem.getNamespaceURI();
            if ("X509Certificate".equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                newContent.add(this.unmarshalX509Certificate(childElem));
                continue;
            }
            if ("X509IssuerSerial".equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                newContent.add(new DOMX509IssuerSerial(childElem));
                continue;
            }
            if ("X509SubjectName".equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                newContent.add(childElem.getFirstChild().getNodeValue());
                continue;
            }
            if ("X509SKI".equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                String content = XMLUtils.getFullTextChildrenFromNode(childElem);
                newContent.add(XMLUtils.decode(content));
                continue;
            }
            if ("X509CRL".equals(localName) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                newContent.add(this.unmarshalX509CRL(childElem));
                continue;
            }
            newContent.add(new javax.xml.crypto.dom.DOMStructure(childElem));
        }
        this.content = Collections.unmodifiableList(newContent);
    }

    public List<Object> getContent() {
        return this.content;
    }

    @Override
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        Document ownerDoc = DOMUtils.getOwnerDocument(parent);
        Element xdElem = DOMUtils.createElement(ownerDoc, "X509Data", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        int size = this.content.size();
        for (int i = 0; i < size; ++i) {
            Object object = this.content.get(i);
            if (object instanceof X509Certificate) {
                this.marshalCert((X509Certificate)object, xdElem, ownerDoc, dsPrefix);
                continue;
            }
            if (object instanceof XMLStructure) {
                if (object instanceof X509IssuerSerial) {
                    ((DOMX509IssuerSerial)object).marshal(xdElem, dsPrefix, context);
                    continue;
                }
                javax.xml.crypto.dom.DOMStructure domContent = (javax.xml.crypto.dom.DOMStructure)object;
                DOMUtils.appendChild(xdElem, domContent.getNode());
                continue;
            }
            if (object instanceof byte[]) {
                this.marshalSKI((byte[])object, xdElem, ownerDoc, dsPrefix);
                continue;
            }
            if (object instanceof String) {
                this.marshalSubjectName((String)object, xdElem, ownerDoc, dsPrefix);
                continue;
            }
            if (!(object instanceof X509CRL)) continue;
            this.marshalCRL((X509CRL)object, xdElem, ownerDoc, dsPrefix);
        }
        parent.appendChild(xdElem);
    }

    private void marshalSKI(byte[] skid, Node parent, Document doc, String dsPrefix) {
        Element skidElem = DOMUtils.createElement(doc, "X509SKI", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        skidElem.appendChild(doc.createTextNode(XMLUtils.encodeToString(skid)));
        parent.appendChild(skidElem);
    }

    private void marshalSubjectName(String name, Node parent, Document doc, String dsPrefix) {
        Element snElem = DOMUtils.createElement(doc, "X509SubjectName", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        snElem.appendChild(doc.createTextNode(name));
        parent.appendChild(snElem);
    }

    private void marshalCert(X509Certificate cert, Node parent, Document doc, String dsPrefix) throws MarshalException {
        Element certElem = DOMUtils.createElement(doc, "X509Certificate", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        try {
            certElem.appendChild(doc.createTextNode(XMLUtils.encodeToString(cert.getEncoded())));
        }
        catch (CertificateEncodingException e) {
            throw new MarshalException("Error encoding X509Certificate", e);
        }
        parent.appendChild(certElem);
    }

    private void marshalCRL(X509CRL crl, Node parent, Document doc, String dsPrefix) throws MarshalException {
        Element crlElem = DOMUtils.createElement(doc, "X509CRL", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        try {
            crlElem.appendChild(doc.createTextNode(XMLUtils.encodeToString(crl.getEncoded())));
        }
        catch (CRLException e) {
            throw new MarshalException("Error encoding X509CRL", e);
        }
        parent.appendChild(crlElem);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private X509Certificate unmarshalX509Certificate(Element elem) throws MarshalException {
        try (ByteArrayInputStream bs = this.unmarshalBase64Binary(elem);){
            X509Certificate x509Certificate = (X509Certificate)this.cf.generateCertificate(bs);
            return x509Certificate;
        }
        catch (CertificateException e) {
            throw new MarshalException("Cannot create X509Certificate", e);
        }
        catch (IOException e) {
            throw new MarshalException("Error closing stream", e);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private X509CRL unmarshalX509CRL(Element elem) throws MarshalException {
        try (ByteArrayInputStream bs = this.unmarshalBase64Binary(elem);){
            X509CRL x509CRL = (X509CRL)this.cf.generateCRL(bs);
            return x509CRL;
        }
        catch (CRLException e) {
            throw new MarshalException("Cannot create X509CRL", e);
        }
        catch (IOException e) {
            throw new MarshalException("Error closing stream", e);
        }
    }

    private ByteArrayInputStream unmarshalBase64Binary(Element elem) throws MarshalException {
        try {
            if (this.cf == null) {
                this.cf = CertificateFactory.getInstance("X.509");
            }
            String content = XMLUtils.getFullTextChildrenFromNode(elem);
            return new ByteArrayInputStream(XMLUtils.decode(content));
        }
        catch (CertificateException e) {
            throw new MarshalException("Cannot create CertificateFactory", e);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof X509Data)) {
            return false;
        }
        X509Data oxd = (X509Data)o;
        List<?> ocontent = oxd.getContent();
        int size = this.content.size();
        if (size != ocontent.size()) {
            return false;
        }
        for (int i = 0; i < size; ++i) {
            Object x = this.content.get(i);
            Object ox = ocontent.get(i);
            if (!(x instanceof byte[] ? !(ox instanceof byte[]) || !Arrays.equals((byte[])x, (byte[])ox) : !x.equals(ox))) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        int result = 17;
        result = 31 * result + this.content.hashCode();
        return result;
    }
}

