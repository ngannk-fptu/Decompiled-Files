/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.metadata.filter;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.tika.config.Field;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.filter.MetadataFilter;

public class FieldNameMappingFilter
extends MetadataFilter {
    Map<String, String> mappings = new LinkedHashMap<String, String>();
    boolean excludeUnmapped = true;

    @Override
    public void filter(Metadata metadata) throws TikaException {
        if (this.excludeUnmapped) {
            for (String n : metadata.names()) {
                if (this.mappings.containsKey(n)) {
                    String[] vals = metadata.getValues(n);
                    metadata.remove(n);
                    for (String val : vals) {
                        metadata.add(this.mappings.get(n), val);
                    }
                    continue;
                }
                metadata.remove(n);
            }
        } else {
            for (String n : metadata.names()) {
                if (!this.mappings.containsKey(n)) continue;
                String[] vals = metadata.getValues(n);
                metadata.remove(n);
                for (String val : vals) {
                    metadata.add(this.mappings.get(n), val);
                }
            }
        }
    }

    @Field
    public void setExcludeUnmapped(boolean excludeUnmapped) {
        this.excludeUnmapped = excludeUnmapped;
    }

    @Field
    public void setMappings(Map<String, String> mappings) {
        for (Map.Entry<String, String> e : mappings.entrySet()) {
            this.mappings.put(e.getKey(), e.getValue());
        }
    }
}

