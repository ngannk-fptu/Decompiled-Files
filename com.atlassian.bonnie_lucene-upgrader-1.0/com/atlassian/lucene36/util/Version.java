/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public enum Version {
    LUCENE_20,
    LUCENE_21,
    LUCENE_22,
    LUCENE_23,
    LUCENE_24,
    LUCENE_29,
    LUCENE_30,
    LUCENE_31,
    LUCENE_32,
    LUCENE_33,
    LUCENE_34,
    LUCENE_35,
    LUCENE_36,
    LUCENE_CURRENT;


    public boolean onOrAfter(Version other) {
        return this.compareTo(other) >= 0;
    }
}

