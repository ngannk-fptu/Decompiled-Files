/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.service.WelcomeMessageService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.renderer.v2.macro.MacroException
 */
package com.atlassian.confluence.plugins.macros.dashboard;

import com.atlassian.confluence.content.service.WelcomeMessageService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.renderer.v2.macro.MacroException;
import java.util.Map;

public class WelcomeMessageMacro
extends BaseMacro {
    public static final String MACRO_NAME = "welcome-message";
    @ComponentImport
    private WelcomeMessageService welcomeMessageService;

    public String execute(Map parameters, String body, RenderContext renderContext) throws MacroException {
        return this.welcomeMessageService.getWelcomeMessage();
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public String getName() {
        return MACRO_NAME;
    }

    public void setWelcomeMessageService(WelcomeMessageService welcomeMessageService) {
        this.welcomeMessageService = welcomeMessageService;
    }
}

