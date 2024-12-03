/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.analysis;

import com.atlassian.confluence.plugins.index.api.CharFilterDescriptor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MappingCharFilterDescriptor
implements CharFilterDescriptor {
    private Map<String, String> map;

    public MappingCharFilterDescriptor(Map<String, String> map) {
        this.map = new HashMap<String, String>(map);
    }

    public Map<String, String> getMap() {
        return Collections.unmodifiableMap(this.map);
    }
}

