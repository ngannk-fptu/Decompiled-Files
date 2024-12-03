/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jcp.xml.dsig.internal.dom;

import java.math.BigInteger;
import java.security.KeyException;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.List;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.PGPData;
import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import org.apache.jcp.xml.dsig.internal.dom.DOMKeyInfo;
import org.apache.jcp.xml.dsig.internal.dom.DOMKeyName;
import org.apache.jcp.xml.dsig.internal.dom.DOMKeyValue;
import org.apache.jcp.xml.dsig.internal.dom.DOMPGPData;
import org.apache.jcp.xml.dsig.internal.dom.DOMRetrievalMethod;
import org.apache.jcp.xml.dsig.internal.dom.DOMURIDereferencer;
import org.apache.jcp.xml.dsig.internal.dom.DOMX509Data;
import org.apache.jcp.xml.dsig.internal.dom.DOMX509IssuerSerial;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public final class DOMKeyInfoFactory
extends KeyInfoFactory {
    public KeyInfo newKeyInfo(List content) {
        return this.newKeyInfo(content, (String)null);
    }

    public KeyInfo newKeyInfo(List content, String id) {
        return new DOMKeyInfo(content, id);
    }

    @Override
    public KeyName newKeyName(String name) {
        return new DOMKeyName(name);
    }

    @Override
    public KeyValue newKeyValue(PublicKey key) throws KeyException {
        String algorithm = key.getAlgorithm();
        if ("DSA".equals(algorithm)) {
            return new DOMKeyValue.DSA((DSAPublicKey)key);
        }
        if ("RSA".equals(algorithm)) {
            return new DOMKeyValue.RSA((RSAPublicKey)key);
        }
        if ("EC".equals(algorithm)) {
            return new DOMKeyValue.EC((ECPublicKey)key);
        }
        throw new KeyException("unsupported key algorithm: " + algorithm);
    }

    @Override
    public PGPData newPGPData(byte[] keyId) {
        return this.newPGPData(keyId, (byte[])null, (List)null);
    }

    public PGPData newPGPData(byte[] keyId, byte[] keyPacket, List other) {
        return new DOMPGPData(keyId, keyPacket, other);
    }

    public PGPData newPGPData(byte[] keyPacket, List other) {
        return new DOMPGPData(keyPacket, other);
    }

    @Override
    public RetrievalMethod newRetrievalMethod(String uri) {
        return this.newRetrievalMethod(uri, (String)null, (List)null);
    }

    public RetrievalMethod newRetrievalMethod(String uri, String type, List transforms) {
        if (uri == null) {
            throw new NullPointerException("uri must not be null");
        }
        return new DOMRetrievalMethod(uri, type, transforms);
    }

    public X509Data newX509Data(List content) {
        return new DOMX509Data(content);
    }

    @Override
    public X509IssuerSerial newX509IssuerSerial(String issuerName, BigInteger serialNumber) {
        return new DOMX509IssuerSerial(issuerName, serialNumber);
    }

    @Override
    public boolean isFeatureSupported(String feature) {
        if (feature == null) {
            throw new NullPointerException();
        }
        return false;
    }

    @Override
    public URIDereferencer getURIDereferencer() {
        return DOMURIDereferencer.INSTANCE;
    }

    @Override
    public KeyInfo unmarshalKeyInfo(XMLStructure xmlStructure) throws MarshalException {
        if (xmlStructure == null) {
            throw new NullPointerException("xmlStructure cannot be null");
        }
        if (!(xmlStructure instanceof DOMStructure)) {
            throw new ClassCastException("xmlStructure must be of type DOMStructure");
        }
        Node node = ((DOMStructure)xmlStructure).getNode();
        node.normalize();
        Element element = null;
        if (node.getNodeType() == 9) {
            element = ((Document)node).getDocumentElement();
        } else if (node.getNodeType() == 1) {
            element = (Element)node;
        } else {
            throw new MarshalException("xmlStructure does not contain a proper Node");
        }
        String tag = element.getLocalName();
        String namespace = element.getNamespaceURI();
        if (tag == null || namespace == null) {
            throw new MarshalException("Document implementation must support DOM Level 2 and be namespace aware");
        }
        if ("KeyInfo".equals(tag) && "http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
            try {
                return new DOMKeyInfo(element, new UnmarshalContext(), this.getProvider());
            }
            catch (MarshalException me) {
                throw me;
            }
            catch (Exception e) {
                throw new MarshalException(e);
            }
        }
        throw new MarshalException("Invalid KeyInfo tag: " + namespace + ":" + tag);
    }

    private static class UnmarshalContext
    extends DOMCryptoContext {
        UnmarshalContext() {
        }
    }
}

