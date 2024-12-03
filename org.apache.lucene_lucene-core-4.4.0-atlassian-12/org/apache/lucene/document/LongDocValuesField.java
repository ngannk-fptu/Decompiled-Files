/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.document;

import org.apache.lucene.document.NumericDocValuesField;

@Deprecated
public class LongDocValuesField
extends NumericDocValuesField {
    public LongDocValuesField(String name, long value) {
        super(name, value);
    }
}

