/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.DateTime
 *  org.opensaml.core.config.InitializationException
 *  org.opensaml.core.config.InitializationService
 *  org.opensaml.core.xml.XMLObject
 *  org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport
 *  org.opensaml.core.xml.schema.XSString
 *  org.opensaml.core.xml.schema.impl.XSStringBuilder
 *  org.opensaml.saml.saml2.core.Assertion
 *  org.opensaml.saml.saml2.core.Attribute
 *  org.opensaml.saml.saml2.core.AttributeStatement
 *  org.opensaml.saml.saml2.core.AttributeValue
 *  org.opensaml.saml.saml2.core.Audience
 *  org.opensaml.saml.saml2.core.AudienceRestriction
 *  org.opensaml.saml.saml2.core.AuthnContext
 *  org.opensaml.saml.saml2.core.AuthnContextClassRef
 *  org.opensaml.saml.saml2.core.AuthnStatement
 *  org.opensaml.saml.saml2.core.Conditions
 *  org.opensaml.saml.saml2.core.Issuer
 *  org.opensaml.saml.saml2.core.NameID
 *  org.opensaml.saml.saml2.core.Subject
 *  org.opensaml.saml.saml2.core.SubjectConfirmation
 *  org.opensaml.saml.saml2.core.SubjectConfirmationData
 */
package com.nimbusds.oauth2.sdk.assertions.saml2;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.SerializeException;
import com.nimbusds.oauth2.sdk.assertions.AssertionDetails;
import com.nimbusds.oauth2.sdk.assertions.saml2.SAML2Utils;
import com.nimbusds.oauth2.sdk.id.Identifier;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.nimbusds.oauth2.sdk.util.MapUtils;
import com.nimbusds.openid.connect.sdk.claims.ACR;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.Immutable;
import org.joda.time.DateTime;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.schema.impl.XSStringBuilder;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.AttributeValue;
import org.opensaml.saml.saml2.core.Audience;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnContext;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;

