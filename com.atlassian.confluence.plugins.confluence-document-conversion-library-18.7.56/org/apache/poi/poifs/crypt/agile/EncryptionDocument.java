/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.agile;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.agile.DataIntegrity;
import org.apache.poi.poifs.crypt.agile.KeyData;
import org.apache.poi.poifs.crypt.agile.KeyEncryptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class EncryptionDocument {
    static final String ENC_NS = "http://schemas.microsoft.com/office/2006/encryption";
    private KeyData keyData;
    private DataIntegrity dataIntegrity;
    private final List<KeyEncryptor> keyEncryptors = new ArrayList<KeyEncryptor>();

    public void parse(Document doc) {
        Element encryption = doc.getDocumentElement();
        if (!ENC_NS.equals(encryption.getNamespaceURI()) || !"encryption".equals(encryption.getLocalName())) {
            throw new EncryptedDocumentException("Unable to parse encryption descriptor");
        }
        this.keyData = new KeyData(encryption);
        this.dataIntegrity = new DataIntegrity(encryption);
        Element keyEncryptors = EncryptionDocument.getTag(encryption, ENC_NS, "keyEncryptors");
        if (keyEncryptors == null) {
            throw new EncryptedDocumentException("Unable to parse encryption descriptor");
        }
        NodeList ke = keyEncryptors.getElementsByTagNameNS(ENC_NS, "keyEncryptor");
        for (int i = 0; i < ke.getLength(); ++i) {
            this.keyEncryptors.add(new KeyEncryptor((Element)ke.item(i)));
        }
    }

    public void write(Document doc) {
        doc.setXmlStandalone(true);
        Element encryption = (Element)doc.appendChild(doc.createElementNS(ENC_NS, "encryption"));
        if (this.keyData != null) {
            this.keyData.write(encryption);
        }
        if (this.dataIntegrity != null) {
            this.dataIntegrity.write(encryption);
        }
        Element keyEncryptors = (Element)encryption.appendChild(doc.createElementNS(ENC_NS, "keyEncryptors"));
        boolean hasPass = false;
        boolean hasCert = false;
        for (KeyEncryptor ke : this.keyEncryptors) {
            ke.write(keyEncryptors);
            hasPass |= ke.getPasswordKeyEncryptor() != null;
            hasCert |= ke.getCertificateKeyEncryptor() != null;
        }
        if (hasPass) {
            encryption.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:p", "http://schemas.microsoft.com/office/2006/keyEncryptor/password");
        }
        if (hasCert) {
            encryption.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:c", "http://schemas.microsoft.com/office/2006/keyEncryptor/certificate");
        }
    }

    public KeyData getKeyData() {
        return this.keyData;
    }

    public void setKeyData(KeyData keyData) {
        this.keyData = keyData;
    }

    public DataIntegrity getDataIntegrity() {
        return this.dataIntegrity;
    }

    public void setDataIntegrity(DataIntegrity dataIntegrity) {
        this.dataIntegrity = dataIntegrity;
    }

    public List<KeyEncryptor> getKeyEncryptors() {
        return this.keyEncryptors;
    }

    static Element getTag(Element el, String ns, String name) {
        if (el == null) {
            return null;
        }
        NodeList nl = el.getElementsByTagNameNS(ns, name);
        return nl.getLength() > 0 ? (Element)nl.item(0) : null;
    }

    static Integer getIntAttr(Element el, String name) {
        String at = el.getAttribute(name);
        return at.isEmpty() ? null : Integer.valueOf(at);
    }

    static byte[] getBinAttr(Element el, String name) {
        String at = el.getAttribute(name);
        return at.isEmpty() ? null : Base64.getDecoder().decode(at);
    }

    static void setIntAttr(Element el, String name, Integer val) {
        EncryptionDocument.setAttr(el, name, val == null ? null : val.toString());
    }

    static void setAttr(Element el, String name, String val) {
        if (val != null) {
            el.setAttribute(name, val);
        }
    }

    static void setBinAttr(Element el, String name, byte[] val) {
        if (val != null) {
            EncryptionDocument.setAttr(el, name, Base64.getEncoder().encodeToString(val));
        }
    }
}

