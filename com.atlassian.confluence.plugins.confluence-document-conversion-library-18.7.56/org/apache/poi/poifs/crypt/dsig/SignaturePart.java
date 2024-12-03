/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.dsig;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.URIDereferencer;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.ooxml.util.XPathHelper;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.poifs.crypt.dsig.KeyInfoKeySelector;
import org.apache.poi.poifs.crypt.dsig.SignatureConfig;
import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.apache.xmlbeans.XmlException;
import org.w3.x2000.x09.xmldsig.SignatureDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SignaturePart {
    private static final Logger LOG = LogManager.getLogger(SignaturePart.class);
    private static final String XMLSEC_VALIDATE_MANIFEST = "org.jcp.xml.dsig.validateManifests";
    private static final String XMLSEC_VALIDATE_SECURE = "org.apache.jcp.xml.dsig.secureValidation";
    private final PackagePart signaturePart;
    private final SignatureInfo signatureInfo;
    private X509Certificate signer;
    private List<X509Certificate> certChain;

    SignaturePart(PackagePart signaturePart, SignatureInfo signatureInfo) {
        this.signaturePart = signaturePart;
        this.signatureInfo = signatureInfo;
    }

    public PackagePart getPackagePart() {
        return this.signaturePart;
    }

    public X509Certificate getSigner() {
        return this.signer;
    }

    public List<X509Certificate> getCertChain() {
        return this.certChain;
    }

    public SignatureDocument getSignatureDocument() throws IOException, XmlException {
        try (InputStream stream = this.signaturePart.getInputStream();){
            SignatureDocument signatureDocument = (SignatureDocument)SignatureDocument.Factory.parse(stream, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            return signatureDocument;
        }
    }

    public boolean validate() {
        KeyInfoKeySelector keySelector = new KeyInfoKeySelector();
        XPath xpath = XPathHelper.getFactory().newXPath();
        xpath.setNamespaceContext(new XPathNSContext());
        try {
            Document doc;
            try (InputStream stream = this.signaturePart.getInputStream();){
                doc = DocumentHelper.readDocument(stream);
            }
            NodeList nl = (NodeList)xpath.compile("//*[@Id]").evaluate(doc, XPathConstants.NODESET);
            int length = nl.getLength();
            for (int i = 0; i < length; ++i) {
                ((Element)nl.item(i)).setIdAttribute("Id", true);
            }
            DOMValidateContext domValidateContext = new DOMValidateContext(keySelector, (Node)doc);
            domValidateContext.setProperty(XMLSEC_VALIDATE_MANIFEST, Boolean.TRUE);
            domValidateContext.setProperty(XMLSEC_VALIDATE_SECURE, this.signatureInfo.getSignatureConfig().isSecureValidation());
            URIDereferencer uriDereferencer = this.signatureInfo.getUriDereferencer();
            domValidateContext.setURIDereferencer(uriDereferencer);
            XMLSignatureFactory xmlSignatureFactory = this.signatureInfo.getSignatureFactory();
            XMLSignature xmlSignature = xmlSignatureFactory.unmarshalXMLSignature(domValidateContext);
            boolean valid = xmlSignature.validate(domValidateContext);
            if (valid) {
                this.signer = keySelector.getSigner();
                this.certChain = keySelector.getCertChain();
                this.extractConfig(doc, xmlSignature);
            }
            return valid;
        }
        catch (IOException e) {
            String s = "error in reading document";
            LOG.atError().withThrowable(e).log(s);
            throw new EncryptedDocumentException(s, e);
        }
        catch (SAXException e) {
            String s = "error in parsing document";
            LOG.atError().withThrowable(e).log(s);
            throw new EncryptedDocumentException(s, e);
        }
        catch (XPathExpressionException e) {
            String s = "error in searching document with xpath expression";
            LOG.atError().withThrowable(e).log(s);
            throw new EncryptedDocumentException(s, e);
        }
        catch (MarshalException e) {
            String s = "error in unmarshalling the signature";
            LOG.atError().withThrowable(e).log(s);
            throw new EncryptedDocumentException(s, e);
        }
        catch (XMLSignatureException e) {
            String s = "error in validating the signature";
            LOG.atError().withThrowable(e).log(s);
            throw new EncryptedDocumentException(s, e);
        }
    }

    private void extractConfig(Document doc, XMLSignature xmlSignature) throws XPathExpressionException {
        SignatureConfig signatureConfig = this.signatureInfo.getSignatureConfig();
        if (!signatureConfig.isUpdateConfigOnValidate()) {
            return;
        }
        signatureConfig.setSigningCertificateChain(this.certChain);
        signatureConfig.setSignatureMethodFromUri(xmlSignature.getSignedInfo().getSignatureMethod().getAlgorithm());
        XPath xpath = XPathHelper.getFactory().newXPath();
        xpath.setNamespaceContext(new XPathNSContext());
        HashMap<String, Consumer<String>> m = new HashMap<String, Consumer<String>>();
        m.put("//mdssi:SignatureTime/mdssi:Value", signatureConfig::setExecutionTime);
        m.put("//xd:ClaimedRole", signatureConfig::setXadesRole);
        m.put("//dsss:SignatureComments", signatureConfig::setSignatureDescription);
        m.put("//xd:QualifyingProperties//xd:SignedSignatureProperties//ds:DigestMethod/@Algorithm", signatureConfig::setXadesDigestAlgo);
        m.put("//ds:CanonicalizationMethod", signatureConfig::setCanonicalizationMethod);
        m.put("//xd:CommitmentTypeId/xd:Description", signatureConfig::setCommitmentType);
        for (Map.Entry me : m.entrySet()) {
            String val = (String)xpath.compile((String)me.getKey()).evaluate(doc, XPathConstants.STRING);
            ((Consumer)me.getValue()).accept(val);
        }
    }

    private class XPathNSContext
    implements NamespaceContext {
        final Map<String, String> nsMap = new HashMap<String, String>();

        private XPathNSContext() {
            SignaturePart.this.signatureInfo.getSignatureConfig().getNamespacePrefixes().forEach((k, v) -> this.nsMap.put((String)v, (String)k));
            this.nsMap.put("dsss", "http://schemas.microsoft.com/office/2006/digsig");
            this.nsMap.put("ds", "http://www.w3.org/2000/09/xmldsig#");
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return this.nsMap.get(prefix);
        }

        public Iterator getPrefixes(String val) {
            return null;
        }

        @Override
        public String getPrefix(String uri) {
            return null;
        }
    }
}

