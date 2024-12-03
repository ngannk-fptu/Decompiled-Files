/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy
 *  org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy
 *  org.apache.logging.log4j.core.config.plugins.Plugin
 */
package org.apache.log4j.builders.rolling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.log4j.builders.AbstractBuilder;
import org.apache.log4j.builders.rolling.TriggeringPolicyBuilder;
import org.apache.log4j.config.PropertiesConfiguration;
import org.apache.log4j.xml.XmlConfiguration;
import org.apache.logging.log4j.core.appender.rolling.CompositeTriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.w3c.dom.Element;

@Plugin(name="org.apache.log4j.rolling.CompositeTriggeringPolicy", category="Log4j Builder")
public class CompositeTriggeringPolicyBuilder
extends AbstractBuilder<TriggeringPolicy>
implements TriggeringPolicyBuilder {
    private static final TriggeringPolicy[] EMPTY_TRIGGERING_POLICIES = new TriggeringPolicy[0];
    private static final String POLICY_TAG = "triggeringPolicy";

    public CompositeTriggeringPolicyBuilder() {
    }

    public CompositeTriggeringPolicyBuilder(String prefix, Properties props) {
        super(prefix, props);
    }

    @Override
    public CompositeTriggeringPolicy parse(Element element, XmlConfiguration configuration) {
        ArrayList<TriggeringPolicy> policies = new ArrayList<TriggeringPolicy>();
        XmlConfiguration.forEachElement(element.getChildNodes(), currentElement -> {
            switch (currentElement.getTagName()) {
                case "triggeringPolicy": {
                    TriggeringPolicy policy = configuration.parseTriggeringPolicy((Element)currentElement);
                    if (policy == null) break;
                    policies.add(policy);
                }
            }
        });
        return this.createTriggeringPolicy(policies);
    }

    @Override
    public CompositeTriggeringPolicy parse(PropertiesConfiguration configuration) {
        return this.createTriggeringPolicy(Collections.emptyList());
    }

    private CompositeTriggeringPolicy createTriggeringPolicy(List<TriggeringPolicy> policies) {
        return CompositeTriggeringPolicy.createPolicy((TriggeringPolicy[])policies.toArray(EMPTY_TRIGGERING_POLICIES));
    }
}

