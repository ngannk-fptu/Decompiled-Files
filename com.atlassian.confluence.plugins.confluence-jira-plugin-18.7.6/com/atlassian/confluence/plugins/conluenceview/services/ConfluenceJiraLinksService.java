/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conluenceview.services;

import com.atlassian.confluence.plugins.conluenceview.rest.dto.LinkedSpaceDto;
import java.util.List;

public interface ConfluenceJiraLinksService {
    public String getODApplicationLinkId();

    public List<LinkedSpaceDto> getLinkedSpaces(String var1, String var2);
}

