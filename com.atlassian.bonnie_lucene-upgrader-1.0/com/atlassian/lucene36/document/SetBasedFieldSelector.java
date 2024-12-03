/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.document;

import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.document.FieldSelectorResult;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SetBasedFieldSelector
implements FieldSelector {
    private Set<String> fieldsToLoad;
    private Set<String> lazyFieldsToLoad;

    public SetBasedFieldSelector(Set<String> fieldsToLoad, Set<String> lazyFieldsToLoad) {
        this.fieldsToLoad = fieldsToLoad;
        this.lazyFieldsToLoad = lazyFieldsToLoad;
    }

    @Override
    public FieldSelectorResult accept(String fieldName) {
        FieldSelectorResult result = FieldSelectorResult.NO_LOAD;
        if (this.fieldsToLoad.contains(fieldName)) {
            result = FieldSelectorResult.LOAD;
        }
        if (this.lazyFieldsToLoad.contains(fieldName)) {
            result = FieldSelectorResult.LAZY_LOAD;
        }
        return result;
    }
}

