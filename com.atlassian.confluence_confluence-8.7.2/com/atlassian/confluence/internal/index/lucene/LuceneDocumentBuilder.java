/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.bonnie.Handle
 *  com.atlassian.bonnie.HandleResolver
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.internal.search.v2.lucene.DocumentBuilder
 *  org.apache.lucene.document.Document
 */
package com.atlassian.confluence.internal.index.lucene;

import com.atlassian.annotations.Internal;
import com.atlassian.bonnie.Handle;
import com.atlassian.bonnie.HandleResolver;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.internal.index.lucene.LuceneFieldVisitor;
import com.atlassian.confluence.internal.search.v2.lucene.DocumentBuilder;
import com.atlassian.confluence.plugins.index.api.FieldDescriptor;
import com.atlassian.confluence.search.v2.AtlassianDocument;
import com.atlassian.confluence.search.v2.AtlassianDocumentBuilder;
import org.apache.lucene.document.Document;

@Internal
public class LuceneDocumentBuilder
implements DocumentBuilder {
    private final AtlassianDocumentBuilder<Searchable> atlassianDocumentBuilder;
    private final LuceneFieldVisitor luceneFieldVisitor;
    private final HandleResolver handleResolver;

    public LuceneDocumentBuilder(AtlassianDocumentBuilder<Searchable> atlassianDocumentBuilder, LuceneFieldVisitor luceneFieldVisitor, HandleResolver handleResolver) {
        this.atlassianDocumentBuilder = atlassianDocumentBuilder;
        this.luceneFieldVisitor = luceneFieldVisitor;
        this.handleResolver = handleResolver;
    }

    public Document getDocument(Searchable searchable) {
        AtlassianDocument atlassianDocument = this.atlassianDocumentBuilder.build(searchable);
        Document document = new Document();
        for (FieldDescriptor field : atlassianDocument.getFields()) {
            document.add(field.accept(this.luceneFieldVisitor));
        }
        return document;
    }

    public Handle getHandle(Object obj) {
        return this.handleResolver.getHandle(obj);
    }
}

