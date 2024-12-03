/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mail;

public interface Settings {
    public static final String ATLASSIAN_MAIL_SEND_DISABLED_SYSTEM_PROPERTY_KEY = "atlassian.mail.senddisabled";

    public boolean isSendingDisabled();

    public static class Default
    implements Settings {
        @Override
        public boolean isSendingDisabled() {
            return Boolean.getBoolean(Settings.ATLASSIAN_MAIL_SEND_DISABLED_SYSTEM_PROPERTY_KEY);
        }
    }
}

