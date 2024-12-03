/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.applinks.internal.common.web.condition;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.core.InternalTypeAccessor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationLinkOfTypeCondition
implements Condition {
    private static final Logger log = LoggerFactory.getLogger(ApplicationLinkOfTypeCondition.class);
    private String typeClassName;
    private final InternalTypeAccessor typeAccessor;

    public ApplicationLinkOfTypeCondition(InternalTypeAccessor typeAccessor) {
        this.typeAccessor = typeAccessor;
    }

    public void init(Map<String, String> params) throws PluginParseException {
        this.typeClassName = params.get("type");
        if (this.typeClassName == null) {
            throw new PluginParseException("Must specify a type parameter for " + this.getClass().getSimpleName());
        }
    }

    public boolean shouldDisplay(Map<String, Object> context) {
        ApplicationLink applicationLink = (ApplicationLink)context.get("applicationLink");
        boolean shouldDisplay = true;
        if (applicationLink == null) {
            log.warn("This page has no applicationLink context. Ignoring " + this.getClass().getSimpleName());
        } else {
            ApplicationType type = this.typeAccessor.loadApplicationType(this.typeClassName);
            if (type == null) {
                log.warn("type '" + this.typeClassName + "' specified in " + this.getClass().getSimpleName() + " is not installed, condition evaluates to false.");
                shouldDisplay = false;
            } else {
                shouldDisplay = type.getClass().isAssignableFrom(applicationLink.getType().getClass());
            }
        }
        return shouldDisplay;
    }
}

