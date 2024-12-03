/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.applinks.internal.common.web.data;

import com.atlassian.applinks.internal.common.docs.DocumentationLinker;
import com.atlassian.applinks.internal.common.json.JacksonJsonableMarshaller;
import com.atlassian.applinks.internal.rest.model.BaseRestEntity;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class HelpPathsDataProvider
implements WebResourceDataProvider {
    private static final String APPLINKS_DOCS_ROOT_KEY = "applinks.docs.root";
    private static final String ENTRIES = "entries";
    private final DocumentationLinker documentationLinker;

    public HelpPathsDataProvider(DocumentationLinker documentationLinker) {
        this.documentationLinker = documentationLinker;
    }

    public Jsonable get() {
        return JacksonJsonableMarshaller.INSTANCE.marshal((Object)BaseRestEntity.createSingleFieldEntity(ENTRIES, this.getAllHelpPaths()));
    }

    private Map<String, String> getAllHelpPaths() {
        return ImmutableMap.builder().put((Object)APPLINKS_DOCS_ROOT_KEY, (Object)this.documentationLinker.getDocumentationBaseUrl().toASCIIString()).putAll(this.documentationLinker.getAllLinkMappings()).build();
    }
}

