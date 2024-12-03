/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.servlet.download;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface SafeContentHeaderGuesser {
    public Map<String, String> computeAttachmentHeaders(String var1, InputStream var2, String var3, String var4, long var5, boolean var7, Map<String, String[]> var8) throws IOException;
}

