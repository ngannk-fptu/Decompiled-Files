/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.onelogin.saml2.authn;

import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.exception.ValidationError;
import com.onelogin.saml2.http.HttpRequest;
import com.onelogin.saml2.model.SamlResponseStatus;
import com.onelogin.saml2.model.SubjectConfirmationIssue;
import com.onelogin.saml2.settings.CompatibilityModeViolationHandler;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.util.Constants;
import com.onelogin.saml2.util.SchemaFactory;
import com.onelogin.saml2.util.Util;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SamlResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(SamlResponse.class);
    private final Saml2Settings settings;
    private String samlResponseString;
    private Document samlResponseDocument;
    private Document decryptedDocument;
    private Map<String, String> nameIdData = null;
    private String currentUrl;
    private Boolean encrypted = false;
    private Exception validationException;
    private SamlResponseStatus responseStatus;

    public SamlResponse(Saml2Settings settings, String currentUrl, String samlResponse) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, SettingsException, ValidationError {
        this.settings = settings;
        this.currentUrl = currentUrl;
        this.loadXmlFromBase64(samlResponse);
    }

    public SamlResponse(Saml2Settings settings, HttpRequest request) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, SettingsException, ValidationError {
        this(settings, request.getRequestURL(), request.getParameter("SAMLResponse"));
    }

    public void loadXmlFromBase64(String responseStr) throws ParserConfigurationException, XPathExpressionException, SAXException, IOException, SettingsException, ValidationError {
        this.samlResponseString = new String(Util.base64decoder(responseStr), "UTF-8");
        this.samlResponseDocument = Util.loadXML(this.samlResponseString);
        if (this.samlResponseDocument == null) {
            throw new ValidationError("SAML Response could not be processed", 14);
        }
        NodeList encryptedAssertionNodes = this.samlResponseDocument.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "EncryptedAssertion");
        if (encryptedAssertionNodes.getLength() != 0) {
            this.decryptedDocument = Util.copyDocument(this.samlResponseDocument);
            this.encrypted = true;
            this.decryptedDocument = this.decryptAssertion(this.decryptedDocument);
        }
    }

    public boolean isValid(String requestId) {
        this.validationException = null;
        try {
            Document documentToCheckAssertion;
            if (this.samlResponseDocument == null) {
                throw new Exception("SAML Response is not loaded");
            }
            if (this.currentUrl == null || this.currentUrl.isEmpty()) {
                throw new Exception("The URL of the current host was not established");
            }
            Element rootElement = this.samlResponseDocument.getDocumentElement();
            rootElement.normalize();
            if (!"2.0".equals(rootElement.getAttribute("Version"))) {
                throw new ValidationError("Unsupported SAML Version.", 0);
            }
            if (!rootElement.hasAttribute("ID")) {
                throw new ValidationError("Missing ID attribute on SAML Response.", 1);
            }
            this.checkStatus();
            if (!this.validateNumAssertions().booleanValue()) {
                throw new ValidationError("SAML Response must contain 1 Assertion.", 2);
            }
            ArrayList<String> signedElements = this.processSignedElements();
            String responseTag = "{urn:oasis:names:tc:SAML:2.0:protocol}Response";
            String assertionTag = "{urn:oasis:names:tc:SAML:2.0:assertion}Assertion";
            boolean hasSignedResponse = signedElements.contains(responseTag);
            boolean hasSignedAssertion = signedElements.contains(assertionTag);
            List<String> issuers = null;
            if (this.settings.isStrict()) {
                NodeList encryptedNameIdNodes;
                String responseInResponseTo;
                if (this.settings.getWantXMLValidation()) {
                    if (!Util.validateXML(this.samlResponseDocument, SchemaFactory.SAML_SCHEMA_PROTOCOL_2_0)) {
                        throw new ValidationError("Invalid SAML Response. Not match the saml-schema-protocol-2.0.xsd", 14);
                    }
                    if (this.encrypted.booleanValue() && !Util.validateXML(this.decryptedDocument, SchemaFactory.SAML_SCHEMA_PROTOCOL_2_0)) {
                        throw new ValidationError("Invalid decrypted SAML Response. Not match the saml-schema-protocol-2.0.xsd", 14);
                    }
                }
                String string = responseInResponseTo = rootElement.hasAttribute("InResponseTo") ? rootElement.getAttribute("InResponseTo") : null;
                if (requestId == null && responseInResponseTo != null && this.settings.isRejectUnsolicitedResponsesWithInResponseTo()) {
                    throw new ValidationError("The Response has an InResponseTo attribute: " + responseInResponseTo + " while no InResponseTo was expected", 15);
                }
                if (requestId != null && !Objects.equals(responseInResponseTo, requestId)) {
                    throw new ValidationError("The InResponseTo of the Response: " + responseInResponseTo + ", does not match the ID of the AuthNRequest sent by the SP: " + requestId, 15);
                }
                if (!this.encrypted.booleanValue() && this.settings.getWantAssertionsEncrypted()) {
                    throw new ValidationError("The assertion of the Response is not encrypted and the SP requires it", 16);
                }
                if (this.settings.getWantNameIdEncrypted() && (encryptedNameIdNodes = this.queryAssertion("/saml:Subject/saml:EncryptedID/xenc:EncryptedData")).getLength() == 0) {
                    throw new ValidationError("The NameID of the Response is not encrypted and the SP requires it", 17);
                }
                if (!this.settings.isCompatibilityMode() && !this.checkOneCondition().booleanValue()) {
                    throw new ValidationError("The Assertion must include a Conditions element", 18);
                }
                if (!this.validateTimestamps()) {
                    throw new Exception("Timing issues (please check your clock settings)");
                }
                if (!this.settings.isCompatibilityMode() && !this.checkOneAuthnStatement().booleanValue()) {
                    throw new ValidationError("The Assertion must include an AuthnStatement element", 21);
                }
                NodeList encryptedAttributeNodes = this.queryAssertion("/saml:AttributeStatement/saml:EncryptedAttribute");
                if (encryptedAttributeNodes.getLength() > 0) {
                    throw new ValidationError("There is an EncryptedAttribute in the Response and this SP does not support them", 23);
                }
                this.validateDestination(rootElement);
                this.validateAudiences();
                issuers = this.getIssuers();
                for (String issuer : issuers) {
                    if (!issuer.isEmpty() && issuer.equals(this.settings.getIdpEntityId())) continue;
                    throw new ValidationError(String.format("Invalid issuer in the Assertion/Response. Was '%s', but expected '%s'", issuer, this.settings.getIdpEntityId()), 29);
                }
                DateTime sessionExpiration = this.getSessionNotOnOrAfter();
                if (sessionExpiration != null && ((sessionExpiration = sessionExpiration.plus(Constants.ALOWED_CLOCK_DRIFT * 1000)).isEqualNow() || sessionExpiration.isBeforeNow())) {
                    throw new ValidationError("The attributes have expired, based on the SessionNotOnOrAfter of the AttributeStatement of this Response", 30);
                }
                this.validateSubjectConfirmation(responseInResponseTo);
                if (this.settings.getWantAssertionsSigned() && !hasSignedAssertion) {
                    throw new ValidationError("The Assertion of the Response is not signed and the SP requires it", 33);
                }
                if (this.settings.getWantMessagesSigned() && !hasSignedResponse) {
                    throw new ValidationError("The Message of the Response is not signed and the SP requires it", 32);
                }
            }
            if (signedElements.isEmpty() || !hasSignedAssertion && !hasSignedResponse) {
                throw new ValidationError("No Signature found. SAML Response rejected", 34);
            }
            X509Certificate cert = this.settings.getIdpx509cert();
            ArrayList<X509Certificate> certList = new ArrayList<X509Certificate>();
            List<X509Certificate> multipleCertList = this.settings.getIdpx509certMulti();
            if (multipleCertList != null && !multipleCertList.isEmpty()) {
                certList.addAll(multipleCertList);
            }
            if (cert != null && !certList.contains(cert)) {
                certList.add(0, cert);
            }
            String fingerprint = this.settings.getIdpCertFingerprint();
            String alg = this.settings.getIdpCertFingerprintAlgorithm();
            if (hasSignedResponse && !Util.validateSign(this.samlResponseDocument, certList, fingerprint, alg, "/samlp:Response/ds:Signature")) {
                throw new ValidationError("Signature validation failed. SAML Response rejected", 42);
            }
            Document document = documentToCheckAssertion = this.encrypted != false ? this.decryptedDocument : this.samlResponseDocument;
            if (hasSignedAssertion && !Util.validateSign(documentToCheckAssertion, certList, fingerprint, alg, "/samlp:Response/saml:Assertion/ds:Signature")) {
                throw new ValidationError("Signature validation failed. SAML Response rejected", 42);
            }
            LOGGER.debug("SAMLResponse validated --> {}", (Object)this.samlResponseString);
            if (issuers != null) {
                this.handleConditionlessResponses(issuers);
            }
            return true;
        }
        catch (Exception e) {
            this.validationException = e;
            LOGGER.debug("SAMLResponse invalid --> {}", (Object)this.samlResponseString);
            LOGGER.error(this.validationException.getMessage());
            return false;
        }
    }

    private void handleConditionlessResponses(List<String> issuers) throws XPathExpressionException {
        CompatibilityModeViolationHandler handler;
        int amountOfConditions = this.getConditions().getLength();
        int amountOfAuthnStatements = this.getAuthnStatements().getLength();
        if ((amountOfConditions == 0 || amountOfAuthnStatements != 1) && (handler = this.settings.getCompatibilityModeViolationHandler()) != null) {
            handler.handleCompatibilityModeAssistedReponse(issuers, this.getConditions().getLength() > 0, amountOfAuthnStatements);
        }
    }

    private void validateSubjectConfirmation(String responseInResponseTo) throws XPathExpressionException, ValidationError {
        ArrayList<SubjectConfirmationIssue> validationIssues = new ArrayList<SubjectConfirmationIssue>();
        boolean validSubjectConfirmation = false;
        NodeList subjectConfirmationNodes = this.queryAssertion("/saml:Subject/saml:SubjectConfirmation");
        for (int i = 0; i < subjectConfirmationNodes.getLength(); ++i) {
            Node scn = subjectConfirmationNodes.item(i);
            Node method = scn.getAttributes().getNamedItem("Method");
            if (method != null && !method.getNodeValue().equals("urn:oasis:names:tc:SAML:2.0:cm:bearer")) continue;
            NodeList subjectConfirmationDataNodes = scn.getChildNodes();
            for (int c = 0; c < subjectConfirmationDataNodes.getLength(); ++c) {
                if (subjectConfirmationDataNodes.item(c).getLocalName() == null || !subjectConfirmationDataNodes.item(c).getLocalName().equals("SubjectConfirmationData")) continue;
                Node recipient = subjectConfirmationDataNodes.item(c).getAttributes().getNamedItem("Recipient");
                SubjectConfirmationIssue issue = this.validateRecipient(recipient, i);
                if (issue != null) {
                    validationIssues.add(issue);
                    continue;
                }
                Node inResponseTo = subjectConfirmationDataNodes.item(c).getAttributes().getNamedItem("InResponseTo");
                if (inResponseTo == null && responseInResponseTo != null || inResponseTo != null && !inResponseTo.getNodeValue().equals(responseInResponseTo)) {
                    validationIssues.add(new SubjectConfirmationIssue(i, "SubjectConfirmationData has an invalid InResponseTo value"));
                    continue;
                }
                Node notOnOrAfter = subjectConfirmationDataNodes.item(c).getAttributes().getNamedItem("NotOnOrAfter");
                if (notOnOrAfter == null) {
                    validationIssues.add(new SubjectConfirmationIssue(i, "SubjectConfirmationData doesn't contain a NotOnOrAfter attribute"));
                    continue;
                }
                DateTime noa = Util.parseDateTime(notOnOrAfter.getNodeValue());
                if ((noa = noa.plus(Constants.ALOWED_CLOCK_DRIFT * 1000)).isEqualNow() || noa.isBeforeNow()) {
                    validationIssues.add(new SubjectConfirmationIssue(i, "SubjectConfirmationData is no longer valid"));
                    continue;
                }
                Node notBefore = subjectConfirmationDataNodes.item(c).getAttributes().getNamedItem("NotBefore");
                if (notBefore != null) {
                    DateTime nb = Util.parseDateTime(notBefore.getNodeValue());
                    if ((nb = nb.minus(Constants.ALOWED_CLOCK_DRIFT * 1000)).isAfterNow()) {
                        validationIssues.add(new SubjectConfirmationIssue(i, "SubjectConfirmationData is not yet valid"));
                        continue;
                    }
                }
                validSubjectConfirmation = true;
            }
        }
        if (!validSubjectConfirmation) {
            throw new ValidationError(SubjectConfirmationIssue.prettyPrintIssues(validationIssues), 31);
        }
    }

    public boolean isValid() {
        return this.isValid(null);
    }

    public Map<String, String> getNameIdData() throws Exception {
        NodeList nameIdNodes;
        if (this.nameIdData != null) {
            return this.nameIdData;
        }
        HashMap<String, String> nameIdData = new HashMap<String, String>();
        NodeList encryptedIDNodes = this.queryAssertion("/saml:Subject/saml:EncryptedID");
        if (encryptedIDNodes.getLength() == 1) {
            NodeList encryptedDataNodes = this.queryAssertion("/saml:Subject/saml:EncryptedID/xenc:EncryptedData");
            if (encryptedDataNodes.getLength() == 1) {
                Element encryptedData = (Element)encryptedDataNodes.item(0);
                PrivateKey key = this.settings.getSPkey();
                if (key == null) {
                    throw new SettingsException("Key is required in order to decrypt the NameID", 4);
                }
                Util.decryptElement(encryptedData, key);
            }
            if ((nameIdNodes = this.queryAssertion("/saml:Subject/saml:EncryptedID/saml:NameID|/saml:Subject/saml:NameID")) == null || nameIdNodes.getLength() == 0) {
                throw new Exception("Not able to decrypt the EncryptedID and get a NameID");
            }
        } else {
            nameIdNodes = this.queryAssertion("/saml:Subject/saml:NameID");
        }
        if (nameIdNodes != null && nameIdNodes.getLength() == 1) {
            Element nameIdElem = (Element)nameIdNodes.item(0);
            if (nameIdElem != null) {
                String value = nameIdElem.getTextContent();
                if (this.settings.isStrict() && value.isEmpty()) {
                    throw new ValidationError("An empty NameID value found", 39);
                }
                nameIdData.put("Value", value);
                if (nameIdElem.hasAttribute("Format")) {
                    nameIdData.put("Format", nameIdElem.getAttribute("Format"));
                }
                if (nameIdElem.hasAttribute("SPNameQualifier")) {
                    String spNameQualifier = nameIdElem.getAttribute("SPNameQualifier");
                    this.validateSpNameQualifier(spNameQualifier);
                    nameIdData.put("SPNameQualifier", spNameQualifier);
                }
                if (nameIdElem.hasAttribute("NameQualifier")) {
                    nameIdData.put("NameQualifier", nameIdElem.getAttribute("NameQualifier"));
                }
            }
        } else if (this.settings.getWantNameId()) {
            throw new ValidationError("No name id found in Document.", 38);
        }
        this.nameIdData = nameIdData;
        return nameIdData;
    }

    public String getNameId() throws Exception {
        Map<String, String> nameIdData = this.getNameIdData();
        String nameID = null;
        if (!nameIdData.isEmpty()) {
            LOGGER.debug("SAMLResponse has NameID --> {}", (Object)nameIdData.get("Value"));
            nameID = nameIdData.get("Value");
        }
        return nameID;
    }

    public String getNameIdFormat() throws Exception {
        Map<String, String> nameIdData = this.getNameIdData();
        String nameidFormat = null;
        if (!nameIdData.isEmpty() && nameIdData.containsKey("Format")) {
            LOGGER.debug("SAMLResponse has NameID Format --> {}", (Object)nameIdData.get("Format"));
            nameidFormat = nameIdData.get("Format");
        }
        return nameidFormat;
    }

    public String getNameIdNameQualifier() throws Exception {
        Map<String, String> nameIdData = this.getNameIdData();
        String nameQualifier = null;
        if (!nameIdData.isEmpty() && nameIdData.containsKey("NameQualifier")) {
            LOGGER.debug("SAMLResponse has NameID NameQualifier --> " + nameIdData.get("NameQualifier"));
            nameQualifier = nameIdData.get("NameQualifier");
        }
        return nameQualifier;
    }

    public String getNameIdSPNameQualifier() throws Exception {
        Map<String, String> nameIdData = this.getNameIdData();
        String spNameQualifier = null;
        if (!nameIdData.isEmpty() && nameIdData.containsKey("SPNameQualifier")) {
            LOGGER.debug("SAMLResponse has NameID NameQualifier --> " + nameIdData.get("SPNameQualifier"));
            spNameQualifier = nameIdData.get("SPNameQualifier");
        }
        return spNameQualifier;
    }

    public HashMap<String, List<String>> getAttributes() throws XPathExpressionException, ValidationError {
        HashMap<String, List<String>> attributes = new HashMap<String, List<String>>();
        NodeList nodes = this.queryAssertion("/saml:AttributeStatement/saml:Attribute");
        if (nodes.getLength() != 0) {
            for (int i = 0; i < nodes.getLength(); ++i) {
                NamedNodeMap attrName = nodes.item(i).getAttributes();
                String attName = attrName.getNamedItem("Name").getNodeValue();
                if (attributes.containsKey(attName) && !this.settings.isAllowRepeatAttributeName()) {
                    throw new ValidationError("Found an Attribute element with duplicated Name", 41);
                }
                NodeList childrens = nodes.item(i).getChildNodes();
                List<Object> attrValues = null;
                attrValues = attributes.containsKey(attName) && this.settings.isAllowRepeatAttributeName() ? attributes.get(attName) : new ArrayList();
                for (int j = 0; j < childrens.getLength(); ++j) {
                    if (!"AttributeValue".equals(childrens.item(j).getLocalName())) continue;
                    attrValues.add(childrens.item(j).getTextContent());
                }
                attributes.put(attName, attrValues);
            }
            LOGGER.debug("SAMLResponse has attributes: " + attributes.toString());
        } else {
            LOGGER.debug("SAMLResponse has no attributes");
        }
        return attributes;
    }

    public SamlResponseStatus getResponseStatus() {
        return this.responseStatus;
    }

    public void checkStatus() throws ValidationError {
        this.responseStatus = SamlResponse.getStatus(this.samlResponseDocument);
        if (!this.responseStatus.is("urn:oasis:names:tc:SAML:2.0:status:Success")) {
            String statusExceptionMsg = "The status code of the Response was not Success, was " + this.responseStatus.getStatusCode();
            if (this.responseStatus.getStatusMessage() != null) {
                statusExceptionMsg = statusExceptionMsg + " -> " + this.responseStatus.getStatusMessage();
            }
            throw new ValidationError(statusExceptionMsg, 5);
        }
    }

    public static SamlResponseStatus getStatus(Document dom) throws ValidationError {
        String statusXpath = "/samlp:Response/samlp:Status";
        return Util.getStatus(statusXpath, dom);
    }

    public Boolean checkOneCondition() throws XPathExpressionException {
        NodeList entries = this.getConditions();
        if (entries.getLength() == 1) {
            return true;
        }
        return false;
    }

    private NodeList getConditions() throws XPathExpressionException {
        return this.queryAssertion("/saml:Conditions");
    }

    public Boolean checkOneAuthnStatement() throws XPathExpressionException {
        NodeList entries = this.getAuthnStatements();
        if (entries.getLength() == 1) {
            return true;
        }
        return false;
    }

    private NodeList getAuthnStatements() throws XPathExpressionException {
        return this.queryAssertion("/saml:AuthnStatement");
    }

    public List<String> getAudiences() throws XPathExpressionException {
        ArrayList<String> audiences = new ArrayList<String>();
        NodeList entries = this.queryAssertion("/saml:Conditions/saml:AudienceRestriction/saml:Audience");
        for (int i = 0; i < entries.getLength(); ++i) {
            String value;
            if (entries.item(i) == null || (value = entries.item(i).getTextContent()) == null || value.trim().isEmpty()) continue;
            audiences.add(value.trim());
        }
        return audiences;
    }

    public List<String> getIssuers() throws XPathExpressionException, ValidationError {
        NodeList assertionIssuer;
        String value;
        ArrayList<String> issuers = new ArrayList<String>();
        NodeList responseIssuer = Util.query(this.samlResponseDocument, "/samlp:Response/saml:Issuer");
        if (responseIssuer.getLength() > 0) {
            if (responseIssuer.getLength() == 1) {
                value = responseIssuer.item(0).getTextContent();
                if (!issuers.contains(value)) {
                    issuers.add(value);
                }
            } else {
                throw new ValidationError("Issuer of the Response is multiple.", 27);
            }
        }
        if ((assertionIssuer = this.queryAssertion("/saml:Issuer")).getLength() == 1) {
            value = assertionIssuer.item(0).getTextContent();
            if (!issuers.contains(value)) {
                issuers.add(value);
            }
        } else {
            throw new ValidationError("Issuer of the Assertion not found or multiple.", 28);
        }
        return issuers;
    }

    public DateTime getSessionNotOnOrAfter() throws XPathExpressionException {
        String notOnOrAfter = null;
        NodeList entries = this.queryAssertion("/saml:AuthnStatement[@SessionNotOnOrAfter]");
        if (entries.getLength() > 0) {
            notOnOrAfter = entries.item(0).getAttributes().getNamedItem("SessionNotOnOrAfter").getNodeValue();
            return Util.parseDateTime(notOnOrAfter);
        }
        return null;
    }

    public String getSessionIndex() throws XPathExpressionException {
        String sessionIndex = null;
        NodeList entries = this.queryAssertion("/saml:AuthnStatement[@SessionIndex]");
        if (entries.getLength() > 0) {
            sessionIndex = entries.item(0).getAttributes().getNamedItem("SessionIndex").getNodeValue();
        }
        return sessionIndex;
    }

    public String getId() {
        return this.samlResponseDocument.getDocumentElement().getAttributes().getNamedItem("ID").getNodeValue();
    }

    public String getAssertionId() throws XPathExpressionException {
        if (!this.validateNumAssertions().booleanValue()) {
            throw new IllegalArgumentException("SAML Response must contain 1 Assertion.");
        }
        NodeList assertionNode = this.queryAssertion("");
        return assertionNode.item(0).getAttributes().getNamedItem("ID").getNodeValue();
    }

    public List<Instant> getAssertionNotOnOrAfter() throws XPathExpressionException {
        NodeList notOnOrAfterNodes = this.queryAssertion("/saml:Subject/saml:SubjectConfirmation/saml:SubjectConfirmationData");
        ArrayList<Instant> notOnOrAfters = new ArrayList<Instant>();
        for (int i = 0; i < notOnOrAfterNodes.getLength(); ++i) {
            Node notOnOrAfterAttribute = notOnOrAfterNodes.item(i).getAttributes().getNamedItem("NotOnOrAfter");
            if (notOnOrAfterAttribute == null) continue;
            notOnOrAfters.add(new Instant(notOnOrAfterAttribute.getNodeValue()));
        }
        return notOnOrAfters;
    }

    public Boolean validateNumAssertions() throws IllegalArgumentException {
        NodeList encryptedAssertionNodes = this.samlResponseDocument.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "EncryptedAssertion");
        NodeList assertionNodes = this.samlResponseDocument.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "Assertion");
        Boolean valid = assertionNodes.getLength() + encryptedAssertionNodes.getLength() == 1;
        if (this.encrypted.booleanValue()) {
            valid = valid != false && this.decryptedDocument.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "Assertion").getLength() == 1;
        }
        return valid;
    }

    public ArrayList<String> processSignedElements() throws XPathExpressionException, ValidationError {
        ArrayList<String> signedElements = new ArrayList<String>();
        ArrayList<String> verifiedSeis = new ArrayList<String>();
        ArrayList<String> verifiedIds = new ArrayList<String>();
        NodeList signNodes = this.query("//ds:Signature", null);
        for (int i = 0; i < signNodes.getLength(); ++i) {
            Node signNode = signNodes.item(i);
            String signedElement = "{" + signNode.getParentNode().getNamespaceURI() + "}" + signNode.getParentNode().getLocalName();
            String responseTag = "{urn:oasis:names:tc:SAML:2.0:protocol}Response";
            String assertionTag = "{urn:oasis:names:tc:SAML:2.0:assertion}Assertion";
            if (!signedElement.equals(responseTag) && !signedElement.equals(assertionTag)) {
                throw new ValidationError("Invalid Signature Element " + signedElement + " SAML Response rejected", 6);
            }
            Node idNode = signNode.getParentNode().getAttributes().getNamedItem("ID");
            if (idNode == null || idNode.getNodeValue() == null || idNode.getNodeValue().isEmpty()) {
                throw new ValidationError("Signed Element must contain an ID. SAML Response rejected", 7);
            }
            String idValue = idNode.getNodeValue();
            if (verifiedIds.contains(idValue)) {
                throw new ValidationError("Duplicated ID. SAML Response rejected", 8);
            }
            verifiedIds.add(idValue);
            NodeList refNodes = Util.query(null, "ds:SignedInfo/ds:Reference", signNode);
            if (refNodes.getLength() == 1) {
                Node refNode = refNodes.item(0);
                Node seiNode = refNode.getAttributes().getNamedItem("URI");
                if (seiNode != null && seiNode.getNodeValue() != null && !seiNode.getNodeValue().isEmpty()) {
                    String sei = seiNode.getNodeValue().substring(1);
                    if (!sei.equals(idValue)) {
                        throw new ValidationError("Found an invalid Signed Element. SAML Response rejected", 9);
                    }
                    if (verifiedSeis.contains(sei)) {
                        throw new ValidationError("Duplicated Reference URI. SAML Response rejected", 10);
                    }
                    verifiedSeis.add(sei);
                }
            } else {
                throw new ValidationError("Unexpected number of Reference nodes found for signature. SAML Response rejected.", 45);
            }
            signedElements.add(signedElement);
        }
        if (!signedElements.isEmpty() && !this.validateSignedElements(signedElements)) {
            throw new ValidationError("Found an unexpected Signature Element. SAML Response rejected", 11);
        }
        return signedElements;
    }

    public boolean validateSignedElements(ArrayList<String> signedElements) throws XPathExpressionException, ValidationError {
        NodeList expectedSignatureNode;
        if (signedElements.size() > 2) {
            return false;
        }
        HashMap<String, Integer> occurrences = new HashMap<String, Integer>();
        for (String e : signedElements) {
            if (occurrences.containsKey(e)) {
                occurrences.put(e, (Integer)occurrences.get(e) + 1);
                continue;
            }
            occurrences.put(e, 1);
        }
        String responseTag = "{urn:oasis:names:tc:SAML:2.0:protocol}Response";
        String assertionTag = "{urn:oasis:names:tc:SAML:2.0:assertion}Assertion";
        if (occurrences.containsKey(responseTag) && (Integer)occurrences.get(responseTag) > 1 || occurrences.containsKey(assertionTag) && (Integer)occurrences.get(assertionTag) > 1 || !occurrences.containsKey(responseTag) && !occurrences.containsKey(assertionTag)) {
            return false;
        }
        if (occurrences.containsKey(responseTag) && (expectedSignatureNode = this.query("/samlp:Response/ds:Signature", null)).getLength() != 1) {
            throw new ValidationError("Unexpected number of Response signatures found. SAML Response rejected.", 12);
        }
        if (occurrences.containsKey(assertionTag) && (expectedSignatureNode = this.query("/samlp:Response/saml:Assertion/ds:Signature", null)).getLength() != 1) {
            throw new ValidationError("Unexpected number of Assertion signatures found. SAML Response rejected.", 13);
        }
        return true;
    }

    public boolean validateTimestamps() throws ValidationError {
        NodeList timestampNodes = this.samlResponseDocument.getElementsByTagNameNS("*", "Conditions");
        if (timestampNodes.getLength() != 0) {
            for (int i = 0; i < timestampNodes.getLength(); ++i) {
                NamedNodeMap attrName = timestampNodes.item(i).getAttributes();
                Node nbAttribute = attrName.getNamedItem("NotBefore");
                Node naAttribute = attrName.getNamedItem("NotOnOrAfter");
                if (naAttribute != null) {
                    DateTime notOnOrAfterDate = Util.parseDateTime(naAttribute.getNodeValue());
                    if ((notOnOrAfterDate = notOnOrAfterDate.plus(Constants.ALOWED_CLOCK_DRIFT * 1000)).isEqualNow() || notOnOrAfterDate.isBeforeNow()) {
                        throw new ValidationError("Could not validate timestamp: expired. Check system clock.", 20);
                    }
                }
                if (nbAttribute == null) continue;
                DateTime notBeforeDate = Util.parseDateTime(nbAttribute.getNodeValue());
                if (!(notBeforeDate = notBeforeDate.minus(Constants.ALOWED_CLOCK_DRIFT * 1000)).isAfterNow()) continue;
                throw new ValidationError("Could not validate timestamp: not yet valid. Check system clock.", 19);
            }
        }
        return true;
    }

    public void setDestinationUrl(String url) {
        this.currentUrl = url;
    }

    public String getError() {
        if (this.validationException != null) {
            return this.validationException.getMessage();
        }
        return null;
    }

    public Exception getValidationException() {
        return this.validationException;
    }

    private NodeList queryAssertion(String assertionXpath) throws XPathExpressionException {
        String nameQuery;
        String assertionExpr = "/saml:Assertion";
        String signatureExpr = "ds:Signature/ds:SignedInfo/ds:Reference";
        String signedAssertionQuery = "/samlp:Response/saml:Assertion/ds:Signature/ds:SignedInfo/ds:Reference";
        NodeList nodeList = this.query(signedAssertionQuery, null);
        if (nodeList.getLength() == 0) {
            String signedMessageQuery = "/samlp:Response/ds:Signature/ds:SignedInfo/ds:Reference";
            nodeList = this.query(signedMessageQuery, null);
            if (nodeList.getLength() == 1) {
                Node responseReferenceNode = nodeList.item(0);
                String responseId = responseReferenceNode.getAttributes().getNamedItem("URI").getNodeValue();
                responseId = responseId != null && !responseId.isEmpty() ? responseId.substring(1) : responseReferenceNode.getParentNode().getParentNode().getParentNode().getAttributes().getNamedItem("ID").getNodeValue();
                nameQuery = "/samlp:Response[@ID='" + responseId + "']";
            } else {
                nameQuery = "/samlp:Response";
            }
            nameQuery = nameQuery + "/saml:Assertion";
        } else {
            Node assertionReferenceNode = nodeList.item(0);
            String assertionId = assertionReferenceNode.getAttributes().getNamedItem("URI").getNodeValue();
            assertionId = assertionId != null && !assertionId.isEmpty() ? assertionId.substring(1) : assertionReferenceNode.getParentNode().getParentNode().getParentNode().getAttributes().getNamedItem("ID").getNodeValue();
            nameQuery = "/samlp:Response//saml:Assertion[@ID='" + assertionId + "']";
        }
        nameQuery = nameQuery + assertionXpath;
        return this.query(nameQuery, null);
    }

    private NodeList query(String nameQuery, Node context) throws XPathExpressionException {
        Document doc = this.encrypted != false ? this.decryptedDocument : this.samlResponseDocument;
        return Util.query(doc, nameQuery, context);
    }

    private Document decryptAssertion(Document dom) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, SettingsException, ValidationError {
        PrivateKey key = this.settings.getSPkey();
        if (key == null) {
            throw new SettingsException("No private key available for decrypt, check settings", 4);
        }
        NodeList encryptedDataNodes = Util.query(dom, "/samlp:Response/saml:EncryptedAssertion/xenc:EncryptedData");
        if (encryptedDataNodes.getLength() == 0) {
            throw new ValidationError("No /samlp:Response/saml:EncryptedAssertion/xenc:EncryptedData element found", 48);
        }
        Element encryptedData = (Element)encryptedDataNodes.item(0);
        Util.decryptElement(encryptedData, key);
        NodeList AssertionDataNodes = Util.query(dom, "/samlp:Response/saml:EncryptedAssertion/saml:Assertion");
        if (encryptedDataNodes.getLength() == 0) {
            throw new ValidationError("No /samlp:Response/saml:EncryptedAssertion/saml:Assertion element found", 48);
        }
        Node assertionNode = AssertionDataNodes.item(0);
        assertionNode.getParentNode().getParentNode().replaceChild(assertionNode, assertionNode.getParentNode());
        String xmlStr = Util.convertDocumentToString(dom);
        Document doc = Util.convertStringToDocument(xmlStr);
        return doc;
    }

    public String getSAMLResponseXml() {
        String xml = this.encrypted != false ? Util.convertDocumentToString(this.decryptedDocument) : this.samlResponseString;
        return xml;
    }

    protected Document getSAMLResponseDocument() {
        Document doc = this.encrypted != false ? this.decryptedDocument : this.samlResponseDocument;
        return doc;
    }

    protected void validateAudiences() throws XPathExpressionException, ValidationError {
        List<String> validAudiences = this.getAudiences();
        if (!validAudiences.isEmpty() && !validAudiences.contains(this.settings.getSpEntityId())) {
            throw new ValidationError(this.settings.getSpEntityId() + " is not a valid audience for this Response", 26);
        }
    }

    protected void validateDestination(Element element) throws ValidationError {
        String destinationUrl;
        if (element.hasAttribute("Destination") && (destinationUrl = element.getAttribute("Destination")) != null) {
            if (destinationUrl.isEmpty()) {
                throw new ValidationError("The response has an empty Destination value", 25);
            }
            if (!destinationUrl.equals(this.currentUrl)) {
                throw new ValidationError("The response was received at " + this.currentUrl + " instead of " + destinationUrl, 24);
            }
        }
    }

    protected SubjectConfirmationIssue validateRecipient(Node recipient, int index) {
        if (recipient == null) {
            return new SubjectConfirmationIssue(index, "SubjectConfirmationData doesn't contain a Recipient");
        }
        if (!recipient.getNodeValue().equals(this.currentUrl)) {
            return new SubjectConfirmationIssue(index, "SubjectConfirmationData doesn't match a valid Recipient");
        }
        return null;
    }

    protected void validateSpNameQualifier(String spNameQualifier) throws ValidationError {
        if (this.settings.isStrict() && !spNameQualifier.equals(this.settings.getSpEntityId())) {
            throw new ValidationError("The SPNameQualifier value mismatch the SP entityID value.", 40);
        }
    }
}

