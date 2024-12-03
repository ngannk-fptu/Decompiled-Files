/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.base.Preconditions
 */
package com.atlassian.crowd.model.backup;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import java.util.Objects;

public class BackupConfiguration {
    private int backupTimeHour;
    private int backupTimeMinute;
    private Boolean resetDomainEnabled;
    private Boolean backupConnectorEnabled;
    private Boolean scheduledBackupEnabled;

    protected BackupConfiguration(int backupTimeHour, int backupTimeMinute, Boolean resetDomainEnabled, Boolean backupConnectorEnabled, Boolean scheduledBackupEnabled) {
        Preconditions.checkArgument((backupTimeHour >= 0 && backupTimeHour <= 23 ? 1 : 0) != 0, (Object)"Backup hour should be between 0 and 23");
        Preconditions.checkArgument((backupTimeMinute >= 0 && backupTimeMinute <= 59 ? 1 : 0) != 0, (Object)"Backup hour should be between 0 and 59");
        this.backupTimeHour = backupTimeHour;
        this.backupTimeMinute = backupTimeMinute;
        this.resetDomainEnabled = Objects.requireNonNull(resetDomainEnabled);
        this.backupConnectorEnabled = Objects.requireNonNull(backupConnectorEnabled);
        this.scheduledBackupEnabled = Objects.requireNonNull(scheduledBackupEnabled);
    }

    public int getBackupTimeHour() {
        return this.backupTimeHour;
    }

    public void setBackupTimeHour(int backupTimeHour) {
        this.backupTimeHour = backupTimeHour;
    }

    public int getBackupTimeMinute() {
        return this.backupTimeMinute;
    }

    public void setBackupTimeMinute(int backupTimeMinute) {
        this.backupTimeMinute = backupTimeMinute;
    }

    public Boolean isResetDomainEnabled() {
        return this.resetDomainEnabled;
    }

    public void setResetDomainEnabled(Boolean resetDomainEnabled) {
        this.resetDomainEnabled = Objects.requireNonNull(resetDomainEnabled);
    }

    public Boolean isBackupConnectorEnabled() {
        return this.backupConnectorEnabled;
    }

    public void setBackupConnectorEnabled(Boolean backupConnectorEnabled) {
        this.backupConnectorEnabled = Objects.requireNonNull(backupConnectorEnabled);
    }

    public Boolean isScheduledBackupEnabled() {
        return this.scheduledBackupEnabled;
    }

    public void setScheduledBackupEnabled(Boolean scheduledBackupEnabled) {
        this.scheduledBackupEnabled = Objects.requireNonNull(scheduledBackupEnabled);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(BackupConfiguration data) {
        return new Builder(data);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BackupConfiguration that = (BackupConfiguration)o;
        return Objects.equals(this.getBackupTimeHour(), that.getBackupTimeHour()) && Objects.equals(this.getBackupTimeMinute(), that.getBackupTimeMinute()) && Objects.equals(this.isResetDomainEnabled(), that.isResetDomainEnabled()) && Objects.equals(this.isBackupConnectorEnabled(), that.isBackupConnectorEnabled()) && Objects.equals(this.isScheduledBackupEnabled(), that.isScheduledBackupEnabled());
    }

    public int hashCode() {
        return Objects.hash(this.getBackupTimeHour(), this.getBackupTimeMinute(), this.isResetDomainEnabled(), this.isBackupConnectorEnabled(), this.isScheduledBackupEnabled());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("backupTimeHour", this.getBackupTimeHour()).add("backupTimeMinute", this.getBackupTimeMinute()).add("resetDomainEnabled", (Object)this.isResetDomainEnabled()).add("backupConnectorEnabled", (Object)this.isBackupConnectorEnabled()).add("scheduledBackupEnabled", (Object)this.isScheduledBackupEnabled()).toString();
    }

    public static final class Builder {
        private int backupTimeHour;
        private int backupTimeMinute;
        private Boolean resetDomainEnabled;
        private Boolean backupConnectorEnabled;
        private Boolean scheduledBackupEnabled;

        private Builder() {
        }

        private Builder(BackupConfiguration initialData) {
            this.backupTimeHour = initialData.getBackupTimeHour();
            this.backupTimeMinute = initialData.getBackupTimeMinute();
            this.resetDomainEnabled = initialData.isResetDomainEnabled();
            this.backupConnectorEnabled = initialData.isBackupConnectorEnabled();
            this.scheduledBackupEnabled = initialData.isScheduledBackupEnabled();
        }

        public Builder setBackupTimeHour(int backupTimeHour) {
            this.backupTimeHour = backupTimeHour;
            return this;
        }

        public Builder setBackupTimeMinute(int backupTimeMinute) {
            this.backupTimeMinute = backupTimeMinute;
            return this;
        }

        public Builder setResetDomainEnabled(Boolean resetDomainEnabled) {
            this.resetDomainEnabled = resetDomainEnabled;
            return this;
        }

        public Builder setBackupConnectorEnabled(Boolean backupConnectorEnabled) {
            this.backupConnectorEnabled = backupConnectorEnabled;
            return this;
        }

        public Builder setScheduledBackupEnabled(Boolean scheduledBackupEnabled) {
            this.scheduledBackupEnabled = scheduledBackupEnabled;
            return this;
        }

        public BackupConfiguration build() {
            return new BackupConfiguration(this.backupTimeHour, this.backupTimeMinute, this.resetDomainEnabled, this.backupConnectorEnabled, this.scheduledBackupEnabled);
        }
    }
}

