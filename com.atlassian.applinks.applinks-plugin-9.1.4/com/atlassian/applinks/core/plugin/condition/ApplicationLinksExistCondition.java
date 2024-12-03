/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  com.google.common.collect.Iterables
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.core.plugin.condition;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.google.common.collect.Iterables;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLinksExistCondition
implements Condition {
    private static final Logger log = LoggerFactory.getLogger(ApplicationLinksExistCondition.class);
    private final ApplicationLinkService applicationLinkService;
    private final InternalTypeAccessor typeAccessor;
    private String typeClassName;

    public ApplicationLinksExistCondition(ApplicationLinkService applicationLinkService, InternalTypeAccessor typeAccessor) {
        this.applicationLinkService = applicationLinkService;
        this.typeAccessor = typeAccessor;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.typeClassName = params.get("type");
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        Iterable links;
        if (this.typeClassName != null) {
            ApplicationType type = this.typeAccessor.loadApplicationType(this.typeClassName);
            if (type == null) {
                log.warn("type specified for ApplicationLinksExistCondition " + this.typeClassName + " is not installed, condition evaluates to false.");
                return false;
            }
            links = this.applicationLinkService.getApplicationLinks(type.getClass());
        } else {
            links = this.applicationLinkService.getApplicationLinks();
        }
        return !Iterables.isEmpty((Iterable)links);
    }
}

