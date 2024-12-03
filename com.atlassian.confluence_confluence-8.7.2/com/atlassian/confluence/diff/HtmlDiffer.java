/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  io.atlassian.util.concurrent.Timeout
 */
package com.atlassian.confluence.diff;

import com.atlassian.annotations.Internal;
import io.atlassian.util.concurrent.Timeout;

@Internal
public interface HtmlDiffer {
    public String diff(String var1, String var2, Timeout var3);
}

