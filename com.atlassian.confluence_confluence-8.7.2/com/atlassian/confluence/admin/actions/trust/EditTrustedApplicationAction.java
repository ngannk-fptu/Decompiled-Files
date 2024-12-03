/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.struts2.interceptor.SessionAware
 *  org.springframework.util.StringUtils
 */
package com.atlassian.confluence.admin.actions.trust;

import com.atlassian.confluence.admin.actions.trust.AbstractTrustedApplicationAction;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.persistence.dao.hibernate.AliasedKey;
import com.atlassian.confluence.security.trust.CertificateRetrievalException;
import com.atlassian.confluence.security.trust.ConfluenceTrustedApplication;
import com.atlassian.confluence.security.trust.HttpCertificateRetrievalService;
import com.atlassian.confluence.security.trust.TrustedApplicationIpRestriction;
import com.atlassian.confluence.security.trust.TrustedApplicationRestriction;
import com.atlassian.confluence.security.trust.TrustedApplicationUrlRestriction;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.util.StringUtils;

@WebSudoRequired
@AdminOnly
public class EditTrustedApplicationAction
extends AbstractTrustedApplicationAction
implements SessionAware {
    private HttpCertificateRetrievalService certificateRetrievalService;
    private String applicationName;
    private String applicationKeyAlias;
    private long id;
    private int requestTimeout;
    private Set<String> ipRestrictions;
    private Set<String> urlRestrictions;
    private static final int DEFAULT_REQUEST_TIMEOUT = 2000;
    private Map sessionMap;
    private static final String PUBLIC_KEY = "com.atlassian.confluence.admin.actions.trust.EditTrustedApplicationAction.publicKey";

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        ConfluenceTrustedApplication application = this.trustedApplicationsManager.getTrustedApplication(this.id);
        this.applicationName = application.getName();
        this.requestTimeout = application.getRequestTimeout();
        this.applicationKeyAlias = application.getPublicKey().getAlias();
        this.ipRestrictions = application.getIpRestrictions();
        this.urlRestrictions = application.getUrlRestrictions();
        return super.execute();
    }

    public String add() {
        if (this.trustedApplicationsManager.getTrustedApplicationByName(this.applicationName) == null) {
            try {
                AliasedKey key = this.certificateRetrievalService.retrieveApplicationCertificate(this.applicationName);
                this.sessionMap.put(PUBLIC_KEY, key);
                this.applicationKeyAlias = key.getAlias();
                this.requestTimeout = 2000;
                return "success";
            }
            catch (CertificateRetrievalException e) {
                this.addActionError("trusted.application.add.error", this.applicationName);
                return "error";
            }
        }
        this.addActionError("trusted.application.add.duplicate", this.applicationName);
        return "error";
    }

    public String save() {
        ConfluenceTrustedApplication application = this.trustedApplicationsManager.getTrustedApplication(this.id);
        if (application == null) {
            application = new ConfluenceTrustedApplication();
            application.setPublicKey((AliasedKey)this.sessionMap.get(PUBLIC_KEY));
        }
        application.setName(this.applicationName);
        application.setRequestTimeout(this.requestTimeout);
        HashSet<TrustedApplicationRestriction> restrictions = new HashSet<TrustedApplicationRestriction>();
        for (String ipPattern : this.ipRestrictions) {
            restrictions.add(new TrustedApplicationIpRestriction(ipPattern));
        }
        application.setRestrictions(restrictions);
        for (String urlRestriction : this.urlRestrictions) {
            application.addRestriction(new TrustedApplicationUrlRestriction(urlRestriction));
        }
        this.trustedApplicationsManager.saveTrustedApplication(application);
        return "success";
    }

    public String getApplicationName() {
        return this.applicationName;
    }

    public String getApplicationKeyAlias() {
        return this.applicationKeyAlias;
    }

    public int getTimeout() {
        return this.requestTimeout;
    }

    public String getIpRestrictions() {
        return this.format(this.ipRestrictions);
    }

    public String getUrlRestrictions() {
        return this.format(this.urlRestrictions);
    }

    public int getRequestTimeout() {
        return this.requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setApplicationKeyAlias(String applicationId) {
        this.applicationKeyAlias = applicationId;
    }

    public void setIpRestrictions(String ipRestrictionsString) {
        this.ipRestrictions = this.tokenize(ipRestrictionsString);
    }

    public void setUrlRestrictions(String urlRestrictionsString) {
        this.urlRestrictions = this.tokenize(urlRestrictionsString);
    }

    private Set<String> tokenize(String commaDelimitedList) {
        return StringUtils.commaDelimitedListToSet((String)StringUtils.trimAllWhitespace((String)commaDelimitedList));
    }

    private String format(Set set) {
        return StringUtils.collectionToCommaDelimitedString((Collection)set);
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCertificateRetrievalService(HttpCertificateRetrievalService certificateRetrievalService) {
        this.certificateRetrievalService = certificateRetrievalService;
    }

    public void setSession(Map map) {
        this.sessionMap = map;
    }
}

