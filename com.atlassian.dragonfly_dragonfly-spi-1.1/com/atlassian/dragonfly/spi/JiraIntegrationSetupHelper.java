/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.ApplicationType
 */
package com.atlassian.dragonfly.spi;

import com.atlassian.crowd.model.application.ApplicationType;
import java.net.URI;

public interface JiraIntegrationSetupHelper {
    public ApplicationType getApplicationType();

    public void switchToCrowdAuthentication(URI var1, String var2, String var3);

    public void switchToDefaultAuthentication();
}

