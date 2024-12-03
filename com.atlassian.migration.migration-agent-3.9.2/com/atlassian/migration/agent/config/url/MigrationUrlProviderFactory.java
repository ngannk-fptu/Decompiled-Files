/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.config.url;

import com.atlassian.migration.agent.config.url.MigrationEnvironment;
import com.atlassian.migration.agent.config.url.MigrationUrlProvider;

public class MigrationUrlProviderFactory {
    private final MigrationUrlProvider defaultUrlProvider = new MigrationUrlProvider(MigrationEnvironment.DEFAULT);
    private final MigrationUrlProvider fedRAMPUrlProvider = new MigrationUrlProvider(MigrationEnvironment.FEDRAMP);

    public MigrationUrlProvider createUrlProvider(MigrationEnvironment environment) {
        if (environment.equals((Object)MigrationEnvironment.FEDRAMP)) {
            return this.fedRAMPUrlProvider;
        }
        return this.defaultUrlProvider;
    }
}

