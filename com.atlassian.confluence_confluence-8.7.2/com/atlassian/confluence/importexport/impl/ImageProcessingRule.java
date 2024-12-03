/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.sitemesh.html.TagRule
 */
package com.atlassian.confluence.importexport.impl;

import com.atlassian.confluence.importexport.impl.ExportImageDescriptor;
import com.opensymphony.module.sitemesh.html.TagRule;
import java.util.Set;

public interface ImageProcessingRule
extends TagRule {
    public Set<ExportImageDescriptor> getExtractedUrls();
}

