/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.annotate.JsonIgnore
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.Duration;
import com.atlassian.confluence.extra.calendar3.model.PersistedSubCalendar;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractJiraSubCalendar
extends PersistedSubCalendar
implements Cloneable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractJiraSubCalendar.class);
    private String id;
    private String applicationName;
    private String applicationId;
    private String projectKey;
    private String projectName;
    private long searchFilterId;
    private String searchFilterName;
    private String jql;
    private String creator;
    private String spaceName;
    private Set<String> dateFieldNames;
    private Set<Duration> durations;
    private long start;
    private long end;

    @Override
    @XmlElement
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public abstract String getType();

    @Override
    @XmlElement
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    @XmlElement
    @JsonIgnore
    public boolean isWatchable() {
        return false;
    }

    @Override
    @XmlElement
    @JsonIgnore
    public boolean isRestrictable() {
        return false;
    }

    @Override
    @XmlElement
    @JsonIgnore
    public boolean isEventInviteesSupported() {
        return false;
    }

    @XmlElement
    public String getApplicationName() {
        return this.applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @XmlElement
    public String getApplicationId() {
        return this.applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    @XmlElement
    public String getProjectKey() {
        return this.projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    @XmlElement
    public long getSearchFilterId() {
        return this.searchFilterId;
    }

    public void setSearchFilterId(long searchFilterId) {
        this.searchFilterId = searchFilterId;
    }

    @XmlElement
    public String getSearchFilterName() {
        return this.searchFilterName;
    }

    public void setSearchFilterName(String searchFilterName) {
        this.searchFilterName = searchFilterName;
    }

    @XmlElement
    public String getJql() {
        return this.jql;
    }

    public void setJql(String jql) {
        this.jql = jql;
    }

    @XmlElement
    public String getProjectName() {
        return this.projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean isShowingVersionDue() {
        return null != this.getDateFieldNames() && this.getDateFieldNames().contains("versiondue");
    }

    @Override
    @XmlElement
    public String getSpaceName() {
        return this.spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    @XmlElement
    public Set<String> getDateFieldNames() {
        return this.dateFieldNames;
    }

    public void setDateFieldNames(Set<String> dateFieldNames) {
        this.dateFieldNames = dateFieldNames;
    }

    @XmlElement
    public Set<Duration> getDurations() {
        return this.durations;
    }

    public void setDurations(Set<Duration> durations) {
        this.durations = durations;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObject = super.toJson();
        try {
            Set<Duration> durations;
            Set<String> dateFieldNames;
            String jql;
            long searchFilterId;
            thisObject.put("sourceLocation", (Object)this.getSourceLocation());
            thisObject.put("applicationName", (Object)this.getApplicationName());
            thisObject.put("applicationId", (Object)this.getApplicationId());
            String projectKey = this.getProjectKey();
            if (StringUtils.isNotBlank((CharSequence)projectKey)) {
                thisObject.put("projectKey", (Object)this.getProjectKey());
                thisObject.put("projectName", (Object)this.getProjectName());
            }
            if (0L < (searchFilterId = this.getSearchFilterId())) {
                thisObject.put("searchFilterId", searchFilterId);
                thisObject.put("searchFilterName", (Object)this.getSearchFilterName());
            }
            if (StringUtils.isNotBlank((CharSequence)(jql = this.getJql()))) {
                thisObject.put("jql", (Object)jql);
            }
            if (null != (dateFieldNames = this.getDateFieldNames()) && !dateFieldNames.isEmpty()) {
                JSONArray dateFieldNamesArray = new JSONArray();
                for (String dateFieldName : dateFieldNames) {
                    dateFieldNamesArray.put((Object)dateFieldName);
                }
                thisObject.put("dateFieldNames", (Object)dateFieldNamesArray);
            }
            if (null != (durations = this.getDurations()) && !durations.isEmpty()) {
                JSONArray durationsArray = new JSONArray();
                for (Duration duration : durations) {
                    durationsArray.put((Object)duration.toJson());
                }
                thisObject.put("durations", (Object)durationsArray);
            }
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObject;
    }

    @Override
    @XmlElement
    public long getStart() {
        return this.start;
    }

    @Override
    public void setStart(long start) {
        this.start = start;
    }

    @Override
    @XmlElement
    public long getEnd() {
        return this.end;
    }

    @Override
    public void setEnd(long end) {
        this.end = end;
    }
}

