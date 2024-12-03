/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.codahale.metrics.jmx;

import com.codahale.metrics.jmx.JmxReporter;
import com.codahale.metrics.jmx.ObjectNameFactory;
import java.util.Hashtable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultObjectNameFactory
implements ObjectNameFactory {
    private static final char[] QUOTABLE_CHARS = new char[]{',', '=', ':', '\"'};
    private static final Logger LOGGER = LoggerFactory.getLogger(JmxReporter.class);

    @Override
    public ObjectName createName(String type, String domain, String name) {
        try {
            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put("name", name);
            properties.put("type", type);
            ObjectName objectName = new ObjectName(domain, properties);
            if (objectName.isDomainPattern()) {
                domain = ObjectName.quote(domain);
            }
            if (objectName.isPropertyValuePattern("name") || this.shouldQuote(objectName.getKeyProperty("name"))) {
                properties.put("name", ObjectName.quote(name));
            }
            if (objectName.isPropertyValuePattern("type") || this.shouldQuote(objectName.getKeyProperty("type"))) {
                properties.put("type", ObjectName.quote(type));
            }
            objectName = new ObjectName(domain, properties);
            return objectName;
        }
        catch (MalformedObjectNameException e) {
            try {
                return new ObjectName(domain, "name", ObjectName.quote(name));
            }
            catch (MalformedObjectNameException e1) {
                LOGGER.warn("Unable to register {} {}", new Object[]{type, name, e1});
                throw new RuntimeException(e1);
            }
        }
    }

    private boolean shouldQuote(String value) {
        for (char quotableChar : QUOTABLE_CHARS) {
            if (value.indexOf(quotableChar) == -1) continue;
            return true;
        }
        return false;
    }
}

