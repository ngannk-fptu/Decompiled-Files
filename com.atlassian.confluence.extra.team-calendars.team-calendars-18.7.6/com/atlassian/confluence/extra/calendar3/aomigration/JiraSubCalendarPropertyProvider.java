/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.aomigration;

import com.atlassian.confluence.extra.calendar3.ActiveObjectsServiceWrapper;
import com.atlassian.confluence.extra.calendar3.JsonPropertyGetter;
import com.atlassian.confluence.extra.calendar3.aomigration.SubCalendarPropertyProvider;
import com.atlassian.confluence.extra.calendar3.model.persistence.SubCalendarEntity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JiraSubCalendarPropertyProvider
implements SubCalendarPropertyProvider {
    private static final Logger LOG = LoggerFactory.getLogger(JiraSubCalendarPropertyProvider.class);

    @Override
    public SubCalendarEntity addProperties(ActiveObjectsServiceWrapper activeObjectsWrapper, SubCalendarEntity subCalendarEntity, JSONObject theSubCalendar, JsonPropertyGetter propertyGetter) {
        Set<String> durations;
        String applicationId = (String)propertyGetter.getProperty(theSubCalendar, "applicationId", true, false, "");
        if (StringUtils.isNotBlank(applicationId)) {
            activeObjectsWrapper.createSubCalendarEntityProperty(subCalendarEntity, "applicationId", applicationId);
        }
        String projectKey = (String)propertyGetter.getProperty(theSubCalendar, "projectKey", true, false, "");
        int searchFilterId = (Integer)propertyGetter.getProperty(theSubCalendar, "searchFilterId", true, false, 0);
        String jql = (String)propertyGetter.getProperty(theSubCalendar, "jql", true, false, "");
        if (StringUtils.isNotBlank(projectKey)) {
            activeObjectsWrapper.createSubCalendarEntityProperty(subCalendarEntity, "projectKey", projectKey);
        } else if (0 < searchFilterId) {
            activeObjectsWrapper.createSubCalendarEntityProperty(subCalendarEntity, "searchFilterId", String.valueOf(searchFilterId));
        } else if (StringUtils.isNotBlank(jql)) {
            activeObjectsWrapper.createSubCalendarEntityProperty(subCalendarEntity, "jql", jql);
        }
        Set<Object> dateFieldNames = new HashSet();
        if (theSubCalendar.has("show")) {
            String showValue = theSubCalendar.optString("show");
            dateFieldNames = StringUtils.equals("roadmap", showValue) ? new HashSet<String>(Arrays.asList("versiondue", "duedate")) : new HashSet<String>(Arrays.asList("duedate", "resolution"));
        } else if (theSubCalendar.has("options")) {
            long options = theSubCalendar.optLong("options");
            if ((options & 1L) != 0L) {
                dateFieldNames.add("versiondue");
            }
            if ((options & 2L) != 0L) {
                dateFieldNames.add("duedate");
            }
            if ((options & 4L) != 0L) {
                dateFieldNames.add("resolution");
            }
        } else if (theSubCalendar.has("dateFieldNames")) {
            dateFieldNames = this.getDateFieldOrDurationProperty(theSubCalendar, "dateFieldNames");
        }
        if (dateFieldNames != null && !dateFieldNames.isEmpty()) {
            for (String string : dateFieldNames) {
                activeObjectsWrapper.createSubCalendarEntityProperty(subCalendarEntity, "dateFieldName", string);
            }
        }
        if ((durations = this.getDateFieldOrDurationProperty(theSubCalendar, "durations")) != null && !durations.isEmpty()) {
            for (String duration : durations) {
                activeObjectsWrapper.createSubCalendarEntityProperty(subCalendarEntity, "duration", duration);
            }
        }
        return (SubCalendarEntity)activeObjectsWrapper.getActiveObjects().get(SubCalendarEntity.class, (Object)subCalendarEntity.getID());
    }

    private Set<String> getDateFieldOrDurationProperty(JSONObject theSubCalendar, String property) {
        JSONArray jsonArray = null;
        HashSet<String> propertyValues = new HashSet<String>();
        try {
            if (theSubCalendar.has(property)) {
                jsonArray = theSubCalendar.getJSONArray(property);
                int length = jsonArray.length();
                if (property.equals("dateFieldNames")) {
                    for (int i = 0; i < length; ++i) {
                        propertyValues.add(jsonArray.getString(i));
                    }
                } else if (property.equals("durations")) {
                    for (int i = 0; i < length; ++i) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        propertyValues.add(jsonObject.opt("startDateFieldName") + "/" + jsonObject.opt("endDateFieldName"));
                    }
                }
            }
        }
        catch (JSONException e) {
            LOG.error("Error get properties ", (Throwable)e);
            return null;
        }
        return propertyValues;
    }
}

