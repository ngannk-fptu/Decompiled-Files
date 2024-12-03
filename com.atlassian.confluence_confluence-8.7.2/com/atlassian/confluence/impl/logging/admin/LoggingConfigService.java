/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.logging.admin;

import com.atlassian.confluence.impl.logging.admin.LoggingConfigEntry;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;

public interface LoggingConfigService {
    public List<LoggingConfigEntry> getLoggerConfig();

    public void setLevelForLogger(String var1, String var2);

    public void resetLoggerLevel(String var1);

    public void turnOffHibernateLogging();

    public void turnOnHibernateLogging();

    public boolean isHibernateLoggingEnabled();

    public boolean isDiagnosticEnabled();

    public void reconfigure(InputStream var1) throws IOException;

    public void rateLimit(String var1, Duration var2, int var3);
}

