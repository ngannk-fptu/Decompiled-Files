/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.util.concurrent.LazyReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.office.canary.aspose.words;

import com.atlassian.confluence.extra.office.canary.AbstractCanaryExecutor;
import com.atlassian.confluence.extra.office.canary.CanaryCage;
import com.atlassian.confluence.extra.office.canary.CanaryCageFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.util.concurrent.LazyReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WordImportCanaryExecutor
extends AbstractCanaryExecutor {
    private static final Logger log = LoggerFactory.getLogger(WordImportCanaryExecutor.class);

    @Autowired
    public WordImportCanaryExecutor(@ComponentImport DarkFeatureManager darkFeatureManager, @ComponentImport ApplicationProperties applicationProperties) {
        super(darkFeatureManager, applicationProperties);
    }

    @Override
    protected LazyReference<CanaryCage> getCanaryCageRef() {
        return new LazyReference<CanaryCage>(){

            protected CanaryCage create() throws Exception {
                try {
                    return CanaryCageFactory.newCanaryCage(WordImportCanaryExecutor.this.canaryCageDirectory(), "OfficeConnector-canary.jar", "com.atlassian.confluence.extra.office.canary.aspose.words.WordImportTestRunner");
                }
                catch (Exception e) {
                    log.error("Failed to initialise word import canary", (Throwable)e);
                    throw e;
                }
            }
        };
    }
}

