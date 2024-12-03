/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.GeneralUtil
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.user.User
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.user.User;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class RecentlyUsedLabelsMacro
extends BaseMacro {
    private static final String TEMPLATE_NAME = "com/atlassian/confluence/plugins/macros/advanced/recentlyusedlabels";
    private static final String TABULAR_TEMPLATE_NAME = "com/atlassian/confluence/plugins/macros/advanced/recentlyusedlabels-tabular.vm";
    private static final String COMPACT_TEMPLATE_NAME = "com/atlassian/confluence/plugins/macros/advanced/recentlyusedlabels-compact.vm";
    private static final String STYLE = "style";
    private static final String COUNT = "count";
    private static final int DEFAULT_COUNT = 10;
    private static final String SCOPE = "scope";
    private static final String DEFAULT_SCOPE = "global";
    private static final String TITLE = "title";
    private static final int MAX_RESULTS = 100;
    private LabelManager labelManager;
    private UserAccessor userAccessor;
    private FormatSettingsManager formatSettingsManager;
    private LocaleManager localeManager;
    private PermissionManager permissionManager;

    public void setLabelManager(LabelManager manager) {
        this.labelManager = manager;
    }

    public void setUserAccessor(UserAccessor userAccessor) {
        this.userAccessor = userAccessor;
    }

    public void setFormatSettingsManager(FormatSettingsManager formatSettingsManager) {
        this.formatSettingsManager = formatSettingsManager;
    }

    public void setLocaleManager(LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    public void setPermissionManager(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    public String execute(Map parameters, String string, RenderContext renderContext) throws MacroException {
        String style;
        Map<String, Object> contextMap = this.getMacroVelocityContext();
        int maxResults = 10;
        if (parameters.containsKey(COUNT)) {
            maxResults = Integer.parseInt((String)parameters.get(COUNT));
        }
        maxResults = maxResults > 100 ? 100 : maxResults;
        String scope = DEFAULT_SCOPE;
        if (StringUtils.isNotEmpty((CharSequence)((String)parameters.get(SCOPE)))) {
            scope = (String)parameters.get(SCOPE);
        }
        if (StringUtils.isNotEmpty((CharSequence)((String)parameters.get(TITLE)))) {
            String title = (String)parameters.get(TITLE);
            contextMap.put(TITLE, title);
        }
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        String spaceKey = ((PageContext)renderContext).getSpaceKey();
        String template = COMPACT_TEMPLATE_NAME;
        if (StringUtils.isNotEmpty((CharSequence)((String)parameters.get(STYLE))) && "table".equalsIgnoreCase(style = (String)parameters.get(STYLE))) {
            template = TABULAR_TEMPLATE_NAME;
        }
        if (TABULAR_TEMPLATE_NAME.equals(template)) {
            contextMap.put("recentlyUsedLabellings", this.getLabellings(spaceKey, scope, (User)user, maxResults));
        } else {
            contextMap.put("recentlyUsedLabels", this.getLabels(spaceKey, scope, (User)user, maxResults));
        }
        ConfluenceUserPreferences pref = this.userAccessor.getConfluenceUserPreferences((User)user);
        DateFormatter df = pref.getDateFormatter(this.formatSettingsManager, this.localeManager);
        contextMap.put("dateFormatter", df);
        contextMap.put("spaceKey", spaceKey);
        contextMap.put("req", ServletContextThreadLocal.getRequest());
        contextMap.put("generalUtil", GeneralUtil.INSTANCE);
        contextMap.put("webwork", GeneralUtil.INSTANCE);
        return this.renderRecentlyUsedLabels(contextMap, template);
    }

    protected String renderRecentlyUsedLabels(Map<String, Object> contextMap, String template) {
        return VelocityUtils.getRenderedTemplate((String)template, contextMap);
    }

    protected Map<String, Object> getMacroVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }

    private List getLabels(String spaceKey, String scope, User user, int maxResults) {
        List labels = scope.equalsIgnoreCase("space") ? this.labelManager.getRecentlyUsedLabelsInSpace(spaceKey, maxResults) : (scope.equalsIgnoreCase("personal") ? (user != null ? this.labelManager.getRecentlyUsedPersonalLabels(user.getName(), maxResults) : this.labelManager.getRecentlyUsedLabels(maxResults)) : this.labelManager.getRecentlyUsedLabels(maxResults));
        return labels;
    }

    private List getLabellings(String spaceKey, String scope, User user, int maxResults) {
        List labellings = scope.equalsIgnoreCase("space") ? this.labelManager.getRecentlyUsedLabellingsInSpace(spaceKey, maxResults) : (scope.equalsIgnoreCase("personal") ? (user != null ? this.labelManager.getRecentlyUsedPersonalLabellings(user.getName(), maxResults) : this.labelManager.getRecentlyUsedLabellings(maxResults)) : this.labelManager.getRecentlyUsedLabellings(maxResults));
        return this.permissionManager.getPermittedEntities(user, Permission.VIEW, labellings);
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public boolean hasBody() {
        return false;
    }

    public boolean isInline() {
        return false;
    }
}

