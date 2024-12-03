/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.impl.service.audit;

import com.atlassian.confluence.audit.StandardAuditResourceTypes;
import com.atlassian.confluence.impl.audit.AuditHelper;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import java.util.Locale;

public class EnglishAuditResourceTypes
implements StandardAuditResourceTypes {
    private static final String PREFIX = AuditHelper.buildTextKey("affected.object.");
    private final I18NBeanFactory i18NBeanFactory;

    public EnglishAuditResourceTypes(I18NBeanFactory i18NBeanFactory) {
        this.i18NBeanFactory = i18NBeanFactory;
    }

    @Override
    public String space() {
        return this.translateToEnglish(PREFIX + "space");
    }

    @Override
    public String user() {
        return this.translateToEnglish(PREFIX + "user");
    }

    @Override
    public String group() {
        return this.translateToEnglish(PREFIX + "group");
    }

    @Override
    public String directory() {
        return this.translateToEnglish(PREFIX + "directory");
    }

    @Override
    public String mailServer() {
        return this.translateToEnglish(PREFIX + "server");
    }

    @Override
    public String cache() {
        return this.translateToEnglish(PREFIX + "cache");
    }

    @Override
    public String license() {
        return this.translateToEnglish(PREFIX + "license");
    }

    @Override
    public String plugin() {
        return this.translateToEnglish(PREFIX + "plugin");
    }

    @Override
    public String pluginModule() {
        return this.translateToEnglish(PREFIX + "plugin.module");
    }

    @Override
    public String pageTemplate() {
        return this.translateToEnglish(PREFIX + "page.template");
    }

    @Override
    public String page() {
        return this.translateToEnglish(PREFIX + "page");
    }

    @Override
    public String blog() {
        return this.translateToEnglish(PREFIX + "blog");
    }

    @Override
    public String comment() {
        return this.translateToEnglish(PREFIX + "comment");
    }

    @Override
    public String scheduledJob() {
        return this.translateToEnglish(PREFIX + "scheduled.job");
    }

    @Override
    public String securityConfig() {
        return this.translateToEnglish(PREFIX + "security.config");
    }

    @Override
    public String attachment() {
        return this.translateToEnglish(PREFIX + "attachment");
    }

    private String translateToEnglish(String key) {
        return this.i18NBeanFactory.getI18NBean(Locale.ENGLISH).getText(key);
    }
}

