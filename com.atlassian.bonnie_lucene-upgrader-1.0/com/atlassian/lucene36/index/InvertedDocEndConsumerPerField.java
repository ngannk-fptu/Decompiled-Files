/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

abstract class InvertedDocEndConsumerPerField {
    InvertedDocEndConsumerPerField() {
    }

    abstract void finish();

    abstract void abort();
}

