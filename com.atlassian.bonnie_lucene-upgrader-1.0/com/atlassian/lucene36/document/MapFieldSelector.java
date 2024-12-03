/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.document;

import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.document.FieldSelectorResult;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class MapFieldSelector
implements FieldSelector {
    Map<String, FieldSelectorResult> fieldSelections;

    public MapFieldSelector(Map<String, FieldSelectorResult> fieldSelections) {
        this.fieldSelections = fieldSelections;
    }

    public MapFieldSelector(List<String> fields) {
        this.fieldSelections = new HashMap<String, FieldSelectorResult>(fields.size() * 5 / 3);
        for (String field : fields) {
            this.fieldSelections.put(field, FieldSelectorResult.LOAD);
        }
    }

    public MapFieldSelector(String ... fields) {
        this(Arrays.asList(fields));
    }

    @Override
    public FieldSelectorResult accept(String field) {
        FieldSelectorResult selection = this.fieldSelections.get(field);
        return selection != null ? selection : FieldSelectorResult.NO_LOAD;
    }
}

