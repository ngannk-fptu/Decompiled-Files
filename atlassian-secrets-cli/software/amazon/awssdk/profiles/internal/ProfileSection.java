/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.profiles.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public enum ProfileSection {
    SSO_SESSION("sso-session", "sso_session");

    private final String sectionTitle;
    private final String propertyKeyName;

    private ProfileSection(String title, String propertyKeyName) {
        this.sectionTitle = title;
        this.propertyKeyName = propertyKeyName;
    }

    public static ProfileSection fromSectionTitle(String sectionTitle) {
        if (sectionTitle == null) {
            return null;
        }
        for (ProfileSection profileSection : ProfileSection.values()) {
            if (!profileSection.sectionTitle.equals(sectionTitle)) continue;
            return profileSection;
        }
        throw new IllegalArgumentException("Unknown enum value for ProfileSection : " + sectionTitle);
    }

    public static ProfileSection fromPropertyKeyName(String propertyName) {
        if (propertyName == null) {
            return null;
        }
        for (ProfileSection section : ProfileSection.values()) {
            if (!section.getPropertyKeyName().equals(propertyName)) continue;
            return section;
        }
        throw new IllegalArgumentException("Unknown enum value for ProfileSection : " + propertyName);
    }

    public String getSectionTitle() {
        return this.sectionTitle;
    }

    public String getPropertyKeyName() {
        return this.propertyKeyName;
    }
}

