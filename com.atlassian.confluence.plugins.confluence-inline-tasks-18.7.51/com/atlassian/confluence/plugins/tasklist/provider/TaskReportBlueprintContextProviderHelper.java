/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Collections2
 *  javax.annotation.Nonnull
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.tasklist.provider;

import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Collections2;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class TaskReportBlueprintContextProviderHelper {
    private static final String PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-inline-tasks";
    private static final String TASK_REPORT_BP_RESOURCE_KEY = "com.atlassian.confluence.plugins.confluence-inline-tasks:task-report-blueprint-resources";
    private final TemplateRenderer templateRenderer;
    private final UserAccessor userAccessor;

    @Autowired
    public TaskReportBlueprintContextProviderHelper(@ComponentImport TemplateRenderer templateRenderer, @ComponentImport UserAccessor userAccessor) {
        this.templateRenderer = templateRenderer;
        this.userAccessor = userAccessor;
    }

    String renderFromSoy(String soyTemplate, Map<String, Object> soyContext) {
        StringBuilder output = new StringBuilder();
        this.templateRenderer.renderTo((Appendable)output, TASK_REPORT_BP_RESOURCE_KEY, soyTemplate, soyContext);
        return output.toString();
    }

    Collection<String> prepareUserKeys(@Nonnull String usernames) {
        List<String> usernamesList = Arrays.asList(usernames.split(","));
        return Collections2.transform(usernamesList, this::getUserKey);
    }

    public String getUserKey(String username) {
        ConfluenceUser user = this.userAccessor.getUserByName(username);
        return user.getKey().getStringValue();
    }
}

