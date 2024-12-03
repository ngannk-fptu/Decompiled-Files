/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy
 *  org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 */
package org.apache.log4j.builders.rolling;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.rolling.TriggeringPolicyBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.core.appender.rolling.SizeBasedTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.rolling.SizeBasedTriggeringPolicy", category="Log4j Builder")
public class SizeBasedTriggeringPolicyBuilder
extends AbstractBuilder<TriggeringPolicy>
implements TriggeringPolicyBuilder {
    private static final String MAX_SIZE_PARAM = "MaxFileSize";
    private static final long DEFAULT_MAX_SIZE = 0xA00000L;

    public SizeBasedTriggeringPolicyBuilder() {
    }

    public SizeBasedTriggeringPolicyBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    public SizeBasedTriggeringPolicy parse(Element element, XmlConfiguration configuration) {
        AtomicLong maxSize = new AtomicLong(0xA00000L);
        XmlConfiguration.forEachElement(element.getChildNodes(), currentElement -> {
            switch (currentElement.getTagName()) {
                case "param": {
                    switch (this.getNameAttributeKey((Element)currentElement)) {
                        case "MaxFileSize": {
                            this.set(MAX_SIZE_PARAM, (Element)currentElement, maxSize);
                        }
                    }
                }
            }
        });
        return this.createTriggeringPolicy(maxSize.get());
    }

    @Override
    public SizeBasedTriggeringPolicy parse(PropertiesConfiguration configuration) {
        long maxSize = this.getLongProperty(MAX_SIZE_PARAM, 0xA00000L);
        return this.createTriggeringPolicy(maxSize);
    }

    private SizeBasedTriggeringPolicy createTriggeringPolicy(long maxSize) {
        return SizeBasedTriggeringPolicy.createPolicy((String)Long.toString(maxSize));
    }
}

