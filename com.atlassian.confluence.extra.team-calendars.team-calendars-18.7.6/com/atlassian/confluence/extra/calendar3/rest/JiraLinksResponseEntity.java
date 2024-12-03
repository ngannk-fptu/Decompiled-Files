/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.google.common.collect.Collections2
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.rest;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import com.google.common.collect.Collections2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class JiraLinksResponseEntity
implements JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(JiraLinksResponseEntity.class);
    @XmlElement
    private List<JiraLink> jiraLinks;

    public JiraLinksResponseEntity(Collection<ApplicationLink> jiraLinks) {
        if (null != jiraLinks) {
            this.setJiraLinks(new ArrayList<JiraLink>(Collections2.transform(jiraLinks, JiraLink::new)));
        }
    }

    public JiraLinksResponseEntity() {
        this(null);
    }

    public List<JiraLink> getJiraLinks() {
        return this.jiraLinks;
    }

    public void setJiraLinks(List<JiraLink> jiraLinks) {
        this.jiraLinks = jiraLinks;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = new JSONObject();
        try {
            JSONArray jiraLinksArray = new JSONArray();
            if (null != this.getJiraLinks()) {
                for (JiraLink jiraLink : this.getJiraLinks()) {
                    jiraLinksArray.put((Object)jiraLink.toJson());
                }
            }
            thisObject.put("jiraLinks", (Object)jiraLinksArray);
        }
        catch (JSONException e) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)e);
        }
        return thisObject;
    }

    @XmlRootElement
    public static class JiraLink
    implements JsonSerializable {
        @XmlElement
        private String linkId;
        @XmlElement
        private String linkName;
        @XmlElement
        private String displayUrl;

        public JiraLink(ApplicationLink jiraLink) {
            if (null != jiraLink) {
                this.setLinkId(jiraLink.getId().toString());
                this.setLinkName(jiraLink.getName());
                this.setDisplayUrl(jiraLink.getDisplayUrl().toString());
            }
        }

        public JiraLink() {
            this(null);
        }

        public String getLinkId() {
            return this.linkId;
        }

        public void setLinkId(String linkId) {
            this.linkId = linkId;
        }

        public String getLinkName() {
            return this.linkName;
        }

        public void setLinkName(String linkName) {
            this.linkName = linkName;
        }

        public String getDisplayUrl() {
            return this.displayUrl;
        }

        public void setDisplayUrl(String displayUrl) {
            this.displayUrl = displayUrl;
        }

        @Override
        public JSONObject toJson() {
            JSONObject jiraLink = new JSONObject();
            try {
                jiraLink.put("id", (Object)this.getLinkId()).put("name", (Object)this.getLinkName()).put("displayUrl", (Object)this.getDisplayUrl());
            }
            catch (JSONException e) {
                LOG.error("Unable to create a JSON object based on this object", (Throwable)e);
            }
            return jiraLink;
        }
    }
}

