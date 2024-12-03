/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy
 *  org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 */
package org.apache.log4j.builders.rolling;

import java.util.Properties;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.rolling.TriggeringPolicyBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.core.appender.rolling.TimeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.rolling.TimeBasedRollingPolicy", category="Log4j Builder")
public class TimeBasedRollingPolicyBuilder
extends AbstractBuilder<TriggeringPolicy>
implements TriggeringPolicyBuilder {
    public TimeBasedRollingPolicyBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    public TimeBasedRollingPolicyBuilder() {
    }

    @Override
    public TimeBasedTriggeringPolicy parse(Element element, XmlConfiguration configuration) {
        return this.createTriggeringPolicy();
    }

    @Override
    public TimeBasedTriggeringPolicy parse(PropertiesConfiguration configuration) {
        return this.createTriggeringPolicy();
    }

    private TimeBasedTriggeringPolicy createTriggeringPolicy() {
        return TimeBasedTriggeringPolicy.newBuilder().build();
    }
}

