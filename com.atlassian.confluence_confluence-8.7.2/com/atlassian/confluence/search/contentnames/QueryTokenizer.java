/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.contentnames;

import com.atlassian.confluence.search.contentnames.QueryToken;
import java.util.List;

public interface QueryTokenizer {
    public List<QueryToken> tokenize(String var1);
}

