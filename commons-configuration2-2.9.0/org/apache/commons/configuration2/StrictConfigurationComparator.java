/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.util.Iterator;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.ConfigurationComparator;

public class StrictConfigurationComparator
implements ConfigurationComparator {
    @Override
    public boolean compare(Configuration a, Configuration b) {
        Object value;
        String key;
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        Iterator<String> keys = a.getKeys();
        while (keys.hasNext()) {
            key = keys.next();
            value = a.getProperty(key);
            if (value.equals(b.getProperty(key))) continue;
            return false;
        }
        keys = b.getKeys();
        while (keys.hasNext()) {
            key = keys.next();
            value = b.getProperty(key);
            if (value.equals(a.getProperty(key))) continue;
            return false;
        }
        return true;
    }
}

