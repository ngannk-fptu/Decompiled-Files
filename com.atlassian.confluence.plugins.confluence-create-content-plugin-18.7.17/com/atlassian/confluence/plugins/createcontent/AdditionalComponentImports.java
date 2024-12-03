/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.createcontent;

import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.stereotype.Component;

@Component
public class AdditionalComponentImports {
    @ComponentImport
    private DarkFeaturesManager darkFeaturesManager;
}

