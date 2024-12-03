/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conluenceview.services;

import com.atlassian.confluence.plugins.conluenceview.query.ConfluencePagesQuery;
import com.atlassian.confluence.plugins.conluenceview.rest.dto.ConfluencePagesDto;

public interface ConfluencePagesService {
    public ConfluencePagesDto getPagesInSpace(ConfluencePagesQuery var1);

    public ConfluencePagesDto getPagesByIds(ConfluencePagesQuery var1);
}

