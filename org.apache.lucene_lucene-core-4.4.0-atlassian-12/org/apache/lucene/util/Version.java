/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

import java.util.Locale;

public enum Version {
    LUCENE_30,
    LUCENE_31,
    LUCENE_32,
    LUCENE_33,
    LUCENE_34,
    LUCENE_35,
    LUCENE_36,
    LUCENE_40,
    LUCENE_41,
    LUCENE_42,
    LUCENE_43,
    LUCENE_44,
    LUCENE_CURRENT;


    public boolean onOrAfter(Version other) {
        return this.compareTo(other) >= 0;
    }

    public static Version parseLeniently(String version) {
        String parsedMatchVersion = version.toUpperCase(Locale.ROOT);
        return Version.valueOf(parsedMatchVersion.replaceFirst("^(\\d)\\.(\\d)$", "LUCENE_$1$2"));
    }
}

