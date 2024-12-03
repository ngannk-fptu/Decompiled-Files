/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.Logger
 *  org.apache.logging.log4j.core.Appender
 *  org.apache.logging.log4j.core.Filter
 *  org.apache.logging.log4j.core.appender.rewrite.RewriteAppender
 *  org.apache.logging.log4j.core.appender.rewrite.RewritePolicy
 *  org.apache.logging.log4j.core.config.AppenderRef
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 *  org.apache.logging.log4j.status.StatusLogger
 *  org.apache.logging.log4j.util.Strings
 */
package org.apache.log4j.builders.appender;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.log4j.Appender;
import org.apache.log4j.bridge.AppenderWrapper;
import org.apache.log4j.bridge.RewritePolicyAdapter;
import org.apache.log4j.bridge.RewritePolicyWrapper;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.BuilderManager;
import org.apache.log4j.builders.appender.AppenderBuilder;
import org.apache.log4j.config.Log4j1Configuration;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.rewrite.RewriteAppender;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.rewrite.RewriteAppender", category="Log4j Builder")
public class RewriteAppenderBuilder
extends AbstractBuilder
implements AppenderBuilder {
    private static final Logger LOGGER = StatusLogger.getLogger();
    private static final String REWRITE_POLICY_TAG = "rewritePolicy";

    public RewriteAppenderBuilder() {
    }

    public RewriteAppenderBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    public Appender parseAppender(Element appenderElement, XmlConfiguration config) {
        String name = this.getNameAttribute(appenderElement);
        AtomicReference appenderRefs = new AtomicReference(new ArrayList());
        AtomicReference rewritePolicyHolder = new AtomicReference();
        AtomicReference level = new AtomicReference();
        AtomicReference filter = new AtomicReference();
        XmlConfiguration.forEachElement(appenderElement.getChildNodes(), currentElement -> {
            switch (currentElement.getTagName()) {
                case "appender-ref": {
                    Appender appender = config.findAppenderByReference((Element)currentElement);
                    if (appender == null) break;
                    ((List)appenderRefs.get()).add(appender.getName());
                    break;
                }
                case "rewritePolicy": {
                    org.apache.log4j.rewrite.RewritePolicy policy = config.parseRewritePolicy((Element)currentElement);
                    if (policy == null) break;
                    rewritePolicyHolder.set(policy);
                    break;
                }
                case "filter": {
                    config.addFilter(filter, (Element)currentElement);
                    break;
                }
                case "param": {
                    if (!this.getNameAttributeKey((Element)currentElement).equalsIgnoreCase("Threshold")) break;
                    this.set("Threshold", (Element)currentElement, level);
                }
            }
        });
        return this.createAppender(name, (String)level.get(), ((List)appenderRefs.get()).toArray(Strings.EMPTY_ARRAY), (org.apache.log4j.rewrite.RewritePolicy)rewritePolicyHolder.get(), (org.apache.log4j.spi.Filter)filter.get(), config);
    }

    @Override
    public Appender parseAppender(String name, String appenderPrefix, String layoutPrefix, String filterPrefix, Properties props, PropertiesConfiguration configuration) {
        String appenderRef = this.getProperty("appender-ref");
        org.apache.log4j.spi.Filter filter = configuration.parseAppenderFilters(props, filterPrefix, name);
        String policyPrefix = appenderPrefix + ".rewritePolicy";
        String className = this.getProperty(policyPrefix);
        org.apache.log4j.rewrite.RewritePolicy policy = configuration.getBuilderManager().parse(className, policyPrefix, props, configuration, BuilderManager.INVALID_REWRITE_POLICY);
        String level = this.getProperty("Threshold");
        if (appenderRef == null) {
            LOGGER.error("No appender references configured for RewriteAppender {}", (Object)name);
            return null;
        }
        Appender appender = configuration.parseAppender(props, appenderRef);
        if (appender == null) {
            LOGGER.error("Cannot locate Appender {}", (Object)appenderRef);
            return null;
        }
        return this.createAppender(name, level, new String[]{appenderRef}, policy, filter, configuration);
    }

    private <T extends Log4j1Configuration> Appender createAppender(String name, String level, String[] appenderRefs, org.apache.log4j.rewrite.RewritePolicy policy, org.apache.log4j.spi.Filter filter, T configuration) {
        if (appenderRefs.length == 0) {
            LOGGER.error("No appender references configured for RewriteAppender {}", (Object)name);
            return null;
        }
        Level logLevel = OptionConverter.convertLevel(level, Level.TRACE);
        AppenderRef[] refs = new AppenderRef[appenderRefs.length];
        int index = 0;
        for (String appenderRef : appenderRefs) {
            refs[index++] = AppenderRef.createAppenderRef((String)appenderRef, (Level)logLevel, null);
        }
        Filter rewriteFilter = RewriteAppenderBuilder.buildFilters(level, filter);
        Object rewritePolicy = policy instanceof RewritePolicyWrapper ? ((RewritePolicyWrapper)policy).getPolicy() : new RewritePolicyAdapter(policy);
        return AppenderWrapper.adapt((org.apache.logging.log4j.core.Appender)RewriteAppender.createAppender((String)name, (String)"true", (AppenderRef[])refs, configuration, (RewritePolicy)rewritePolicy, (Filter)rewriteFilter));
    }
}

