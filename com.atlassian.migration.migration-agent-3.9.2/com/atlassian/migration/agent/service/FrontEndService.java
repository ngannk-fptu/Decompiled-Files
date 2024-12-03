/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service;

import java.io.InputStream;
import java.util.Optional;

public interface FrontEndService {
    public String getFullPath(String var1);

    public Optional<InputStream> openResourceStream(String var1);
}

