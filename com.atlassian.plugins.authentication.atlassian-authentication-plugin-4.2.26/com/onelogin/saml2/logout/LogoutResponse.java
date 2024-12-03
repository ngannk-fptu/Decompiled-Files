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
import com.onelogin.saml2.http.HttpRequest;
import com.onelogin.saml2.model.SamlResponseStatus;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.util.SchemaFactory;
import com.onelogin.saml2.util.Util;
import java.io.IOException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class LogoutResponse {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogoutResponse.class);
    private String logoutResponseString;
    private Document logoutResponseDocument;
    private String id;
    private final Saml2Settings settings;
    private final HttpRequest request;
    private String currentUrl;
    private String inResponseTo;
    private Calendar issueInstant;
    private Exception validationException;
    private SamlResponseStatus responseStatus;

    public LogoutResponse(Saml2Settings settings, HttpRequest request) {
        this.settings = settings;
        this.request = request;
        String samlLogoutResponse = null;
        if (request != null) {
            this.currentUrl = request.getRequestURL();
            samlLogoutResponse = request.getParameter("SAMLResponse");
        }
        if (samlLogoutResponse != null && !samlLogoutResponse.isEmpty()) {
            this.logoutResponseString = Util.base64decodedInflated(samlLogoutResponse);
            this.logoutResponseDocument = Util.loadXML(this.logoutResponseString);
        }
    }

    public String getEncodedLogoutResponse(Boolean deflated) throws IOException {
        if (deflated == null) {
            deflated = this.settings.isCompressResponseEnabled();
        }
        String encodedLogoutResponse = deflated != false ? Util.deflatedBase64encoded(this.getLogoutResponseXml()) : Util.base64encoder(this.getLogoutResponseXml());
        return encodedLogoutResponse;
    }

    public String getEncodedLogoutResponse() throws IOException {
        return this.getEncodedLogoutResponse(null);
    }

    public String getLogoutResponseXml() {
        return this.logoutResponseString;
    }

    public String getId() {
        String idvalue = null;
        if (this.id != null) {
            idvalue = this.id;
        } else if (this.logoutResponseDocument != null) {
            idvalue = this.logoutResponseDocument.getDocumentElement().getAttributes().getNamedItem("ID").getNodeValue();
        }
        return idvalue;
    }

    public Boolean isValid(String requestId) {
        this.validationException = null;
        try {
            if (this.logoutResponseDocument == null) {
                throw new ValidationError("SAML Logout Response is not loaded", 14);
            }
            if (this.currentUrl == null || this.currentUrl.isEmpty()) {
                throw new Exception("The URL of the current host was not established");
            }
            String signature = this.request.getParameter("Signature");
            if (this.settings.isStrict()) {
                String destinationUrl;
                String responseInResponseTo;
                Element rootElement = this.logoutResponseDocument.getDocumentElement();
                rootElement.normalize();
                if (this.settings.getWantXMLValidation() && !Util.validateXML(this.logoutResponseDocument, SchemaFactory.SAML_SCHEMA_PROTOCOL_2_0)) {
                    throw new ValidationError("Invalid SAML Logout Response. Not match the saml-schema-protocol-2.0.xsd", 14);
                }
                String string = responseInResponseTo = rootElement.hasAttribute("InResponseTo") ? rootElement.getAttribute("InResponseTo") : null;
                if (requestId == null && responseInResponseTo != null && this.settings.isRejectUnsolicitedResponsesWithInResponseTo()) {
                    throw new ValidationError("The Response has an InResponseTo attribute: " + responseInResponseTo + " while no InResponseTo was expected", 15);
                }
                if (requestId != null && !Objects.equals(responseInResponseTo, requestId)) {
                    throw new ValidationError("The InResponseTo of the Logout Response: " + responseInResponseTo + ", does not match the ID of the Logout request sent by the SP: " + requestId, 15);
                }
                String issuer = this.getIssuer();
                if (issuer != null && !issuer.isEmpty() && !issuer.equals(this.settings.getIdpEntityId())) {
                    throw new ValidationError(String.format("Invalid issuer in the Logout Response. Was '%s', but expected '%s'", issuer, this.settings.getIdpEntityId()), 29);
                }
                if (rootElement.hasAttribute("Destination") && (destinationUrl = rootElement.getAttribute("Destination")) != null && !destinationUrl.isEmpty() && !destinationUrl.equals(this.currentUrl)) {
                    throw new ValidationError("The LogoutResponse was received at " + this.currentUrl + " instead of " + destinationUrl, 24);
                }
                if (this.settings.getWantMessagesSigned() && (signature == null || signature.isEmpty())) {
                    throw new ValidationError("The Message of the Logout Response is not signed and the SP requires it", 32);
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
                    throw new SettingsException("In order to validate the sign on the Logout Response, the x509cert of the IdP is required", 3);
                }
                String signAlg = this.request.getParameter("SigAlg");
                if (signAlg == null || signAlg.isEmpty()) {
                    signAlg = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
                }
                String signedQuery = "SAMLResponse=" + this.request.getEncodedParameter("SAMLResponse");
                String relayState = this.request.getEncodedParameter("RelayState");
                if (relayState != null && !relayState.isEmpty()) {
                    signedQuery = signedQuery + "&RelayState=" + relayState;
                }
                if (!Util.validateBinarySignature(signedQuery = signedQuery + "&SigAlg=" + this.request.getEncodedParameter("SigAlg", signAlg), Util.base64decoder(signature), certList, signAlg).booleanValue()) {
                    throw new ValidationError("Signature validation failed. Logout Response rejected", 42);
                }
            }
            LOGGER.debug("LogoutRequest validated --> " + this.logoutResponseString);
            return true;
        }
        catch (Exception e) {
            this.validationException = e;
            LOGGER.debug("LogoutResponse invalid --> " + this.logoutResponseString);
            LOGGER.error(this.validationException.getMessage());
            return false;
        }
    }

    public Boolean isValid() {
        return this.isValid(null);
    }

    public String getIssuer() throws XPathExpressionException {
        String issuer = null;
        NodeList issuers = this.query("/samlp:LogoutResponse/saml:Issuer");
        if (issuers.getLength() == 1) {
            issuer = issuers.item(0).getTextContent();
        }
        return issuer;
    }

    public String getStatus() throws XPathExpressionException {
        String statusCode = null;
        NodeList entries = this.query("/samlp:LogoutResponse/samlp:Status/samlp:StatusCode");
        if (entries.getLength() == 1) {
            statusCode = entries.item(0).getAttributes().getNamedItem("Value").getNodeValue();
        }
        return statusCode;
    }

    public SamlResponseStatus getSamlResponseStatus() throws ValidationError {
        String statusXpath = "/samlp:LogoutResponse/samlp:Status";
        return Util.getStatus(statusXpath, this.logoutResponseDocument);
    }

    private NodeList query(String query) throws XPathExpressionException {
        return Util.query(this.logoutResponseDocument, query, null);
    }

    public void build(String inResponseTo, String statusCode) {
        this.id = Util.generateUniqueID(this.settings.getUniqueIDPrefix());
        this.issueInstant = Calendar.getInstance();
        this.inResponseTo = inResponseTo;
        StrSubstitutor substitutor = this.generateSubstitutor(this.settings, statusCode);
        this.logoutResponseString = substitutor.replace((CharSequence)LogoutResponse.getLogoutResponseTemplate());
    }

    public void build(String inResponseTo) {
        this.build(inResponseTo, "urn:oasis:names:tc:SAML:2.0:status:Success");
    }

    public void build() {
        this.build(null);
    }

    private StrSubstitutor generateSubstitutor(Saml2Settings settings, String statusCode) {
        HashMap<String, String> valueMap = new HashMap<String, String>();
        valueMap.put("id", this.id);
        String issueInstantString = Util.formatDateTime(this.issueInstant.getTimeInMillis());
        valueMap.put("issueInstant", issueInstantString);
        String destinationStr = "";
        URL slo = settings.getIdpSingleLogoutServiceResponseUrl();
        if (slo != null) {
            destinationStr = " Destination=\"" + slo.toString() + "\"";
        }
        valueMap.put("destinationStr", destinationStr);
        String inResponseStr = "";
        if (this.inResponseTo != null) {
            inResponseStr = " InResponseTo=\"" + this.inResponseTo + "\"";
        }
        valueMap.put("inResponseStr", inResponseStr);
        String statusStr = "";
        if (statusCode != null) {
            statusStr = "Value=\"" + statusCode + "\"";
        }
        valueMap.put("statusStr", statusStr);
        valueMap.put("issuer", settings.getSpEntityId());
        return new StrSubstitutor(valueMap);
    }

    private StrSubstitutor generateSubstitutor(Saml2Settings settings) {
        return this.generateSubstitutor(settings, "urn:oasis:names:tc:SAML:2.0:status:Success");
    }

    private static StringBuilder getLogoutResponseTemplate() {
        StringBuilder template = new StringBuilder();
        template.append("<samlp:LogoutResponse xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ");
        template.append("ID=\"${id}\" ");
        template.append("Version=\"2.0\" ");
        template.append("IssueInstant=\"${issueInstant}\"${destinationStr}${inResponseStr} >");
        template.append("<saml:Issuer>${issuer}</saml:Issuer>");
        template.append("<samlp:Status>");
        template.append("<samlp:StatusCode ${statusStr} />");
        template.append("</samlp:Status>");
        template.append("</samlp:LogoutResponse>");
        return template;
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
}

