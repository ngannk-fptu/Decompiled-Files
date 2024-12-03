/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.retention.RetentionFeatureChecker
 *  com.atlassian.confluence.pages.TrashManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.retentionrules;

import com.atlassian.confluence.api.service.retention.RetentionFeatureChecker;
import com.atlassian.confluence.pages.TrashManager;
import com.atlassian.confluence.plugins.retentionrules.FrontendServlet;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;

public class GlobalRetentionRulesFrontendServlet
extends FrontendServlet {
    @Autowired
    public GlobalRetentionRulesFrontendServlet(@ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport RetentionFeatureChecker retentionFeatureChecker, @ComponentImport UserManager userManager, @ComponentImport LoginUriProvider loginUriProvider, @ComponentImport WebSudoManager webSudoManager, @ComponentImport TrashManager trashManager) {
        super(soyTemplateRenderer, retentionFeatureChecker, userManager, loginUriProvider, webSudoManager, trashManager);
    }
}

