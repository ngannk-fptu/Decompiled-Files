/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.event.ClearCacheEvent
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.upm.license.internal;

import com.atlassian.jira.event.ClearCacheEvent;
import com.atlassian.upm.license.internal.HostLicenseEventReader;
import java.util.Objects;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class JiraHostLicenseEventReader
implements HostLicenseEventReader,
InitializingBean,
DisposableBean {
    private Class<?> jiraLicenseEventClass = null;

    public void destroy() throws Exception {
        this.jiraLicenseEventClass = null;
    }

    public void afterPropertiesSet() throws Exception {
        try {
            this.jiraLicenseEventClass = Class.forName("com.atlassian.jira.license.LicenseChangedEvent", false, this.getClass().getClassLoader());
        }
        catch (Exception e) {
            try {
                this.jiraLicenseEventClass = Class.forName("com.atlassian.jira.license.NewLicenseEvent", false, this.getClass().getClassLoader());
            }
            catch (Exception e2) {
                this.jiraLicenseEventClass = null;
            }
        }
    }

    @Override
    public boolean isHostLicenseUpdated(Object event) {
        Objects.requireNonNull(event, "event");
        return this.isLicenseEvent(event) || this.isClearCacheEvent(event);
    }

    private boolean isLicenseEvent(Object event) {
        return this.jiraLicenseEventClass != null && this.jiraLicenseEventClass.isAssignableFrom(event.getClass());
    }

    private boolean isClearCacheEvent(Object event) {
        return ClearCacheEvent.class.isAssignableFrom(event.getClass());
    }
}

