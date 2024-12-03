/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.json.jsonorg.JSONException
 *  com.atlassian.json.jsonorg.JSONObject
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.Jsonable$JsonMappingException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableMap
 *  javax.inject.Inject
 *  javax.servlet.http.HttpSession
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.plugins.pulp.wrm;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.pulp.VersionManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.json.jsonorg.JSONException;
import com.atlassian.json.jsonorg.JSONObject;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import java.util.Date;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import org.checkerframework.checker.nullness.qual.NonNull;

public class UpgradeStatusWrmDataProvider
implements WebResourceDataProvider {
    @VisibleForTesting
    static final String UPDATE_KEY = "update";
    private final ConfluenceInfo confluenceInfo;
    private final UserAccessor userAccessor;
    private final FormatSettingsManager formatSettingsManager;
    private final LocaleManager localeManager;
    private final VersionManager versionManager;

    @Inject
    public UpgradeStatusWrmDataProvider(@ComponentImport SystemInformationService systemInformationService, @ComponentImport UserAccessor userAccessor, @ComponentImport FormatSettingsManager formatSettingsManager, @ComponentImport LocaleManager localeManager, VersionManager versionManager) {
        this.confluenceInfo = systemInformationService.getConfluenceInfo();
        this.userAccessor = userAccessor;
        this.formatSettingsManager = formatSettingsManager;
        this.localeManager = localeManager;
        this.versionManager = versionManager;
    }

    public @NonNull Jsonable get() {
        return writer -> {
            try {
                this.getUpgradeStatus().write(writer);
            }
            catch (JSONException e) {
                throw new Jsonable.JsonMappingException((Throwable)e);
            }
        };
    }

    private JSONObject getUpgradeStatus() throws JSONException {
        JSONObject upgradeStatus = new JSONObject();
        upgradeStatus.put(UPDATE_KEY, (Object)this.getUpdateDateJson());
        upgradeStatus.put("version", (Object)this.confluenceInfo.getVersion());
        upgradeStatus.put("isRedirected", this.checkRedirected());
        return upgradeStatus;
    }

    private boolean checkRedirected() {
        HttpSession httpSession = ServletContextThreadLocal.getRequest().getSession();
        Object redirected = httpSession.getAttribute("redirected-to-pulp");
        httpSession.removeAttribute("redirected-to-pulp");
        return Boolean.TRUE.equals(redirected);
    }

    private JSONObject getUpdateDateJson() {
        ConfluenceUserPreferences userPreferences = this.userAccessor.getConfluenceUserPreferences((User)AuthenticatedUserThreadLocal.get());
        DateFormatter dateFormatter = userPreferences.getDateFormatter(this.formatSettingsManager, this.localeManager);
        Date date = this.versionManager.getUpgradeDate().orElse(this.confluenceInfo.getInstallationDate());
        return new JSONObject((Map)ImmutableMap.of((Object)"day", (Object)dateFormatter.format(date), (Object)"time", (Object)dateFormatter.formatTime(date)));
    }
}

