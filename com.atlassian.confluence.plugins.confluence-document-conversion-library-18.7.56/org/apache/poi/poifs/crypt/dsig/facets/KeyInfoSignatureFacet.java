/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.jcp.xml.dsig.internal.dom.DOMKeyInfo
 */
package org.apache.poi.poifs.crypt.dsig.facets;

import java.security.Key;
import java.security.KeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import org.apache.jcp.xml.dsig.internal.dom.DOMKeyInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.apache.poi.poifs.crypt.dsig.facets.SignatureFacet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class KeyInfoSignatureFacet
implements SignatureFacet {
    private static final Logger LOG = LogManager.getLogger(KeyInfoSignatureFacet.class);

    @Override
    public void postSign(SignatureInfo signatureInfo, Document document) throws MarshalException {
        LOG.atDebug().log("postSign");
        NodeList nl = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Object");
        Node nextSibling = nl.getLength() == 0 ? null : nl.item(0);
        KeyInfoFactory keyInfoFactory = signatureInfo.getKeyInfoFactory();
        ArrayList<Object> x509DataObjects = new ArrayList<Object>();
        SignatureConfig signatureConfig = signatureInfo.getSignatureConfig();
        X509Certificate signingCertificate = signatureConfig.getSigningCertificateChain().get(0);
        ArrayList<XMLStructure> keyInfoContent = new ArrayList<XMLStructure>();
        if (signatureConfig.isIncludeKeyValue()) {
            KeyValue keyValue;
            try {
                keyValue = keyInfoFactory.newKeyValue(signingCertificate.getPublicKey());
            }
            catch (KeyException e) {
                throw new RuntimeException("key exception: " + e.getMessage(), e);
            }
            keyInfoContent.add(keyValue);
        }
        if (signatureConfig.isIncludeIssuerSerial()) {
            x509DataObjects.add(keyInfoFactory.newX509IssuerSerial(signingCertificate.getIssuerX500Principal().toString(), signingCertificate.getSerialNumber()));
        }
        if (signatureConfig.isIncludeEntireCertificateChain()) {
            x509DataObjects.addAll(signatureConfig.getSigningCertificateChain());
        } else {
            x509DataObjects.add(signingCertificate);
        }
        if (!x509DataObjects.isEmpty()) {
            X509Data x509Data = keyInfoFactory.newX509Data(x509DataObjects);
            keyInfoContent.add(x509Data);
        }
        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(keyInfoContent);
        DOMKeyInfo domKeyInfo = (DOMKeyInfo)keyInfo;
        Key key = new Key(){
            private static final long serialVersionUID = 1L;

            @Override
            public String getAlgorithm() {
                return null;
            }

            @Override
            public byte[] getEncoded() {
                return null;
            }

            @Override
            public String getFormat() {
                return null;
            }
        };
        Element n = document.getDocumentElement();
        DOMSignContext domSignContext = nextSibling == null ? new DOMSignContext(key, (Node)n) : new DOMSignContext(key, (Node)n, nextSibling);
        signatureConfig.getNamespacePrefixes().forEach(domSignContext::putNamespacePrefix);
        DOMStructure domStructure = new DOMStructure(n);
        domKeyInfo.marshal((XMLStructure)domStructure, (XMLCryptoContext)domSignContext);
        if (nextSibling != null) {
            NodeList kiNl = document.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo");
            if (kiNl.getLength() != 1) {
                throw new RuntimeException("KeyInfo wasn't set");
            }
            nextSibling.getParentNode().insertBefore(kiNl.item(0), nextSibling);
        }
    }
}

