/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;
import org.jfree.base.config.ModifiableConfiguration;

public class DefaultConfiguration
extends Properties
implements ModifiableConfiguration {
    public String getConfigProperty(String key) {
        return this.getProperty(key);
    }

    public String getConfigProperty(String key, String defaultValue) {
        return this.getProperty(key, defaultValue);
    }

    public Iterator findPropertyKeys(String prefix) {
        TreeSet<String> collector = new TreeSet<String>();
        Enumeration<Object> enum1 = this.keys();
        while (enum1.hasMoreElements()) {
            String key = (String)enum1.nextElement();
            if (!key.startsWith(prefix) || collector.contains(key)) continue;
            collector.add(key);
        }
        return Collections.unmodifiableSet(collector).iterator();
    }

    public Enumeration getConfigProperties() {
        return this.keys();
    }

    public void setConfigProperty(String key, String value) {
        if (value == null) {
            this.remove(key);
        } else {
            this.setProperty(key, value);
        }
    }
}

