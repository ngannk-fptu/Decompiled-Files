/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.text.StrSubstitutor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.onelogin.saml2.settings;

import com.onelogin.saml2.model.AttributeConsumingService;
import com.onelogin.saml2.model.Contact;
import com.onelogin.saml2.model.Organization;
import com.onelogin.saml2.model.RequestedAttribute;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.util.Util;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.xml.xpath.XPathExpressionException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class Metadata {
    private static final Logger LOGGER = LoggerFactory.getLogger(Metadata.class);
    private static final int N_DAYS_VALID_UNTIL = 2;
    private static final int SECONDS_CACHED = 604800;
    private AttributeConsumingService attributeConsumingService = null;
    private final String metadataString;
    private final Calendar validUntilTime;
    private final Integer cacheDuration;

    public Metadata(Saml2Settings settings, Calendar validUntilTime, Integer cacheDuration, AttributeConsumingService attributeConsumingService) throws CertificateEncodingException {
        this.validUntilTime = validUntilTime;
        this.attributeConsumingService = attributeConsumingService;
        this.cacheDuration = cacheDuration;
        StrSubstitutor substitutor = this.generateSubstitutor(settings);
        String unsignedMetadataString = substitutor.replace((CharSequence)Metadata.getMetadataTemplate());
        LOGGER.debug("metadata --> " + unsignedMetadataString);
        this.metadataString = unsignedMetadataString;
    }

    public Metadata(Saml2Settings settings, Calendar validUntilTime, Integer cacheDuration) throws CertificateEncodingException {
        this(settings, validUntilTime, cacheDuration, null);
    }

    public Metadata(Saml2Settings settings) throws CertificateEncodingException {
        this.validUntilTime = Calendar.getInstance();
        this.validUntilTime.add(6, 2);
        this.cacheDuration = 604800;
        StrSubstitutor substitutor = this.generateSubstitutor(settings);
        String unsignedMetadataString = substitutor.replace((CharSequence)Metadata.getMetadataTemplate());
        LOGGER.debug("metadata --> " + unsignedMetadataString);
        this.metadataString = unsignedMetadataString;
    }

    private StrSubstitutor generateSubstitutor(Saml2Settings settings) throws CertificateEncodingException {
        HashMap<String, String> valueMap = new HashMap<String, String>();
        Boolean wantsEncrypted = settings.getWantAssertionsEncrypted() || settings.getWantNameIdEncrypted();
        valueMap.put("id", Util.generateUniqueID(settings.getUniqueIDPrefix()));
        String validUntilTimeStr = "";
        if (this.validUntilTime != null) {
            String validUntilTimeValue = Util.formatDateTime(this.validUntilTime.getTimeInMillis());
            validUntilTimeStr = " validUntil=\"" + validUntilTimeValue + "\"";
        }
        valueMap.put("validUntilTimeStr", validUntilTimeStr);
        String cacheDurationStr = "";
        if (this.cacheDuration != null) {
            String cacheDurationValue = String.valueOf(this.cacheDuration);
            cacheDurationStr = " cacheDuration=\"PT" + cacheDurationValue + "S\"";
        }
        valueMap.put("cacheDurationStr", cacheDurationStr);
        valueMap.put("spEntityId", settings.getSpEntityId());
        valueMap.put("strAuthnsign", String.valueOf(settings.getAuthnRequestsSigned()));
        valueMap.put("strWsign", String.valueOf(settings.getWantAssertionsSigned()));
        valueMap.put("spNameIDFormat", settings.getSpNameIDFormat());
        valueMap.put("spAssertionConsumerServiceBinding", settings.getSpAssertionConsumerServiceBinding());
        valueMap.put("spAssertionConsumerServiceUrl", settings.getSpAssertionConsumerServiceUrl().toString());
        valueMap.put("sls", this.toSLSXml(settings.getSpSingleLogoutServiceUrl(), settings.getSpSingleLogoutServiceBinding()));
        valueMap.put("strAttributeConsumingService", this.getAttributeConsumingServiceXml());
        valueMap.put("strKeyDescriptor", this.toX509KeyDescriptorsXML(settings.getSPcert(), settings.getSPcertNew(), wantsEncrypted));
        valueMap.put("strContacts", this.toContactsXml(settings.getContacts()));
        valueMap.put("strOrganization", this.toOrganizationXml(settings.getOrganization()));
        return new StrSubstitutor(valueMap);
    }

    private static StringBuilder getMetadataTemplate() {
        StringBuilder template = new StringBuilder();
        template.append("<?xml version=\"1.0\"?>");
        template.append("<md:EntityDescriptor xmlns:md=\"urn:oasis:names:tc:SAML:2.0:metadata\"");
        template.append("${validUntilTimeStr}");
        template.append("${cacheDurationStr}");
        template.append(" entityID=\"${spEntityId}\"");
        template.append(" ID=\"${id}\">");
        template.append("<md:SPSSODescriptor AuthnRequestsSigned=\"${strAuthnsign}\" WantAssertionsSigned=\"${strWsign}\" protocolSupportEnumeration=\"urn:oasis:names:tc:SAML:2.0:protocol\">");
        template.append("${strKeyDescriptor}");
        template.append("${sls}<md:NameIDFormat>${spNameIDFormat}</md:NameIDFormat>");
        template.append("<md:AssertionConsumerService Binding=\"${spAssertionConsumerServiceBinding}\"");
        template.append(" Location=\"${spAssertionConsumerServiceUrl}\"");
        template.append(" index=\"1\"/>");
        template.append("${strAttributeConsumingService}");
        template.append("</md:SPSSODescriptor>${strOrganization}${strContacts}");
        template.append("</md:EntityDescriptor>");
        return template;
    }

    private String getAttributeConsumingServiceXml() {
        StringBuilder attributeConsumingServiceXML = new StringBuilder();
        if (this.attributeConsumingService != null) {
            String serviceName = this.attributeConsumingService.getServiceName();
            String serviceDescription = this.attributeConsumingService.getServiceDescription();
            List<RequestedAttribute> requestedAttributes = this.attributeConsumingService.getRequestedAttributes();
            attributeConsumingServiceXML.append("<md:AttributeConsumingService index=\"1\">");
            if (serviceName != null && !serviceName.isEmpty()) {
                attributeConsumingServiceXML.append("<md:ServiceName xml:lang=\"en\">" + serviceName + "</md:ServiceName>");
            }
            if (serviceDescription != null && !serviceDescription.isEmpty()) {
                attributeConsumingServiceXML.append("<md:ServiceDescription xml:lang=\"en\">" + serviceDescription + "</md:ServiceDescription>");
            }
            if (requestedAttributes != null && !requestedAttributes.isEmpty()) {
                for (RequestedAttribute requestedAttribute : requestedAttributes) {
                    String name = requestedAttribute.getName();
                    String friendlyName = requestedAttribute.getFriendlyName();
                    String nameFormat = requestedAttribute.getNameFormat();
                    Boolean isRequired = requestedAttribute.isRequired();
                    List<String> attrValues = requestedAttribute.getAttributeValues();
                    String contentStr = "<md:RequestedAttribute";
                    if (name != null && !name.isEmpty()) {
                        contentStr = contentStr + " Name=\"" + name + "\"";
                    }
                    if (nameFormat != null && !nameFormat.isEmpty()) {
                        contentStr = contentStr + " NameFormat=\"" + nameFormat + "\"";
                    }
                    if (friendlyName != null && !friendlyName.isEmpty()) {
                        contentStr = contentStr + " FriendlyName=\"" + friendlyName + "\"";
                    }
                    if (isRequired != null) {
                        contentStr = contentStr + " isRequired=\"" + isRequired.toString() + "\"";
                    }
                    if (attrValues != null && !attrValues.isEmpty()) {
                        contentStr = contentStr + ">";
                        for (String attrValue : attrValues) {
                            contentStr = contentStr + "<saml:AttributeValue xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">" + attrValue + "</saml:AttributeValue>";
                        }
                        attributeConsumingServiceXML.append(contentStr + "</md:RequestedAttribute>");
                        continue;
                    }
                    attributeConsumingServiceXML.append(contentStr + " />");
                }
            }
            attributeConsumingServiceXML.append("</md:AttributeConsumingService>");
        }
        return attributeConsumingServiceXML.toString();
    }

    private String toContactsXml(List<Contact> contacts) {
        StringBuilder contactsXml = new StringBuilder();
        for (Contact contact : contacts) {
            contactsXml.append("<md:ContactPerson contactType=\"" + contact.getContactType() + "\">");
            contactsXml.append("<md:GivenName>" + contact.getGivenName() + "</md:GivenName>");
            contactsXml.append("<md:EmailAddress>" + contact.getEmailAddress() + "</md:EmailAddress>");
            contactsXml.append("</md:ContactPerson>");
        }
        return contactsXml.toString();
    }

    private String toOrganizationXml(Organization organization) {
        String orgXml = "";
        if (organization != null) {
            String lang = organization.getOrgLangAttribute();
            orgXml = "<md:Organization><md:OrganizationName xml:lang=\"" + lang + "\">" + organization.getOrgName() + "</md:OrganizationName><md:OrganizationDisplayName xml:lang=\"" + lang + "\">" + organization.getOrgDisplayName() + "</md:OrganizationDisplayName><md:OrganizationURL xml:lang=\"" + lang + "\">" + organization.getOrgUrl() + "</md:OrganizationURL></md:Organization>";
        }
        return orgXml;
    }

    private String toX509KeyDescriptorsXML(X509Certificate cert, Boolean wantsEncrypted) throws CertificateEncodingException {
        return this.toX509KeyDescriptorsXML(cert, null, wantsEncrypted);
    }

    private String toX509KeyDescriptorsXML(X509Certificate certCurrent, X509Certificate certNew, Boolean wantsEncrypted) throws CertificateEncodingException {
        StringBuilder keyDescriptorXml = new StringBuilder();
        List<X509Certificate> certs = Arrays.asList(certCurrent, certNew);
        for (X509Certificate cert : certs) {
            if (cert == null) continue;
            Base64 encoder = new Base64(64);
            byte[] encodedCert = cert.getEncoded();
            String certString = new String(encoder.encode(encodedCert));
            keyDescriptorXml.append("<md:KeyDescriptor use=\"signing\">");
            keyDescriptorXml.append("<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">");
            keyDescriptorXml.append("<ds:X509Data>");
            keyDescriptorXml.append("<ds:X509Certificate>" + certString + "</ds:X509Certificate>");
            keyDescriptorXml.append("</ds:X509Data>");
            keyDescriptorXml.append("</ds:KeyInfo>");
            keyDescriptorXml.append("</md:KeyDescriptor>");
            if (!wantsEncrypted.booleanValue()) continue;
            keyDescriptorXml.append("<md:KeyDescriptor use=\"encryption\">");
            keyDescriptorXml.append("<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">");
            keyDescriptorXml.append("<ds:X509Data>");
            keyDescriptorXml.append("<ds:X509Certificate>" + certString + "</ds:X509Certificate>");
            keyDescriptorXml.append("</ds:X509Data>");
            keyDescriptorXml.append("</ds:KeyInfo>");
            keyDescriptorXml.append("</md:KeyDescriptor>");
        }
        return keyDescriptorXml.toString();
    }

    private String toSLSXml(URL spSingleLogoutServiceUrl, String spSingleLogoutServiceBinding) {
        StringBuilder slsXml = new StringBuilder();
        if (spSingleLogoutServiceUrl != null) {
            slsXml.append("<md:SingleLogoutService Binding=\"" + spSingleLogoutServiceBinding + "\"");
            slsXml.append(" Location=\"" + spSingleLogoutServiceUrl.toString() + "\"/>");
        }
        return slsXml.toString();
    }

    public final String getMetadataString() {
        return this.metadataString;
    }

    public static String signMetadata(String metadata, PrivateKey key, X509Certificate cert, String signAlgorithm) throws XPathExpressionException, XMLSecurityException {
        return Metadata.signMetadata(metadata, key, cert, signAlgorithm, "http://www.w3.org/2000/09/xmldsig#sha1");
    }

    public static String signMetadata(String metadata, PrivateKey key, X509Certificate cert, String signAlgorithm, String digestAlgorithm) throws XPathExpressionException, XMLSecurityException {
        Document metadataDoc = Util.loadXML(metadata);
        String signedMetadata = Util.addSign(metadataDoc, key, cert, signAlgorithm, digestAlgorithm);
        LOGGER.debug("Signed metadata --> " + signedMetadata);
        return signedMetadata;
    }
}

