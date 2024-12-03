/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.bootstrap;

import com.atlassian.confluence.bootstrap.XmlUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SynchronyProxyWatchdog {
    private static final Logger logger = LoggerFactory.getLogger(SynchronyProxyWatchdog.class);
    private static final String FILE_LOGGER_NAME = "SynchronyProxyWatchdogFileLogger";
    public static final String FILE_LOGGER_PATH = "/logs/synchrony-proxy-watchdog.log";
    private static final String FILE_LOGGER_PATTERN = "/logs/synchrony-proxy-watchdog.%i.log.gz";
    private static final String FILE_LOGGER_TRIGGER_POLICY_SIZE = "10 MB";
    private static final String FILE_LOGGER_ROLLOVER_STRATEGY_MAX_NUM = "100";

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            logger.error("You must supply at least the CATALINA_HOME value as the first argument.");
            return;
        }
        File catalinaHome = new File(args[0]);
        String catalinaHomePath = catalinaHome.getAbsolutePath();
        String logFilePath = args.length > 1 && args[1] != null && !args[1].isEmpty() ? args[1] : catalinaHomePath;
        SynchronyProxyWatchdog.addLogFileAppender(logFilePath);
        File serverXmlFile = new File(catalinaHomePath + File.separator + "conf" + File.separator + "server.xml");
        if (serverXmlFile.exists()) {
            Optional<String> synchronyProxyContext = XmlUtils.getAttributeFromXmlFile(serverXmlFile, "path", "//Context[@path='${confluence.context.path}/synchrony-proxy']");
            if (synchronyProxyContext.isPresent()) {
                logger.info("A Context element for ${confluence.context.path}/synchrony-proxy is found in {}. No further action is required", (Object)serverXmlFile.getAbsolutePath());
                return;
            }
        } else {
            logger.error("{} is missing. Please check", (Object)serverXmlFile.getAbsolutePath());
            return;
        }
        String confluenceContextPath = XmlUtils.getAttributeFromXmlFile(serverXmlFile, "path", "//Context[@path]").orElse("");
        logger.info("The Confluence context path is {}", (Object)(confluenceContextPath.isEmpty() ? "empty. No further action is required." : confluenceContextPath));
        if (confluenceContextPath.isEmpty()) {
            return;
        }
        String localhostDirectory = catalinaHome.getAbsolutePath() + File.separator + "conf" + File.separator + "Standalone" + File.separator + "localhost";
        logger.info("The new XML will be stored in {}", (Object)localhostDirectory);
        if (confluenceContextPath.length() > 0) {
            File synchronyProxyXmlFile = new File(localhostDirectory + File.separator + confluenceContextPath + "#synchrony-proxy.xml");
            SynchronyProxyWatchdog.tryToCreateSynchronyProxyXmlFile(synchronyProxyXmlFile);
        }
        SynchronyProxyWatchdog.removeLogFileAppender();
    }

    private static void tryToCreateSynchronyProxyXmlFile(File synchronyProxyXmlFile) {
        if (!synchronyProxyXmlFile.exists()) {
            try {
                int readBytes;
                InputStream inputStream = SynchronyProxyWatchdog.class.getClassLoader().getResourceAsStream("synchrony-proxy.xml");
                byte[] buffer = new byte[4096];
                FileOutputStream outputStream = new FileOutputStream(synchronyProxyXmlFile);
                while ((readBytes = inputStream.read(buffer)) > 0) {
                    ((OutputStream)outputStream).write(buffer, 0, readBytes);
                }
            }
            catch (IOException e) {
                logger.error("{}", (Object)e.getMessage());
                logger.info("Please grant the write permission for {} if Confluence is running as a standalone application (i.e not in a Data Center)", (Object)synchronyProxyXmlFile.getParent());
                return;
            }
            logger.info("{} has been created.", (Object)synchronyProxyXmlFile.getAbsolutePath());
        } else {
            logger.info("{} already exists. No further action is required.", (Object)synchronyProxyXmlFile.getAbsolutePath());
        }
    }

    private static RollingFileAppender buildRollingFileAppender(String catalinaHomePath) {
        return ((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((AbstractAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((AbstractAppender.Builder)RollingFileAppender.newBuilder()).withName(FILE_LOGGER_NAME)).withFileName(catalinaHomePath + FILE_LOGGER_PATH)).withFilePattern(catalinaHomePath + FILE_LOGGER_PATTERN)).withLayout(PatternLayout.newBuilder().withPattern("%d %p [%t] [%c{4}] %m%n").build())).withPolicy(SizeBasedTriggeringPolicy.createPolicy(FILE_LOGGER_TRIGGER_POLICY_SIZE))).withStrategy(DefaultRolloverStrategy.newBuilder().withMax(FILE_LOGGER_ROLLOVER_STRATEGY_MAX_NUM).build())).withAppend(true)).build();
    }

    private static void addLogFileAppender(String catalinaHomePath) {
        RollingFileAppender appender = SynchronyProxyWatchdog.buildRollingFileAppender(catalinaHomePath);
        appender.start();
        SynchronyProxyWatchdog.getCoreLogger().get().addAppender(appender, Level.INFO, null);
    }

    private static void removeLogFileAppender() {
        SynchronyProxyWatchdog.getCoreLogger().get().getAppenders().get(FILE_LOGGER_NAME).stop();
        SynchronyProxyWatchdog.getCoreLogger().get().removeAppender(FILE_LOGGER_NAME);
    }

    private static org.apache.logging.log4j.core.Logger getCoreLogger() {
        return (org.apache.logging.log4j.core.Logger)LogManager.getLogger(SynchronyProxyWatchdog.class);
    }
}

