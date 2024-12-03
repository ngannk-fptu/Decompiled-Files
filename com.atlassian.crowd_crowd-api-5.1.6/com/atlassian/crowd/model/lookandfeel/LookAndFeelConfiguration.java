/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nullable
 */
package com.atlassian.crowd.model.lookandfeel;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import javax.annotation.Nullable;

public class LookAndFeelConfiguration {
    private final boolean showLogo;
    private final String customLogoId;
    private final String header;
    private final String welcomeText;
    private final String primaryColor;
    private final boolean showAnnouncement;
    @Nullable
    private final String announcementText;

    public LookAndFeelConfiguration() {
        this.showLogo = false;
        this.customLogoId = null;
        this.header = null;
        this.welcomeText = null;
        this.primaryColor = null;
        this.showAnnouncement = false;
        this.announcementText = null;
    }

    private LookAndFeelConfiguration(Builder builder) {
        this.showLogo = builder.showLogo;
        this.customLogoId = builder.customLogoId;
        this.header = builder.header;
        this.welcomeText = builder.welcomeText;
        this.primaryColor = builder.primaryColor;
        this.showAnnouncement = builder.showAnnouncement;
        this.announcementText = builder.announcementText;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(LookAndFeelConfiguration lookAndFeelConfiguration) {
        return new Builder(lookAndFeelConfiguration);
    }

    public boolean isShowLogo() {
        return this.showLogo;
    }

    public String getCustomLogoId() {
        return this.customLogoId;
    }

    public String getHeader() {
        return this.header;
    }

    public String getWelcomeText() {
        return this.welcomeText;
    }

    public String getPrimaryColor() {
        return this.primaryColor;
    }

    public boolean isShowAnnouncement() {
        return this.showAnnouncement;
    }

    public String getAnnouncementText() {
        return this.announcementText;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LookAndFeelConfiguration that = (LookAndFeelConfiguration)o;
        return Objects.equals(this.showLogo, that.showLogo) && Objects.equals(this.customLogoId, that.customLogoId) && Objects.equals(this.header, that.header) && Objects.equals(this.welcomeText, that.welcomeText) && Objects.equals(this.primaryColor, that.primaryColor) && Objects.equals(this.showAnnouncement, that.showAnnouncement) && Objects.equals(this.announcementText, that.announcementText);
    }

    public int hashCode() {
        return Objects.hash(this.showLogo, this.customLogoId, this.header, this.welcomeText, this.primaryColor, this.showAnnouncement, this.announcementText);
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("showLogo", this.showLogo).add("customLogoId", (Object)this.customLogoId).add("header", (Object)this.header).add("welcomeText", (Object)this.welcomeText).add("primaryColor", (Object)this.primaryColor).add("showAnnouncement", this.showAnnouncement).add("announcementText", (Object)this.announcementText).toString();
    }

    public static final class Builder {
        private boolean showLogo;
        private String customLogoId;
        private String header;
        private String welcomeText;
        private String primaryColor;
        private boolean showAnnouncement;
        private String announcementText;

        private Builder() {
        }

        private Builder(LookAndFeelConfiguration lookAndFeelConfiguration) {
            this.showLogo = lookAndFeelConfiguration.isShowLogo();
            this.customLogoId = lookAndFeelConfiguration.getCustomLogoId();
            this.header = lookAndFeelConfiguration.getHeader();
            this.welcomeText = lookAndFeelConfiguration.getWelcomeText();
            this.primaryColor = lookAndFeelConfiguration.getPrimaryColor();
            this.showAnnouncement = lookAndFeelConfiguration.isShowAnnouncement();
            this.announcementText = lookAndFeelConfiguration.getAnnouncementText();
        }

        public Builder setShowLogo(boolean showLogo) {
            this.showLogo = showLogo;
            return this;
        }

        public Builder setCustomLogoId(String customLogoId) {
            this.customLogoId = customLogoId;
            return this;
        }

        public Builder setHeader(String header) {
            this.header = header;
            return this;
        }

        public Builder setWelcomeText(String welcomeText) {
            this.welcomeText = welcomeText;
            return this;
        }

        public Builder setPrimaryColor(String primaryColor) {
            this.primaryColor = primaryColor;
            return this;
        }

        public Builder setShowAnnouncement(boolean showAnnouncement) {
            this.showAnnouncement = showAnnouncement;
            return this;
        }

        public Builder setAnnouncementText(String announcementText) {
            this.announcementText = announcementText;
            return this;
        }

        public LookAndFeelConfiguration build() {
            return new LookAndFeelConfiguration(this);
        }
    }
}

