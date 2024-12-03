/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.HelpPath
 *  com.atlassian.sal.api.message.HelpPathResolver
 */
package com.atlassian.sal.core.message;

import com.atlassian.sal.api.message.HelpPath;
import com.atlassian.sal.api.message.HelpPathResolver;

public class NoopHelpPathResolver
implements HelpPathResolver {
    public HelpPath getHelpPath(String key) {
        return null;
    }
}

