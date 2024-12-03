/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.DocInverterPerField;
import com.atlassian.lucene36.index.FieldInfo;
import com.atlassian.lucene36.index.InvertedDocEndConsumerPerField;

abstract class InvertedDocEndConsumerPerThread {
    InvertedDocEndConsumerPerThread() {
    }

    abstract void startDocument();

    abstract InvertedDocEndConsumerPerField addField(DocInverterPerField var1, FieldInfo var2);

    abstract void finishDocument();

    abstract void abort();
}

