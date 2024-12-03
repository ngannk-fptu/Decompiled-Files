/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.onelogin.saml2.settings;

import com.onelogin.saml2.model.Contact;
import com.onelogin.saml2.model.Organization;
import com.onelogin.saml2.settings.CompatibilityModeViolationHandler;
import com.onelogin.saml2.settings.Metadata;
import com.onelogin.saml2.util.SchemaFactory;
import com.onelogin.saml2.util.Util;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Saml2Settings {
    private static final Logger LOGGER = LoggerFactory.getLogger(Saml2Settings.class);
    private boolean strict = true;
    private boolean debug = false;
    private String spEntityId = "";
    private URL spAssertionConsumerServiceUrl = null;
    private String spAssertionConsumerServiceBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST";
    private URL spSingleLogoutServiceUrl = null;
    private String spSingleLogoutServiceBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect";
    private String spNameIDFormat = "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";
    private X509Certificate spX509cert = null;
    private X509Certificate spX509certNew = null;
    private PrivateKey spPrivateKey = null;
    private String idpEntityId = "";
    private URL idpSingleSignOnServiceUrl = null;
    private String idpSingleSignOnServiceBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect";
    private URL idpSingleLogoutServiceUrl = null;
    private URL idpSingleLogoutServiceResponseUrl = null;
    private String idpSingleLogoutServiceBinding = "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect";
    private X509Certificate idpx509cert = null;
    private List<X509Certificate> idpx509certMulti = null;
    private String idpCertFingerprint = null;
    private String idpCertFingerprintAlgorithm = "sha1";
    private boolean nameIdEncrypted = false;
    private boolean authnRequestsSigned = false;
    private boolean logoutRequestSigned = false;
    private boolean logoutResponseSigned = false;
    private boolean wantMessagesSigned = false;
    private boolean wantAssertionsSigned = false;
    private boolean wantAssertionsEncrypted = false;
    private boolean wantNameId = true;
    private boolean wantNameIdEncrypted = false;
    private boolean signMetadata = false;
    private List<String> requestedAuthnContext = new ArrayList<String>();
    private String requestedAuthnContextComparison = "exact";
    private boolean wantXMLValidation = true;
    private String signatureAlgorithm = "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
    private String digestAlgorithm = "http://www.w3.org/2000/09/xmldsig#sha1";
    private boolean rejectUnsolicitedResponsesWithInResponseTo = false;
    private boolean allowRepeatAttributeName = false;
    private String uniqueIDPrefix = null;
    private boolean compatibilityMode = false;
    private boolean compressRequest = true;
    private boolean compressResponse = true;
    private List<Contact> contacts = new LinkedList<Contact>();
    private Organization organization = null;
    private CompatibilityModeViolationHandler compatibilityModeViolationHandler;
    private boolean spValidationOnly = false;

    public final boolean isStrict() {
        return this.strict;
    }

    public final String getSpEntityId() {
        return this.spEntityId;
    }

    public final URL getSpAssertionConsumerServiceUrl() {
        return this.spAssertionConsumerServiceUrl;
    }

    public final String getSpAssertionConsumerServiceBinding() {
        return this.spAssertionConsumerServiceBinding;
    }

    public final URL getSpSingleLogoutServiceUrl() {
        return this.spSingleLogoutServiceUrl;
    }

    public final String getSpSingleLogoutServiceBinding() {
        return this.spSingleLogoutServiceBinding;
    }

    public final String getSpNameIDFormat() {
        return this.spNameIDFormat;
    }

    public boolean isAllowRepeatAttributeName() {
        return this.allowRepeatAttributeName;
    }

    public final X509Certificate getSPcert() {
        return this.spX509cert;
    }

    public final X509Certificate getSPcertNew() {
        return this.spX509certNew;
    }

    public final PrivateKey getSPkey() {
        return this.spPrivateKey;
    }

    public final String getIdpEntityId() {
        return this.idpEntityId;
    }

    public final URL getIdpSingleSignOnServiceUrl() {
        return this.idpSingleSignOnServiceUrl;
    }

    public final String getIdpSingleSignOnServiceBinding() {
        return this.idpSingleSignOnServiceBinding;
    }

    public final URL getIdpSingleLogoutServiceUrl() {
        return this.idpSingleLogoutServiceUrl;
    }

    public final URL getIdpSingleLogoutServiceResponseUrl() {
        if (this.idpSingleLogoutServiceResponseUrl == null) {
            return this.getIdpSingleLogoutServiceUrl();
        }
        return this.idpSingleLogoutServiceResponseUrl;
    }

    public final String getIdpSingleLogoutServiceBinding() {
        return this.idpSingleLogoutServiceBinding;
    }

    public final X509Certificate getIdpx509cert() {
        return this.idpx509cert;
    }

    public final String getIdpCertFingerprint() {
        return this.idpCertFingerprint;
    }

    public final String getIdpCertFingerprintAlgorithm() {
        return this.idpCertFingerprintAlgorithm;
    }

    public List<X509Certificate> getIdpx509certMulti() {
        return this.idpx509certMulti;
    }

    public boolean getNameIdEncrypted() {
        return this.nameIdEncrypted;
    }

    public boolean getAuthnRequestsSigned() {
        return this.authnRequestsSigned;
    }

    public boolean getLogoutRequestSigned() {
        return this.logoutRequestSigned;
    }

    public boolean getLogoutResponseSigned() {
        return this.logoutResponseSigned;
    }

    public boolean getWantMessagesSigned() {
        return this.wantMessagesSigned;
    }

    public boolean getWantAssertionsSigned() {
        return this.wantAssertionsSigned;
    }

    public boolean getWantAssertionsEncrypted() {
        return this.wantAssertionsEncrypted;
    }

    public boolean getWantNameId() {
        return this.wantNameId;
    }

    public boolean getWantNameIdEncrypted() {
        return this.wantNameIdEncrypted;
    }

    public boolean getSignMetadata() {
        return this.signMetadata;
    }

    public List<String> getRequestedAuthnContext() {
        return this.requestedAuthnContext;
    }

    public String getRequestedAuthnContextComparison() {
        return this.requestedAuthnContextComparison;
    }

    public boolean getWantXMLValidation() {
        return this.wantXMLValidation;
    }

    public String getSignatureAlgorithm() {
        return this.signatureAlgorithm;
    }

    public String getDigestAlgorithm() {
        return this.digestAlgorithm;
    }

    public List<Contact> getContacts() {
        return this.contacts;
    }

    public Organization getOrganization() {
        return this.organization;
    }

    public String getUniqueIDPrefix() {
        return this.uniqueIDPrefix;
    }

    public boolean isDebugActive() {
        return this.debug;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    protected final void setSpEntityId(String spEntityId) {
        this.spEntityId = spEntityId;
    }

    protected final void setSpAssertionConsumerServiceUrl(URL spAssertionConsumerServiceUrl) {
        this.spAssertionConsumerServiceUrl = spAssertionConsumerServiceUrl;
    }

    protected final void setSpAssertionConsumerServiceBinding(String spAssertionConsumerServiceBinding) {
        this.spAssertionConsumerServiceBinding = spAssertionConsumerServiceBinding;
    }

    protected final void setSpSingleLogoutServiceUrl(URL spSingleLogoutServiceUrl) {
        this.spSingleLogoutServiceUrl = spSingleLogoutServiceUrl;
    }

    protected final void setSpSingleLogoutServiceBinding(String spSingleLogoutServiceBinding) {
        this.spSingleLogoutServiceBinding = spSingleLogoutServiceBinding;
    }

    protected final void setSpNameIDFormat(String spNameIDFormat) {
        this.spNameIDFormat = spNameIDFormat;
    }

    public void setAllowRepeatAttributeName(boolean allowRepeatAttributeName) {
        this.allowRepeatAttributeName = allowRepeatAttributeName;
    }

    protected final void setSpX509cert(X509Certificate spX509cert) {
        this.spX509cert = spX509cert;
    }

    protected final void setSpX509certNew(X509Certificate spX509certNew) {
        this.spX509certNew = spX509certNew;
    }

    protected final void setSpPrivateKey(PrivateKey spPrivateKey) {
        this.spPrivateKey = spPrivateKey;
    }

    protected final void setUniqueIDPrefix(String uniqueIDPrefix) {
        this.uniqueIDPrefix = uniqueIDPrefix;
    }

    protected final void setIdpEntityId(String idpEntityId) {
        this.idpEntityId = idpEntityId;
    }

    protected final void setIdpSingleSignOnServiceUrl(URL idpSingleSignOnServiceUrl) {
        this.idpSingleSignOnServiceUrl = idpSingleSignOnServiceUrl;
    }

    protected final void setIdpSingleSignOnServiceBinding(String idpSingleSignOnServiceBinding) {
        this.idpSingleSignOnServiceBinding = idpSingleSignOnServiceBinding;
    }

    protected final void setIdpSingleLogoutServiceUrl(URL idpSingleLogoutServiceUrl) {
        this.idpSingleLogoutServiceUrl = idpSingleLogoutServiceUrl;
    }

    protected final void setIdpSingleLogoutServiceResponseUrl(URL idpSingleLogoutServiceResponseUrl) {
        this.idpSingleLogoutServiceResponseUrl = idpSingleLogoutServiceResponseUrl;
    }

    protected final void setIdpSingleLogoutServiceBinding(String idpSingleLogoutServiceBinding) {
        this.idpSingleLogoutServiceBinding = idpSingleLogoutServiceBinding;
    }

    protected final void setIdpx509cert(X509Certificate idpX509cert) {
        this.idpx509cert = idpX509cert;
    }

    protected final void setIdpCertFingerprint(String idpCertFingerprint) {
        this.idpCertFingerprint = idpCertFingerprint;
    }

    protected final void setIdpCertFingerprintAlgorithm(String idpCertFingerprintAlgorithm) {
        this.idpCertFingerprintAlgorithm = idpCertFingerprintAlgorithm;
    }

    public void setIdpx509certMulti(List<X509Certificate> idpx509certMulti) {
        this.idpx509certMulti = idpx509certMulti;
    }

    public void setNameIdEncrypted(boolean nameIdEncrypted) {
        this.nameIdEncrypted = nameIdEncrypted;
    }

    public void setAuthnRequestsSigned(boolean authnRequestsSigned) {
        this.authnRequestsSigned = authnRequestsSigned;
    }

    public void setLogoutRequestSigned(boolean logoutRequestSigned) {
        this.logoutRequestSigned = logoutRequestSigned;
    }

    public void setLogoutResponseSigned(boolean logoutResponseSigned) {
        this.logoutResponseSigned = logoutResponseSigned;
    }

    public void setWantMessagesSigned(boolean wantMessagesSigned) {
        this.wantMessagesSigned = wantMessagesSigned;
    }

    public void setWantAssertionsSigned(boolean wantAssertionsSigned) {
        this.wantAssertionsSigned = wantAssertionsSigned;
    }

    public void setWantAssertionsEncrypted(boolean wantAssertionsEncrypted) {
        this.wantAssertionsEncrypted = wantAssertionsEncrypted;
    }

    public void setWantNameId(boolean wantNameId) {
        this.wantNameId = wantNameId;
    }

    public void setWantNameIdEncrypted(boolean wantNameIdEncrypted) {
        this.wantNameIdEncrypted = wantNameIdEncrypted;
    }

    public void setSignMetadata(boolean signMetadata) {
        this.signMetadata = signMetadata;
    }

    public void setRequestedAuthnContext(List<String> requestedAuthnContext) {
        if (requestedAuthnContext != null) {
            this.requestedAuthnContext = requestedAuthnContext;
        }
    }

    public void setRequestedAuthnContextComparison(String requestedAuthnContextComparison) {
        this.requestedAuthnContextComparison = requestedAuthnContextComparison;
    }

    public void setWantXMLValidation(boolean wantXMLValidation) {
        this.wantXMLValidation = wantXMLValidation;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    public void setRejectUnsolicitedResponsesWithInResponseTo(boolean rejectUnsolicitedResponsesWithInResponseTo) {
        this.rejectUnsolicitedResponsesWithInResponseTo = rejectUnsolicitedResponsesWithInResponseTo;
    }

    public boolean isRejectUnsolicitedResponsesWithInResponseTo() {
        return this.rejectUnsolicitedResponsesWithInResponseTo;
    }

    public void setCompressRequest(boolean compressRequest) {
        this.compressRequest = compressRequest;
    }

    public boolean isCompressRequestEnabled() {
        return this.compressRequest;
    }

    public void setCompressResponse(boolean compressResponse) {
        this.compressResponse = compressResponse;
    }

    public boolean isCompressResponseEnabled() {
        return this.compressResponse;
    }

    protected final void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    protected final void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public List<String> checkSettings() {
        ArrayList<String> errors = new ArrayList<String>(this.checkSPSettings());
        if (!this.spValidationOnly) {
            errors.addAll(this.checkIdPSettings());
        }
        return errors;
    }

    public List<String> checkIdPSettings() {
        String errorMsg;
        ArrayList<String> errors = new ArrayList<String>();
        if (!this.checkRequired(this.getIdpEntityId())) {
            errorMsg = "idp_entityId_not_found";
            errors.add(errorMsg);
            LOGGER.error(errorMsg);
        }
        if (!this.checkRequired(this.getIdpSingleSignOnServiceUrl())) {
            errorMsg = "idp_sso_url_invalid";
            errors.add(errorMsg);
            LOGGER.error(errorMsg);
        }
        if (!this.checkIdpx509certRequired() && !this.checkRequired(this.getIdpCertFingerprint())) {
            errorMsg = "idp_cert_or_fingerprint_not_found_and_required";
            errors.add(errorMsg);
            LOGGER.error(errorMsg);
        }
        if (!this.checkIdpx509certRequired() && this.getNameIdEncrypted()) {
            errorMsg = "idp_cert_not_found_and_required";
            errors.add(errorMsg);
            LOGGER.error(errorMsg);
        }
        return errors;
    }

    private boolean checkIdpx509certRequired() {
        if (this.getIdpx509cert() != null) {
            return true;
        }
        return this.getIdpx509certMulti() != null && !this.getIdpx509certMulti().isEmpty();
    }

    public List<String> checkSPSettings() {
        Organization org;
        List<Contact> contacts;
        String errorMsg;
        ArrayList<String> errors = new ArrayList<String>();
        if (!this.checkRequired(this.getSpEntityId())) {
            errorMsg = "sp_entityId_not_found";
            errors.add(errorMsg);
            LOGGER.error(errorMsg);
        }
        if (!this.checkRequired(this.getSpAssertionConsumerServiceUrl())) {
            errorMsg = "sp_acs_not_found";
            errors.add(errorMsg);
            LOGGER.error(errorMsg);
        }
        if ((this.getAuthnRequestsSigned() || this.getLogoutRequestSigned() || this.getLogoutResponseSigned() || this.getWantAssertionsEncrypted() || this.getWantNameIdEncrypted()) && !this.checkSPCerts()) {
            errorMsg = "sp_cert_not_found_and_required";
            errors.add(errorMsg);
            LOGGER.error(errorMsg);
        }
        if (!(contacts = this.getContacts()).isEmpty()) {
            for (Contact contact : contacts) {
                if (!contact.getEmailAddress().isEmpty() && !contact.getGivenName().isEmpty()) continue;
                errorMsg = "contact_not_enought_data";
                errors.add(errorMsg);
                LOGGER.error(errorMsg);
            }
        }
        if ((org = this.getOrganization()) != null && (org.getOrgDisplayName().isEmpty() || org.getOrgName().isEmpty() || org.getOrgUrl().isEmpty())) {
            errorMsg = "organization_not_enought_data";
            errors.add(errorMsg);
            LOGGER.error(errorMsg);
        }
        return errors;
    }

    public boolean checkSPCerts() {
        X509Certificate cert = this.getSPcert();
        PrivateKey key = this.getSPkey();
        return cert != null && key != null;
    }

    private boolean checkRequired(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof String && ((String)value).isEmpty()) {
            return false;
        }
        return !(value instanceof List) || !((List)value).isEmpty();
    }

    public void setSPValidationOnly(boolean spValidationOnly) {
        this.spValidationOnly = spValidationOnly;
    }

    public boolean getSPValidationOnly() {
        return this.spValidationOnly;
    }

    public boolean isCompatibilityMode() {
        return this.compatibilityMode;
    }

    public void setCompatibilityMode(boolean compatibilityMode) {
        this.compatibilityMode = compatibilityMode;
    }

    public void setCompatibilityModeViolationHandler(CompatibilityModeViolationHandler handler) {
        this.compatibilityModeViolationHandler = handler;
    }

    public CompatibilityModeViolationHandler getCompatibilityModeViolationHandler() {
        return this.compatibilityModeViolationHandler;
    }

    public String getSPMetadata() throws CertificateEncodingException {
        Metadata metadataObj = new Metadata(this);
        String metadataString = metadataObj.getMetadataString();
        boolean signMetadata = this.getSignMetadata();
        if (signMetadata) {
            try {
                metadataString = Metadata.signMetadata(metadataString, this.getSPkey(), this.getSPcert(), this.getSignatureAlgorithm(), this.getDigestAlgorithm());
            }
            catch (Exception e) {
                LOGGER.debug("Error executing signMetadata: " + e.getMessage(), (Throwable)e);
            }
        }
        return metadataString;
    }

    public static List<String> validateMetadata(String metadataString) throws Exception {
        metadataString = metadataString.replace("<?xml version=\"1.0\"?>", "");
        Document metadataDocument = Util.loadXML(metadataString);
        ArrayList<String> errors = new ArrayList<String>();
        if (!Util.validateXML(metadataDocument, SchemaFactory.SAML_SCHEMA_METADATA_2_0)) {
            errors.add("Invalid SAML Metadata. Not match the saml-schema-metadata-2.0.xsd");
        } else {
            Element rootElement = metadataDocument.getDocumentElement();
            if (!rootElement.getLocalName().equals("EntityDescriptor")) {
                errors.add("noEntityDescriptor_xml");
            } else if (rootElement.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:metadata", "SPSSODescriptor").getLength() != 1) {
                errors.add("onlySPSSODescriptor_allowed_xml");
            } else {
                long expireTime;
                String validUntil = null;
                String cacheDuration = null;
                if (rootElement.hasAttribute("cacheDuration")) {
                    cacheDuration = rootElement.getAttribute("cacheDuration");
                }
                if (rootElement.hasAttribute("validUntil")) {
                    validUntil = rootElement.getAttribute("validUntil");
                }
                if ((expireTime = Util.getExpireTime(cacheDuration, validUntil)) != 0L && Util.getCurrentTimeStamp() > expireTime) {
                    errors.add("expired_xml");
                }
            }
        }
        return errors;
    }
}

