/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.text.StrSubstitutor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.onelogin.saml2.authn;

import com.onelogin.saml2.model.Organization;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.util.Util;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthnRequest {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthnRequest.class);
    private final String authnRequestString;
    private final String id;
    private final Saml2Settings settings;
    private final boolean forceAuthn;
    private final boolean isPassive;
    private final boolean setNameIdPolicy;
    private final String nameIdValueReq;
    private final Calendar issueInstant;

    public AuthnRequest(Saml2Settings settings) {
        this(settings, false, false, true);
    }

    public AuthnRequest(Saml2Settings settings, boolean forceAuthn, boolean isPassive, boolean setNameIdPolicy, String nameIdValueReq) {
        this.id = Util.generateUniqueID(settings.getUniqueIDPrefix());
        this.issueInstant = Calendar.getInstance();
        this.isPassive = isPassive;
        this.settings = settings;
        this.forceAuthn = forceAuthn;
        this.setNameIdPolicy = setNameIdPolicy;
        this.nameIdValueReq = nameIdValueReq;
        StrSubstitutor substitutor = this.generateSubstitutor(settings);
        this.authnRequestString = substitutor.replace((CharSequence)AuthnRequest.getAuthnRequestTemplate());
        LOGGER.debug("AuthNRequest --> " + this.authnRequestString);
    }

    public AuthnRequest(Saml2Settings settings, boolean forceAuthn, boolean isPassive, boolean setNameIdPolicy) {
        this(settings, forceAuthn, isPassive, setNameIdPolicy, null);
    }

    public String getEncodedAuthnRequest(Boolean deflated) throws IOException {
        if (deflated == null) {
            deflated = this.settings.isCompressRequestEnabled();
        }
        String encodedAuthnRequest = deflated != false ? Util.deflatedBase64encoded(this.getAuthnRequestXml()) : Util.base64encoder(this.getAuthnRequestXml());
        return encodedAuthnRequest;
    }

    public String getEncodedAuthnRequest() throws IOException {
        return this.getEncodedAuthnRequest(null);
    }

    public String getAuthnRequestXml() {
        return this.authnRequestString;
    }

    private StrSubstitutor generateSubstitutor(Saml2Settings settings) {
        String displayName;
        HashMap<String, String> valueMap = new HashMap<String, String>();
        String forceAuthnStr = "";
        if (this.forceAuthn) {
            forceAuthnStr = " ForceAuthn=\"true\"";
        }
        String isPassiveStr = "";
        if (this.isPassive) {
            isPassiveStr = " IsPassive=\"true\"";
        }
        valueMap.put("forceAuthnStr", forceAuthnStr);
        valueMap.put("isPassiveStr", isPassiveStr);
        String destinationStr = "";
        URL sso = settings.getIdpSingleSignOnServiceUrl();
        if (sso != null) {
            destinationStr = " Destination=\"" + sso.toString() + "\"";
        }
        valueMap.put("destinationStr", destinationStr);
        String subjectStr = "";
        if (this.nameIdValueReq != null && !this.nameIdValueReq.isEmpty()) {
            String nameIDFormat = settings.getSpNameIDFormat();
            subjectStr = "<saml:Subject>";
            subjectStr = subjectStr + "<saml:NameID Format=\"" + nameIDFormat + "\">" + this.nameIdValueReq + "</saml:NameID>";
            subjectStr = subjectStr + "<saml:SubjectConfirmation Method=\"urn:oasis:names:tc:SAML:2.0:cm:bearer\"></saml:SubjectConfirmation>";
            subjectStr = subjectStr + "</saml:Subject>";
        }
        valueMap.put("subjectStr", subjectStr);
        String nameIDPolicyStr = "";
        if (this.setNameIdPolicy) {
            String nameIDPolicyFormat = settings.getSpNameIDFormat();
            if (settings.getWantNameIdEncrypted()) {
                nameIDPolicyFormat = "urn:oasis:names:tc:SAML:2.0:nameid-format:encrypted";
            }
            nameIDPolicyStr = "<samlp:NameIDPolicy Format=\"" + nameIDPolicyFormat + "\" AllowCreate=\"true\" />";
        }
        valueMap.put("nameIDPolicyStr", nameIDPolicyStr);
        String providerStr = "";
        Organization organization = settings.getOrganization();
        if (organization != null && !(displayName = organization.getOrgDisplayName()).isEmpty()) {
            providerStr = " ProviderName=\"" + displayName + "\"";
        }
        valueMap.put("providerStr", providerStr);
        String issueInstantString = Util.formatDateTime(this.issueInstant.getTimeInMillis());
        valueMap.put("issueInstant", issueInstantString);
        valueMap.put("id", String.valueOf(this.id));
        valueMap.put("assertionConsumerServiceURL", String.valueOf(settings.getSpAssertionConsumerServiceUrl()));
        valueMap.put("protocolBinding", settings.getSpAssertionConsumerServiceBinding());
        valueMap.put("spEntityid", settings.getSpEntityId());
        String requestedAuthnContextStr = "";
        List<String> requestedAuthnContexts = settings.getRequestedAuthnContext();
        if (requestedAuthnContexts != null && !requestedAuthnContexts.isEmpty()) {
            String requestedAuthnContextCmp = settings.getRequestedAuthnContextComparison();
            requestedAuthnContextStr = "<samlp:RequestedAuthnContext Comparison=\"" + requestedAuthnContextCmp + "\">";
            for (String requestedAuthnContext : requestedAuthnContexts) {
                requestedAuthnContextStr = requestedAuthnContextStr + "<saml:AuthnContextClassRef>" + requestedAuthnContext + "</saml:AuthnContextClassRef>";
            }
            requestedAuthnContextStr = requestedAuthnContextStr + "</samlp:RequestedAuthnContext>";
        }
        valueMap.put("requestedAuthnContextStr", requestedAuthnContextStr);
        return new StrSubstitutor(valueMap);
    }

    private static StringBuilder getAuthnRequestTemplate() {
        StringBuilder template = new StringBuilder();
        template.append("<samlp:AuthnRequest xmlns:samlp=\"urn:oasis:names:tc:SAML:2.0:protocol\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\" ID=\"${id}\" Version=\"2.0\" IssueInstant=\"${issueInstant}\"${providerStr}${forceAuthnStr}${isPassiveStr}${destinationStr} ProtocolBinding=\"${protocolBinding}\" AssertionConsumerServiceURL=\"${assertionConsumerServiceURL}\">");
        template.append("<saml:Issuer>${spEntityid}</saml:Issuer>");
        template.append("${subjectStr}${nameIDPolicyStr}${requestedAuthnContextStr}</samlp:AuthnRequest>");
        return template;
    }

    public String getId() {
        return this.id;
    }
}

