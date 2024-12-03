/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 *  com.atlassian.confluence.util.HtmlUtil
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.confluence.plugins.metadata.jira.model;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import com.atlassian.confluence.util.HtmlUtil;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JiraUnauthorisedAppLink {
    private ApplicationId applicationId;
    @XmlElement
    private String name;
    @XmlElement
    private String htmlSafeName;
    @XmlElement
    private String authorisationUrl;

    public JiraUnauthorisedAppLink(ReadOnlyApplicationLink appLink, String authorisationUrl) {
        this.applicationId = appLink.getId();
        this.name = appLink.getName();
        this.htmlSafeName = HtmlUtil.htmlEncode((String)this.name);
        this.authorisationUrl = authorisationUrl;
    }

    public ApplicationId getApplicationId() {
        return this.applicationId;
    }

    public String getName() {
        return this.name;
    }

    public String getHtmlSafeName() {
        return this.htmlSafeName;
    }

    public String getAuthorisationUrl() {
        return this.authorisationUrl;
    }
}

