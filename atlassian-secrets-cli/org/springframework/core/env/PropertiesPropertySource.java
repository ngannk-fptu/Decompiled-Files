/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.env;

import java.util.Map;
import java.util.Properties;
import org.springframework.core.env.MapPropertySource;

public class PropertiesPropertySource
extends MapPropertySource {
    public PropertiesPropertySource(String name, Properties source) {
        super(name, source);
    }

    protected PropertiesPropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }
}

