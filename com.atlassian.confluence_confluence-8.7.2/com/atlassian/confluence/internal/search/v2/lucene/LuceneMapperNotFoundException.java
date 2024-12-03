/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.v2.lucene;

public class LuceneMapperNotFoundException
extends RuntimeException {
    private final String mappeeKey;
    private final Class mappeeClass;

    public LuceneMapperNotFoundException(Class mappeeClass, String mappeeKey) {
        super("A lucene mapper could not be found to map an object of type: " + mappeeClass.getName() + " and key: " + mappeeKey);
        this.mappeeKey = mappeeKey;
        this.mappeeClass = mappeeClass;
    }

    public String getMappeeKey() {
        return this.mappeeKey;
    }

    public Class getMappeeClass() {
        return this.mappeeClass;
    }
}

