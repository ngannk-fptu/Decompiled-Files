/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 */
package com.atlassian.confluence.extra.impresence2;

import com.atlassian.confluence.extra.impresence2.IMMacro;
import com.atlassian.confluence.extra.impresence2.PresenceManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import java.util.Map;

public class OldAimMacro
extends IMMacro {
    public OldAimMacro(LocaleManager localeManager, I18NBeanFactory i18NBeanFactory, PresenceManager presenceManager, PermissionManager permissionManager, VelocityHelperService velocityHelperService) {
        super(localeManager, i18NBeanFactory, presenceManager, permissionManager, velocityHelperService);
    }

    @Override
    protected String getImService(Map<String, String> parameters) {
        return "aim";
    }
}

