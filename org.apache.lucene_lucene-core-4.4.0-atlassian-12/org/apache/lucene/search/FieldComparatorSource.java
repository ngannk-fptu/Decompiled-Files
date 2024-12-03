/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.io.IOException;
import org.apache.lucene.search.FieldComparator;

public abstract class FieldComparatorSource {
    public abstract FieldComparator<?> newComparator(String var1, int var2, int var3, boolean var4) throws IOException;
}

