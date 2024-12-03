/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.search.FieldComparator;
import java.io.IOException;
import java.io.Serializable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class FieldComparatorSource
implements Serializable {
    public abstract FieldComparator<?> newComparator(String var1, int var2, int var3, boolean var4) throws IOException;
}

