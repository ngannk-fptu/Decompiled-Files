/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserManager
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.synchrony;

import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ComponentImports {
    private final UserManager userManager;
    private final I18NBeanFactory i18NBeanFactory;

    @Autowired
    public ComponentImports(@ComponentImport UserManager userManager, @ComponentImport I18NBeanFactory i18NBeanFactory) {
        this.userManager = userManager;
        this.i18NBeanFactory = i18NBeanFactory;
    }
}

