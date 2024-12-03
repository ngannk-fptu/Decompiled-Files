/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira;

import com.atlassian.confluence.extra.jira.Channel;
import java.io.IOException;
import java.util.Collection;

public interface FlexigridResponseGenerator {
    public String generate(Channel var1, Collection<String> var2, int var3, boolean var4, boolean var5) throws IOException;
}

