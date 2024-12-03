/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.sharelinks;

import com.atlassian.confluence.plugins.sharelinks.LinkMetaData;
import java.net.URISyntaxException;

public interface LinkMetaDataExtractor {
    public LinkMetaData parseMetaData(String var1, boolean var2) throws URISyntaxException;
}

