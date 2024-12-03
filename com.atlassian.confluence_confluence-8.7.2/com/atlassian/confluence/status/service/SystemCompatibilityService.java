/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.status.service;

import java.util.Collection;

public interface SystemCompatibilityService {
    public Collection<String> getSupportedJavaVersions();

    public Collection<String> getSupportedJavaRuntimes();

    @Deprecated
    public String getSupportedJavaRuntime();

    public Collection<String> getSupportedOperatingSystems();

    public Collection<String> getSupportedDatabases();

    public Collection<String> getSupportedTomcatVersions();
}

