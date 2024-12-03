/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.document;

import com.atlassian.lucene36.document.FieldSelector;
import com.atlassian.lucene36.document.FieldSelectorResult;

public class LoadFirstFieldSelector
implements FieldSelector {
    public FieldSelectorResult accept(String fieldName) {
        return FieldSelectorResult.LOAD_AND_BREAK;
    }
}

