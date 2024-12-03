/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.apache.commons.lang3.StringUtils
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import com.atlassian.confluence.extra.calendar3.model.Project;
import com.atlassian.confluence.extra.calendar3.model.SearchFilter;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class QueryOptions
implements JsonSerializable,
Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(QueryOptions.class);
    @XmlElement
    private List<Project> projects;
    @XmlElement
    private List<SearchFilter> searchFilters;
    @XmlElement
    private boolean jqlNotSupported;
    @XmlElement
    private String visibleFieldNamesJson;
    @XmlElement
    private String visibleFunctionNamesJson;
    @XmlElement
    private String jqlReservedKeywordsJson;
    @XmlElement
    private boolean dateRangeSupported;

    public QueryOptions(List<Project> projects, List<SearchFilter> searchFilters, boolean jqlNotSupported, String visibleFieldNamesJson, String visibleFunctionNamesJson, String jqlReservedKeywordsJson, boolean dateRangeSupported) {
        this.setProjects(projects);
        this.setSearchFilters(searchFilters);
        this.setJqlNotSupported(jqlNotSupported);
        this.setVisibleFieldNamesJson(visibleFieldNamesJson);
        this.setVisibleFunctionNamesJson(visibleFunctionNamesJson);
        this.setJqlReservedKeywordsJson(jqlReservedKeywordsJson);
        this.setDateRangeSupported(dateRangeSupported);
    }

    public QueryOptions() {
        this(null, null, false, null, null, null, false);
    }

    public List<Project> getProjects() {
        return this.projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<SearchFilter> getSearchFilters() {
        return this.searchFilters;
    }

    public void setSearchFilters(List<SearchFilter> searchFilters) {
        this.searchFilters = searchFilters;
    }

    public boolean isJqlNotSupported() {
        return this.jqlNotSupported;
    }

    public void setJqlNotSupported(boolean jqlNotSupported) {
        this.jqlNotSupported = jqlNotSupported;
    }

    public String getVisibleFieldNamesJson() {
        return this.visibleFieldNamesJson;
    }

    public void setVisibleFieldNamesJson(String visibleFieldNamesJson) {
        this.visibleFieldNamesJson = visibleFieldNamesJson;
    }

    public String getVisibleFunctionNamesJson() {
        return this.visibleFunctionNamesJson;
    }

    public void setVisibleFunctionNamesJson(String visibleFunctionNamesJson) {
        this.visibleFunctionNamesJson = visibleFunctionNamesJson;
    }

    public String getJqlReservedKeywordsJson() {
        return this.jqlReservedKeywordsJson;
    }

    public void setJqlReservedKeywordsJson(String jqlReservedKeywordsJson) {
        this.jqlReservedKeywordsJson = jqlReservedKeywordsJson;
    }

    public boolean isDateRangeSupported() {
        return this.dateRangeSupported;
    }

    public void setDateRangeSupported(boolean dateRangeSupported) {
        this.dateRangeSupported = dateRangeSupported;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObj = new JSONObject();
        try {
            String jqlReservedKeywords;
            String string;
            List<SearchFilter> searchFilters;
            List<Project> projects = this.getProjects();
            if (null != projects && !projects.isEmpty()) {
                JSONArray projectsArray = new JSONArray();
                for (Project project : projects) {
                    projectsArray.put((Object)project.toJson());
                }
                thisObj.put("projects", (Object)projectsArray);
            }
            if (null != (searchFilters = this.getSearchFilters()) && !searchFilters.isEmpty()) {
                JSONArray searchFiltersArray = new JSONArray();
                for (SearchFilter searchFilter : searchFilters) {
                    searchFiltersArray.put((Object)searchFilter.toJson());
                }
                thisObj.put("searchRequests", (Object)searchFiltersArray);
            }
            thisObj.put("jqlNotSupported", this.isJqlNotSupported());
            String visibleFieldNamesJson = this.getVisibleFieldNamesJson();
            if (StringUtils.isNotBlank((CharSequence)visibleFieldNamesJson)) {
                thisObj.put("visibleFieldNamesJson", (Object)visibleFieldNamesJson);
            }
            if (StringUtils.isNotBlank((CharSequence)(string = this.getVisibleFunctionNamesJson()))) {
                thisObj.put("visibleFunctionNamesJson", (Object)string);
            }
            if (StringUtils.isNotBlank((CharSequence)(jqlReservedKeywords = this.getJqlReservedKeywordsJson()))) {
                thisObj.put("jqlReservedKeywordsJson", (Object)jqlReservedKeywords);
            }
            thisObj.put("dateRangeSupported", this.isDateRangeSupported());
        }
        catch (JSONException json) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)json);
        }
        return thisObj;
    }
}

