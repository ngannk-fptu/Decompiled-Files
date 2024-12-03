/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.text.StrSubstitutor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.onelogin.saml2.logout;

import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.exception.ValidationError;
import com.onelogin.saml2.exception.XMLEntityException;
import com.onelogin.saml2.http.HttpRequest;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.util.SchemaFactory;
import com.onelogin.saml2.util.Util;
import java.io.IOException;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LogoutRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutRequest.class);
    private final String logoutRequestString;
    public String id;
    private final Saml2Settings settings;
    private final HttpRequest request;
    private String nameId;
    private String nameIdFormat;
    private String nameIdNameQualifier;
    private String nameIdSPNameQualifier;
    private String sessionIndex;
    private String currentUrl;
    private Calendar issueInstant;
    private Exception validationException;

    public LogoutRequest(Saml2Settings settings, HttpRequest request, String nameId, String sessionIndex, String nameIdFormat, String nameIdNameQualifier, String nameIdSPNameQualifier) throws XMLEntityException {
        this.settings = settings;
        this.request = request;
        String samlLogoutRequest = null;
        if (request != null) {
            samlLogoutRequest = request.getParameter("SAMLRequest");
            this.currentUrl = request.getRequestURL();
        }
        if (samlLogoutRequest == null) {
            this.id = Util.generateUniqueID(settings.getUniqueIDPrefix());
            this.issueInstant = Calendar.getInstance();
            this.nameId = nameId;
            this.nameIdFormat = nameIdFormat;
            this.nameIdNameQualifier = nameIdNameQualifier;
            this.nameIdSPNameQualifier = nameIdSPNameQualifier;
            this.sessionIndex = sessionIndex;
            StrSubstitutor substitutor = this.generateSubstitutor(settings);
            this.logoutRequestString = substitutor.replace((CharSequence)LogoutRequest.getLogoutRequestTemplate());
        } else {
            this.logoutRequestString = Util.base64decodedInflated(samlLogoutRequest);
            this.id = LogoutRequest.getId(this.logoutRequestString);
        }
    }

    public LogoutRequest(Saml2Settings settings, HttpRequest request, String nameId, String sessionIndex, String nameIdFormat, String nameIdNameQualifier) throws XMLEntityException {
        this(settings, request, nameId, sessionIndex, nameIdFormat, nameIdNameQualifier, null);
    }

    public LogoutRequest(Saml2Settings settings, HttpRequest request, String nameId, String sessionIndex, String nameIdFormat) throws XMLEntityException {
        this(settings, request, nameId, sessionIndex, nameIdFormat, null);
    }

    public LogoutRequest(Saml2Settings settings, HttpRequest request, String nameId, String sessionIndex) throws XMLEntityException {
        this(settings, request, nameId, sessionIndex, null);
    }

    public LogoutRequest(Saml2Settings settings) throws XMLEntityException {
        this(settings, null, null, null);
    }

    public LogoutRequest(Saml2Settings settings, HttpRequest request) throws XMLEntityException {
        this(settings, request, null, null);
    }

    public String getEncodedLogoutRequest(Boolean deflated) throws IOException {
        if (deflated == null) {
            deflated = this.settings.isCompressRequestEnabled();
        }
        String encodedLogoutRequest = deflated != false ? Util.deflatedBase64encoded(this.getLogoutRequestXml()) : Util.base64encoder(this.getLogoutRequestXml());
        return encodedLogoutRequest;
    }

    public String getEncodedLogoutRequest() throws IOException {
        return this.getEncodedLogoutRequest(null);
    }

    public String getLogoutRequestXml() {
        return this.logoutRequestString;
    }

    private StrSubstitutor generateSubstitutor(Saml2Settings settings) {
        List<X509Certificate> multipleCertList;
        HashMap<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("id", this.id);
        String issueInstantString = Util.formatDateTime(this.issueInstant.getTimeInMillis());
        valueMap.put("issueInstant", issueInstantString);
        String destinationStr = "";
        URL slo = settings.getIdpSingleLogoutServiceUrl();
        if (slo != null) {
            destinationStr = " Destination=\"" + slo.toString() + "\"";
        }
        valueMap.put("destinationStr", destinationStr);
        valueMap.put("issuer", settings.getSpEntityId());
        String nameIdFormat = null;
        String spNameQualifier = this.nameIdSPNameQualifier;
        String nameQualifier = this.nameIdNameQualifier;
        if (this.nameId != null) {
            nameIdFormat = this.nameIdFormat == null && !settings.getSpNameIDFormat().equals("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified") ? settings.getSpNameIDFormat() : this.nameIdFormat;
        } else {
            this.nameId = settings.getIdpEntityId();
            nameIdFormat = "urn:oasis:names:tc:SAML:2.0:nameid-format:entity";
        }
        if (nameIdFormat != null && nameIdFormat.equals("urn:oasis:names:tc:SAML:2.0:nameid-format:entity")) {
            nameQualifier = null;
            spNameQualifier = null;
        }
        if (nameIdFormat != null && nameIdFormat.equals("urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified")) {
            nameIdFormat = null;
        }
        X509Certificate cert = null;
        if (settings.getNameIdEncrypted() && (cert = settings.getIdpx509cert()) == null && (multipleCertList = settings.getIdpx509certMulti()) != null && !multipleCertList.isEmpty()) {
            cert = multipleCertList.get(0);
        }
        String nameIdStr = Util.generateNameId(this.nameId, spNameQualifier, nameIdFormat, nameQualifier, cert);
        valueMap.put("nameIdStr", nameIdStr);
        String sessionIndexStr = "";
        if (this.sessionIndex != null) {
            sessionIndexStr = " <samlp:SessionIndex>" + this.sessionIndex + "</samlp:SessionIndex>";
        }
        valueMap.put("sessionIndexStr", sessionIndexStr);
        return new StrSubstitutor(valueMap);
    }

    private static StringBuilder getLogoutRequestTemplate() {
        StringBuilder template = new StringBuilder();
        template.append("<samlp:LogoutRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ");
        template.append("ID=\"${id}\" ");
        template.append("Version=\"2.0\" ");
        template.append("IssueInstant=\"${issueInstant}\"${destinationStr} >");
        template.append("<saml:Issuer>${issuer}</saml:Issuer>");
        template.append("${nameIdStr}${sessionIndexStr}</samlp:LogoutRequest>");
        return template;
    }

    public Boolean isValid() throws Exception {
        this.validationException = null;
        try {
            if (this.logoutRequestString == null || this.logoutRequestString.isEmpty()) {
                throw new ValidationError("SAML Logout Request is not loaded", 14);
            }
            if (this.request == null) {
                throw new Exception("The HttpRequest of the current host was not established");
            }
            if (this.currentUrl == null || this.currentUrl.isEmpty()) {
                throw new Exception("The URL of the current host was not established");
            }
            String signature = this.request.getParameter("Signature");
            Document logoutRequestDocument = Util.loadXML(this.logoutRequestString);
            if (this.settings.isStrict()) {
                String destinationUrl;
                String notOnOrAfter;
                DateTime notOnOrAfterDate;
                Element rootElement = logoutRequestDocument.getDocumentElement();
                rootElement.normalize();
                if (this.settings.getWantXMLValidation() && !Util.validateXML(logoutRequestDocument, SchemaFactory.SAML_SCHEMA_PROTOCOL_2_0)) {
                    throw new ValidationError("Invalid SAML Logout Request. Not match the saml-schema-protocol-2.0.xsd", 14);
                }
                if (rootElement.hasAttribute("NotOnOrAfter") && ((notOnOrAfterDate = Util.parseDateTime(notOnOrAfter = rootElement.getAttribute("NotOnOrAfter"))).isEqualNow() || notOnOrAfterDate.isBeforeNow())) {
                    throw new ValidationError("Could not validate timestamp: expired. Check system clock.", 44);
                }
                if (rootElement.hasAttribute("Destination") && (destinationUrl = rootElement.getAttribute("Destination")) != null && !destinationUrl.isEmpty() && !destinationUrl.equals(this.currentUrl)) {
                    throw new ValidationError("The LogoutRequest was received at " + this.currentUrl + " instead of " + destinationUrl, 24);
                }
                String nameID = LogoutRequest.getNameId(logoutRequestDocument, this.settings.getSPkey());
                String issuer = LogoutRequest.getIssuer(logoutRequestDocument);
                if (issuer != null && (issuer.isEmpty() || !issuer.equals(this.settings.getIdpEntityId()))) {
                    throw new ValidationError(String.format("Invalid issuer in the Logout Request. Was '%s', but expected '%s'", issuer, this.settings.getIdpEntityId()), 29);
                }
                if (this.settings.getWantMessagesSigned() && (signature == null || signature.isEmpty())) {
                    throw new ValidationError("The Message of the Logout Request is not signed and the SP requires it", 32);
                }
            }
            if (signature != null && !signature.isEmpty()) {
                X509Certificate cert = this.settings.getIdpx509cert();
                ArrayList<X509Certificate> certList = new ArrayList<X509Certificate>();
                List<X509Certificate> multipleCertList = this.settings.getIdpx509certMulti();
                if (multipleCertList != null && multipleCertList.size() != 0) {
                    certList.addAll(multipleCertList);
                }
                if (cert != null && (certList.isEmpty() || !certList.contains(cert))) {
                    certList.add(0, cert);
                }
                if (certList.isEmpty()) {
                    throw new SettingsException("In order to validate the sign on the Logout Request, the x509cert of the IdP is required", 3);
                }
                String signAlg = this.request.getParameter("SigAlg");
                if (signAlg == null || signAlg.isEmpty()) {
                    signAlg = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
                }
                String relayState = this.request.getEncodedParameter("RelayState");
                String signedQuery = "SAMLRequest=" + this.request.getEncodedParameter("SAMLRequest");
                if (relayState != null && !relayState.isEmpty()) {
                    signedQuery = signedQuery + "&RelayState=" + relayState;
                }
                if (!Util.validateBinarySignature(signedQuery = signedQuery + "&SigAlg=" + this.request.getEncodedParameter("SigAlg", signAlg), Util.base64decoder(signature), certList, signAlg).booleanValue()) {
                    throw new ValidationError("Signature validation failed. Logout Request rejected", 42);
                }
            }
            LOGGER.debug("LogoutRequest validated --> " + this.logoutRequestString);
            return true;
        }
        catch (Exception e) {
            this.validationException = e;
            LOGGER.debug("LogoutRequest invalid --> " + this.logoutRequestString);
            LOGGER.error(this.validationException.getMessage());
            return false;
        }
    }

    public static String getId(Document samlLogoutRequestDocument) {
        String id = null;
        try {
            Element rootElement = samlLogoutRequestDocument.getDocumentElement();
            rootElement.normalize();
            id = rootElement.getAttribute("ID");
        }
        catch (Exception exception) {
            // empty catch block
        }
        return id;
    }

    public static String getId(String samlLogoutRequestString) {
        Document doc = Util.loadXML(samlLogoutRequestString);
        return LogoutRequest.getId(doc);
    }

    public static Map<String, String> getNameIdData(Document samlLogoutRequestDocument, PrivateKey key) throws Exception {
        NodeList nameIdNodes;
        NodeList encryptedIDNodes = Util.query(samlLogoutRequestDocument, "/samlp:LogoutRequest/saml:EncryptedID");
        if (encryptedIDNodes.getLength() == 1) {
            if (key == null) {
                throw new SettingsException("Key is required in order to decrypt the NameID", 4);
            }
            Element encryptedData = (Element)encryptedIDNodes.item(0);
            Util.decryptElement(encryptedData, key);
            nameIdNodes = Util.query(samlLogoutRequestDocument, "/samlp:LogoutRequest/saml:NameID");
            if (nameIdNodes == null || nameIdNodes.getLength() != 1) {
                throw new Exception("Not able to decrypt the EncryptedID and get a NameID");
            }
        } else {
            nameIdNodes = Util.query(samlLogoutRequestDocument, "/samlp:LogoutRequest/saml:NameID");
        }
        if (nameIdNodes == null || nameIdNodes.getLength() != 1) {
            throw new ValidationError("No name id found in Logout Request.", 38);
        }
        Element nameIdElem = (Element)nameIdNodes.item(0);
        HashMap<String, String> nameIdData = new HashMap<String, String>();
        if (nameIdElem != null) {
            nameIdData.put("Value", nameIdElem.getTextContent());
            if (nameIdElem.hasAttribute("Format")) {
                nameIdData.put("Format", nameIdElem.getAttribute("Format"));
            }
            if (nameIdElem.hasAttribute("SPNameQualifier")) {
                nameIdData.put("SPNameQualifier", nameIdElem.getAttribute("SPNameQualifier"));
            }
            if (nameIdElem.hasAttribute("NameQualifier")) {
                nameIdData.put("NameQualifier", nameIdElem.getAttribute("NameQualifier"));
            }
        }
        return nameIdData;
    }

    public static Map<String, String> getNameIdData(String samlLogoutRequestString, PrivateKey key) throws Exception {
        Document doc = Util.loadXML(samlLogoutRequestString);
        return LogoutRequest.getNameIdData(doc, key);
    }

    public static String getNameId(Document samlLogoutRequestDocument, PrivateKey key) throws Exception {
        Map<String, String> nameIdData = LogoutRequest.getNameIdData(samlLogoutRequestDocument, key);
        LOGGER.debug("LogoutRequest has NameID --> " + nameIdData.get("Value"));
        return nameIdData.get("Value");
    }

    public static String getNameId(Document samlLogoutRequestDocument) throws Exception {
        return LogoutRequest.getNameId(samlLogoutRequestDocument, null);
    }

    public static String getNameId(String samlLogoutRequestString, PrivateKey key) throws Exception {
        Map<String, String> nameId = LogoutRequest.getNameIdData(samlLogoutRequestString, key);
        return nameId.get("Value");
    }

    public static String getNameId(String samlLogoutRequestString) throws Exception {
        return LogoutRequest.getNameId(samlLogoutRequestString, null);
    }

    public static String getIssuer(Document samlLogoutRequestDocument) throws XPathExpressionException {
        String issuer = null;
        NodeList nodes = Util.query(samlLogoutRequestDocument, "/samlp:LogoutRequest/saml:Issuer");
        if (nodes.getLength() == 1) {
            issuer = nodes.item(0).getTextContent();
        }
        return issuer;
    }

    public static String getIssuer(String samlLogoutRequestString) throws XPathExpressionException {
        Document doc = Util.loadXML(samlLogoutRequestString);
        return LogoutRequest.getIssuer(doc);
    }

    public static List<String> getSessionIndexes(Document samlLogoutRequestDocument) throws XPathExpressionException {
        ArrayList<String> sessionIndexes = new ArrayList<String>();
        NodeList nodes = Util.query(samlLogoutRequestDocument, "/samlp:LogoutRequest/samlp:SessionIndex");
        for (int i = 0; i < nodes.getLength(); ++i) {
            sessionIndexes.add(nodes.item(i).getTextContent());
        }
        return sessionIndexes;
    }

    public static List<String> getSessionIndexes(String samlLogoutRequestString) throws XPathExpressionException {
        Document doc = Util.loadXML(samlLogoutRequestString);
        return LogoutRequest.getSessionIndexes(doc);
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

    public String getId() {
        return this.id;
    }
}

