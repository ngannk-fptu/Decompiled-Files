/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jsoup.nodes.Document
 */
package com.atlassian.confluence.plugins.sharelinks;

import com.atlassian.confluence.plugins.sharelinks.LinkMetaData;
import org.jsoup.nodes.Document;

public interface DOMMetadataExtractor {
    public void updateMetadata(LinkMetaData var1, Document var2);
}

