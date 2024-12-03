/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider
 *  com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.retrospectives;

import com.atlassian.confluence.plugins.SoftwareBlueprintsContextProviderHelper;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.AbstractBlueprintContextProvider;
import com.atlassian.confluence.plugins.createcontent.api.contextproviders.BlueprintContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class RetrospectivesContextProvider
extends AbstractBlueprintContextProvider {
    public static final String TEMPLATE_PROVIDER_PLUGIN_KEY = "com.atlassian.confluence.plugins.confluence-software-blueprints:retrospective-resources";
    private static final String USERS_TEMPLATE = "Confluence.Blueprints.Common.users.soy";
    private static final String PARTICIPANTS_KEY = "userKeys";
    private static final String USER_LIST_TEMPLATE = "Confluence.Blueprints.Common.userList.soy";
    private static final String USER_TABLE_TEMPLATE = "Confluence.Blueprints.Common.userTable.soy";
    private final SoftwareBlueprintsContextProviderHelper helper;

    public RetrospectivesContextProvider(SoftwareBlueprintsContextProviderHelper helper) {
        this.helper = helper;
    }

    protected BlueprintContext updateBlueprintContext(BlueprintContext context) {
        HashMap<String, Object> soyContext = new HashMap<String, Object>();
        String participantParam = (String)context.get("retro-participants");
        if (StringUtils.isNotBlank((CharSequence)participantParam)) {
            soyContext.put(PARTICIPANTS_KEY, participantParam.split(","));
            context.put("participants", (Object)this.helper.renderFromSoy(TEMPLATE_PROVIDER_PLUGIN_KEY, USERS_TEMPLATE, soyContext));
        }
        context.setTitle((String)context.get("retro-title"));
        context.put("currentDateLozenge", (Object)this.helper.createStorageFormatForToday());
        this.addLegacyTemplateVariables(context, soyContext);
        this.doAnalytic(participantParam);
        return context;
    }

    private void addLegacyTemplateVariables(BlueprintContext context, Map<String, Object> soyContext) {
        context.put("currentDate", (Object)this.helper.serverFormatDate(new Date()));
        if (soyContext.containsKey(PARTICIPANTS_KEY)) {
            context.put("participantList", (Object)this.helper.renderFromSoy(TEMPLATE_PROVIDER_PLUGIN_KEY, USER_LIST_TEMPLATE, soyContext));
            context.put("participantTable", (Object)this.helper.renderFromSoy(TEMPLATE_PROVIDER_PLUGIN_KEY, USER_TABLE_TEMPLATE, soyContext));
        }
    }

    private void doAnalytic(String participants) {
        ConfluenceUser authenticatedUser = AuthenticatedUserThreadLocal.get();
        if (authenticatedUser != null && authenticatedUser.getKey().getStringValue().equals(participants)) {
            this.helper.publishAnalyticEvent("confluence.software.blueprints.retrospective.participants");
        }
        this.helper.publishAnalyticEvent("confluence.software.blueprints.retrospective.create");
    }
}

