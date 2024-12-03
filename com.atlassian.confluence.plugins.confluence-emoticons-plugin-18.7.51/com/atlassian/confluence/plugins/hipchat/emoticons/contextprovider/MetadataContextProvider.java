/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.contextprovider;

import com.atlassian.confluence.plugins.hipchat.emoticons.service.AdminConfigurationService;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import java.util.HashMap;
import java.util.Map;

public class MetadataContextProvider
implements ContextProvider {
    private final AdminConfigurationService adminConfigurationService;
    private final UserManager userManager;

    public MetadataContextProvider(AdminConfigurationService adminConfigurationService, UserManager userManager) {
        this.adminConfigurationService = adminConfigurationService;
        this.userManager = userManager;
    }

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> contextMap) {
        boolean isUserAllowUploadEmojis = this.adminConfigurationService.isAllowUserUploadCustomEmojis();
        UserKey currentUserKey = this.userManager.getRemoteUserKey();
        boolean isCurrentUserAdmin = this.userManager.isAdmin(currentUserKey);
        boolean isCurrentUserAllowUploadEmojis = isUserAllowUploadEmojis || isCurrentUserAdmin;
        HashMap<String, String> emojisMeta = new HashMap<String, String>();
        emojisMeta.put("allow-current-user-upload-emojis", String.valueOf(isCurrentUserAllowUploadEmojis));
        emojisMeta.put("max-upload-file-size", String.valueOf(AdminConfigurationService.getMaxFileSizeMb()));
        contextMap.put("emojisMeta", emojisMeta);
        return contextMap;
    }
}

