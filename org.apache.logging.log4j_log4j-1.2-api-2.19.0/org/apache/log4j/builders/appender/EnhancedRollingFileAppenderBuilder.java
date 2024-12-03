/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.Layout
 *  org.apache.logging.log4j.core.appender.RollingFileAppender
 *  org.apache.logging.log4j.core.appender.RollingFileAppender$Builder
 *  org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy
 *  org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy$Builder
 *  org.apache.logging.log4j.core.appender.rolling.RolloverStrategy
 *  org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy
 *  org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy
 *  org.apache.logging.log4j.core.config.Configuration
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.status.StatusLogger
 */
package org.apache.log4j.builders.appender;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.bridge.AppenderWrapper;
import org.apache.log4j.bridge.LayoutAdapter;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.appender.AppenderBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.DefaultRolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.status.StatusLogger;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.rolling.RollingFileAppender", category="Log4j Builder")
public class EnhancedRollingFileAppenderBuilder
extends AbstractBuilder<Appender>
implements AppenderBuilder<Appender> {
    private static final String TIME_BASED_ROLLING_POLICY = "org.apache.log4j.rolling.TimeBasedRollingPolicy";
    private static final String FIXED_WINDOW_ROLLING_POLICY = "org.apache.log4j.rolling.FixedWindowRollingPolicy";
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final String TRIGGERING_TAG = "triggeringPolicy";
    private static final String ROLLING_TAG = "rollingPolicy";
    private static final int DEFAULT_MIN_INDEX = 1;
    private static final int DEFAULT_MAX_INDEX = 7;
    private static final String ACTIVE_FILE_PARAM = "ActiveFileName";
    private static final String FILE_PATTERN_PARAM = "FileNamePattern";
    private static final String MIN_INDEX_PARAM = "MinIndex";
    private static final String MAX_INDEX_PARAM = "MaxIndex";

    public EnhancedRollingFileAppenderBuilder() {
    }

    public EnhancedRollingFileAppenderBuilder(String prefix, Properties properties) {
        super(prefix, properties);
    }

    private void parseRollingPolicy(Element element, XmlConfiguration configuration, AtomicReference<String> rollingPolicyClassName, AtomicReference<String> activeFileName, AtomicReference<String> fileNamePattern, AtomicInteger minIndex, AtomicInteger maxIndex) {
        rollingPolicyClassName.set(configuration.subst(element.getAttribute("class"), this.getProperties()));
        XmlConfiguration.forEachElement(element.getChildNodes(), currentElement -> {
            block3 : switch (currentElement.getTagName()) {
                case "param": {
                    switch (this.getNameAttributeKey((Element)currentElement)) {
                        case "ActiveFileName": {
                            this.set(ACTIVE_FILE_PARAM, (Element)currentElement, activeFileName);
                            break block3;
                        }
                        case "FileNamePattern": {
                            this.set(FILE_PATTERN_PARAM, (Element)currentElement, fileNamePattern);
                            break block3;
                        }
                        case "MinIndex": {
                            this.set(MIN_INDEX_PARAM, (Element)currentElement, minIndex);
                            break block3;
                        }
                        case "MaxIndex": {
                            this.set(MAX_INDEX_PARAM, (Element)currentElement, maxIndex);
                        }
                    }
                }
            }
        });
    }

    @Override
    public Appender parseAppender(Element element, XmlConfiguration configuration) {
        String name = this.getNameAttribute(element);
        AtomicReference layout = new AtomicReference();
        AtomicReference filter = new AtomicReference();
        AtomicReference fileName = new AtomicReference();
        AtomicReference level = new AtomicReference();
        AtomicBoolean immediateFlush = new AtomicBoolean(true);
        AtomicBoolean append = new AtomicBoolean(true);
        AtomicBoolean bufferedIo = new AtomicBoolean();
        AtomicInteger bufferSize = new AtomicInteger(8192);
        AtomicReference rollingPolicyClassName = new AtomicReference();
        AtomicReference activeFileName = new AtomicReference();
        AtomicReference fileNamePattern = new AtomicReference();
        AtomicInteger minIndex = new AtomicInteger(1);
        AtomicInteger maxIndex = new AtomicInteger(7);
        AtomicReference triggeringPolicy = new AtomicReference();
        XmlConfiguration.forEachElement(element.getChildNodes(), currentElement -> {
            block7 : switch (currentElement.getTagName()) {
                case "rollingPolicy": {
                    this.parseRollingPolicy((Element)currentElement, configuration, rollingPolicyClassName, activeFileName, fileNamePattern, minIndex, maxIndex);
                    break;
                }
                case "triggeringPolicy": {
                    triggeringPolicy.set(configuration.parseTriggeringPolicy((Element)currentElement));
                    break;
                }
                case "layout": {
                    layout.set(configuration.parseLayout((Element)currentElement));
                    break;
                }
                case "filter": {
                    configuration.addFilter(filter, (Element)currentElement);
                    break;
                }
                case "param": {
                    switch (this.getNameAttributeKey((Element)currentElement)) {
                        case "File": {
                            this.set("File", (Element)currentElement, fileName);
                            break block7;
                        }
                        case "Append": {
                            this.set("Append", (Element)currentElement, append);
                            break block7;
                        }
                        case "BufferedIO": {
                            this.set("BufferedIO", (Element)currentElement, bufferedIo);
                            break block7;
                        }
                        case "BufferSize": {
                            this.set("BufferSize", (Element)currentElement, bufferSize);
                            break block7;
                        }
                        case "Threshold": {
                            this.set("Threshold", (Element)currentElement, level);
                            break block7;
                        }
                        case "ImmediateFlush": {
                            this.set("ImmediateFlush", (Element)currentElement, immediateFlush);
                        }
                    }
                }
            }
        });
        return this.createAppender(name, (Layout)layout.get(), (org.apache.log4j.spi.Filter)filter.get(), (String)fileName.get(), (String)level.get(), immediateFlush.get(), append.get(), bufferedIo.get(), bufferSize.get(), (String)rollingPolicyClassName.get(), (String)activeFileName.get(), (String)fileNamePattern.get(), minIndex.get(), maxIndex.get(), (TriggeringPolicy)triggeringPolicy.get(), (Configuration)configuration);
    }

    @Override
    public Appender parseAppender(String name, String appenderPrefix, String layoutPrefix, String filterPrefix, Properties props, PropertiesConfiguration configuration) {
        Layout layout = configuration.parseLayout(layoutPrefix, name, props);
        org.apache.log4j.spi.Filter filter = configuration.parseAppenderFilters(props, filterPrefix, name);
        String level = this.getProperty("Threshold");
        String fileName = this.getProperty("File");
        boolean append = this.getBooleanProperty("Append", true);
        boolean immediateFlush = this.getBooleanProperty("ImmediateFlush", true);
        boolean bufferedIo = this.getBooleanProperty("BufferedIO", false);
        int bufferSize = Integer.parseInt(this.getProperty("BufferSize", "8192"));
        String rollingPolicyClassName = this.getProperty(ROLLING_TAG);
        int minIndex = this.getIntegerProperty("rollingPolicy.MinIndex", 1);
        int maxIndex = this.getIntegerProperty("rollingPolicy.MaxIndex", 7);
        String activeFileName = this.getProperty("rollingPolicy.ActiveFileName");
        String fileNamePattern = this.getProperty("rollingPolicy.FileNamePattern");
        TriggeringPolicy triggeringPolicy = configuration.parseTriggeringPolicy(props, appenderPrefix + "." + TRIGGERING_TAG);
        return this.createAppender(name, layout, filter, fileName, level, immediateFlush, append, bufferedIo, bufferSize, rollingPolicyClassName, activeFileName, fileNamePattern, minIndex, maxIndex, triggeringPolicy, (Configuration)configuration);
    }

    private Appender createAppender(String name, Layout layout, org.apache.log4j.spi.Filter filter, String fileName, String level, boolean immediateFlush, boolean append, boolean bufferedIo, int bufferSize, String rollingPolicyClassName, String activeFileName, String fileNamePattern, int minIndex, int maxIndex, TriggeringPolicy triggeringPolicy, Configuration configuration) {
        TriggeringPolicy actualTriggeringPolicy;
        String actualFileName;
        org.apache.logging.log4j.core.Layout<?> fileLayout = LayoutAdapter.adapt(layout);
        boolean actualImmediateFlush = bufferedIo ? false : immediateFlush;
        Filter fileFilter = EnhancedRollingFileAppenderBuilder.buildFilters(level, filter);
        if (rollingPolicyClassName == null) {
            LOGGER.error("Unable to create RollingFileAppender, no rolling policy provided.");
            return null;
        }
        String string = actualFileName = activeFileName != null ? activeFileName : fileName;
        if (actualFileName == null) {
            LOGGER.error("Unable to create RollingFileAppender, no file name provided.");
            return null;
        }
        if (fileNamePattern == null) {
            LOGGER.error("Unable to create RollingFileAppender, no file name pattern provided.");
            return null;
        }
        DefaultRolloverStrategy.Builder rolloverStrategyBuilder = DefaultRolloverStrategy.newBuilder();
        switch (rollingPolicyClassName) {
            case "org.apache.log4j.rolling.FixedWindowRollingPolicy": {
                rolloverStrategyBuilder.withMin(Integer.toString(minIndex)).withMax(Integer.toString(maxIndex));
                break;
            }
            case "org.apache.log4j.rolling.TimeBasedRollingPolicy": {
                break;
            }
            default: {
                LOGGER.warn("Unsupported rolling policy: {}", (Object)rollingPolicyClassName);
            }
        }
        if (triggeringPolicy != null) {
            actualTriggeringPolicy = triggeringPolicy;
        } else if (rollingPolicyClassName.equals(TIME_BASED_ROLLING_POLICY)) {
            actualTriggeringPolicy = TimeBasedTriggeringPolicy.newBuilder().build();
        } else {
            LOGGER.error("Unable to create RollingFileAppender, no triggering policy provided.");
            return null;
        }
        return AppenderWrapper.adapt((org.apache.logging.log4j.core.Appender)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)((RollingFileAppender.Builder)RollingFileAppender.newBuilder().withAppend(append).setBufferedIo(bufferedIo)).setBufferSize(bufferedIo ? bufferSize : 0)).setConfiguration(configuration)).withFileName(actualFileName).withFilePattern(fileNamePattern).setFilter(fileFilter)).setImmediateFlush(actualImmediateFlush)).setLayout(fileLayout)).setName(name)).withPolicy(actualTriggeringPolicy).withStrategy((RolloverStrategy)rolloverStrategyBuilder.build()).build());
    }
}

