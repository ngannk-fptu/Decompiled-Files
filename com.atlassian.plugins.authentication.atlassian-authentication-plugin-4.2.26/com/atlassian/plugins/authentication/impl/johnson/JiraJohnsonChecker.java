/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.component.ComponentAccessor
 *  com.atlassian.jira.config.properties.JiraSystemProperties
 *  com.atlassian.jira.startup.mode.StartupModeReference
 *  com.atlassian.jira.util.johnson.JohnsonEventPredicates
 *  com.atlassian.johnson.Johnson
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.johnson.event.EventPredicates
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  com.google.common.annotations.VisibleForTesting
 *  javax.servlet.ServletContext
 */
package com.atlassian.plugins.authentication.impl.johnson;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.JiraSystemProperties;
import com.atlassian.jira.startup.mode.StartupModeReference;
import com.atlassian.jira.util.johnson.JohnsonEventPredicates;
import com.atlassian.johnson.Johnson;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.event.EventPredicates;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugins.authentication.impl.johnson.JohnsonChecker;
import com.google.common.annotations.VisibleForTesting;
import java.util.Optional;
import java.util.function.Supplier;
import javax.servlet.ServletContext;

@JiraComponent
public class JiraJohnsonChecker
implements JohnsonChecker {
    private final Supplier<Optional<StartupModeReference>> startupModeReferenceSupplier;

    public JiraJohnsonChecker() {
        this.startupModeReferenceSupplier = ComponentAccessor.safeSupplierOf(StartupModeReference.class);
    }

    @VisibleForTesting
    JiraJohnsonChecker(Supplier<Optional<StartupModeReference>> startupModeReferenceSupplier) {
        this.startupModeReferenceSupplier = startupModeReferenceSupplier;
    }

    @Override
    public boolean isInstanceJohnsoned(ServletContext servletContext) {
        return Johnson.isInitialized() && this.johnsonHasDisplayableEvents(Johnson.getEventContainer((ServletContext)servletContext));
    }

    private boolean johnsonHasDisplayableEvents(JohnsonEventContainer container) {
        if (container.hasEvent(JohnsonEventPredicates.blocksStartup())) {
            return true;
        }
        return this.isUpgrading() && this.isIgnoreDismissiblesEnabled() == false && container.hasEvent(EventPredicates.attributeEquals((String)"dismissible", (Object)true));
    }

    private boolean isUpgrading() {
        return this.startupModeReferenceSupplier.get().map(StartupModeReference::isUpgrading).orElse(false);
    }

    private Boolean isIgnoreDismissiblesEnabled() {
        return JiraSystemProperties.getInstance().getBoolean("jira.startup.warnings.disable");
    }
}

