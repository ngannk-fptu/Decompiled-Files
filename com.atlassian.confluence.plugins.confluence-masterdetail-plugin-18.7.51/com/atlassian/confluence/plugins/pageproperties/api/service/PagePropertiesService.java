/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.plugins.pageproperties.api.service;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.pageproperties.api.model.PagePropertiesMacroReport;

public interface PagePropertiesService {
    public PagePropertiesMacroReport getReportFromContent(ContentEntityObject var1);
}

