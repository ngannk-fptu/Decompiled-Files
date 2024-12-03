/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 */
package com.atlassian.confluence.impl.logging.log4j.appender;

import com.atlassian.confluence.impl.logging.log4j.appender.Log4j2ConfluenceHomeLogAppenderBuilder;
import java.util.Properties;
import org.apache.logging.log4j.core.config.plugins.Plugin;

public final class Log4j2LegacyConfluenceHomeLogAppenderBuilders {
    private static Properties withLogFileName(Properties properties, String prefix, String logFileName) {
        properties.setProperty(prefix + ".LogFileName", logFileName);
        return properties;
    }

    private Log4j2LegacyConfluenceHomeLogAppenderBuilders() {
    }

    @Plugin(name="com.atlassian.confluence.logging.SynchronyLogAppender", category="Log4j Builder")
    public static class SynchronyLogAppenderBuilder
    extends Log4j2ConfluenceHomeLogAppenderBuilder {
        public SynchronyLogAppenderBuilder(String prefix, Properties props) {
            super(prefix, Log4j2LegacyConfluenceHomeLogAppenderBuilders.withLogFileName(props, prefix, "atlassian-synchrony.log"));
        }
    }

    @Plugin(name="com.atlassian.confluence.logging.SqlLogAppender", category="Log4j Builder")
    public static class SqlLogAppenderBuilder
    extends Log4j2ConfluenceHomeLogAppenderBuilder {
        public SqlLogAppenderBuilder(String prefix, Properties props) {
            super(prefix, Log4j2LegacyConfluenceHomeLogAppenderBuilders.withLogFileName(props, prefix, "atlassian-confluence-sql.log"));
        }
    }

    @Plugin(name="com.atlassian.confluence.logging.SecurityLogAppender", category="Log4j Builder")
    public static class SecurityLogAppenderBuilder
    extends Log4j2ConfluenceHomeLogAppenderBuilder {
        public SecurityLogAppenderBuilder(String prefix, Properties props) {
            super(prefix, Log4j2LegacyConfluenceHomeLogAppenderBuilders.withLogFileName(props, prefix, "atlassian-confluence-security.log"));
        }
    }

    @Plugin(name="com.atlassian.confluence.logging.OutgoingMailLogAppender", category="Log4j Builder")
    public static class OutgoingMailLogAppenderBuilder
    extends Log4j2ConfluenceHomeLogAppenderBuilder {
        public OutgoingMailLogAppenderBuilder(String prefix, Properties props) {
            super(prefix, Log4j2LegacyConfluenceHomeLogAppenderBuilders.withLogFileName(props, prefix, "atlassian-confluence-outgoing-mail.log"));
        }
    }

    @Plugin(name="com.atlassian.confluence.logging.IpdLogAppender", category="Log4j Builder")
    public static class IpdLogAppenderBuilder
    extends Log4j2ConfluenceHomeLogAppenderBuilder {
        public IpdLogAppenderBuilder(String prefix, Properties props) {
            super(prefix, Log4j2LegacyConfluenceHomeLogAppenderBuilders.withLogFileName(props, prefix, "atlassian-confluence-ipd-monitoring.log"));
        }
    }

    @Plugin(name="com.atlassian.confluence.logging.JmxLogAppender", category="Log4j Builder")
    public static class JmxLogAppenderBuilder
    extends Log4j2ConfluenceHomeLogAppenderBuilder {
        public JmxLogAppenderBuilder(String prefix, Properties props) {
            super(prefix, Log4j2LegacyConfluenceHomeLogAppenderBuilders.withLogFileName(props, prefix, "atlassian-confluence-jmx.log"));
        }
    }

    @Plugin(name="com.atlassian.confluence.logging.IndexLogAppender", category="Log4j Builder")
    public static class IndexLogAppenderBuilder
    extends Log4j2ConfluenceHomeLogAppenderBuilder {
        public IndexLogAppenderBuilder(String prefix, Properties props) {
            super(prefix, Log4j2LegacyConfluenceHomeLogAppenderBuilders.withLogFileName(props, prefix, "atlassian-confluence-index.log"));
        }
    }

    @Plugin(name="com.atlassian.confluence.logging.AtlassianDiagnosticsLogAppender", category="Log4j Builder")
    public static class AtlassianDiagnosticsLogAppenderBuilder
    extends Log4j2ConfluenceHomeLogAppenderBuilder {
        public AtlassianDiagnosticsLogAppenderBuilder(String prefix, Properties props) {
            super(prefix, Log4j2LegacyConfluenceHomeLogAppenderBuilders.withLogFileName(props, prefix, "atlassian-diagnostics.log"));
        }
    }
}

