/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dragonfly.api;

import java.net.URI;

public interface JiraAccessUtil {
    public boolean checkTargetIsSupportedJira(URI var1);

    public boolean checkAdminCredential(URI var1, String var2, String var3);
}

