/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.BootstrapManager
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck;

import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.troubleshooting.api.healthcheck.IndexInfoService;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluenceIndexInfoService
implements IndexInfoService {
    private final BootstrapManager bootstrapManager;

    @Autowired
    public ConfluenceIndexInfoService(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    @Override
    public String getIndexRootPath() {
        return this.bootstrapManager.getFilePathProperty("lucene.index.dir");
    }
}

