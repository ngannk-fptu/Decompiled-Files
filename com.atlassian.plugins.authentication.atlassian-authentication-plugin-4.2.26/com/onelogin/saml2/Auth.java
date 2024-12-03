/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.onelogin.saml2;

import com.onelogin.saml2.authn.AuthnRequest;
import com.onelogin.saml2.authn.SamlResponse;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.exception.XMLEntityException;
import com.onelogin.saml2.http.HttpRequest;
import com.onelogin.saml2.logout.LogoutRequest;
import com.onelogin.saml2.logout.LogoutResponse;
import com.onelogin.saml2.model.KeyStoreSettings;
import com.onelogin.saml2.model.SamlResponseStatus;
import com.onelogin.saml2.servlet.ServletUtils;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;
import com.onelogin.saml2.util.Util;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Auth {
    private static final Logger LOGGER = LoggerFactory.getLogger(Auth.class);
    private Saml2Settings settings;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String nameid;
    private String nameidFormat;
    private String nameidNameQualifier;
    private String nameidSPNameQualifier;
    private String sessionIndex;
    private DateTime sessionExpiration;
    private String lastMessageId;
    private String lastAssertionId;
    private List<Instant> lastAssertionNotOnOrAfter;
    private Map<String, List<String>> attributes = new HashMap<String, List<String>>();
    private boolean authenticated = false;
    private List<String> errors = new ArrayList<String>();
    private String errorReason;
    private Exception validationException;
    private String lastRequestId;
    private String lastRequest;
    private String lastResponse;

    public Auth() throws IOException, SettingsException, Error {
        this(new SettingsBuilder().fromFile("onelogin.saml.properties").build(), null, null);
    }

    public Auth(KeyStoreSettings keyStoreSetting) throws IOException, SettingsException, Error {
        this("onelogin.saml.properties", keyStoreSetting);
    }

    public Auth(String filename) throws IOException, SettingsException, Error {
        this(filename, null, null, null);
    }

    public Auth(String filename, KeyStoreSettings keyStoreSetting) throws IOException, SettingsException, Error {
        this(new SettingsBuilder().fromFile(filename, keyStoreSetting).build(), null, null);
    }

    public Auth(HttpServletRequest request, HttpServletResponse response) throws IOException, SettingsException, Error {
        this(new SettingsBuilder().fromFile("onelogin.saml.properties").build(), request, response);
    }

    public Auth(KeyStoreSettings keyStoreSetting, HttpServletRequest request, HttpServletResponse response) throws IOException, SettingsException, Error {
        this(new SettingsBuilder().fromFile("onelogin.saml.properties", keyStoreSetting).build(), request, response);
    }

    public Auth(String filename, HttpServletRequest request, HttpServletResponse response) throws SettingsException, IOException, Error {
        this(filename, null, request, response);
    }

    public Auth(String filename, KeyStoreSettings keyStoreSetting, HttpServletRequest request, HttpServletResponse response) throws SettingsException, IOException, Error {
        this(new SettingsBuilder().fromFile(filename, keyStoreSetting).build(), request, response);
    }

    public Auth(Saml2Settings settings, HttpServletRequest request, HttpServletResponse response) throws SettingsException {
        this.settings = settings;
        this.request = request;
        this.response = response;
        List<String> settingsErrors = settings.checkSettings();
        if (!settingsErrors.isEmpty()) {
            String errorMsg = "Invalid settings: ";
            errorMsg = errorMsg + StringUtils.join(settingsErrors, (String)", ");
            LOGGER.error(errorMsg);
            throw new SettingsException(errorMsg, 2);
        }
        LOGGER.debug("Settings validated");
    }

    public void setStrict(Boolean value) {
        this.settings.setStrict(value);
    }

    public String login(String returnTo, Boolean forceAuthn, Boolean isPassive, Boolean setNameIdPolicy, Boolean stay, String nameIdValueReq) throws IOException, SettingsException {
        HashMap<String, String> parameters = new HashMap<String, String>();
        AuthnRequest authnRequest = new AuthnRequest(this.settings, forceAuthn, isPassive, setNameIdPolicy, nameIdValueReq);
        String samlRequest = authnRequest.getEncodedAuthnRequest();
        parameters.put("SAMLRequest", samlRequest);
        String relayState = returnTo == null ? ServletUtils.getSelfRoutedURLNoQuery(this.request) : returnTo;
        if (!relayState.isEmpty()) {
            parameters.put("RelayState", relayState);
        }
        if (this.settings.getAuthnRequestsSigned()) {
            String sigAlg = this.settings.getSignatureAlgorithm();
            String signature = this.buildRequestSignature(samlRequest, relayState, sigAlg);
            parameters.put("SigAlg", sigAlg);
            parameters.put("Signature", signature);
        }
        String ssoUrl = this.getSSOurl();
        this.lastRequestId = authnRequest.getId();
        this.lastRequest = authnRequest.getAuthnRequestXml();
        if (!stay.booleanValue()) {
            LOGGER.debug("AuthNRequest sent to " + ssoUrl + " --> " + samlRequest);
        }
        return ServletUtils.sendRedirect(this.response, ssoUrl, parameters, stay);
    }

    public String login(String returnTo, Boolean forceAuthn, Boolean isPassive, Boolean setNameIdPolicy, Boolean stay) throws IOException, SettingsException {
        return this.login(returnTo, forceAuthn, isPassive, setNameIdPolicy, stay, null);
    }

    public void login(String returnTo, Boolean forceAuthn, Boolean isPassive, Boolean setNameIdPolicy) throws IOException, SettingsException {
        this.login(returnTo, forceAuthn, isPassive, setNameIdPolicy, false);
    }

    public void login() throws IOException, SettingsException {
        this.login(null, false, false, true);
    }

    public void login(String returnTo) throws IOException, SettingsException {
        this.login(returnTo, false, false, true);
    }

    public String logout(String returnTo, String nameId, String sessionIndex, Boolean stay, String nameidFormat, String nameIdNameQualifier, String nameIdSPNameQualifier) throws IOException, XMLEntityException, SettingsException {
        HashMap<String, String> parameters = new HashMap<String, String>();
        LogoutRequest logoutRequest = new LogoutRequest(this.settings, null, nameId, sessionIndex, nameidFormat, nameIdNameQualifier, nameIdSPNameQualifier);
        String samlLogoutRequest = logoutRequest.getEncodedLogoutRequest();
        parameters.put("SAMLRequest", samlLogoutRequest);
        String relayState = returnTo == null ? ServletUtils.getSelfRoutedURLNoQuery(this.request) : returnTo;
        if (!relayState.isEmpty()) {
            parameters.put("RelayState", relayState);
        }
        if (this.settings.getLogoutRequestSigned()) {
            String sigAlg = this.settings.getSignatureAlgorithm();
            String signature = this.buildRequestSignature(samlLogoutRequest, relayState, sigAlg);
            parameters.put("SigAlg", sigAlg);
            parameters.put("Signature", signature);
        }
        String sloUrl = this.getSLOurl();
        this.lastRequestId = logoutRequest.getId();
        this.lastRequest = logoutRequest.getLogoutRequestXml();
        if (!stay.booleanValue()) {
            LOGGER.debug("Logout request sent to " + sloUrl + " --> " + samlLogoutRequest);
        }
        return ServletUtils.sendRedirect(this.response, sloUrl, parameters, stay);
    }

    public String logout(String returnTo, String nameId, String sessionIndex, Boolean stay, String nameidFormat, String nameIdNameQualifier) throws IOException, XMLEntityException, SettingsException {
        return this.logout(returnTo, nameId, sessionIndex, stay, nameidFormat, nameIdNameQualifier, null);
    }

    public String logout(String returnTo, String nameId, String sessionIndex, Boolean stay, String nameidFormat) throws IOException, XMLEntityException, SettingsException {
        return this.logout(returnTo, nameId, sessionIndex, stay, nameidFormat, null);
    }

    public String logout(String returnTo, String nameId, String sessionIndex, Boolean stay) throws IOException, XMLEntityException, SettingsException {
        return this.logout(returnTo, nameId, sessionIndex, stay, null);
    }

    public void logout(String returnTo, String nameId, String sessionIndex, String nameidFormat, String nameIdNameQualifier, String nameIdSPNameQualifier) throws IOException, XMLEntityException, SettingsException {
        this.logout(returnTo, nameId, sessionIndex, false, nameidFormat, nameIdNameQualifier, nameIdSPNameQualifier);
    }

    public void logout(String returnTo, String nameId, String sessionIndex, String nameidFormat, String nameIdNameQualifier) throws IOException, XMLEntityException, SettingsException {
        this.logout(returnTo, nameId, sessionIndex, false, nameidFormat, nameIdNameQualifier);
    }

    public void logout(String returnTo, String nameId, String sessionIndex, String nameidFormat) throws IOException, XMLEntityException, SettingsException {
        this.logout(returnTo, nameId, sessionIndex, false, nameidFormat);
    }

    public void logout(String returnTo, String nameId, String sessionIndex) throws IOException, XMLEntityException, SettingsException {
        this.logout(returnTo, nameId, sessionIndex, false, null);
    }

    public void logout() throws IOException, XMLEntityException, SettingsException {
        this.logout(null, null, null, false);
    }

    public void logout(String returnTo) throws IOException, XMLEntityException, SettingsException {
        this.logout(returnTo, null, null);
    }

    public String getSSOurl() {
        return this.settings.getIdpSingleSignOnServiceUrl().toString();
    }

    public String getSLOurl() {
        return this.settings.getIdpSingleLogoutServiceUrl().toString();
    }

    public String getSLOResponseUrl() {
        return this.settings.getIdpSingleLogoutServiceResponseUrl().toString();
    }

    public void processResponse(String requestId) throws Exception {
        this.authenticated = false;
        HttpRequest httpRequest = ServletUtils.makeHttpRequest(this.request);
        String samlResponseParameter = httpRequest.getParameter("SAMLResponse");
        if (samlResponseParameter != null) {
            SamlResponse samlResponse = new SamlResponse(this.settings, httpRequest);
            this.lastResponse = samlResponse.getSAMLResponseXml();
            if (samlResponse.isValid(requestId)) {
                this.nameid = samlResponse.getNameId();
                this.nameidFormat = samlResponse.getNameIdFormat();
                this.nameidNameQualifier = samlResponse.getNameIdNameQualifier();
                this.nameidSPNameQualifier = samlResponse.getNameIdSPNameQualifier();
                this.authenticated = true;
                this.attributes = samlResponse.getAttributes();
                this.sessionIndex = samlResponse.getSessionIndex();
                this.sessionExpiration = samlResponse.getSessionNotOnOrAfter();
                this.lastMessageId = samlResponse.getId();
                this.lastAssertionId = samlResponse.getAssertionId();
                this.lastAssertionNotOnOrAfter = samlResponse.getAssertionNotOnOrAfter();
                LOGGER.debug("processResponse success --> " + samlResponseParameter);
            } else {
                this.errorReason = samlResponse.getError();
                this.validationException = samlResponse.getValidationException();
                SamlResponseStatus samlResponseStatus = samlResponse.getResponseStatus();
                if (samlResponseStatus.getStatusCode() == null || !samlResponseStatus.getStatusCode().equals("urn:oasis:names:tc:SAML:2.0:status:Success")) {
                    this.errors.add("response_not_success");
                    LOGGER.error("processResponse error. sso_not_success");
                    LOGGER.debug(" --> " + samlResponseParameter);
                    this.errors.add(samlResponseStatus.getStatusCode());
                    if (samlResponseStatus.getSubStatusCode() != null) {
                        this.errors.add(samlResponseStatus.getSubStatusCode());
                    }
                } else {
                    this.errors.add("invalid_response");
                    LOGGER.error("processResponse error. invalid_response");
                    LOGGER.debug(" --> " + samlResponseParameter);
                }
            }
        } else {
            this.errors.add("invalid_binding");
            String errorMsg = "SAML Response not found, Only supported HTTP_POST Binding";
            LOGGER.error("processResponse error." + errorMsg);
            throw new Error(errorMsg, 3);
        }
    }

    public void processResponse() throws Exception {
        this.processResponse(null);
    }

    public String processSLO(Boolean keepLocalSession, String requestId, Boolean stay) throws Exception {
        HttpRequest httpRequest = ServletUtils.makeHttpRequest(this.request);
        String samlRequestParameter = httpRequest.getParameter("SAMLRequest");
        String samlResponseParameter = httpRequest.getParameter("SAMLResponse");
        if (samlResponseParameter != null) {
            LogoutResponse logoutResponse = new LogoutResponse(this.settings, httpRequest);
            this.lastResponse = logoutResponse.getLogoutResponseXml();
            if (!logoutResponse.isValid(requestId).booleanValue()) {
                this.errors.add("invalid_logout_response");
                LOGGER.error("processSLO error. invalid_logout_response");
                LOGGER.debug(" --> " + samlResponseParameter);
                this.errorReason = logoutResponse.getError();
                this.validationException = logoutResponse.getValidationException();
            } else {
                SamlResponseStatus samlResponseStatus = logoutResponse.getSamlResponseStatus();
                String status = samlResponseStatus.getStatusCode();
                if (status == null || !status.equals("urn:oasis:names:tc:SAML:2.0:status:Success")) {
                    this.errors.add("logout_not_success");
                    LOGGER.error("processSLO error. logout_not_success");
                    LOGGER.debug(" --> " + samlResponseParameter);
                    this.errors.add(samlResponseStatus.getStatusCode());
                    if (samlResponseStatus.getSubStatusCode() != null) {
                        this.errors.add(samlResponseStatus.getSubStatusCode());
                    }
                } else {
                    this.lastMessageId = logoutResponse.getId();
                    LOGGER.debug("processSLO success --> " + samlResponseParameter);
                    if (!keepLocalSession.booleanValue()) {
                        this.request.getSession().invalidate();
                    }
                }
            }
            return null;
        }
        if (samlRequestParameter != null) {
            LogoutRequest logoutRequest = new LogoutRequest(this.settings, httpRequest);
            this.lastRequest = logoutRequest.getLogoutRequestXml();
            if (!logoutRequest.isValid().booleanValue()) {
                this.errors.add("invalid_logout_request");
                LOGGER.error("processSLO error. invalid_logout_request");
                LOGGER.debug(" --> " + samlRequestParameter);
                this.errorReason = logoutRequest.getError();
                this.validationException = logoutRequest.getValidationException();
                return null;
            }
            this.lastMessageId = logoutRequest.getId();
            LOGGER.debug("processSLO success --> " + samlRequestParameter);
            if (!keepLocalSession.booleanValue()) {
                this.request.getSession().invalidate();
            }
            String inResponseTo = logoutRequest.id;
            LogoutResponse logoutResponseBuilder = new LogoutResponse(this.settings, httpRequest);
            logoutResponseBuilder.build(inResponseTo, "urn:oasis:names:tc:SAML:2.0:status:Success");
            this.lastResponse = logoutResponseBuilder.getLogoutResponseXml();
            String samlLogoutResponse = logoutResponseBuilder.getEncodedLogoutResponse();
            LinkedHashMap<String, String> parameters = new LinkedHashMap<String, String>();
            parameters.put("SAMLResponse", samlLogoutResponse);
            String relayState = this.request.getParameter("RelayState");
            if (relayState != null) {
                parameters.put("RelayState", relayState);
            }
            if (this.settings.getLogoutResponseSigned()) {
                String sigAlg = this.settings.getSignatureAlgorithm();
                String signature = this.buildResponseSignature(samlLogoutResponse, relayState, sigAlg);
                parameters.put("SigAlg", sigAlg);
                parameters.put("Signature", signature);
            }
            String sloUrl = this.getSLOResponseUrl();
            if (!stay.booleanValue()) {
                LOGGER.debug("Logout response sent to " + sloUrl + " --> " + samlLogoutResponse);
            }
            return ServletUtils.sendRedirect(this.response, sloUrl, parameters, stay);
        }
        this.errors.add("invalid_binding");
        String errorMsg = "SAML LogoutRequest/LogoutResponse not found. Only supported HTTP_REDIRECT Binding";
        LOGGER.error("processSLO error." + errorMsg);
        throw new Error(errorMsg, 4);
    }

    public void processSLO(Boolean keepLocalSession, String requestId) throws Exception {
        this.processSLO(keepLocalSession, requestId, false);
    }

    public void processSLO() throws Exception {
        this.processSLO(false, null);
    }

    public final boolean isAuthenticated() {
        return this.authenticated;
    }

    public final List<String> getAttributesName() {
        return new ArrayList<String>(this.attributes.keySet());
    }

    public final Map<String, List<String>> getAttributes() {
        return this.attributes;
    }

    public final Collection<String> getAttribute(String name) {
        return this.attributes.get(name);
    }

    public final String getNameId() {
        return this.nameid;
    }

    public final String getNameIdFormat() {
        return this.nameidFormat;
    }

    public final String getNameIdNameQualifier() {
        return this.nameidNameQualifier;
    }

    public final String getNameIdSPNameQualifier() {
        return this.nameidSPNameQualifier;
    }

    public final String getSessionIndex() {
        return this.sessionIndex;
    }

    public final DateTime getSessionExpiration() {
        return this.sessionExpiration;
    }

    public String getLastMessageId() {
        return this.lastMessageId;
    }

    public String getLastAssertionId() {
        return this.lastAssertionId;
    }

    public List<Instant> getLastAssertionNotOnOrAfter() {
        return this.lastAssertionNotOnOrAfter;
    }

    public List<String> getErrors() {
        return this.errors;
    }

    public String getLastErrorReason() {
        return this.errorReason;
    }

    public Exception getLastValidationException() {
        return this.validationException;
    }

    public String getLastRequestId() {
        return this.lastRequestId;
    }

    public Saml2Settings getSettings() {
        return this.settings;
    }

    public Boolean isDebugActive() {
        return this.settings.isDebugActive();
    }

    public String buildRequestSignature(String samlRequest, String relayState, String signAlgorithm) throws SettingsException {
        return this.buildSignature(samlRequest, relayState, signAlgorithm, "SAMLRequest");
    }

    public String buildResponseSignature(String samlResponse, String relayState, String signAlgorithm) throws SettingsException {
        return this.buildSignature(samlResponse, relayState, signAlgorithm, "SAMLResponse");
    }

    private String buildSignature(String samlMessage, String relayState, String signAlgorithm, String type) throws SettingsException, IllegalArgumentException {
        String signature = "";
        if (!this.settings.checkSPCerts()) {
            String errorMsg = "Trying to sign the " + type + " but can't load the SP private key";
            LOGGER.error("buildSignature error. " + errorMsg);
            throw new SettingsException(errorMsg, 4);
        }
        PrivateKey key = this.settings.getSPkey();
        String msg = type + "=" + Util.urlEncoder(samlMessage);
        if (StringUtils.isNotEmpty((CharSequence)relayState)) {
            msg = msg + "&RelayState=" + Util.urlEncoder(relayState);
        }
        if (StringUtils.isEmpty((CharSequence)signAlgorithm)) {
            signAlgorithm = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
        }
        msg = msg + "&SigAlg=" + Util.urlEncoder(signAlgorithm);
        try {
            signature = Util.base64encoder(Util.sign(msg, key, signAlgorithm));
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e) {
            String errorMsg = "buildSignature error." + e.getMessage();
            LOGGER.error(errorMsg);
        }
        if (signature.isEmpty()) {
            String errorMsg = "There was a problem when calculating the Signature of the " + type;
            LOGGER.error("buildSignature error. " + errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        LOGGER.debug("buildResponseSignature success. --> " + signature);
        return signature;
    }

    public String getLastRequestXML() {
        return this.lastRequest;
    }

    public String getLastResponseXML() {
        return this.lastResponse;
    }
}

