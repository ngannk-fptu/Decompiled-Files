/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.document;

import com.atlassian.lucene36.document.FieldSelectorResult;
import java.io.Serializable;

public interface FieldSelector
extends Serializable {
    public FieldSelectorResult accept(String var1);
}

