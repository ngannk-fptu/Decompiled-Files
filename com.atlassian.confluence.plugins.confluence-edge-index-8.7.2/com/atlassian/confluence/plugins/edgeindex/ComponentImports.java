/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.search.v2.CustomSearchIndexRegistry
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.setup.settings.ConfluenceDirectories
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.edgeindex;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.search.v2.CustomSearchIndexRegistry;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.setup.settings.ConfluenceDirectories;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.stereotype.Component;

@Component
public class ComponentImports {
    @ComponentImport
    private ConfluenceAccessManager confluenceAccessManager;
    @ComponentImport
    private ConfluenceDirectories confluenceDirectories;
    @ComponentImport
    private ContentEntityManager contentEntityManager;
    @ComponentImport
    private SearchManager searchManager;
    @ComponentImport
    private CustomSearchIndexRegistry customSearchIndexRegistry;
}