@Immutable
public class SAML2AssertionDetails
extends AssertionDetails {
    private final String subjectFormat;
    private final Date subjectAuthTime;
    private final ACR subjectACR;
    private final Date nbf;
    private final InetAddress clientAddress;
    private final Map<String, List<String>> attrStatement;

    public SAML2AssertionDetails(com.nimbusds.oauth2.sdk.id.Issuer issuer, com.nimbusds.oauth2.sdk.id.Subject subject, com.nimbusds.oauth2.sdk.id.Audience audience) {
        this(issuer, subject, null, null, null, audience.toSingleAudienceList(), new Date(new Date().getTime() + 300000L), null, new Date(), new Identifier(), null, null);
    }

    public SAML2AssertionDetails(com.nimbusds.oauth2.sdk.id.Issuer issuer, com.nimbusds.oauth2.sdk.id.Subject subject, String subjectFormat, Date subjectAuthTime, ACR subjectACR, List<com.nimbusds.oauth2.sdk.id.Audience> audience, Date exp, Date nbf, Date iat, Identifier id, InetAddress clientAddress, Map<String, List<String>> attrStatement) {
        super(issuer, subject, audience, iat, exp, id);
        if (iat == null) {
            throw new IllegalArgumentException("The issue time must not be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("The assertion identifier must not be null");
        }
        this.subjectFormat = subjectFormat;
        this.subjectAuthTime = subjectAuthTime;
        this.subjectACR = subjectACR;
        this.clientAddress = clientAddress;
        this.nbf = nbf;
        this.attrStatement = attrStatement;
    }

    public String getSubjectFormat() {
        return this.subjectFormat;
    }

    public Date getSubjectAuthenticationTime() {
        return this.subjectAuthTime;
    }

    public ACR getSubjectACR() {
        return this.subjectACR;
    }

    public Date getNotBeforeTime() {
        return this.nbf;
    }

    public InetAddress getClientInetAddress() {
        return this.clientAddress;
    }

    public Map<String, List<String>> getAttributeStatement() {
        return this.attrStatement;
    }

    public Assertion toSAML2Assertion() throws SerializeException {
        try {
            InitializationService.initialize();
        }
        catch (InitializationException e) {
            throw new SerializeException(e.getMessage(), e);
        }
        Assertion a = SAML2Utils.buildSAMLObject(Assertion.class);
        a.setID(this.getID().getValue());
        a.setIssueInstant(new DateTime((Object)this.getIssueTime()));
        Issuer iss = SAML2Utils.buildSAMLObject(Issuer.class);
        iss.setValue(this.getIssuer().getValue());
        a.setIssuer(iss);
        Conditions conditions = SAML2Utils.buildSAMLObject(Conditions.class);
        AudienceRestriction audRestriction = SAML2Utils.buildSAMLObject(AudienceRestriction.class);
        for (com.nimbusds.oauth2.sdk.id.Audience audItem : this.getAudience()) {
            Audience aud = SAML2Utils.buildSAMLObject(Audience.class);
            aud.setAudienceURI(audItem.getValue());
            audRestriction.getAudiences().add(aud);
        }
        conditions.getAudienceRestrictions().add(audRestriction);
        a.setConditions(conditions);
        Subject sub = SAML2Utils.buildSAMLObject(Subject.class);
        NameID nameID = SAML2Utils.buildSAMLObject(NameID.class);
        nameID.setFormat(this.subjectFormat);
        nameID.setValue(this.getSubject().getValue());
        sub.setNameID(nameID);
        SubjectConfirmation subCm = SAML2Utils.buildSAMLObject(SubjectConfirmation.class);
        subCm.setMethod("urn:oasis:names:tc:SAML:2.0:cm:bearer");
        SubjectConfirmationData subCmData = SAML2Utils.buildSAMLObject(SubjectConfirmationData.class);
        subCmData.setNotOnOrAfter(new DateTime((Object)this.getExpirationTime()));
        subCmData.setNotBefore(this.getNotBeforeTime() != null ? new DateTime((Object)this.getNotBeforeTime()) : null);
        subCmData.setRecipient(this.getAudience().get(0).getValue());
        if (this.clientAddress != null) {
            subCmData.setAddress(this.clientAddress.getHostAddress());
        }
        subCm.setSubjectConfirmationData(subCmData);
        sub.getSubjectConfirmations().add(subCm);
        a.setSubject(sub);
        if (this.subjectAuthTime != null || this.subjectACR != null) {
            AuthnStatement authnStmt = SAML2Utils.buildSAMLObject(AuthnStatement.class);
            if (this.subjectAuthTime != null) {
                authnStmt.setAuthnInstant(new DateTime((Object)this.subjectAuthTime));
            }
            if (this.subjectACR != null) {
                AuthnContext authnCtx = SAML2Utils.buildSAMLObject(AuthnContext.class);
                AuthnContextClassRef acr = SAML2Utils.buildSAMLObject(AuthnContextClassRef.class);
                acr.setAuthnContextClassRef(this.subjectACR.getValue());
                authnCtx.setAuthnContextClassRef(acr);
                authnStmt.setAuthnContext(authnCtx);
            }
            a.getAuthnStatements().add(authnStmt);
        }
        if (MapUtils.isNotEmpty(this.attrStatement)) {
            AttributeStatement attrSet = SAML2Utils.buildSAMLObject(AttributeStatement.class);
            for (Map.Entry<String, List<String>> entry : this.attrStatement.entrySet()) {
                Attribute attr = SAML2Utils.buildSAMLObject(Attribute.class);
                attr.setName(entry.getKey());
                XSStringBuilder stringBuilder = (XSStringBuilder)XMLObjectProviderRegistrySupport.getBuilderFactory().getBuilder(XSString.TYPE_NAME);
                for (String v : entry.getValue()) {
                    XSString stringValue = (XSString)stringBuilder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, XSString.TYPE_NAME);
                    stringValue.setValue(v);
                    attr.getAttributeValues().add(stringValue);
                }
                attrSet.getAttributes().add(attr);
            }
            a.getAttributeStatements().add(attrSet);
        }
        return a;
    }

    public static SAML2AssertionDetails parse(Assertion assertion) throws ParseException {
        Date nbf;
        List subCms;
        if (assertion.getIssuer() == null) {
            throw new ParseException("Missing Assertion Issuer element");
        }
        com.nimbusds.oauth2.sdk.id.Issuer issuer = new com.nimbusds.oauth2.sdk.id.Issuer(assertion.getIssuer().getValue());
        if (assertion.getSubject() == null) {
            throw new ParseException("Missing Assertion Subject element");
        }
        if (assertion.getSubject().getNameID() == null) {
            throw new ParseException("Missing Assertion Subject NameID element");
        }
        com.nimbusds.oauth2.sdk.id.Subject subject = new com.nimbusds.oauth2.sdk.id.Subject(assertion.getSubject().getNameID().getValue());
        String subjectFormat = assertion.getSubject().getNameID().getFormat();
        Date subjectAuthTime = null;
        ACR subjectACR = null;
        if (CollectionUtils.isNotEmpty(assertion.getAuthnStatements())) {
            for (AuthnStatement authStmt : assertion.getAuthnStatements()) {
                if (authStmt == null) continue;
                if (authStmt.getAuthnInstant() != null) {
                    subjectAuthTime = authStmt.getAuthnInstant().toDate();
                }
                if (authStmt.getAuthnContext() == null || authStmt.getAuthnContext().getAuthnContextClassRef() == null) continue;
                subjectACR = new ACR(authStmt.getAuthnContext().getAuthnContextClassRef().getAuthnContextClassRef());
            }
        }
        if (CollectionUtils.isEmpty(subCms = assertion.getSubject().getSubjectConfirmations())) {
            throw new ParseException("Missing SubjectConfirmation element");
        }
        boolean bearerMethodFound = false;
        for (SubjectConfirmation subCm : subCms) {
            if (!"urn:oasis:names:tc:SAML:2.0:cm:bearer".equals(subCm.getMethod())) continue;
            bearerMethodFound = true;
            break;
        }
        if (!bearerMethodFound) {
            throw new ParseException("Missing SubjectConfirmation Method urn:oasis:names:tc:SAML:2.0:cm:bearer attribute");
        }
        Conditions conditions = assertion.getConditions();
        if (conditions == null) {
            throw new ParseException("Missing Conditions element");
        }
        List audRestrictions = conditions.getAudienceRestrictions();
        if (CollectionUtils.isEmpty(audRestrictions)) {
            throw new ParseException("Missing AudienceRestriction element");
        }
        HashSet<com.nimbusds.oauth2.sdk.id.Audience> audSet = new HashSet<com.nimbusds.oauth2.sdk.id.Audience>();
        for (AudienceRestriction audRestriction : audRestrictions) {
            if (CollectionUtils.isEmpty(audRestriction.getAudiences())) continue;
            for (Audience aud : audRestriction.getAudiences()) {
                audSet.add(new com.nimbusds.oauth2.sdk.id.Audience(aud.getAudienceURI()));
            }
        }
        for (SubjectConfirmation subCm : subCms) {
            if (subCm.getSubjectConfirmationData() == null) continue;
            if (subCm.getSubjectConfirmationData().getRecipient() == null) {
                throw new ParseException("Missing SubjectConfirmationData Recipient attribute");
            }
            audSet.add(new com.nimbusds.oauth2.sdk.id.Audience(subCm.getSubjectConfirmationData().getRecipient()));
        }
        Date exp = conditions.getNotOnOrAfter() != null ? conditions.getNotOnOrAfter().toDate() : null;
        Date date = nbf = conditions.getNotBefore() != null ? conditions.getNotBefore().toDate() : null;
        if (exp == null) {
            for (SubjectConfirmation subCm : subCms) {
                if (subCm.getSubjectConfirmationData() == null) continue;
                exp = subCm.getSubjectConfirmationData().getNotOnOrAfter() != null ? subCm.getSubjectConfirmationData().getNotOnOrAfter().toDate() : null;
                nbf = subCm.getSubjectConfirmationData().getNotBefore() != null ? subCm.getSubjectConfirmationData().getNotBefore().toDate() : null;
            }
        }
        if (assertion.getID() == null) {
            throw new ParseException("Missing Assertion ID attribute");
        }
        Identifier id = new Identifier(assertion.getID());
        if (assertion.getIssueInstant() == null) {
            throw new ParseException("Missing Assertion IssueInstant attribute");
        }
        Date iat = assertion.getIssueInstant().toDate();
        InetAddress clientAddress = null;
        for (SubjectConfirmation subCm : subCms) {
            if (subCm.getSubjectConfirmationData() == null || subCm.getSubjectConfirmationData().getAddress() == null) continue;
            try {
                clientAddress = InetAddress.getByName(subCm.getSubjectConfirmationData().getAddress());
            }
            catch (UnknownHostException e) {
                throw new ParseException("Invalid Address: " + e.getMessage(), e);
            }
        }
        HashMap<String, List<String>> attrStatement = null;
        if (CollectionUtils.isNotEmpty(assertion.getAttributeStatements())) {
            attrStatement = new HashMap<String, List<String>>();
            for (AttributeStatement attrStmt : assertion.getAttributeStatements()) {
                if (attrStmt == null) continue;
                for (Attribute attr : attrStmt.getAttributes()) {
                    String name = attr.getName();
                    LinkedList<String> values = new LinkedList<String>();
                    for (XMLObject v : attr.getAttributeValues()) {
                        values.add(v.getDOM().getTextContent());
                    }
                    attrStatement.put(name, values);
                }
            }
        }
        return new SAML2AssertionDetails(issuer, subject, subjectFormat, subjectAuthTime, subjectACR, new ArrayList<com.nimbusds.oauth2.sdk.id.Audience>(audSet), exp, nbf, iat, id, clientAddress, attrStatement);
    }
}

