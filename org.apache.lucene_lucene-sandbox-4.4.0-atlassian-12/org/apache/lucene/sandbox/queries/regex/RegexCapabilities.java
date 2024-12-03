/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.sandbox.queries.regex;

import org.apache.lucene.util.BytesRef;

public interface RegexCapabilities {
    public RegexMatcher compile(String var1);

    public static interface RegexMatcher {
        public boolean match(BytesRef var1);

        public String prefix();
    }
}

