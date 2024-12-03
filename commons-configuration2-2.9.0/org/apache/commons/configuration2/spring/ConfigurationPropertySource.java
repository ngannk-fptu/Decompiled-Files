/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.env.EnumerablePropertySource
 */
package org.apache.commons.configuration2.spring;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.commons.configuration2.Configuration;
import org.springframework.core.env.EnumerablePropertySource;

public class ConfigurationPropertySource
extends EnumerablePropertySource<Configuration> {
    public ConfigurationPropertySource(String name, Configuration source) {
        super(name, (Object)source);
    }

    protected ConfigurationPropertySource(String name) {
        super(name);
    }

    public String[] getPropertyNames() {
        ArrayList<String> keys = new ArrayList<String>();
        Iterator<String> keysIterator = ((Configuration)this.source).getKeys();
        while (keysIterator.hasNext()) {
            keys.add(keysIterator.next());
        }
        return keys.toArray(new String[keys.size()]);
    }

    public Object getProperty(String name) {
        return ((Configuration)this.source).getProperty(name);
    }
}

