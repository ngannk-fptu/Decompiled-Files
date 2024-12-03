/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.hibernate.boot.model.source.spi.ToolingHint;
import org.hibernate.mapping.MetaAttribute;

public class ToolingHintContext {
    private final ConcurrentMap<String, ToolingHint> toolingHintMap = new ConcurrentHashMap<String, ToolingHint>();

    public ToolingHintContext(ToolingHintContext baseline) {
        if (baseline == null) {
            return;
        }
        for (ToolingHint toolingHint : baseline.toolingHintMap.values()) {
            if (!toolingHint.isInheritable()) continue;
            this.toolingHintMap.put(toolingHint.getName(), toolingHint);
        }
    }

    public Collection<ToolingHint> getToolingHints() {
        return this.toolingHintMap.values();
    }

    public Iterable<String> getKeys() {
        return this.toolingHintMap.keySet();
    }

    public ToolingHint getToolingHint(String key) {
        return (ToolingHint)this.toolingHintMap.get(key);
    }

    public void add(ToolingHint toolingHint) {
        this.toolingHintMap.put(toolingHint.getName(), toolingHint);
    }

    public Map<String, MetaAttribute> getMetaAttributeMap() {
        ConcurrentHashMap<String, MetaAttribute> collectedAttributeMap = new ConcurrentHashMap<String, MetaAttribute>();
        for (ToolingHint toolingHint : this.toolingHintMap.values()) {
            collectedAttributeMap.put(toolingHint.getName(), toolingHint.asMetaAttribute());
        }
        return collectedAttributeMap;
    }
}

