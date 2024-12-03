/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.lookandfeel.LookAndFeelConfiguration
 */
package com.atlassian.crowd.event.configuration;

import com.atlassian.crowd.model.lookandfeel.LookAndFeelConfiguration;
import java.util.Objects;

public class LookAndFeelUpdatedEvent {
    private final LookAndFeelConfiguration oldConfiguration;
    private final LookAndFeelConfiguration newConfiguration;

    public LookAndFeelUpdatedEvent(LookAndFeelConfiguration oldConfiguration, LookAndFeelConfiguration newConfiguration) {
        this.oldConfiguration = oldConfiguration;
        this.newConfiguration = newConfiguration;
    }

    public LookAndFeelConfiguration getOldConfiguration() {
        return this.oldConfiguration;
    }

    public LookAndFeelConfiguration getNewConfiguration() {
        return this.newConfiguration;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LookAndFeelUpdatedEvent that = (LookAndFeelUpdatedEvent)o;
        return Objects.equals(this.oldConfiguration, that.oldConfiguration) && Objects.equals(this.newConfiguration, that.newConfiguration);
    }

    public int hashCode() {
        return Objects.hash(this.oldConfiguration, this.newConfiguration);
    }
}

