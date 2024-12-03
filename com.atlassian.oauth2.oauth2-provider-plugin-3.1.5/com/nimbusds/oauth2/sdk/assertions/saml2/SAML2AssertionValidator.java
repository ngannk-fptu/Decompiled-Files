/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.opensaml.core.config.InitializationException
 *  org.opensaml.core.config.InitializationService
 *  org.opensaml.core.xml.XMLObject
 *  org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport
 *  org.opensaml.core.xml.io.UnmarshallingException
 *  org.opensaml.saml.saml2.core.Assertion
 *  org.opensaml.saml.security.impl.SAMLSignatureProfileValidator
 *  org.opensaml.security.credential.BasicCredential
 *  org.opensaml.security.credential.Credential
 *  org.opensaml.security.credential.UsageType
 *  org.opensaml.xmlsec.signature.Signature
 *  org.opensaml.xmlsec.signature.support.SignatureException
 *  org.opensaml.xmlsec.signature.support.SignatureValidator
 */
package com.nimbusds.oauth2.sdk.assertions.saml2;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.assertions.saml2.BadSAML2AssertionException;
import com.nimbusds.oauth2.sdk.assertions.saml2.SAML2AssertionDetails;
import com.nimbusds.oauth2.sdk.assertions.saml2.SAML2AssertionDetailsVerifier;
import com.nimbusds.oauth2.sdk.id.Issuer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.PublicKey;
import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import net.jcip.annotations.ThreadSafe;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.credential.BasicCredential;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@ThreadSafe
public class SAML2AssertionValidator {
    private final SAML2AssertionDetailsVerifier detailsVerifier;

    public SAML2AssertionValidator(SAML2AssertionDetailsVerifier detailsVerifier) {
        if (detailsVerifier == null) {
            throw new IllegalArgumentException("The SAML 2.0 assertion details verifier must not be null");
        }
        this.detailsVerifier = detailsVerifier;
    }

    public SAML2AssertionDetailsVerifier getDetailsVerifier() {
        return this.detailsVerifier;
    }

    public static Assertion parse(String xml) throws ParseException {
        XMLObject xmlObject;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalDTD", "");
        documentBuilderFactory.setAttribute("http://javax.xml.XMLConstants/property/accessExternalSchema", "");
        documentBuilderFactory.setNamespaceAware(true);
        try {
            DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))));
            Element element = document.getDocumentElement();
            xmlObject = XMLObjectProviderRegistrySupport.getUnmarshallerFactory().getUnmarshaller(element).unmarshall(element);
        }
        catch (IOException | ParserConfigurationException | UnmarshallingException | SAXException e) {
            throw new ParseException("SAML 2.0 assertion parsing failed: " + e.getMessage(), e);
        }
        if (!(xmlObject instanceof Assertion)) {
            throw new ParseException("Top-level XML element not a SAML 2.0 assertion");
        }
        return (Assertion)xmlObject;
    }

    public static void verifySignature(Signature signature, Key key) throws BadSAML2AssertionException {
        BasicCredential credential;
        SAMLSignatureProfileValidator profileValidator = new SAMLSignatureProfileValidator();
        try {
            profileValidator.validate(signature);
        }
        catch (SignatureException e) {
            throw new BadSAML2AssertionException("Invalid SAML 2.0 signature format: " + e.getMessage(), e);
        }
        if (key instanceof SecretKey) {
            credential = new BasicCredential((SecretKey)key);
        } else if (key instanceof PublicKey) {
            credential = new BasicCredential((PublicKey)key);
            credential.setUsageType(UsageType.SIGNING);
        } else {
            throw new BadSAML2AssertionException("Unsupported key type: " + key.getAlgorithm());
        }
        try {
            SignatureValidator.validate((Signature)signature, (Credential)credential);
        }
        catch (SignatureException e) {
            throw new BadSAML2AssertionException("Bad SAML 2.0 signature: " + e.getMessage(), e);
        }
    }

    public Assertion validate(Assertion assertion, Issuer expectedIssuer, Key key) throws BadSAML2AssertionException {
        SAML2AssertionDetails assertionDetails;
        try {
            assertionDetails = SAML2AssertionDetails.parse(assertion);
        }
        catch (ParseException e) {
            throw new BadSAML2AssertionException("Invalid SAML 2.0 assertion: " + e.getMessage(), e);
        }
        this.detailsVerifier.verify(assertionDetails);
        if (!expectedIssuer.equals(assertionDetails.getIssuer())) {
            throw new BadSAML2AssertionException("Unexpected issuer: " + assertionDetails.getIssuer());
        }
        if (!assertion.isSigned()) {
            throw new BadSAML2AssertionException("Missing XML signature");
        }
        SAML2AssertionValidator.verifySignature(assertion.getSignature(), key);
        return assertion;
    }

    public Assertion validate(String xml, Issuer expectedIssuer, Key key) throws BadSAML2AssertionException {
        Assertion assertion;
        try {
            assertion = SAML2AssertionValidator.parse(xml);
        }
        catch (ParseException e) {
            throw new BadSAML2AssertionException("Invalid SAML 2.0 assertion: " + e.getMessage(), e);
        }
        return this.validate(assertion, expectedIssuer, key);
    }

    static {
        try {
            InitializationService.initialize();
        }
        catch (InitializationException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

