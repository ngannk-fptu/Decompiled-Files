/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContextPathHolder
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.search.model;

import com.atlassian.confluence.core.ContextPathHolder;
import com.atlassian.confluence.plugins.search.model.AttachmentResultBuilder;
import com.atlassian.confluence.plugins.search.model.ContentResultBuilder;
import com.atlassian.confluence.plugins.search.model.LastModificationFormatter;
import com.atlassian.confluence.plugins.search.model.SearchResultBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="searchResultBuilderFactory")
public class SearchResultBuilderFactory {
    private final ContextPathHolder contextPathHolder;
    private final LastModificationFormatter lastModificationFormatter;

    @Autowired
    public SearchResultBuilderFactory(LastModificationFormatter lastModificationFormatter, @ComponentImport ContextPathHolder contextPathHolder) {
        this.lastModificationFormatter = lastModificationFormatter;
        this.contextPathHolder = contextPathHolder;
    }

    public SearchResultBuilder createBuilder(String type, User user) {
        return "attachment".equals(type) ? new AttachmentResultBuilder(this.lastModificationFormatter, this.contextPathHolder, user) : new ContentResultBuilder(this.lastModificationFormatter, this.contextPathHolder, user);
    }
}

