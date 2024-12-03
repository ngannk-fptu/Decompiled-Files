/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 */
package com.atlassian.cluster.monitoring;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.webresource.api.assembler.PageBuilderService;

public class Wiring {
    @ComponentImport
    private PageBuilderService pageBuilderService;
    @ComponentImport
    private HelpPathResolver helpPathResolver;
    @ComponentImport
    private LocaleResolver localeResolver;
}

