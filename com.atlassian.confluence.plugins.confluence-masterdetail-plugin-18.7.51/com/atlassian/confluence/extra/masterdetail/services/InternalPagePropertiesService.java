/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 */
package com.atlassian.confluence.extra.masterdetail.services;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.masterdetail.ExtractedDetails;
import com.atlassian.confluence.extra.masterdetail.analytics.DetailsSummaryMacroMetricsEvent;
import com.atlassian.confluence.plugins.pageproperties.api.service.PagePropertiesService;
import java.util.Collection;
import java.util.List;

public interface InternalPagePropertiesService
extends PagePropertiesService {
    public List<ExtractedDetails> getDetailsFromContent(Collection<ContentEntityObject> var1, String var2, DetailsSummaryMacroMetricsEvent.Builder var3);
}

