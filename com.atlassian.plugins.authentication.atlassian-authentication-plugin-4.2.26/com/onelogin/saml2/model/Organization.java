/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.onelogin.saml2.model;

import java.net.URL;
import org.apache.commons.lang3.StringUtils;

public class Organization {
    private final String orgName;
    private final String orgDisplayName;
    private final String orgUrl;
    private final String orgLangAttribute;

    public Organization(String orgName, String orgDisplayName, URL orgUrl, String orgLangAttribute) {
        this(orgName, orgDisplayName, orgUrl != null ? orgUrl.toString() : "", orgLangAttribute);
    }

    public Organization(String orgName, String orgDisplayName, URL orgUrl) {
        this(orgName, orgDisplayName, orgUrl, "en");
    }

    public Organization(String orgName, String orgDisplayName, String orgUrl, String orgLangAttribute) {
        this.orgName = orgName != null ? orgName : "";
        this.orgDisplayName = orgDisplayName != null ? orgDisplayName : "";
        this.orgUrl = orgUrl != null ? orgUrl : "";
        this.orgLangAttribute = (String)StringUtils.defaultIfBlank((CharSequence)orgLangAttribute, (CharSequence)"en");
    }

    public Organization(String orgName, String orgDisplayName, String orgUrl) {
        this(orgName, orgDisplayName, orgUrl, "en");
    }

    public final String getOrgName() {
        return this.orgName;
    }

    public final String getOrgDisplayName() {
        return this.orgDisplayName;
    }

    public final String getOrgUrl() {
        return this.orgUrl;
    }

    public final String getOrgLangAttribute() {
        return this.orgLangAttribute;
    }

    public final Boolean equalsTo(Organization org) {
        return this.orgName.equals(org.getOrgName()) && this.orgDisplayName.equals(org.getOrgDisplayName()) && this.orgUrl.equals(org.getOrgUrl()) && this.orgLangAttribute.equals(org.getOrgLangAttribute());
    }
}

