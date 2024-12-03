/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.component;

import aQute.bnd.header.Attrs;
import aQute.libg.tuple.Pair;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MergedRequirement {
    private static final String MULTIPLE = "\"multiple\"";
    private static final String OPTIONAL = "\"optional\"";
    private final Map<FilterEffectivePair, Attrs> filterMap = new LinkedHashMap<FilterEffectivePair, Attrs>();
    private final String namespace;

    public MergedRequirement(String namespace) {
        this.namespace = namespace;
    }

    public void put(String filter, String effective, boolean optional, boolean multiple) {
        FilterEffectivePair key = new FilterEffectivePair(filter, effective);
        Attrs existing = this.filterMap.get(key);
        if (existing != null) {
            boolean existingOptional = OPTIONAL.equals(existing.get("resolution:"));
            optional = optional && existingOptional;
            boolean existingMultiple = MULTIPLE.equals(existing.get("cardinality:"));
            multiple = multiple || existingMultiple;
        }
        Attrs newAttrs = new Attrs();
        newAttrs.put("filter:", '\"' + filter + '\"');
        if (effective != null) {
            newAttrs.put("effective:", '\"' + effective + '\"');
        }
        if (optional) {
            newAttrs.put("resolution:", OPTIONAL);
        }
        if (multiple) {
            newAttrs.put("cardinality:", MULTIPLE);
        }
        this.filterMap.put(key, newAttrs);
    }

    public List<String> toStringList() {
        ArrayList<String> strings = new ArrayList<String>(this.filterMap.size());
        for (Map.Entry<FilterEffectivePair, Attrs> entry : this.filterMap.entrySet()) {
            Attrs attrs = entry.getValue();
            strings.add(this.namespace + ';' + attrs);
        }
        return strings;
    }

    private static class FilterEffectivePair
    extends Pair<String, String> {
        private static final long serialVersionUID = 1L;

        FilterEffectivePair(String filter, String effective) {
            super(filter, effective);
        }
    }
}

