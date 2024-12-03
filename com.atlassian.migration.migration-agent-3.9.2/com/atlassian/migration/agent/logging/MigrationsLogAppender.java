/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  org.apache.log4j.Appender
 *  org.apache.log4j.Layout
 *  org.apache.log4j.Level
 *  org.apache.log4j.Logger
 *  org.apache.log4j.RollingFileAppender
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.logging;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.migration.agent.annotation.ConditionalOnClass;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.RollingFileAppender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ConditionalOnClass(value={"com.atlassian.confluence.util.PatternLayoutWithContext", "org.apache.log4j.RollingFileAppender"})
public class MigrationsLogAppender {
    private static final Logger log = LoggerFactory.getLogger(MigrationsLogAppender.class);

    public MigrationsLogAppender(ApplicationConfiguration applicationConfiguration) {
        this.initAppender(applicationConfiguration);
    }

    private void initAppender(ApplicationConfiguration applicationConfiguration) {
        try {
            org.apache.log4j.Logger migrationLogger = org.apache.log4j.Logger.getLogger((String)"com.atlassian.migration.agent");
            if (migrationLogger.getAppender("migrationslog") == null) {
                RollingFileAppender migrationsLogAppender = new RollingFileAppender();
                migrationsLogAppender.setName("migrationslog");
                Path path = Paths.get(applicationConfiguration.getApplicationHome(), "logs", "atlassian-confluence-migrations.log");
                migrationsLogAppender.setFile(path.toString());
                migrationsLogAppender.setMaxFileSize("20480KB");
                migrationsLogAppender.setMaxBackupIndex(10);
                Class<?> patternLayoutWithContextClass = Class.forName("com.atlassian.confluence.util.PatternLayoutWithContext");
                Method setConversionPattern = patternLayoutWithContextClass.getMethod("setConversionPattern", String.class);
                Object patternLayoutWithContext = patternLayoutWithContextClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                setConversionPattern.invoke(patternLayoutWithContext, "%d %p [%t] [%c{4}] %M %m%n");
                migrationsLogAppender.setLayout((Layout)patternLayoutWithContext);
                migrationsLogAppender.activateOptions();
                migrationLogger.addAppender((Appender)migrationsLogAppender);
                migrationLogger.setLevel(Level.INFO);
                migrationLogger.setAdditivity(true);
            }
        }
        catch (Exception exception) {
            log.warn("An unhandled exception occurred when creating migrations log appender.", (Throwable)exception);
        }
    }
}

