/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.maintenance.model;

public class MaintenanceInfo {
    private final boolean bannerMessageEnabled;
    private final String bannerMessage;

    public MaintenanceInfo(boolean bannerMessageEnabled, String bannerMessage) {
        this.bannerMessageEnabled = bannerMessageEnabled;
        this.bannerMessage = bannerMessage;
    }

    public boolean isBannerMessageOn() {
        return this.bannerMessageEnabled;
    }

    public String getBannerMessage() {
        return this.bannerMessage;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MaintenanceInfo that = (MaintenanceInfo)o;
        if (this.bannerMessageEnabled != that.bannerMessageEnabled) {
            return false;
        }
        return this.bannerMessage != null ? this.bannerMessage.equals(that.bannerMessage) : that.bannerMessage == null;
    }

    public int hashCode() {
        int result = this.bannerMessageEnabled ? 1 : 0;
        result = 31 * result + (this.bannerMessage != null ? this.bannerMessage.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "MaintenanceInfo{bannerMessageEnabled=" + this.bannerMessageEnabled + ", bannerMessage='" + this.bannerMessage + "'}";
    }

    public static class Builder {
        private boolean bannerMessageEnabled;
        private String bannerMessage;

        public Builder bannerMessageEnabled(boolean bannerMessageEnabled) {
            this.bannerMessageEnabled = bannerMessageEnabled;
            return this;
        }

        public Builder bannerMessage(String bannerMessage) {
            this.bannerMessage = bannerMessage;
            return this;
        }

        public MaintenanceInfo build() {
            return new MaintenanceInfo(this.bannerMessageEnabled, this.bannerMessage);
        }
    }
}

