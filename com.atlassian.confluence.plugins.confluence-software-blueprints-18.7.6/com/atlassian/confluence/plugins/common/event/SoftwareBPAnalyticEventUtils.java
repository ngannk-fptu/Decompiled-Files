/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.Label
 */
package com.atlassian.confluence.plugins.common.event;

import com.atlassian.confluence.labels.Label;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoftwareBPAnalyticEventUtils {
    public static final String BLUEPRINT_INDEX_PAGE = "blueprint-index-page";
    public static final String JIRA_REPORT_BP_NAME = "jirareports-blueprint";
    public static final String RETROSPECTIVE_BP_NAME = "retrospectives-blueprint";
    public static final String PRODUCT_REQUIREMENT_BP_NAME = "requirements-blueprint";
    public static final String RETROSPECTIVE_LABEL = "retrospective";
    public static final String CHANGE_LOG_REPORT_LABEL = "changelog";
    public static final String STATUS_REPORT_LABEL = "statusreport";
    public static final String REQUIREMENTS_LABEL = "requirements";
    public static final String RETROSPECTIVE_CREATE_EVENT_NAME = "confluence.software.blueprints.retrospective.create";
    public static final String RETROSPECTIVE_UPDATE_PARTICIPANTS_EVENT_NAME = "confluence.software.blueprints.retrospective.participants";
    public static final String REQUIREMENT_CREATE_EVENT_NAME = "confluence.software.blueprints.product.requirement.create";
    public static final String CHANGELOG_CREATE_EVENT_NAME = "confluence.software.blueprints.changelog.create";
    public static final String CHANGELOG_CREATE_SIMPLE_EVENT_NAME = "confluence.software.blueprints.changelog.simple";
    public static final String CHANGELOG_CREATE_STATIC_EVENT_NAME = "confluence.software.blueprints.changelog.static";
    public static final String CHANGELOG_CREATE_DYNAMIC_EVENT_NAME = "confluence.software.blueprints.changelog.dynamic";
    public static final String STATUS_REPORT_CREATE_EVENT_NAME = "confluence.software.blueprints.statusreport.create";
    public static final String STATUS_REPORT_CREATE_SIMPLE_EVENT_NAME = "confluence.software.blueprints.statusreport.simple";
    public static final String STATUS_REPORT_CREATE_DYNAMIC_EVENT_NAME = "confluence.software.blueprints.statusreport.dynamic";
    private static final Map<String, String> viewPageEventNameMap = new HashMap<String, String>();

    public static String getViewIndexPageEventName(Label label) {
        String prefix = label.getNamespace().getPrefix();
        if (prefix.contains(JIRA_REPORT_BP_NAME)) {
            return "confluence.software.blueprints.jirareport.index.view";
        }
        if (prefix.contains(RETROSPECTIVE_BP_NAME)) {
            return "confluence.software.blueprints.retrospective.index.view";
        }
        if (prefix.contains(PRODUCT_REQUIREMENT_BP_NAME)) {
            return "confluence.software.blueprints.product.requirement.index.view";
        }
        return null;
    }

    public static String getAnalyticEventName(List<Label> labels) {
        if (labels != null) {
            for (Label label : labels) {
                String labelName = label.getName();
                if (labelName.equals(BLUEPRINT_INDEX_PAGE)) {
                    return SoftwareBPAnalyticEventUtils.getViewIndexPageEventName(label);
                }
                if (viewPageEventNameMap.get(labelName) == null) continue;
                return viewPageEventNameMap.get(labelName);
            }
        }
        return null;
    }

    static {
        viewPageEventNameMap.put(RETROSPECTIVE_LABEL, "confluence.software.blueprints.retrospective.view");
        viewPageEventNameMap.put(REQUIREMENTS_LABEL, "confluence.software.blueprints.product.requirement.view");
        viewPageEventNameMap.put(CHANGE_LOG_REPORT_LABEL, "confluence.software.blueprints.changelog.view");
        viewPageEventNameMap.put(STATUS_REPORT_LABEL, "confluence.software.blueprints.statusreport.view");
    }
}

