/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.user.UserManager
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.restapi;

import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.user.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AdditionalImports {
    private final DarkFeatureManager darkFeatureManager;

    @Autowired
    public AdditionalImports(@ComponentImport @Qualifier(value="darkFeatureManager") DarkFeatureManager darkFeatureManager, @ComponentImport UserManager userManager) {
        this.darkFeatureManager = darkFeatureManager;
    }
}

