/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.action.impl;

import com.atlassian.troubleshooting.stp.ValidationLog;
import com.atlassian.troubleshooting.stp.action.AbstractSupportToolsAction;
import com.atlassian.troubleshooting.stp.action.SupportToolsAction;
import com.atlassian.troubleshooting.stp.salext.ApplicationType;
import com.atlassian.troubleshooting.stp.salext.SupportApplicationInfo;
import com.atlassian.troubleshooting.stp.servlet.SafeHttpServletRequest;
import com.atlassian.troubleshooting.stp.spi.SupportDataDetail;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public class DetectIssuesAction
extends AbstractSupportToolsAction {
    public static final String ACTION_NAME = "atst-detect-issues";
    private static final List<ApplicationType> SUPPORTED_APPS = ImmutableList.of((Object)((Object)ApplicationType.CONFLUENCE), (Object)((Object)ApplicationType.JIRA), (Object)((Object)ApplicationType.BAMBOO));
    private final SupportApplicationInfo info;

    DetectIssuesAction(SupportApplicationInfo info) {
        super(ACTION_NAME, "stp.instance.health.title", "stp.instance.health.title", "templates/html/detect-issues.vm");
        this.info = info;
    }

    static boolean isAvailable(ApplicationType applicationType) {
        return SUPPORTED_APPS.contains((Object)applicationType);
    }

    @Override
    @Nonnull
    public SupportToolsAction newInstance() {
        return new DetectIssuesAction(this.info);
    }

    @Override
    public void prepare(Map<String, Object> context, SafeHttpServletRequest request, ValidationLog validationLog) {
        context.put("info", this.info);
        context.put("props", this.info.loadProperties(SupportDataDetail.BASIC));
    }
}

