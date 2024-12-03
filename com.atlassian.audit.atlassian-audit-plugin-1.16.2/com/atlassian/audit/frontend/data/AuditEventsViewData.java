/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.audit.frontend.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AuditEventsViewData {
    private final LinkedHashMap<String, String> actionFilterValues = new LinkedHashMap();
    private final List<AffectedObjectsFilter> affectedObjectsFilters = new ArrayList<AffectedObjectsFilter>();
    private final LinkedHashMap<String, String> categoryFilterValues = new LinkedHashMap();
    private final Map<String, List<String>> delegatedViewsAffectedObjectFilters = new HashMap<String, List<String>>();
    private final List<String> globalAffectedObjectsFilters = new ArrayList<String>();
    private boolean advancedFiltersEnabled;
    private String pageTitle;
    private boolean selectiveExportEnabled = false;
    private String serverTimeZone;
    private long totalCountInDatabase;
    private String userTimeZone;
    private String userLocale;

    public AuditEventsViewData withSelectiveExportEnabled(boolean selectiveExportEnabled) {
        this.selectiveExportEnabled = selectiveExportEnabled;
        return this;
    }

    public AuditEventsViewData withPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

    public AuditEventsViewData withUserTimeZone(String userTimeZone) {
        this.userTimeZone = userTimeZone;
        return this;
    }

    public AuditEventsViewData withServerTimeZone(String serverTimeZone) {
        this.serverTimeZone = serverTimeZone;
        return this;
    }

    public AuditEventsViewData withUserLocale(String userLocale) {
        this.userLocale = userLocale;
        return this;
    }

    public AuditEventsViewData withAdvancedFilters(boolean advancedFiltersEnabled) {
        this.advancedFiltersEnabled = advancedFiltersEnabled;
        return this;
    }

    public AuditEventsViewData affectedObjectsFilters(Collection<AffectedObjectsFilter> affectedObjectsFilter) {
        this.affectedObjectsFilters.addAll(affectedObjectsFilter);
        return this;
    }

    public AuditEventsViewData globalAffectedObjectsFilters(Collection<String> globalAffectedObjectsFilters) {
        this.globalAffectedObjectsFilters.addAll(globalAffectedObjectsFilters);
        return this;
    }

    public AuditEventsViewData delegatedAffectedObjectsFilters(Map<String, List<String>> delegatedViewsAffectedObjectFilters) {
        this.delegatedViewsAffectedObjectFilters.putAll(delegatedViewsAffectedObjectFilters);
        return this;
    }

    public AuditEventsViewData actionFilter(@Nonnull LinkedHashMap<String, String> actionsAndTranslatedLabel) {
        this.actionFilterValues.putAll(actionsAndTranslatedLabel);
        return this;
    }

    public AuditEventsViewData categoryFilter(@Nonnull LinkedHashMap<String, String> categoriesAndTranslatedLabel) {
        this.categoryFilterValues.putAll(categoriesAndTranslatedLabel);
        return this;
    }

    public AuditEventsViewData count(long count) {
        this.totalCountInDatabase = count;
        return this;
    }

    public String getPageTitle() {
        return this.pageTitle;
    }

    public boolean isSelectiveExportEnabled() {
        return this.selectiveExportEnabled;
    }

    public String getUserTimeZone() {
        return this.userTimeZone;
    }

    public String getServerTimeZone() {
        return this.serverTimeZone;
    }

    public String getUserLocale() {
        return this.userLocale;
    }

    public boolean isAdvancedFiltersEnabled() {
        return this.advancedFiltersEnabled;
    }

    public List<AffectedObjectsFilter> getAffectedObjectsFilters() {
        return this.affectedObjectsFilters;
    }

    public List<String> getGlobalAffectedObjectsFilters() {
        return this.globalAffectedObjectsFilters;
    }

    public Map<String, List<String>> getDelegatedViewsAffectedObjectFilters() {
        return this.delegatedViewsAffectedObjectFilters;
    }

    public Map<String, String> getCategoryFilterValues() {
        return this.categoryFilterValues;
    }

    public Map<String, String> getActionFilterValues() {
        return this.actionFilterValues;
    }

    public long getTotalCountInDatabase() {
        return this.totalCountInDatabase;
    }

    public String toString() {
        return "AuditGlobalViewData{pageTitle='" + this.pageTitle + '\'' + ", selectiveExportEnabled=" + this.selectiveExportEnabled + ", serverTimeZone='" + this.serverTimeZone + '\'' + ", userTimeZone='" + this.userTimeZone + '\'' + ", userLocale='" + this.userLocale + '\'' + ", advancedFiltersEnabled=" + this.advancedFiltersEnabled + ", affectedObjectsFilters=" + this.affectedObjectsFilters + ", globalAffectedObjectsFilters=" + this.globalAffectedObjectsFilters + ", delegatedViewsAffectedObjectFilters=" + this.delegatedViewsAffectedObjectFilters + ", categoryFilterValues=" + this.categoryFilterValues + ", actionFilterValues=" + this.actionFilterValues + ", totalCountInDatabase=" + this.totalCountInDatabase + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuditEventsViewData)) {
            return false;
        }
        AuditEventsViewData that = (AuditEventsViewData)o;
        return this.selectiveExportEnabled == that.selectiveExportEnabled && this.advancedFiltersEnabled == that.advancedFiltersEnabled && Objects.equals(this.pageTitle, that.pageTitle) && Objects.equals(this.serverTimeZone, that.serverTimeZone) && Objects.equals(this.userTimeZone, that.userTimeZone) && Objects.equals(this.userLocale, that.userLocale) && Objects.equals(this.affectedObjectsFilters, that.affectedObjectsFilters) && Objects.equals(this.globalAffectedObjectsFilters, that.globalAffectedObjectsFilters) && Objects.equals(this.delegatedViewsAffectedObjectFilters, that.delegatedViewsAffectedObjectFilters) && Objects.equals(this.categoryFilterValues, that.categoryFilterValues) && Objects.equals(this.actionFilterValues, that.actionFilterValues) && Objects.equals(this.totalCountInDatabase, that.totalCountInDatabase);
    }

    public int hashCode() {
        return Objects.hash(this.pageTitle, this.selectiveExportEnabled, this.serverTimeZone, this.userTimeZone, this.userLocale, this.advancedFiltersEnabled, this.affectedObjectsFilters, this.globalAffectedObjectsFilters, this.delegatedViewsAffectedObjectFilters, this.categoryFilterValues, this.actionFilterValues, this.totalCountInDatabase);
    }

    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class AffectedObjectsFilter {
        private String resourceType;
        private String placeholderText;

        public AffectedObjectsFilter(String resourceType, String placeholderText) {
            this.resourceType = resourceType;
            this.placeholderText = placeholderText;
        }

        public String getResourceType() {
            return this.resourceType;
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public String getPlaceholderText() {
            return this.placeholderText;
        }

        public void setPlaceholderText(String placeholderText) {
            this.placeholderText = placeholderText;
        }

        public String toString() {
            return "{resourceType='" + this.resourceType + '\'' + ", placeholderText='" + this.placeholderText + '\'' + '}';
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            AffectedObjectsFilter that = (AffectedObjectsFilter)o;
            return Objects.equals(this.resourceType, that.resourceType) && Objects.equals(this.placeholderText, that.placeholderText);
        }

        public int hashCode() {
            return Objects.hash(this.resourceType, this.placeholderText);
        }
    }
}

