/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.mail;

import com.atlassian.upm.mail.UpmEmail;

public enum EmailType {
    ADDON_REQUESTED("upm.addon.requested.subject", "addon-requested.vm"),
    ADDON_REQUEST_UPDATED("upm.addon.request.updated.subject", "addon-request-updated.vm"),
    ADDON_REQUEST_FULFILLED("upm.addon.request.fulfilled.subject", "addon-request-fulfilled.vm"),
    ADDON_REQUEST_DISMISSED("upm.addon.request.dismissed.subject", "addon-request-dismissed.vm"),
    ADDON_UPDATE_FREE_TO_PAID("upm.addon.manual.update.required.subject", "addon-update-free-to-paid.vm");

    private final String i18nSubject;
    private final String bodyTemplateName;

    private EmailType(String i18nSubject, String bodyTemplateName) {
        this.i18nSubject = i18nSubject;
        this.bodyTemplateName = bodyTemplateName;
    }

    public String getI18nSubject() {
        return this.i18nSubject;
    }

    public String getBodyTemplate(UpmEmail.Format format) {
        switch (format) {
            case HTML: {
                return this.getBodyTemplatePath("html");
            }
            case TEXT: {
                return this.getBodyTemplatePath("text");
            }
        }
        throw new IllegalArgumentException("Invalid format type");
    }

    private String getBodyTemplatePath(String format) {
        return "templates/mail/" + format + "/" + this.bodyTemplateName;
    }
}

