/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.sso.ApplicationSamlConfiguration
 */
package com.atlassian.crowd.event.application;

import com.atlassian.crowd.model.sso.ApplicationSamlConfiguration;
import java.util.Objects;

public class ApplicationSamlConfigurationUpdatedEvent {
    protected final ApplicationSamlConfiguration oldApplicationSamlConfiguration;
    protected final ApplicationSamlConfiguration newApplicationSamlConfiguration;

    public ApplicationSamlConfigurationUpdatedEvent(ApplicationSamlConfiguration oldApplicationSamlConfiguration, ApplicationSamlConfiguration newApplicationSamlConfiguration) {
        this.oldApplicationSamlConfiguration = oldApplicationSamlConfiguration;
        this.newApplicationSamlConfiguration = newApplicationSamlConfiguration;
    }

    public ApplicationSamlConfiguration getOldApplicationSamlConfiguration() {
        return this.oldApplicationSamlConfiguration;
    }

    public ApplicationSamlConfiguration getNewApplicationSamlConfiguration() {
        return this.newApplicationSamlConfiguration;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ApplicationSamlConfigurationUpdatedEvent that = (ApplicationSamlConfigurationUpdatedEvent)o;
        return Objects.equals(this.oldApplicationSamlConfiguration, that.oldApplicationSamlConfiguration) && Objects.equals(this.newApplicationSamlConfiguration, that.newApplicationSamlConfiguration);
    }

    public int hashCode() {
        return Objects.hash(this.oldApplicationSamlConfiguration, this.newApplicationSamlConfiguration);
    }
}

