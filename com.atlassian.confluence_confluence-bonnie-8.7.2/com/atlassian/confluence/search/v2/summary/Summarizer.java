/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.summary;

import com.atlassian.confluence.search.v2.summary.Summary;
import java.io.IOException;

public interface Summarizer {
    public Summary getSummary(String var1) throws IOException;

    public Summary getSummary(String var1, String var2) throws IOException;
}

