/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.favicon.core.FaviconManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.xwork.XsrfTokenGenerator
 */
package com.atlassian.confluence.plugins.lookandfeel;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.favicon.core.FaviconManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.xwork.XsrfTokenGenerator;

public class ActionImports {
    public ActionImports(@ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport XsrfTokenGenerator tokenGenerator, @ComponentImport I18nResolver i18nResolver, @ComponentImport FaviconManager faviconManager, @ComponentImport EventPublisher eventPublisher) {
    }
}

