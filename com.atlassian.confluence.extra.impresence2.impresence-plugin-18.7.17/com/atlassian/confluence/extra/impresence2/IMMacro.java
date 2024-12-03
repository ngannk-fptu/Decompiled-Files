/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.plugin.services.VelocityHelperService
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.confluence.extra.impresence2;

import com.atlassian.confluence.extra.impresence2.AbstractPresenceMacro;
import com.atlassian.confluence.extra.impresence2.PresenceManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.Map;
import org.apache.commons.lang.StringUtils;

public class IMMacro
extends AbstractPresenceMacro {
    public IMMacro(@ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory, PresenceManager presenceManager, @ComponentImport PermissionManager permissionManager, @ComponentImport VelocityHelperService velocityHelperService) {
        super(localeManager, i18NBeanFactory, presenceManager, permissionManager, velocityHelperService);
    }

    @Override
    protected String getImService(Map<String, String> parameters) {
        return StringUtils.defaultIfEmpty((String)parameters.get("service"), (String)parameters.get("1"));
    }
}

