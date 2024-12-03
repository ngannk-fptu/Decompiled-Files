/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.onelogin.saml2.settings;

import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.model.Contact;
import com.onelogin.saml2.model.KeyStoreSettings;
import com.onelogin.saml2.model.Organization;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.util.Util;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SettingsBuilder.class);
    private Map<String, Object> samlData = new LinkedHashMap<String, Object>();
    private Saml2Settings saml2Setting;
    public static final String STRICT_PROPERTY_KEY = "onelogin.saml2.strict";
    public static final String DEBUG_PROPERTY_KEY = "onelogin.saml2.debug";
    public static final String SP_ENTITYID_PROPERTY_KEY = "onelogin.saml2.sp.entityid";
    public static final String SP_ASSERTION_CONSUMER_SERVICE_URL_PROPERTY_KEY = "onelogin.saml2.sp.assertion_consumer_service.url";
    public static final String SP_ASSERTION_CONSUMER_SERVICE_BINDING_PROPERTY_KEY = "onelogin.saml2.sp.assertion_consumer_service.binding";
    public static final String SP_SINGLE_LOGOUT_SERVICE_URL_PROPERTY_KEY = "onelogin.saml2.sp.single_logout_service.url";
    public static final String SP_SINGLE_LOGOUT_SERVICE_BINDING_PROPERTY_KEY = "onelogin.saml2.sp.single_logout_service.binding";
    public static final String SP_NAMEIDFORMAT_PROPERTY_KEY = "onelogin.saml2.sp.nameidformat";
    public static final String SP_X509CERT_PROPERTY_KEY = "onelogin.saml2.sp.x509cert";
    public static final String SP_PRIVATEKEY_PROPERTY_KEY = "onelogin.saml2.sp.privatekey";
    public static final String SP_X509CERTNEW_PROPERTY_KEY = "onelogin.saml2.sp.x509certNew";
    public static final String KEYSTORE_KEY = "onelogin.saml2.keystore.store";
    public static final String KEYSTORE_ALIAS = "onelogin.saml2.keystore.alias";
    public static final String KEYSTORE_KEY_PASSWORD = "onelogin.saml2.keystore.key.password";
    public static final String IDP_ENTITYID_PROPERTY_KEY = "onelogin.saml2.idp.entityid";
    public static final String IDP_SINGLE_SIGN_ON_SERVICE_URL_PROPERTY_KEY = "onelogin.saml2.idp.single_sign_on_service.url";
    public static final String IDP_SINGLE_SIGN_ON_SERVICE_BINDING_PROPERTY_KEY = "onelogin.saml2.idp.single_sign_on_service.binding";
    public static final String IDP_SINGLE_LOGOUT_SERVICE_URL_PROPERTY_KEY = "onelogin.saml2.idp.single_logout_service.url";
    public static final String IDP_SINGLE_LOGOUT_SERVICE_RESPONSE_URL_PROPERTY_KEY = "onelogin.saml2.idp.single_logout_service.response.url";
    public static final String IDP_SINGLE_LOGOUT_SERVICE_BINDING_PROPERTY_KEY = "onelogin.saml2.idp.single_logout_service.binding";
    public static final String IDP_X509CERT_PROPERTY_KEY = "onelogin.saml2.idp.x509cert";
    public static final String IDP_X509CERTMULTI_PROPERTY_KEY = "onelogin.saml2.idp.x509certMulti";
    public static final String CERTFINGERPRINT_PROPERTY_KEY = "onelogin.saml2.idp.certfingerprint";
    public static final String CERTFINGERPRINT_ALGORITHM_PROPERTY_KEY = "onelogin.saml2.idp.certfingerprint_algorithm";
    public static final String SECURITY_NAMEID_ENCRYPTED = "onelogin.saml2.security.nameid_encrypted";
    public static final String SECURITY_AUTHREQUEST_SIGNED = "onelogin.saml2.security.authnrequest_signed";
    public static final String SECURITY_LOGOUTREQUEST_SIGNED = "onelogin.saml2.security.logoutrequest_signed";
    public static final String SECURITY_LOGOUTRESPONSE_SIGNED = "onelogin.saml2.security.logoutresponse_signed";
    public static final String SECURITY_WANT_MESSAGES_SIGNED = "onelogin.saml2.security.want_messages_signed";
    public static final String SECURITY_WANT_ASSERTIONS_SIGNED = "onelogin.saml2.security.want_assertions_signed";
    public static final String SECURITY_WANT_ASSERTIONS_ENCRYPTED = "onelogin.saml2.security.want_assertions_encrypted";
    public static final String SECURITY_COMPATIBILITY_MODE = "onelogin.saml2.security.compatibility_mode";
    public static final String SECURITY_WANT_NAMEID = "onelogin.saml2.security.want_nameid";
    public static final String SECURITY_WANT_NAMEID_ENCRYPTED = "onelogin.saml2.security.want_nameid_encrypted";
    public static final String SECURITY_SIGN_METADATA = "onelogin.saml2.security.sign_metadata";
    public static final String SECURITY_REQUESTED_AUTHNCONTEXT = "onelogin.saml2.security.requested_authncontext";
    public static final String SECURITY_REQUESTED_AUTHNCONTEXTCOMPARISON = "onelogin.saml2.security.requested_authncontextcomparison";
    public static final String SECURITY_WANT_XML_VALIDATION = "onelogin.saml2.security.want_xml_validation";
    public static final String SECURITY_SIGNATURE_ALGORITHM = "onelogin.saml2.security.signature_algorithm";
    public static final String SECURITY_REJECT_UNSOLICITED_RESPONSES_WITH_INRESPONSETO = "onelogin.saml2.security.reject_unsolicited_responses_with_inresponseto";
    public static final String SECURITY_ALLOW_REPEAT_ATTRIBUTE_NAME_PROPERTY_KEY = "onelogin.saml2.security.allow_duplicated_attribute_name";
    public static final String COMPRESS_REQUEST = "onelogin.saml2.compress.request";
    public static final String COMPRESS_RESPONSE = "onelogin.saml2.compress.response";
    public static final String CONTACT_TECHNICAL_GIVEN_NAME = "onelogin.saml2.contacts.technical.given_name";
    public static final String CONTACT_TECHNICAL_EMAIL_ADDRESS = "onelogin.saml2.contacts.technical.email_address";
    public static final String CONTACT_SUPPORT_GIVEN_NAME = "onelogin.saml2.contacts.support.given_name";
    public static final String CONTACT_SUPPORT_EMAIL_ADDRESS = "onelogin.saml2.contacts.support.email_address";
    public static final String ORGANIZATION_NAME = "onelogin.saml2.organization.name";
    public static final String ORGANIZATION_DISPLAYNAME = "onelogin.saml2.organization.displayname";
    public static final String ORGANIZATION_URL = "onelogin.saml2.organization.url";
    public static final String ORGANIZATION_LANG = "onelogin.saml2.organization.lang";
    public static final String UNIQUE_ID_PREFIX_PROPERTY_KEY = "onelogin.saml2.unique_id_prefix";

    public SettingsBuilder fromFile(String propFileName) throws Error, IOException {
        return this.fromFile(propFileName, null);
    }

    public SettingsBuilder fromFile(String propFileName, KeyStoreSettings keyStoreSetting) throws Error, IOException {
        block16: {
            ClassLoader classLoader = this.getClass().getClassLoader();
            try (InputStream inputStream = classLoader.getResourceAsStream(propFileName);){
                if (inputStream != null) {
                    Properties prop = new Properties();
                    prop.load(inputStream);
                    this.parseProperties(prop);
                    LOGGER.debug("properties file '{}' loaded succesfully", (Object)propFileName);
                    break block16;
                }
                String errorMsg = "properties file '" + propFileName + "' not found in the classpath";
                LOGGER.error(errorMsg);
                throw new Error(errorMsg, 1);
            }
            catch (IOException e) {
                String errorMsg = "properties file'" + propFileName + "' cannot be loaded.";
                LOGGER.error(errorMsg, (Throwable)e);
                throw new Error(errorMsg, 1);
            }
        }
        if (keyStoreSetting != null) {
            this.parseKeyStore(keyStoreSetting);
        }
        return this;
    }

    public SettingsBuilder fromProperties(Properties prop) {
        this.parseProperties(prop);
        return this;
    }

    public SettingsBuilder fromValues(Map<String, Object> samlData) {
        return this.fromValues(samlData, null);
    }

    public SettingsBuilder fromValues(Map<String, Object> samlData, KeyStoreSettings keyStoreSetting) {
        if (samlData != null) {
            this.samlData.putAll(samlData);
        }
        if (keyStoreSetting != null) {
            this.parseKeyStore(keyStoreSetting);
        }
        return this;
    }

    public Saml2Settings build() {
        return this.build(new Saml2Settings());
    }

    public Saml2Settings build(Saml2Settings saml2Setting) {
        Boolean debug;
        this.saml2Setting = saml2Setting;
        Boolean strict = this.loadBooleanProperty(STRICT_PROPERTY_KEY);
        if (strict != null) {
            saml2Setting.setStrict(strict);
        }
        if ((debug = this.loadBooleanProperty(DEBUG_PROPERTY_KEY)) != null) {
            saml2Setting.setDebug(debug);
        }
        this.loadSpSetting();
        this.loadIdpSetting();
        this.loadSecuritySetting();
        this.loadCompressSetting();
        saml2Setting.setContacts(this.loadContacts());
        saml2Setting.setOrganization(this.loadOrganization());
        saml2Setting.setUniqueIDPrefix(this.loadUniqueIDPrefix());
        return saml2Setting;
    }

    private void loadIdpSetting() {
        String idpCertFingerprintAlgorithm;
        String idpCertFingerprint;
        X509Certificate idpX509cert;
        List<X509Certificate> idpX509certMulti;
        String idpSingleLogoutServiceBinding;
        URL idpSingleLogoutServiceResponseUrl;
        URL idpSingleLogoutServiceUrl;
        String idpSingleSignOnServiceBinding;
        URL idpSingleSignOnServiceUrl;
        String idpEntityID = this.loadStringProperty(IDP_ENTITYID_PROPERTY_KEY);
        if (idpEntityID != null) {
            this.saml2Setting.setIdpEntityId(idpEntityID);
        }
        if ((idpSingleSignOnServiceUrl = this.loadURLProperty(IDP_SINGLE_SIGN_ON_SERVICE_URL_PROPERTY_KEY)) != null) {
            this.saml2Setting.setIdpSingleSignOnServiceUrl(idpSingleSignOnServiceUrl);
        }
        if ((idpSingleSignOnServiceBinding = this.loadStringProperty(IDP_SINGLE_SIGN_ON_SERVICE_BINDING_PROPERTY_KEY)) != null) {
            this.saml2Setting.setIdpSingleSignOnServiceBinding(idpSingleSignOnServiceBinding);
        }
        if ((idpSingleLogoutServiceUrl = this.loadURLProperty(IDP_SINGLE_LOGOUT_SERVICE_URL_PROPERTY_KEY)) != null) {
            this.saml2Setting.setIdpSingleLogoutServiceUrl(idpSingleLogoutServiceUrl);
        }
        if ((idpSingleLogoutServiceResponseUrl = this.loadURLProperty(IDP_SINGLE_LOGOUT_SERVICE_RESPONSE_URL_PROPERTY_KEY)) != null) {
            this.saml2Setting.setIdpSingleLogoutServiceResponseUrl(idpSingleLogoutServiceResponseUrl);
        }
        if ((idpSingleLogoutServiceBinding = this.loadStringProperty(IDP_SINGLE_LOGOUT_SERVICE_BINDING_PROPERTY_KEY)) != null) {
            this.saml2Setting.setIdpSingleLogoutServiceBinding(idpSingleLogoutServiceBinding);
        }
        if ((idpX509certMulti = this.loadCertificateListFromProp(IDP_X509CERTMULTI_PROPERTY_KEY)) != null) {
            this.saml2Setting.setIdpx509certMulti(idpX509certMulti);
        }
        if ((idpX509cert = this.loadCertificateFromProp(IDP_X509CERT_PROPERTY_KEY)) != null) {
            this.saml2Setting.setIdpx509cert(idpX509cert);
        }
        if ((idpCertFingerprint = this.loadStringProperty(CERTFINGERPRINT_PROPERTY_KEY)) != null) {
            this.saml2Setting.setIdpCertFingerprint(idpCertFingerprint);
        }
        if ((idpCertFingerprintAlgorithm = this.loadStringProperty(CERTFINGERPRINT_ALGORITHM_PROPERTY_KEY)) != null && !idpCertFingerprintAlgorithm.isEmpty()) {
            this.saml2Setting.setIdpCertFingerprintAlgorithm(idpCertFingerprintAlgorithm);
        }
    }

    private void loadSecuritySetting() {
        Boolean allowRepeatAttributeName;
        Boolean rejectUnsolicitedResponsesWithInResponseTo;
        String signatureAlgorithm;
        String requestedAuthnContextComparison;
        List<String> requestedAuthnContext;
        Boolean signMetadata;
        Boolean compatibilityMode;
        Boolean wantXMLValidation;
        Boolean wantNameIdEncrypted;
        Boolean wantNameId;
        Boolean wantAssertionsEncrypted;
        Boolean wantAssertionsSigned;
        Boolean wantMessagesSigned;
        Boolean logoutResponseSigned;
        Boolean logoutRequestSigned;
        Boolean authnRequestsSigned;
        Boolean nameIdEncrypted = this.loadBooleanProperty(SECURITY_NAMEID_ENCRYPTED);
        if (nameIdEncrypted != null) {
            this.saml2Setting.setNameIdEncrypted(nameIdEncrypted);
        }
        if ((authnRequestsSigned = this.loadBooleanProperty(SECURITY_AUTHREQUEST_SIGNED)) != null) {
            this.saml2Setting.setAuthnRequestsSigned(authnRequestsSigned);
        }
        if ((logoutRequestSigned = this.loadBooleanProperty(SECURITY_LOGOUTREQUEST_SIGNED)) != null) {
            this.saml2Setting.setLogoutRequestSigned(logoutRequestSigned);
        }
        if ((logoutResponseSigned = this.loadBooleanProperty(SECURITY_LOGOUTRESPONSE_SIGNED)) != null) {
            this.saml2Setting.setLogoutResponseSigned(logoutResponseSigned);
        }
        if ((wantMessagesSigned = this.loadBooleanProperty(SECURITY_WANT_MESSAGES_SIGNED)) != null) {
            this.saml2Setting.setWantMessagesSigned(wantMessagesSigned);
        }
        if ((wantAssertionsSigned = this.loadBooleanProperty(SECURITY_WANT_ASSERTIONS_SIGNED)) != null) {
            this.saml2Setting.setWantAssertionsSigned(wantAssertionsSigned);
        }
        if ((wantAssertionsEncrypted = this.loadBooleanProperty(SECURITY_WANT_ASSERTIONS_ENCRYPTED)) != null) {
            this.saml2Setting.setWantAssertionsEncrypted(wantAssertionsEncrypted);
        }
        if ((wantNameId = this.loadBooleanProperty(SECURITY_WANT_NAMEID)) != null) {
            this.saml2Setting.setWantNameId(wantNameId);
        }
        if ((wantNameIdEncrypted = this.loadBooleanProperty(SECURITY_WANT_NAMEID_ENCRYPTED)) != null) {
            this.saml2Setting.setWantNameIdEncrypted(wantNameIdEncrypted);
        }
        if ((wantXMLValidation = this.loadBooleanProperty(SECURITY_WANT_XML_VALIDATION)) != null) {
            this.saml2Setting.setWantXMLValidation(wantXMLValidation);
        }
        if ((compatibilityMode = this.loadBooleanProperty(SECURITY_COMPATIBILITY_MODE)) != null) {
            this.saml2Setting.setCompatibilityMode(compatibilityMode);
        }
        if ((signMetadata = this.loadBooleanProperty(SECURITY_SIGN_METADATA)) != null) {
            this.saml2Setting.setSignMetadata(signMetadata);
        }
        if ((requestedAuthnContext = this.loadListProperty(SECURITY_REQUESTED_AUTHNCONTEXT)) != null) {
            this.saml2Setting.setRequestedAuthnContext(requestedAuthnContext);
        }
        if ((requestedAuthnContextComparison = this.loadStringProperty(SECURITY_REQUESTED_AUTHNCONTEXTCOMPARISON)) != null && !requestedAuthnContextComparison.isEmpty()) {
            this.saml2Setting.setRequestedAuthnContextComparison(requestedAuthnContextComparison);
        }
        if ((signatureAlgorithm = this.loadStringProperty(SECURITY_SIGNATURE_ALGORITHM)) != null && !signatureAlgorithm.isEmpty()) {
            this.saml2Setting.setSignatureAlgorithm(signatureAlgorithm);
        }
        if ((rejectUnsolicitedResponsesWithInResponseTo = this.loadBooleanProperty(SECURITY_REJECT_UNSOLICITED_RESPONSES_WITH_INRESPONSETO)) != null) {
            this.saml2Setting.setRejectUnsolicitedResponsesWithInResponseTo(rejectUnsolicitedResponsesWithInResponseTo);
        }
        if ((allowRepeatAttributeName = this.loadBooleanProperty(SECURITY_ALLOW_REPEAT_ATTRIBUTE_NAME_PROPERTY_KEY)) != null) {
            this.saml2Setting.setAllowRepeatAttributeName(allowRepeatAttributeName);
        }
    }

    private void loadCompressSetting() {
        Boolean compressResponse;
        Boolean compressRequest = this.loadBooleanProperty(COMPRESS_REQUEST);
        if (compressRequest != null) {
            this.saml2Setting.setCompressRequest(compressRequest);
        }
        if ((compressResponse = this.loadBooleanProperty(COMPRESS_RESPONSE)) != null) {
            this.saml2Setting.setCompressResponse(compressResponse);
        }
    }

    private Organization loadOrganization() {
        Organization orgResult = null;
        String orgName = this.loadStringProperty(ORGANIZATION_NAME);
        String orgDisplayName = this.loadStringProperty(ORGANIZATION_DISPLAYNAME);
        URL orgUrl = this.loadURLProperty(ORGANIZATION_URL);
        String orgLangAttribute = this.loadStringProperty(ORGANIZATION_LANG);
        if (StringUtils.isNotBlank((CharSequence)orgName) || StringUtils.isNotBlank((CharSequence)orgDisplayName) || orgUrl != null) {
            orgResult = new Organization(orgName, orgDisplayName, orgUrl, orgLangAttribute);
        }
        return orgResult;
    }

    private List<Contact> loadContacts() {
        LinkedList<Contact> contacts = new LinkedList<Contact>();
        String technicalGn = this.loadStringProperty(CONTACT_TECHNICAL_GIVEN_NAME);
        String technicalEmailAddress = this.loadStringProperty(CONTACT_TECHNICAL_EMAIL_ADDRESS);
        if (technicalGn != null && !technicalGn.isEmpty() || technicalEmailAddress != null && !technicalEmailAddress.isEmpty()) {
            Contact technical = new Contact("technical", technicalGn, technicalEmailAddress);
            contacts.add(technical);
        }
        String supportGn = this.loadStringProperty(CONTACT_SUPPORT_GIVEN_NAME);
        String supportEmailAddress = this.loadStringProperty(CONTACT_SUPPORT_EMAIL_ADDRESS);
        if (supportGn != null && !supportGn.isEmpty() || supportEmailAddress != null && !supportEmailAddress.isEmpty()) {
            Contact support = new Contact("support", supportGn, supportEmailAddress);
            contacts.add(support);
        }
        return contacts;
    }

    private String loadUniqueIDPrefix() {
        String uniqueIDPrefix = this.loadStringProperty(UNIQUE_ID_PREFIX_PROPERTY_KEY);
        if (StringUtils.isNotEmpty((CharSequence)uniqueIDPrefix)) {
            return uniqueIDPrefix;
        }
        return "ONELOGIN_";
    }

    private void loadSpSetting() {
        X509Certificate spX509certNew;
        PrivateKey spPrivateKey;
        X509Certificate spX509cert;
        boolean keyStoreEnabled;
        String spNameIDFormat;
        String spSingleLogoutServiceBinding;
        URL spSingleLogoutServiceUrl;
        String spAssertionConsumerServiceBinding;
        URL assertionConsumerServiceUrl;
        String spEntityID = this.loadStringProperty(SP_ENTITYID_PROPERTY_KEY);
        if (spEntityID != null) {
            this.saml2Setting.setSpEntityId(spEntityID);
        }
        if ((assertionConsumerServiceUrl = this.loadURLProperty(SP_ASSERTION_CONSUMER_SERVICE_URL_PROPERTY_KEY)) != null) {
            this.saml2Setting.setSpAssertionConsumerServiceUrl(assertionConsumerServiceUrl);
        }
        if ((spAssertionConsumerServiceBinding = this.loadStringProperty(SP_ASSERTION_CONSUMER_SERVICE_BINDING_PROPERTY_KEY)) != null) {
            this.saml2Setting.setSpAssertionConsumerServiceBinding(spAssertionConsumerServiceBinding);
        }
        if ((spSingleLogoutServiceUrl = this.loadURLProperty(SP_SINGLE_LOGOUT_SERVICE_URL_PROPERTY_KEY)) != null) {
            this.saml2Setting.setSpSingleLogoutServiceUrl(spSingleLogoutServiceUrl);
        }
        if ((spSingleLogoutServiceBinding = this.loadStringProperty(SP_SINGLE_LOGOUT_SERVICE_BINDING_PROPERTY_KEY)) != null) {
            this.saml2Setting.setSpSingleLogoutServiceBinding(spSingleLogoutServiceBinding);
        }
        if ((spNameIDFormat = this.loadStringProperty(SP_NAMEIDFORMAT_PROPERTY_KEY)) != null && !spNameIDFormat.isEmpty()) {
            this.saml2Setting.setSpNameIDFormat(spNameIDFormat);
        }
        boolean bl = keyStoreEnabled = this.samlData.get(KEYSTORE_KEY) != null && this.samlData.get(KEYSTORE_ALIAS) != null && this.samlData.get(KEYSTORE_KEY_PASSWORD) != null;
        if (keyStoreEnabled) {
            KeyStore ks = (KeyStore)this.samlData.get(KEYSTORE_KEY);
            String alias = (String)this.samlData.get(KEYSTORE_ALIAS);
            String password = (String)this.samlData.get(KEYSTORE_KEY_PASSWORD);
            spX509cert = this.getCertificateFromKeyStore(ks, alias, password);
            spPrivateKey = this.getPrivateKeyFromKeyStore(ks, alias, password);
        } else {
            spX509cert = this.loadCertificateFromProp(SP_X509CERT_PROPERTY_KEY);
            spPrivateKey = this.loadPrivateKeyFromProp(SP_PRIVATEKEY_PROPERTY_KEY);
        }
        if (spX509cert != null) {
            this.saml2Setting.setSpX509cert(spX509cert);
        }
        if (spPrivateKey != null) {
            this.saml2Setting.setSpPrivateKey(spPrivateKey);
        }
        if ((spX509certNew = this.loadCertificateFromProp(SP_X509CERTNEW_PROPERTY_KEY)) != null) {
            this.saml2Setting.setSpX509certNew(spX509certNew);
        }
    }

    private String loadStringProperty(String propertyKey) {
        Object propValue = this.samlData.get(propertyKey);
        if (this.isString(propValue)) {
            return StringUtils.trimToNull((String)((String)propValue));
        }
        return null;
    }

    private Boolean loadBooleanProperty(String propertyKey) {
        Object propValue = this.samlData.get(propertyKey);
        if (this.isString(propValue)) {
            return Boolean.parseBoolean(((String)propValue).trim());
        }
        if (propValue instanceof Boolean) {
            return (Boolean)propValue;
        }
        return null;
    }

    private List<String> loadListProperty(String propertyKey) {
        Object propValue = this.samlData.get(propertyKey);
        if (this.isString(propValue)) {
            String[] values = ((String)propValue).trim().split(",");
            for (int i = 0; i < values.length; ++i) {
                values[i] = values[i].trim();
            }
            return Arrays.asList(values);
        }
        if (propValue instanceof List) {
            return (List)propValue;
        }
        return null;
    }

    private URL loadURLProperty(String propertyKey) {
        Object propValue = this.samlData.get(propertyKey);
        if (this.isString(propValue)) {
            try {
                return new URL(((String)propValue).trim());
            }
            catch (MalformedURLException e) {
                LOGGER.error("'{}' contains malformed url.", (Object)propertyKey, (Object)e);
                return null;
            }
        }
        if (propValue instanceof URL) {
            return (URL)propValue;
        }
        return null;
    }

    protected PrivateKey getPrivateKeyFromKeyStore(KeyStore keyStore, String alias, String password) {
        try {
            if (keyStore.containsAlias(alias)) {
                Key key = keyStore.getKey(alias, password.toCharArray());
                if (key instanceof PrivateKey) {
                    return (PrivateKey)key;
                }
            } else {
                LOGGER.error("Entry for alias {} not found in keystore", (Object)alias);
            }
        }
        catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            LOGGER.error("Error loading private key from keystore. {}", (Throwable)e);
        }
        return null;
    }

    protected X509Certificate getCertificateFromKeyStore(KeyStore keyStore, String alias, String password) {
        try {
            if (keyStore.containsAlias(alias)) {
                Certificate cert;
                Key key = keyStore.getKey(alias, password.toCharArray());
                if (key instanceof PrivateKey && (cert = keyStore.getCertificate(alias)) instanceof X509Certificate) {
                    return (X509Certificate)cert;
                }
            } else {
                LOGGER.error("Entry for alias {} not found in keystore", (Object)alias);
            }
        }
        catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            LOGGER.error("Error loading certificate from keystore. {}", (Throwable)e);
        }
        return null;
    }

    protected X509Certificate loadCertificateFromProp(Object propValue) {
        if (this.isString(propValue)) {
            try {
                return Util.loadCert(((String)propValue).trim());
            }
            catch (CertificateException e) {
                LOGGER.error("Error loading certificate from properties.", (Throwable)e);
                return null;
            }
        }
        if (propValue instanceof X509Certificate) {
            return (X509Certificate)propValue;
        }
        return null;
    }

    protected X509Certificate loadCertificateFromProp(String propertyKey) {
        return this.loadCertificateFromProp(this.samlData.get(propertyKey));
    }

    private List<X509Certificate> loadCertificateListFromProp(String propertyKey) {
        Object propValue;
        ArrayList<X509Certificate> list = new ArrayList<X509Certificate>();
        int i = 0;
        while ((propValue = this.samlData.get(propertyKey + "." + i++)) != null) {
            list.add(this.loadCertificateFromProp(propValue));
        }
        return list;
    }

    protected PrivateKey loadPrivateKeyFromProp(String propertyKey) {
        Object propValue = this.samlData.get(propertyKey);
        if (this.isString(propValue)) {
            try {
                return Util.loadPrivateKey(((String)propValue).trim());
            }
            catch (Exception e) {
                LOGGER.error("Error loading privatekey from properties.", (Throwable)e);
                return null;
            }
        }
        if (propValue instanceof PrivateKey) {
            return (PrivateKey)propValue;
        }
        return null;
    }

    private void parseProperties(Properties properties) {
        if (properties != null) {
            for (String propertyKey : properties.stringPropertyNames()) {
                this.samlData.put(propertyKey, properties.getProperty(propertyKey));
            }
        }
    }

    private void parseKeyStore(KeyStoreSettings setting) {
        this.samlData.put(KEYSTORE_KEY, setting.getKeyStore());
        this.samlData.put(KEYSTORE_ALIAS, setting.getSpAlias());
        this.samlData.put(KEYSTORE_KEY_PASSWORD, setting.getSpKeyPass());
    }

    private boolean isString(Object propValue) {
        return propValue instanceof String && StringUtils.isNotBlank((CharSequence)((String)propValue));
    }
}

