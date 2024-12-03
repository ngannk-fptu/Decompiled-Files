/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.xwork;

import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.AdminConfigurationService;
import com.atlassian.sal.api.websudo.WebSudoRequired;

@ReadOnlyAccessAllowed
@WebSudoRequired
public class GlobalSettingsAction
extends ConfluenceActionSupport {
    private AdminConfigurationService adminConfigurationService;

    public GlobalSettingsAction(AdminConfigurationService adminConfigurationService) {
        this.adminConfigurationService = adminConfigurationService;
    }

    public boolean isAllowUserUploadCustomEmojis() {
        return this.adminConfigurationService.isAllowUserUploadCustomEmojis();
    }
}

