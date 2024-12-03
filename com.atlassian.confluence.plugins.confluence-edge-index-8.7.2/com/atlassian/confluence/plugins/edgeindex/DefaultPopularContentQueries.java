/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.edgeindex.EdgeQueries;
import com.atlassian.confluence.plugins.edgeindex.EdgeQueryParameter;
import com.atlassian.confluence.plugins.edgeindex.PopularContentQueries;
import com.atlassian.confluence.plugins.edgeindex.model.ContentEntityEdgeTargetInfo;
import com.atlassian.confluence.plugins.edgeindex.model.EdgeTargetInfo;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={PopularContentQueries.class})
public class DefaultPopularContentQueries
implements PopularContentQueries {
    private final EdgeQueries edgeQueries;
    private final ContentEntityManager contentEntityManager;

    @Autowired
    public DefaultPopularContentQueries(EdgeQueries edgeQueries, @ComponentImport ContentEntityManager contentEntityManager) {
        this.edgeQueries = edgeQueries;
        this.contentEntityManager = contentEntityManager;
    }

    @Override
    public List<ContentEntityObject> getMostPopular(int maxResults, EdgeQueryParameter queryParameter) {
        return this.convertToObjects(maxResults, this.edgeQueries.getMostPopular(queryParameter));
    }

    private List<ContentEntityObject> convertToObjects(int maxResults, List<EdgeTargetInfo> targets) {
        return targets.stream().filter(item -> item instanceof ContentEntityEdgeTargetInfo).map(item -> this.contentEntityManager.getById(((ContentEntityEdgeTargetInfo)item).getTargetId().getId().longValue())).filter(Objects::nonNull).limit(maxResults).collect(Collectors.toList());
    }
}

