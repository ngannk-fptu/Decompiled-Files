/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.impresence2.reporter;

import com.atlassian.confluence.extra.impresence2.reporter.PresenceException;
import java.io.IOException;

public interface PresenceReporter {
    public String getKey();

    public String getName();

    public String getServiceHomepage();

    public boolean hasConfig();

    public boolean requiresConfig();

    public String getPresenceXHTML(String var1, boolean var2) throws IOException, PresenceException;
}

