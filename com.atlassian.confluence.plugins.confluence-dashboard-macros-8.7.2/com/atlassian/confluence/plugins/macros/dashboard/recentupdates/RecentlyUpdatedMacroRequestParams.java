/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.web.context.HttpContext
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates;

import com.atlassian.confluence.web.context.HttpContext;
import javax.servlet.http.HttpServletRequest;

public class RecentlyUpdatedMacroRequestParams {
    public static final String SELECTED_TAB_PARAM = "updatesSelectedTab";
    public static final String SELECTED_CATEGORY_PARAM = "updatesSelectedTeam";
    public static final String MAX_RESULTS_PARAM = "maxRecentlyUpdatedPageCount";
    private Integer maxRecentUpdates;
    private String selectedCategory;
    private String selectedTab;

    public RecentlyUpdatedMacroRequestParams(Integer maxRecentUpdates, String selectedCategory, String selectedTab) {
        this.maxRecentUpdates = maxRecentUpdates;
        this.selectedCategory = selectedCategory;
        this.selectedTab = selectedTab;
    }

    public RecentlyUpdatedMacroRequestParams(HttpContext httpContext) {
        HttpServletRequest request = httpContext.getRequest();
        String strMaxRecentUpdates = request.getParameter(MAX_RESULTS_PARAM);
        try {
            this.maxRecentUpdates = Integer.parseInt(strMaxRecentUpdates);
        }
        catch (NumberFormatException numberFormatException) {
            // empty catch block
        }
        this.selectedTab = request.getParameter(SELECTED_TAB_PARAM);
        this.selectedCategory = request.getParameter(SELECTED_CATEGORY_PARAM);
    }

    public boolean hasMaxRecentUpdates() {
        return this.maxRecentUpdates != null;
    }

    public Integer getMaxRecentUpdates() {
        return this.maxRecentUpdates;
    }

    public String getSelectedCategory() {
        return this.selectedCategory;
    }

    public String getSelectedTab() {
        return this.selectedTab;
    }
}

