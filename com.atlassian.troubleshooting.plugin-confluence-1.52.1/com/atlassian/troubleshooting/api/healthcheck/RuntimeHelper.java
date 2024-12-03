/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.api.healthcheck;

import java.util.Optional;

public interface RuntimeHelper {
    public Optional<Process> spawnProcessSafely(String ... var1);
}

