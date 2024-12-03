/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 */
package com.atlassian.confluence.plugins.pulp;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.webresource.api.assembler.PageBuilderService;

public class ServiceImports {
    @ComponentImport
    private DarkFeatureManager darkFeatureManager;
    @ComponentImport
    private PageBuilderService pageBuilderService;
}

