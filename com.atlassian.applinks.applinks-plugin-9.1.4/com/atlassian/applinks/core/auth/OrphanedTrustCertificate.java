/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 */
package com.atlassian.applinks.core.auth;

import com.atlassian.velocity.htmlsafe.HtmlSafe;

public class OrphanedTrustCertificate {
    private final String id;
    private final String description;
    private final Type type;

    public OrphanedTrustCertificate(String id, String description, Type type) {
        this.id = id;
        this.description = description;
        this.type = type;
    }

    @HtmlSafe
    public String getId() {
        return this.id;
    }

    @HtmlSafe
    public String getDescription() {
        return this.description;
    }

    public Type getType() {
        return this.type;
    }

    public static enum Type {
        TRUSTED_APPS("applinks.orphaned.trust.trusted.apps"),
        OAUTH_SERVICE_PROVIDER("applinks.orphaned.trust.oauth.service.provider"),
        OAUTH("applinks.orphaned.trust.oauth");

        private final String i18nKey;

        private Type(String i18nKey) {
            this.i18nKey = i18nKey;
        }

        public String getI18nKey() {
            return this.i18nKey;
        }
    }
}

